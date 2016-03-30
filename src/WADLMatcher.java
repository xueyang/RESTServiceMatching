import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import wordnet.SimilarityInfo;
import wordnet.SimilarityMeasure;

//WADL服务匹配器
public class WADLMatcher {
	WordSimilarity wordnet = new WordSimilarity();

	public WADLMatcher() {
		String wadlfile = "Douban-book.wadl";
	}

	// 单词分割器。把URL片段和参数名分割成WordNet可识别的简单词
	public ArrayList<String> wordSpliter(String word, Boolean resourceMode)
			throws Exception {
		// word = "book/music-app/userName/taobao.get";
		String[] delimiterSplited = word.split("[/\\-_.]");// 正则表达式里包含的分隔符：/（URL），-、_、.（复合词）
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < delimiterSplited.length; i++)// 根据大写字母进行分割。此段正则放到上面会出现大写字母本身被删掉的情况，无法解决，只能单独分割
		{
			String[] tempString = delimiterSplited[i].split("(?=[A-Z])");
			for (int j = 0; j < tempString.length; j++) {
				if (resourceMode && tempString[j].startsWith("{") && tempString[j].endsWith("}"))
					tempString[j] = tempString[j].substring(1, tempString[j].length() - 1);
				if(!resourceMode)
					if(tempString[j].equals("id") || tempString[j].equals("Id"))
						continue;
				result.add(tempString[j]);
			}
		}
		// System.out.println(result);
		// String[] x = hyphenSplited[5].split("(?=[A-Z])");
		return result;

	}

	public String wordFind(String siteName, String word)
	{
		SupplementWordsDictionary swd = new SupplementWordsDictionary();
		String tempWord;
		if(siteName.equals("Facebook"))
			return word;
		else 
		{
			tempWord = swd.supplementWordsForCommon.get(word);
			if(tempWord!=null)
				return tempWord;
			else 
			{
				switch(siteName)
				{
					case "Douban": tempWord = swd.supplementWordsForDouban.get(word); break;
					case "flickr": tempWord = swd.supplementWordsForFlickr.get(word); break;
					case "Renren": tempWord = swd.supplementWordsForRenren.get(word); break;
					case "Twitter": tempWord = swd.supplementWordsForTwitter.get(word); break;
					case "Weibo": tempWord = swd.supplementWordsForWeibo.get(word); break;
				}
				if(tempWord!=null)
					return tempWord;
				else 
					return word;
			}
		}
	}
	
	// 参数检查，检查是否有特殊参数
	public void paramCheck(Method method1, Method method2) {
		int i, j;
		ArrayList<Param> m1params = method1.params;
		ArrayList<Param> m2params = method2.params;
		int p1size = m1params.size();
		int p2size = m2params.size();
		Param tempParam;
		for (i = 0; i < p1size; i++) {
			tempParam = m1params.get(i);
			if ((tempParam.isResource != null)
					&& (tempParam.isResource == true)) {
				if (tempParam.fixedValue != null)
					method1.resourcePath = method1.resourcePath + "/" + tempParam.fixedValue;
				else if (tempParam.optionValues != null) {
					for (j = 0; j < tempParam.optionValues.size(); j++) {
						method1.resourcePath = method1.resourcePath + "/" + tempParam.optionValues.get(j);
					}
				}
			}
		}
		for (i = 0; i < p2size; i++) {
			tempParam = m2params.get(i);
			if ((tempParam.isResource != null) && (tempParam.isResource = true)) {
				if (tempParam.fixedValue != null)
					method2.resourcePath = method2.resourcePath + "/" + tempParam.fixedValue;
				else if (tempParam.optionValues != null) {
					for (j = 0; j < tempParam.optionValues.size(); j++) {
						method2.resourcePath = method2.resourcePath + "/" + tempParam.optionValues.get(j);
					}
				}
			}
		}

	}

	// 参数匹配
	public double matchParams(ArrayList<Param> m1params, ArrayList<Param> m2params, String site1Name, String site2Name) throws Exception {
		int i, j, k, l;
		int r1size = m1params.size();// 方法1参数列表长度
		int r2size = m2params.size();// 方法2参数列表长度
		if (r1size == 0) {
			if (r2size == 0)
				return 1;
			else
				return 0;
		} else if (r2size == 0)
			return 0;
		int dimension = r1size > r2size ? r1size : r2size;
		double[][] paramSimilarityMatrix = new double[dimension][dimension];// 采用较长的参数列表长度作为维数，间接地为较短的参数列表补足虚节点，满足KM算法需要（要求匹配双方长度相同）
		double wordSimilarity = 0;
		ArrayList<String> splitedFoundWord = new ArrayList<String>();
		SimilarityMeasure sim = wordnet.initializeWordNet();// 初始化WordNet
		for (i = 0; i < m1params.size(); i++) {
			for (j = 0; j < m2params.size(); j++) {
				// paramSimilarityMatrix[i][j] = 0.6 *
				// similarity.wordSimilarity(m1params.get(i).paramName,
				// m2params.get(j).paramName) + 0.4 *
				// dataTypeSimilarity(m1params.get(i).paramType,
				// m2params.get(j).paramType);
				ArrayList<String> p1SplitedResult = wordSpliter(m1params.get(i).paramName, false);// 分割字符
				ArrayList<String> p2SplitedResult = wordSpliter(m2params.get(j).paramName, false);
				for (k = 0; k < p1SplitedResult.size(); k++)
				{
					splitedFoundWord.addAll(wordSpliter(wordFind(site1Name, p1SplitedResult.get(k)), false));
				}
				p1SplitedResult.clear();
				p1SplitedResult.addAll(splitedFoundWord);
				splitedFoundWord.clear();
				for (k = 0; k < p2SplitedResult.size(); k++)
				{
					splitedFoundWord.addAll(wordSpliter(wordFind(site2Name, p2SplitedResult.get(k)), false));
				}
				p2SplitedResult.clear();
				p2SplitedResult.addAll(splitedFoundWord);
				splitedFoundWord.clear();
				double p1p2Similarity = 0;
				for (k = 0; k < p1SplitedResult.size(); k++)// 调用WordNet计算语义相似度。如果某参数包含多个简单词，应把它们看做整体，语义相似度取和平均作为该参数的语义相似度
				{
					for (l = 0; l < p2SplitedResult.size(); l++) {
						try {
							SimilarityInfo simResult = sim.getSimilarity(p1SplitedResult.get(k), p2SplitedResult.get(l));
							System.out.println(p1SplitedResult.get(k) + " " + p2SplitedResult.get(l));
							p1p2Similarity += simResult.getSimilarity();
						} catch (Exception e) {//这个异常往往是WordNet查找不到对应的词
							e.printStackTrace();
						}
					}
				}
				wordSimilarity = p1p2Similarity	/ (p1SplitedResult.size() * p2SplitedResult.size());
				if((m1params.get(i).paramType != null) && (m2params.get(j).paramType != null))
				{
					paramSimilarityMatrix[i][j] = 0.6 * wordSimilarity + 0.4 * dataTypeSimilarity(m1params.get(i).paramType, m2params.get(j).paramType);// 名称语义相似度和格式相似度综合起来作为匹配参数的相似度
				}
				else
					paramSimilarityMatrix[i][j] = wordSimilarity;
				if (m1params.get(i).isRequired)// 服务请求中必须包含的参数匹配相似度应该有所加成，彰显这些参数的重要，但加成不能太高
					paramSimilarityMatrix[i][j] = paramSimilarityMatrix[i][j] * 1.2;
				if (m2params.get(j).isRequired)
					paramSimilarityMatrix[i][j] = paramSimilarityMatrix[i][j] * 1.2;

			}
		}
		/*
		 * KM km = new KM(paramSimilarityMatrix); boolean hasPerfactMatch =
		 * km.km(); double sumNum = 0; if(hasPerfactMatch){ int a[] = new int[paramSimilarityMatrix.length]; a = km.getPerfactMatch();
		 * System.out.println("该二分图的最优匹配对为"); for(i = 0;i < a.length;i++){
		 * System.out.println(i+","+a[i]); sumNum +=
		 * paramSimilarityMatrix[i][a[i]]; } } else{ return 0; } sumNum = sumNum
		 * / dimension; System.out.println("\n参数匹配结果（二分图最优匹配）：" + sumNum);
		 */
		// 调用KM算法求二分图最优匹配
		MyKM km = new MyKM(paramSimilarityMatrix);
		if (km.km()) {
			double result = 0;
			// System.out.println(Arrays.toString(km.result()));
			for (i = 0; i < km.result().length; i++) {
				result += paramSimilarityMatrix[i][km.result()[i]];
			}
			result = result / dimension;
			System.out.println("\n参数匹配结果（二分图最优匹配）：" + result);
			return result;
		} else
			return 0;
	}
	
	//资源（path属性）匹配
	public double matchResource(String resourcepath1, String resourcepath2, String site1Name, String site2Name)	throws Exception {
		ArrayList<String> r1pathwords = wordSpliter(resourcepath1, true);
		ArrayList<String> r2pathwords = wordSpliter(resourcepath2, true);
		//int r1size = r1pathwords.size();
		//int r2size = r2pathwords.size();
		int i, j, r1WeightSquareSum = 0, r2WeightSquareSum = 0;
		// double[][] similarityMatrix= new double[r1size][r2size];
		//int dimension = r1size >= r2size ? r1size : r2size;
		//int shortdimension = r1size >= r2size ? r2size : r1size;
		//double[][] similarityMatrix = new double[dimension][dimension];
		//int[] r1Weight = new int[r1size];//词语向量权值，原则上前面的应该大于后面的
		//int[] r2Weight = new int[r2size];
		double numerator = 0, denominator = 0, result;
		ArrayList<String> splitedFoundWord = new ArrayList<String>();
		SimilarityMeasure sim = wordnet.initializeWordNet();//初始化WordNet
		for (i = 0; i < r1pathwords.size(); i++)
		{
			splitedFoundWord.addAll(wordSpliter(wordFind(site1Name, r1pathwords.get(i)), false));
		}
		r1pathwords.clear();
		r1pathwords.addAll(splitedFoundWord);
		splitedFoundWord.clear();
		for (i = 0; i < r2pathwords.size(); i++)
		{
			splitedFoundWord.addAll(wordSpliter(wordFind(site2Name, r2pathwords.get(i)), false));
		}
		r2pathwords.clear();
		r2pathwords.addAll(splitedFoundWord);
		splitedFoundWord.clear();
		int r1size = r1pathwords.size();
		int r2size = r2pathwords.size();
		int dimension = r1size >= r2size ? r1size : r2size;
		int shortdimension = r1size >= r2size ? r2size : r1size;
		double[][] similarityMatrix = new double[dimension][dimension];
		int[] r1Weight = new int[r1size];//词语向量权值，原则上前面的应该大于后面的
		int[] r2Weight = new int[r2size];
		for (i = 0; i < r1size; i++) {//取权值平方和，用于计算分母
			if (i == 0)
				r1Weight[i] = 2;
			else
				r1Weight[i] = 1;
			r1WeightSquareSum += r1Weight[i] * r1Weight[i];
		}
		for (i = 0; i < r2size; i++) {
			if (i == 0)
				r2Weight[i] = 2;
			else
				r2Weight[i] = 1;
			r2WeightSquareSum += r2Weight[i] * r2Weight[i];
		}
		//调用WordNet计算语义相似度，组建相似度矩阵
		for (i = 0; i < r1pathwords.size(); i++) {
			for (j = 0; j < r2pathwords.size(); j++) {
				try {
					SimilarityInfo simResult = sim.getSimilarity(r1pathwords.get(i), r2pathwords.get(j));
					similarityMatrix[i][j] = simResult.getSimilarity();
				} catch (Exception e) {
					e.printStackTrace();
					similarityMatrix[i][j] = 0;
				}
			}
		}
		//找寻与某单词相似度最高的单词，构造匹配对，取相似度
		int matchedwordscount = 0;
		for (i = 0; i < r1size; i++) {
			int maxIndex = max(similarityMatrix[i]);
			if (similarityMatrix[i][maxIndex] == 0)
				continue;
			matchedwordscount++;
			numerator += similarityMatrix[i][maxIndex] * r1Weight[i] * r2Weight[maxIndex];
			if(matchedwordscount == shortdimension)
				break;
			System.out.println(matchedwordscount + " " + shortdimension);
			// System.out.println("numerator " + similarityMatrix[i][maxIndex] +
			// " " + r1Weight[i]+r1pathwords.get(i) + " " + r2Weight[maxIndex]
			// +r2pathwords.get(maxIndex)+ " " + numerator);
			for (j = 0; j < r1size; j++)
				similarityMatrix[j][maxIndex] = 0;
		}
		denominator = Math.sqrt(r1WeightSquareSum) * Math.sqrt(r2WeightSquareSum); //分母计算
		result = numerator / denominator;
		System.out.println("\n资源路径匹配结果：" + result);
		return result;
	}

	public static int max(double[] a) {
		// 返回数组最大值
		int result = 0;
		double temp;
		temp = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] > temp) {
				temp = a[i];
				result = i;
			}
		}
		return result;
	}

	public static double dataTypeSimilarity(String type1, String type2) {//数据类型相似度
		String[][] compatibleTypes = {//兼容的XML Schema数据类型。兼容的数据类型都是派生于某一特定的数据类型。资料参考W3School的描述
				{ "string", "QName", "Name", "NCName", "token",
						"nomalizedString" },
				{ "date", "dateTime", "duration", "gDay", "gMonth",
						"gMonthDay", "gYear", "gYearMonth", "time" },
				{ "byte", "decimal", "double", "float", "int", "integer",
						"long", "negativeInteger", "nonNegativeInteger",
						"nonPositiveInteger", "positiveInteger", "short",
						"unsignedLong", "unsignedInt", "unsignedShort",
						"unsignedByte" }, };
		if (type1.equals(type2))//类型相同，匹配度为1；类型兼容，匹配度0.5；不兼容，匹配度为0
			return 1;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < compatibleTypes[i].length; j++) {
				if (type1.contains(compatibleTypes[i][j])) {
					for (int k = 0; k < compatibleTypes[i].length; k++) {
						if (type2.contains(compatibleTypes[i][k])) {
							return 0.5;
						}
					}
					return 0;
				}
			}
		}
		return 0;
	}

	public enum HTTPMethods {
		GET, PUT, POST, DELETE
	}

	public double matchOtherItems(Method method1, Method method2) {
		String method1Name = method1.methodName;
		String method2Name = method2.methodName;
		int m1EnumNo = HTTPMethods.valueOf(method1Name).ordinal();
		int m2EnumNo = HTTPMethods.valueOf(method2Name).ordinal();
		double[][] HTTPMethodSimilarityMatrix = { { 1, 0.6, 0.4, 0 },
				{ 0.6, 1, 0.8, 0 }, { 0.4, 0.8, 1, 0 }, { 0, 0, 0, 1 } };//HTTP Method相似度数据参考别人论文
		double methodNameSimilarity = HTTPMethodSimilarityMatrix[m1EnumNo][m2EnumNo];
		String[] m1Response = method1.responseRepresatation.split("/");
		String[] m2Response = method2.responseRepresatation.split("/");
		double responseSimilarity = (m1Response[0].equals(m2Response[0]) ? 1: 0) * 0.3 + (m1Response[1].equals(m2Response[1]) ? 1 : 0) * 0.7;//返回类型相似度
		double result = 0.2 * methodNameSimilarity + 0.1 * responseSimilarity;
		System.out.println("其他项匹配结果：" + result);
		return result;
	}

	public static double match(String WADLFile1, String WADLFile2)//匹配主程序
			throws Exception {
		WADLFile1 = "WADL/Douban-music-review-PUT.wadl";
		WADLFile2 = "WADL/Weibo-place-pois-add_photo.wadl";
		String site1Name = WADLFile1.split("/")[1].split("-")[0];
		String site2Name = WADLFile2.split("/")[1].split("-")[0];
		System.out.println(site1Name + " " + site2Name);
		WADLMatcher matcher = new WADLMatcher();
		WADLParser parser = new WADLParser();
		Map<String, String> w1ParseResult = parser.parse(WADLFile1);//解析WADL文件
		Map<String, String> w2ParseResult = parser.parse(WADLFile2);
		MethodBuilder builder = new MethodBuilder();
		ArrayList<Method> methodList1 = builder.build(w1ParseResult);//构建方法，作为匹配单位。默认每个WADL里只包含一个方法
		ArrayList<Method> methodList2 = builder.build(w2ParseResult);
		Method method1 = methodList1.get(0);
		Method method2 = methodList2.get(0);
		matcher.paramCheck(method1, method2);//参数检查
		double paramMatchResult = matcher.matchParams(method1.params, method2.params, site1Name, site2Name);//参数匹配
		double resourceMatchResult = matcher.matchResource(method1.resourcePath, method2.resourcePath, site1Name, site2Name);//资源路径匹配
		double otherItemsMatchResult = matcher.matchOtherItems(method1, method2);//匹配其他项
		double WADLMatchResult = 0.4 * paramMatchResult + 0.3 * resourceMatchResult + otherItemsMatchResult; //综合匹配度计算
		return WADLMatchResult;
		//System.out.println("最终匹配结果：" + WADLMatchResult);
	}

	public static void main(String[] args) throws Exception {
		match("1", "2");
		/*
		 * WADLMatcher matcher = new WADLMatcher(); WADLParser parser = new
		 * WADLParser(); Map<String, String> result =
		 * parser.parse("Douban-book.wadl"); //String Resource MethodBuilder
		 * builder = new MethodBuilder(); ArrayList<Method> methodList =
		 * builder.build(result); Method method1 = methodList.get(8); Method
		 * method2 = methodList.get(9); matcher.paramCheck(method1, method2);
		 * matcher.matchParams(method1.params, method2.params);
		 * matcher.matchResource(method1.resourcePath, method2.resourcePath);
		 * //matcher.matchOtherItems(method1, method2);
		 */
	}
}
