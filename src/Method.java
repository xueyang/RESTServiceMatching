import java.util.ArrayList;

//method�ڵ���
public class Method {
	String resourcePath;  //��ԴURL·�����ڸ��ڵ�resource��
	String methodName;	  //HTTP����������GET/POST/PUT/DELETE
	ArrayList<Param> templateParams;  //template������
	ArrayList<Param> params;			//��ͨ����
	int responseCode;				//����������״̬��
	String responseRepresatation;   //������������Դ����
}
