import java.util.ArrayList;

//�����ڵ���
public class Param {
	String paramName;	//������
	String paramType;	//�������ݸ�ʽ
	Boolean isRequired;	//�����Ƿ��ѡ
	Boolean isResource; //���������� 
	ArrayList<String> optionValues;	//������ѡֵ����fixedValueֻ��ѡ��һ���������������������
	String fixedValue;  //�����̶�ֵ����optionValuesֻ��ѡ��һ���������������������
}
