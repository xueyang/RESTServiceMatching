import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import wordnet.SimilarityInfo;
import wordnet.SimilarityMeasure;

//WADL����ƥ����
public class WADLMatcher {
	WordSimilarity wordnet = new WordSimilarity();

	public WADLMatcher() {
		String wadlfile = "Douban-book.wadl";
	}

	// ���ʷָ�������URLƬ�κͲ������ָ��WordNet��ʶ��ļ򵥴�
	public ArrayList<String> wordSpliter(String word, Boolean resourceMode)
			throws Exception {
		// word = "book/music-app/userName/taobao.get";
		String[] delimiterSplited = word.split("[/\\-_.]");// ������ʽ������ķָ�����/��URL����-��_��.�����ϴʣ�
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < delimiterSplited.length; i++)// ���ݴ�д��ĸ���зָ�˶�����ŵ��������ִ�д��ĸ����ɾ����������޷������ֻ�ܵ����ָ�
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
	
	// ������飬����Ƿ����������
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

	// ����ƥ��
	public double matchParams(ArrayList<Param> m1params, ArrayList<Param> m2params, String site1Name, String site2Name) throws Exception {
		int i, j, k, l;
		int r1size = m1params.size();// ����1�����б���
		int r2size = m2params.size();// ����2�����б���
		if (r1size == 0) {
			if (r2size == 0)
				return 1;
			else
				return 0;
		} else if (r2size == 0)
			return 0;
		int dimension = r1size > r2size ? r1size : r2size;
		double[][] paramSimilarityMatrix = new double[dimension][dimension];// ���ýϳ��Ĳ����б�����Ϊά������ӵ�Ϊ�϶̵Ĳ����б�����ڵ㣬����KM�㷨��Ҫ��Ҫ��ƥ��˫��������ͬ��
		double wordSimilarity = 0;
		ArrayList<String> splitedFoundWord = new ArrayList<String>();
		SimilarityMeasure sim = wordnet.initializeWordNet();// ��ʼ��WordNet
		for (i = 0; i < m1params.size(); i++) {
			for (j = 0; j < m2params.size(); j++) {
				// paramSimilarityMatrix[i][j] = 0.6 *
				// similarity.wordSimilarity(m1params.get(i).paramName,
				// m2params.get(j).paramName) + 0.4 *
				// dataTypeSimilarity(m1params.get(i).paramType,
				// m2params.get(j).paramType);
				ArrayList<String> p1SplitedResult = wordSpliter(m1params.get(i).paramName, false);// �ָ��ַ�
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
				for (k = 0; k < p1SplitedResult.size(); k++)// ����WordNet�����������ƶȡ����ĳ������������򵥴ʣ�Ӧ�����ǿ������壬�������ƶ�ȡ��ƽ����Ϊ�ò������������ƶ�
				{
					for (l = 0; l < p2SplitedResult.size(); l++) {
						try {
							SimilarityInfo simResult = sim.getSimilarity(p1SplitedResult.get(k), p2SplitedResult.get(l));
							System.out.println(p1SplitedResult.get(k) + " " + p2SplitedResult.get(l));
							p1p2Similarity += simResult.getSimilarity();
						} catch (Exception e) {//����쳣������WordNet���Ҳ�����Ӧ�Ĵ�
							e.printStackTrace();
						}
					}
				}
				wordSimilarity = p1p2Similarity	/ (p1SplitedResult.size() * p2SplitedResult.size());
				if((m1params.get(i).paramType != null) && (m2params.get(j).paramType != null))
				{
					paramSimilarityMatrix[i][j] = 0.6 * wordSimilarity + 0.4 * dataTypeSimilarity(m1params.get(i).paramType, m2params.get(j).paramType);// �����������ƶȺ͸�ʽ���ƶ��ۺ�������Ϊƥ����������ƶ�
				}
				else
					paramSimilarityMatrix[i][j] = wordSimilarity;
				if (m1params.get(i).isRequired)// ���������б�������Ĳ���ƥ�����ƶ�Ӧ�������ӳɣ�������Щ��������Ҫ�����ӳɲ���̫��
					paramSimilarityMatrix[i][j] = paramSimilarityMatrix[i][j] * 1.2;
				if (m2params.get(j).isRequired)
					paramSimilarityMatrix[i][j] = paramSimilarityMatrix[i][j] * 1.2;

			}
		}
		/*
		 * KM km = new KM(paramSimilarityMatrix); boolean hasPerfactMatch =
		 * km.km(); double sumNum = 0; if(hasPerfactMatch){ int a[] = new int[paramSimilarityMatrix.length]; a = km.getPerfactMatch();
		 * System.out.println("�ö���ͼ������ƥ���Ϊ"); for(i = 0;i < a.length;i++){
		 * System.out.println(i+","+a[i]); sumNum +=
		 * paramSimilarityMatrix[i][a[i]]; } } else{ return 0; } sumNum = sumNum
		 * / dimension; System.out.println("\n����ƥ����������ͼ����ƥ�䣩��" + sumNum);
		 */
		// ����KM�㷨�����ͼ����ƥ��
		MyKM km = new MyKM(paramSimilarityMatrix);
		if (km.km()) {
			double result = 0;
			// System.out.println(Arrays.toString(km.result()));
			for (i = 0; i < km.result().length; i++) {
				result += paramSimilarityMatrix[i][km.result()[i]];
			}
			result = result / dimension;
			System.out.println("\n����ƥ����������ͼ����ƥ�䣩��" + result);
			return result;
		} else
			return 0;
	}
	
	//��Դ��path���ԣ�ƥ��
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
		//int[] r1Weight = new int[r1size];//��������Ȩֵ��ԭ����ǰ���Ӧ�ô��ں����
		//int[] r2Weight = new int[r2size];
		double numerator = 0, denominator = 0, result;
		ArrayList<String> splitedFoundWord = new ArrayList<String>();
		SimilarityMeasure sim = wordnet.initializeWordNet();//��ʼ��WordNet
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
		int[] r1Weight = new int[r1size];//��������Ȩֵ��ԭ����ǰ���Ӧ�ô��ں����
		int[] r2Weight = new int[r2size];
		for (i = 0; i < r1size; i++) {//ȡȨֵƽ���ͣ����ڼ����ĸ
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
		//����WordNet�����������ƶȣ��齨���ƶȾ���
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
		//��Ѱ��ĳ�������ƶ���ߵĵ��ʣ�����ƥ��ԣ�ȡ���ƶ�
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
		denominator = Math.sqrt(r1WeightSquareSum) * Math.sqrt(r2WeightSquareSum); //��ĸ����
		result = numerator / denominator;
		System.out.println("\n��Դ·��ƥ������" + result);
		return result;
	}

	public static int max(double[] a) {
		// �����������ֵ
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

	public static double dataTypeSimilarity(String type1, String type2) {//�����������ƶ�
		String[][] compatibleTypes = {//���ݵ�XML Schema�������͡����ݵ��������Ͷ���������ĳһ�ض����������͡����ϲο�W3School������
				{ "string", "QName", "Name", "NCName", "token",
						"nomalizedString" },
				{ "date", "dateTime", "duration", "gDay", "gMonth",
						"gMonthDay", "gYear", "gYearMonth", "time" },
				{ "byte", "decimal", "double", "float", "int", "integer",
						"long", "negativeInteger", "nonNegativeInteger",
						"nonPositiveInteger", "positiveInteger", "short",
						"unsignedLong", "unsignedInt", "unsignedShort",
						"unsignedByte" }, };
		if (type1.equals(type2))//������ͬ��ƥ���Ϊ1�����ͼ��ݣ�ƥ���0.5�������ݣ�ƥ���Ϊ0
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
				{ 0.6, 1, 0.8, 0 }, { 0.4, 0.8, 1, 0 }, { 0, 0, 0, 1 } };//HTTP Method���ƶ����ݲο���������
		double methodNameSimilarity = HTTPMethodSimilarityMatrix[m1EnumNo][m2EnumNo];
		String[] m1Response = method1.responseRepresatation.split("/");
		String[] m2Response = method2.responseRepresatation.split("/");
		double responseSimilarity = (m1Response[0].equals(m2Response[0]) ? 1: 0) * 0.3 + (m1Response[1].equals(m2Response[1]) ? 1 : 0) * 0.7;//�����������ƶ�
		double result = 0.2 * methodNameSimilarity + 0.1 * responseSimilarity;
		System.out.println("������ƥ������" + result);
		return result;
	}

	public static double match(String WADLFile1, String WADLFile2)//ƥ��������
			throws Exception {
		WADLFile1 = "WADL/Douban-music-review-PUT.wadl";
		WADLFile2 = "WADL/Weibo-place-pois-add_photo.wadl";
		String site1Name = WADLFile1.split("/")[1].split("-")[0];
		String site2Name = WADLFile2.split("/")[1].split("-")[0];
		System.out.println(site1Name + " " + site2Name);
		WADLMatcher matcher = new WADLMatcher();
		WADLParser parser = new WADLParser();
		Map<String, String> w1ParseResult = parser.parse(WADLFile1);//����WADL�ļ�
		Map<String, String> w2ParseResult = parser.parse(WADLFile2);
		MethodBuilder builder = new MethodBuilder();
		ArrayList<Method> methodList1 = builder.build(w1ParseResult);//������������Ϊƥ�䵥λ��Ĭ��ÿ��WADL��ֻ����һ������
		ArrayList<Method> methodList2 = builder.build(w2ParseResult);
		Method method1 = methodList1.get(0);
		Method method2 = methodList2.get(0);
		matcher.paramCheck(method1, method2);//�������
		double paramMatchResult = matcher.matchParams(method1.params, method2.params, site1Name, site2Name);//����ƥ��
		double resourceMatchResult = matcher.matchResource(method1.resourcePath, method2.resourcePath, site1Name, site2Name);//��Դ·��ƥ��
		double otherItemsMatchResult = matcher.matchOtherItems(method1, method2);//ƥ��������
		double WADLMatchResult = 0.4 * paramMatchResult + 0.3 * resourceMatchResult + otherItemsMatchResult; //�ۺ�ƥ��ȼ���
		return WADLMatchResult;
		//System.out.println("����ƥ������" + WADLMatchResult);
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
