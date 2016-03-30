import java.util.ArrayList;

//method节点类
public class Method {
	String resourcePath;  //资源URL路径，在父节点resource中
	String methodName;	  //HTTP方法名，即GET/POST/PUT/DELETE
	ArrayList<Param> templateParams;  //template风格参数
	ArrayList<Param> params;			//普通参数
	int responseCode;				//服务器返回状态码
	String responseRepresatation;   //服务器返回资源类型
}
