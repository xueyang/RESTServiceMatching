import java.util.ArrayList;

//参数节点类
public class Param {
	String paramName;	//参数名
	String paramType;	//参数数据格式
	Boolean isRequired;	//参数是否必选
	Boolean isResource; //特殊参数标记 
	ArrayList<String> optionValues;	//参数可选值，和fixedValue只可选其一，仅用在特殊参数处理中
	String fixedValue;  //参数固定值，和optionValues只可选其一，仅用在特殊参数处理中
}
