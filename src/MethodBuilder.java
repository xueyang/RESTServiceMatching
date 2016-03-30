import java.util.ArrayList;
import java.util.Map;

//�������������ӽ��������ص���Ϣ����ȡ��Ҫ��Ϣ���齨WADL����ƥ�䵥λ��������
public class MethodBuilder {
	public ArrayList<Method> build(Map<String, String> information) {
		int resourceCount = Integer.parseInt(information.get("resourceCount"));
		int methodCount = 0;
		ArrayList<Method> methodList = new ArrayList<Method>();
		int templateParamCount = 0;
		int paramCount = 0;
		int optionValueCount = 0;
		// int responseCount = 0;
		int i, j, k, l;
		String paramKey;
		for (i = 1; i <= resourceCount; i++) {
			methodCount = Integer.parseInt(information.get("r" + i
					+ "methodCount"));
			templateParamCount = Integer.parseInt(information.get("r" + i
					+ "templateParamCount"));
			ArrayList<Param> templateParams = new ArrayList<Param>();
			Param templateParam = new Param();
			for (j = 1; j <= templateParamCount; j++) {//��ȡtemplate�������б�
				templateParam.paramName = information.get("r" + i + "tp" + j + "name");
				templateParam.paramType = information.get("r" + i + "tp" + j + "type");
				templateParam.isRequired = true;
				templateParams.add(templateParam);
			}
			for (j = 1; j <= methodCount; j++) {//����method�ڵ㣬��Ҫ������Դ·��
				Method tempMethod = new Method();
				String methodKey = "r" + i + "m" + j;
				tempMethod.methodName = information.get(methodKey + "name");
				tempMethod.resourcePath = information.get("r" + i + "path");
				// tempMethod.responseCode =
				// Integer.parseInt(information.get(methodKey + "r1status"));
				tempMethod.responseRepresatation = information.get(methodKey + "r1mediaType");
				tempMethod.templateParams = templateParams;
				paramCount = Integer.parseInt(information.get(methodKey	+ "paramCount"));
				ArrayList<Param> params = new ArrayList<Param>();
				// Param tempParam = new Param();
				for (k = 1; k <= paramCount; k++) {//������ͨ�����б�
					Param tempParam = new Param();
					paramKey = "r" + i + "m" + j + "p" + k;
					tempParam.paramName = information.get(paramKey + "name");
					tempParam.paramType = information.get(paramKey + "type");
					if ((information.get(paramKey + "required") != null)
							&& (information.get(paramKey + "required").equals("true")))
						tempParam.isRequired = true;
					else
						tempParam.isRequired = false;
					optionValueCount = Integer.parseInt(information.get(paramKey + "optionValueCount"));
					if ((information.get(paramKey + "isResource") != null)
							&& (information.get(paramKey + "isResource").equals("true"))) {
						tempParam.isResource = true;
						if (information.get(paramKey + "fixed") != null)
							tempParam.fixedValue = information.get(paramKey	+ "fixed");
						else {
							tempParam.optionValues = new ArrayList<String>();
							for (l = 1; l <= optionValueCount; l++) {
								tempParam.optionValues.add(information.get(paramKey + "o" + l + "value"));
								// System.out.println(information.get(paramKey +
								// "o" + l +"value"));
							}
						}
					}

					params.add(tempParam);
				}
				tempMethod.params = params;
				methodList.add(tempMethod);
			}
		}
		return methodList;

	}
}
