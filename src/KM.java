import java.util.*;

public class KM {

    public static final int INF = 1000000;

    private int row, col, size;

    private double[][] edge;

    private int[] flag;

    private double[] hQuan;

    private double[] vQuan;

    private boolean[] hToken;

    private boolean[] vToken;
    
    private int [] perfactMatch;

    public KM(double[][] pic) {
        edge = pic;
        row = edge.length;
        col = edge[0].length;
        hToken = new boolean[row];
        vToken = new boolean[col];
        hQuan = new double[row];
        vQuan = new double[col];
        size = row > col ? col : row;
        if (row == size) {
            flag = new int[col];
        } else {
            flag = new int[row];
        }
        perfactMatch = new int [row];
        init();
    }

    private void init() {
        for (int i = 0; i < flag.length; i++) {
            flag[i] = -1;
        }
        for (int i = 0; i < hToken.length; i++) {
            hToken[i] = false;
        }
        for (int i = 0; i < vToken.length; i++) {
            vToken[i] = false;
        }
        if (row == size) {
            for (int i = 0; i < vQuan.length; i++) {
                vQuan[i] = 0;
            }
            for (int i = 0; i < hQuan.length; i++) {
                hQuan[i] = -INF;
                for (int j = 0; j < vQuan.length; j++) {
                    hQuan[i] = max(hQuan[i], edge[i][j]);
                }
            }
        } else {
            for (int i = 0; i < hQuan.length; i++) {
                hQuan[i] = 0;
            }
            for (int i = 0; i < vQuan.length; i++) {
                vQuan[i] = -INF;
                for (int j = 0; j < hQuan.length; j++) {
                    vQuan[i] = max(vQuan[i], edge[j][i]);
                }
            }
        }
    }

    public boolean km() {
        int[][] map = new int[row][col];
        //�����С�ڵ�����
        if (row == size) {
            double dmin = INF;
            do {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        //hQuan vQuan��ʾ����
                        if (hQuan[i] + vQuan[j] == edge[i][j])
                            map[i][j] = 1;
                        else
                            map[i][j] = 0;
                    }
                }
                //map��ʾ����������ͼ
                //��������������ͼ���Ƿ����걸ƥ��
                //���û���걸ƥ�䣬����KM�㷨������Ķ���
             
                if (hasPerfectMatch(map)){
                    return true;
                }
                    
                    

                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (hToken[i] && !vToken[j]) {
                            dmin = min(dmin, hQuan[i] + vQuan[j] - edge[i][j]);
                        }
                    }
                }
                if (dmin != INF && dmin > 0) {
                    for (int i = 0; i < row; i++) {
                        if (hToken[i])
                            hQuan[i] -= dmin;
                    }
                    for (int i = 0; i < col; i++) {
                        if (vToken[i])
                            vQuan[i] += dmin;
                    }
                }
            } while (dmin != INF && dmin > 0);
            return false;
        }
        /**
         * ����д�����
         * */
        else {
            double dmin = INF;
            do {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < col; j++) {
                        if (hQuan[i] + vQuan[j] == edge[i][j])
                            map[i][j] = 1;
                        else
                            map[i][j] = 0;
                    }
                }
                if (hasPerfectMatch(map))
                    return true;

                for (int i = 0; i < col; i++) {
                    for (int j = 0; j < row; j++) {
                        if (vToken[i] && !hToken[j]) {
                            dmin = min(dmin, hQuan[i] + vQuan[j] - edge[i][j]);
                        }
                    }
                }
                if (dmin != INF && dmin > 0) {
                    for (int i = 0; i < row; i++) {
                        if (hToken[i])
                            hQuan[i] += dmin;
                    }
                    for (int i = 0; i < col; i++) {
                        if (vToken[i])
                            vQuan[i] -= dmin;
                    }
                }
            } while (dmin != INF && dmin > 0);
            return false;
        }
        
    }

    /**
     * �ж��Ƿ�����ѣ�������ƥ��
     * judge whether the map has a perfect match.
     * @param map indicates whether each two points have been connected.
     * @return true if there is a perfect match in the map, or false.
     */
    public boolean hasPerfectMatch(int[][] map) {
        int i, j;
        //��С�ڵ�����
        if (row == size) {
            for (i = 0; i < flag.length; i++)
                flag[i] = -1;

            for (i = 0; i < size; i++) {

                for (j = 0; j < hToken.length; j++)
                    hToken[j] = false;
                for (j = 0; j < vToken.length; j++)
                    vToken[j] = false;
                if (!findAugumentPath(i, map/* ,token */))
                    break;
            }
            //�����������Ҳ�������·��
            //����Ҳ�������·��ʱ��ƥ����Ŀ���������������ô���������ƥ��
            //if (i < row)
                //return false;
            return true;
        }
        //�д�����
        else {
            for (i = 0; i < flag.length; i++)
                flag[i] = -1;

            for (i = 0; i < size; i++) {

                for (j = 0; j < hToken.length; j++)
                    hToken[j] = false;
                for (j = 0; j < vToken.length; j++)
                    vToken[j] = false;
                if (!findAugumentPath(i, map))
                    break;
            }
            //if (i < row)
                //return false;
            return true;
        }
    }

    /**
     * �����Ƿ�������·
     * judge whether there is a augument path at the <code>pos</code> point in
     * <code>map</code>
     * @param pos the order of the special point in the map
     * @param map the map's maltrix
     * @return true if there is a augumemt path at the <code>pos</code> point in
     *  the <code>map</code
     */
    public boolean findAugumentPath(int pos, int[][] map) {
        if (row == size) {
            hToken[pos] = true;
            //��������ͼ�ұ�ÿһ����
            for (int i = 0; i < col; i++) {
                //�����pos,i֮�������߲���i����v�����У���ôֱ�Ӽ���
                if (map[pos][i] == 1 && vToken[i] == false) {
                    vToken[i] = true;

                    if (flag[i] == -1 ||findAugumentPath(flag[i], map) ) {
                        flag[i] = pos;
                   
                        for(int m = 0;m < flag.length;m++){
                            //System.out.println(flag[m]);
                            //sumNum += edge[flag[m]][m];                           
                            if(flag[m] >= 0){
                                perfactMatch[flag[m]] = m;
                            }
                        }
                        return true;
                    }
                    
                }
            }
            return false;
        } else {
            vToken[pos] = true;
            for (int i = 0; i < row; i++) {
                if (map[i][pos] == 1 && hToken[i] == false) {
                    hToken[i] = true;

                    if (flag[i] == -1 || findAugumentPath(flag[i], map)) {
                        flag[i] = pos;
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * compute the max number of two
     * @param i one digit used to compare with another one
     * @param j the other digit used to compare with the first one
     * @return the bigger one number of this two
     */
    public double max(double i, double j) {
        return i > j ? i : j;
    }

    public double min(double i, double j) {
        return i > j ? j : i;
    }

    //������Ȩ��
    public int[] getPerfactMatch(){
        return perfactMatch;
    }
    public static void main(String[] args) {
        //int[][] edge1 = { { 7, 6, 5 }, { 4, 1, 8 }, { 3, 2, 9 } };
        //int[][] edge1 = { { 3, 5, 5,4,1 }, { 2, 2, 0,2,2 }, { 2, 4, 4,1,0 },{0,1,1,0,0},{1,2,1,3,3} };
        //double[][] edge1 =  { { 3.00001, 8.00001, 7.00001,6.00001 }, { 4.00001, 3.00001, 1.00001,2.00001 }, { 5.00001, 6.00001, 1.00001,8.00001 },{7.00001,3.00001,5.00001,4.00001} };
    	//double[][] edge1 =  { { 3.32487, 8.234, 7.34534,6.2312,3.234 }, { 4.234, 3.546, 1.23534,2.5436,9.453 }, { 5.3252, 6.09864, 1.523,8.43598,5.293847 },{7.3457,3.3984,5.2348,4.43590,9.3458},{3.2847,5.325,8.3457,7.234,0.324 }};
        double[][] edge1 = {{-1,2,3},{-1,3,-1},{-1,1,1}};
    	KM temp = new KM(edge1);
        boolean hasPerfactMatch = temp.km();  
        if(hasPerfactMatch){
            int a[] = new int [edge1.length];
            a = temp.getPerfactMatch();
            System.out.println("�ö���ͼ������ƥ���Ϊ");
            double sumNum = 0;
            for(int i = 0;i < a.length;i++){
                System.out.println(i+","+a[i]);
                sumNum += edge1[i][a[i]];
            }
           System.out.println("�ö���ͼ������ƥ���ȨֵΪ"+sumNum);
        }
        else{
            System.out.println("�ö���ͼ����������ƥ��");
        }
    }
}