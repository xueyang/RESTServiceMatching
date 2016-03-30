import java.util.Arrays;


public class MyKM {

   /**
    * @param args
    */
   public static void main(String[] args) {
       //MyKM km = new MyKM(new int[][]{ { 3, 8, 7,6 }, { 4, 3, 1,2 }, { 5, 6, 1,8 },{7,3,5,4} });
	   MyKM km=new MyKM(new double[][]{{-1,2,3},{-1,3,-1},{-1,1,1}});
       System.out.println(km.hungarian());
       System.out.println(Arrays.toString(km.result()));
       System.out.println(km.km());
       System.out.println(Arrays.toString(km.result()));
   }

   double edges[][];//�ߵ�Ȩ��,-1��ʾû�б�
   //int edges[][];//�ߵ�Ȩ��,-1��ʾû�б�
   int xmatch[];//���յ�ƥ��ͼ�У�x���϶�Ӧ��y����//���match[i]=-1,��ô˵�����x��û��ƥ��ͼ��
   int ymatch[];//����ƥ��ͼ�У�y���϶�Ӧ��x�㡣
   double lx[];//x�Ķ���
   double ly[];//y�Ķ���

   double subEdges[][];//�����ͼ



   public MyKM(double[][] edges) {
       this.edges=new double[edges.length][edges[0].length];
       for(int i=0;i<this.edges.length;i++){
           for(int j=0;j<this.edges[0].length;j++){
               this.edges[i][j]=edges[i][j];
           }
       }
       this.subEdges=new double[edges.length][edges[0].length];
       for(int i=0;i<subEdges.length;i++){
           for(int j=0;j<subEdges[0].length;j++){
               subEdges[i][j]=edges[i][j];
           }
       }
       xmatch=new int[edges.length];
       ymatch=new int[edges[0].length];
       lx=new double[edges.length];
       ly=new double[edges[0].length];

       for(int i=0;i<xmatch.length;i++){
           xmatch[i]=-1;
       }
       for(int i=0;i<ymatch.length;i++){
           ymatch[i]=-1;
       }
   }

   public int hungarian(){//�������㷨�����ƥ��
       for(int i=0;i<xmatch.length;i++){
           xmatch[i]=-1;
       }
       for(int i=0;i<ymatch.length;i++){
           ymatch[i]=-1;
       }
       int ans=0;
       for(int i=0;i<subEdges.length;i++){

           if (xmatch[i]==-1){
               if(findAugmentPath(i,null,null)){
                   ans++;
               }
           }
       }
       return ans;
   }
   public int[] result(){
       return xmatch;
   }
   /**
    * Ѱ������·����������ֳɹ�������·������ֱ���޸���Ӧ��ƥ���������tree X Y��Ϊnull���򽫼���о����ĵ��¼����
    * @param i  ��x���ϵ��ĸ��㿪ʼѰ��
    * @param treeX  ����Ϊ�գ������Ϊ�գ����¼����·���Ͼ�������Щ�㣬Ϊkm�㷨�����
    * @param treeY  ����Ϊ�գ������Ϊ�գ����¼����·���Ͼ�������Щ�㣬Ϊkm�㷨�����
    * @return
    */
   boolean findAugmentPath(int i,boolean treeX[],boolean treeY[]){
       boolean checkedy[];//��ȱ���ʱ���ж��Ƿ�Ҫ�����ýڵ��
       checkedy=new boolean[edges[0].length];
       for(int j=0;j<ymatch.length;j++){
           checkedy[j]=false;
       }
       return subFindAugmentPath(i,treeX,treeY,checkedy);
   }
   /**
    * Ѱ������·����������ֳɹ�������·������ֱ���޸���Ӧ��ƥ���������tree X Y��Ϊnull���򽫼���о����ĵ��¼����
    * @param i  ��x���ϵ��ĸ��㿪ʼѰ��
    * @param treeX  ����Ϊ�գ������Ϊ�գ����¼����·���Ͼ�������Щ�㣬Ϊkm�㷨�����
    * @param treeY  ����Ϊ�գ������Ϊ�գ����¼����·���Ͼ�������Щ�㣬Ϊkm�㷨�����
    * @param checkedy ��¼��ȱ���ʱ���ж��Ƿ�Ҫ�����ýڵ��
    * @return
    */
   private boolean subFindAugmentPath(int i,boolean treeX[],boolean treeY[], boolean[] checkedy){
       double[] edge=subEdges[i];
       if(treeX!=null){
           treeX[i]=true;
       }
       for(int j=0;j<edge.length;j++){
           if(edge[j]==-1||checkedy[j]){//x[i]��y[j]�ޱߣ�����һ����
               continue;
           }
           if(treeY!=null)
               treeY[j]=true;
           if(edge[j]>=0&&ymatch[j]==-1){//x[i]��y[j]����һ������·��
               xmatch[i]=j;//��¼����·��
               ymatch[j]=i;
               return true;
           }
           if(edge[j]>=0&&ymatch[j]!=-1){
               checkedy[j]=true;
               if(subFindAugmentPath(ymatch[j],treeX,treeY,checkedy)){
                   //��¼����·��(������ȡ���Ѿ��ڵݹ��������)
                   xmatch[i]=j;
                   ymatch[j]=i;
                   return true;
               }
           }
       }
       return false;
   }
   public boolean km(){
       //�����е�-1ȫ����0��ʾ
       for(int i=0;i<xmatch.length;i++)
           for(int j=0;j<ymatch.length;j++)
               if(edges[i][j]==-1)
                   edges[i][j]=0;
       //��ʼ������
       for(int i=0;i<xmatch.length;i++){
           lx[i]=Integer.MIN_VALUE;
           for(double j:edges[i]){
               if(lx[i]<j){
                   lx[i]=j;
               }
           }
       }
       for(int i=0;i<ymatch.length;i++){
           ly[i]=0;
       }
       /*System.out.println("[DEBUG]��ʼ����");
       for(int i=0;i<xmatch.length;i++){
           System.out.print(i+":"+lx[i]+",");
       }
       System.out.println();*/
       boolean treeX[]=new boolean[xmatch.length];
       boolean treeY[]=new boolean[ymatch.length];

       /*System.out.println("[DEBUG]ԭͼ");
       for(int i=0;i<xmatch.length;i++){
           System.out.print(i+":");
           for(int j=0;j<ymatch.length;j++){
               if(edges[i][j]!=0)
                   System.out.print(j+"("+edges[i][j]+")\t");
           }
           System.out.println();
       }*/
      
       //��ʼ��Ѱ��һ�������ͼ
       for(int i=0;i<xmatch.length;i++){
               for(int j=0;j<ymatch.length;j++){
                   if(edges[i][j]==lx[i]+ly[j]){
                       subEdges[i][j]=edges[i][j];
                   }else{
                       subEdges[i][j]=-1;
                   }
               }
       }
       double d=Integer.MAX_VALUE;
       for(int t=0;d>0;t++){
           //t<=xmatch.length&&t<=ymatch.length
           /*System.out.println("[DEBUG]�����ͼ"+t);
           for(int i=0;i<xmatch.length;i++){
               for(int j=0;j<ymatch.length;j++){
                   if(subEdges[i][j]!=-1)
                       System.out.print(i+","+j+"\t");
               }
           }
           System.out.println();*/
           //Ѱ��һ�����ƥ��
           this.hungarian();
           /*System.out.println("[DEBUG]���ƥ��"+t);
           for(int i=0;i<xmatch.length;i++){
               if(xmatch[i]!=-1)
                   System.out.print(i+","+xmatch[i]+"\t");
           }
           System.out.println();*/
           //�ж��Ƿ����걸ƥ��
           int i=0;
           for(i=0;i<xmatch.length;i++){
               if (xmatch[i]==-1)
                   break;
           }
           //����ǣ�����
           if(i==xmatch.length)
               return true;
           //������ǣ���һ��ʧ�ܵ�����·��
           for(int j=0;j<treeX.length;j++)
               treeX[j]=false;
           for(int j=0;j<treeY.length;j++)
               treeY[j]=false;           
           this.findAugmentPath(i,treeX,treeY);
           /*System.out.println("[DEBUG]ʧ�ܵ�����·��x"+t);
           for(int k=0;k<xmatch.length;k++){
               if(treeX[k])
                   System.out.print(k+"\t");
           }
           System.out.println();
           System.out.println("[DEBUG]ʧ�ܵ�����·��y"+t);
           for(int k=0;k<ymatch.length;k++){
               if(treeY[k])
                   System.out.print(k+"\t");
           }
           System.out.println();*/

           //��·���ϵ�����x=x-d,y=y+d��ʹ֮��Ȼ���㶥�������

           for(int j=0;j<treeX.length;j++){
               if(treeX[j]){
                   for(int k=0;k<ymatch.length;k++){
                       if(!treeY[k])
                           if(d>lx[j]+ly[k]-edges[j][k]){
                               d=lx[j]+ly[k]-edges[j][k];
                               if(d<=0)
                                   System.out.println(t+"\t error!");
                           }

                   }
               }
           }
           //System.out.println("[DEBUG]dֵ��"+d);
           for(int j=0;j<treeX.length;j++){
               if(treeX[j]){
                   lx[j]-=d;
               }
           }
           /*System.out.println("[DEBUG]x����");
           for(i=0;i<xmatch.length;i++){
               System.out.print(i+":"+lx[i]+",");
           }
           System.out.println();*/
           for(int j=0;j<treeY.length;j++){
               if(treeY[j]){
                   ly[j]+=d;
               }
           }
           /*System.out.println("[DEBUG]y����");
           for(i=0;i<ymatch.length;i++){
               System.out.print(i+":"+ly[i]+",");
           }
           System.out.println();*/
           //���������ͼ
           for(i=0;i<xmatch.length;i++){
               if(treeX[i]){
                   for(int j=0;j<ymatch.length;j++){
                       if(edges[i][j]==lx[i]+ly[j]){//ֻ�ӱ� ������
                           subEdges[i][j]=edges[i][j];
                       }
                   }
               }
               int tmp=0;
               for(int j=0;j<ymatch.length;j++){
                   tmp+=subEdges[i][j];
               }
               if(tmp==-17){
                   System.out.println(t+"\terror:"+i);
               }
           }
           //System.out.println("========================");
       }
       return false;
   }
}
