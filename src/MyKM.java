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

   double edges[][];//边的权重,-1表示没有边
   //int edges[][];//边的权重,-1表示没有边
   int xmatch[];//最终的匹配图中，x集合对应的y集合//如果match[i]=-1,那么说明这个x点没在匹配图中
   int ymatch[];//最终匹配图中，y集合对应的x点。
   double lx[];//x的顶标
   double ly[];//y的顶标

   double subEdges[][];//相等子图



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

   public int hungarian(){//匈牙利算法求最大匹配
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
    * 寻找增广路径，如果发现成功的增广路径，则直接修改相应的匹配结果。如果tree X Y不为null，则将检测中经过的点记录下来
    * @param i  从x集合的哪个点开始寻找
    * @param treeX  可以为空，如果不为空，则记录增广路径上经过了哪些点，为km算法服务的
    * @param treeY  可以为空，如果不为空，则记录增广路径上经过了哪些点，为km算法服务的
    * @return
    */
   boolean findAugmentPath(int i,boolean treeX[],boolean treeY[]){
       boolean checkedy[];//深度遍历时，判断是否要跳过该节点的
       checkedy=new boolean[edges[0].length];
       for(int j=0;j<ymatch.length;j++){
           checkedy[j]=false;
       }
       return subFindAugmentPath(i,treeX,treeY,checkedy);
   }
   /**
    * 寻找增广路径，如果发现成功的增广路径，则直接修改相应的匹配结果。如果tree X Y不为null，则将检测中经过的点记录下来
    * @param i  从x集合的哪个点开始寻找
    * @param treeX  可以为空，如果不为空，则记录增广路径上经过了哪些点，为km算法服务的
    * @param treeY  可以为空，如果不为空，则记录增广路径上经过了哪些点，为km算法服务的
    * @param checkedy 记录深度遍历时，判断是否要跳过该节点的
    * @return
    */
   private boolean subFindAugmentPath(int i,boolean treeX[],boolean treeY[], boolean[] checkedy){
       double[] edge=subEdges[i];
       if(treeX!=null){
           treeX[i]=true;
       }
       for(int j=0;j<edge.length;j++){
           if(edge[j]==-1||checkedy[j]){//x[i]与y[j]无边，找下一个点
               continue;
           }
           if(treeY!=null)
               treeY[j]=true;
           if(edge[j]>=0&&ymatch[j]==-1){//x[i]到y[j]就是一条增广路径
               xmatch[i]=j;//记录增广路径
               ymatch[j]=i;
               return true;
           }
           if(edge[j]>=0&&ymatch[j]!=-1){
               checkedy[j]=true;
               if(subFindAugmentPath(ymatch[j],treeX,treeY,checkedy)){
                   //记录增广路径(其他的取反已经在递归中完成了)
                   xmatch[i]=j;
                   ymatch[j]=i;
                   return true;
               }
           }
       }
       return false;
   }
   public boolean km(){
       //将边中的-1全部用0表示
       for(int i=0;i<xmatch.length;i++)
           for(int j=0;j<ymatch.length;j++)
               if(edges[i][j]==-1)
                   edges[i][j]=0;
       //初始化顶标
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
       /*System.out.println("[DEBUG]初始顶标");
       for(int i=0;i<xmatch.length;i++){
           System.out.print(i+":"+lx[i]+",");
       }
       System.out.println();*/
       boolean treeX[]=new boolean[xmatch.length];
       boolean treeY[]=new boolean[ymatch.length];

       /*System.out.println("[DEBUG]原图");
       for(int i=0;i<xmatch.length;i++){
           System.out.print(i+":");
           for(int j=0;j<ymatch.length;j++){
               if(edges[i][j]!=0)
                   System.out.print(j+"("+edges[i][j]+")\t");
           }
           System.out.println();
       }*/
      
       //初始化寻找一个相等子图
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
           /*System.out.println("[DEBUG]相等子图"+t);
           for(int i=0;i<xmatch.length;i++){
               for(int j=0;j<ymatch.length;j++){
                   if(subEdges[i][j]!=-1)
                       System.out.print(i+","+j+"\t");
               }
           }
           System.out.println();*/
           //寻找一个最大匹配
           this.hungarian();
           /*System.out.println("[DEBUG]最大匹配"+t);
           for(int i=0;i<xmatch.length;i++){
               if(xmatch[i]!=-1)
                   System.out.print(i+","+xmatch[i]+"\t");
           }
           System.out.println();*/
           //判断是否是完备匹配
           int i=0;
           for(i=0;i<xmatch.length;i++){
               if (xmatch[i]==-1)
                   break;
           }
           //如果是，结束
           if(i==xmatch.length)
               return true;
           //如果不是，找一个失败的增广路径
           for(int j=0;j<treeX.length;j++)
               treeX[j]=false;
           for(int j=0;j<treeY.length;j++)
               treeY[j]=false;           
           this.findAugmentPath(i,treeX,treeY);
           /*System.out.println("[DEBUG]失败的增广路径x"+t);
           for(int k=0;k<xmatch.length;k++){
               if(treeX[k])
                   System.out.print(k+"\t");
           }
           System.out.println();
           System.out.println("[DEBUG]失败的增广路径y"+t);
           for(int k=0;k<ymatch.length;k++){
               if(treeY[k])
                   System.out.print(k+"\t");
           }
           System.out.println();*/

           //将路径上的所有x=x-d,y=y+d，使之仍然满足顶标的性质

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
           //System.out.println("[DEBUG]d值："+d);
           for(int j=0;j<treeX.length;j++){
               if(treeX[j]){
                   lx[j]-=d;
               }
           }
           /*System.out.println("[DEBUG]x顶标");
           for(i=0;i<xmatch.length;i++){
               System.out.print(i+":"+lx[i]+",");
           }
           System.out.println();*/
           for(int j=0;j<treeY.length;j++){
               if(treeY[j]){
                   ly[j]+=d;
               }
           }
           /*System.out.println("[DEBUG]y顶标");
           for(i=0;i<ymatch.length;i++){
               System.out.print(i+":"+ly[i]+",");
           }
           System.out.println();*/
           //更新相等子图
           for(i=0;i<xmatch.length;i++){
               if(treeX[i]){
                   for(int j=0;j<ymatch.length;j++){
                       if(edges[i][j]==lx[i]+ly[j]){//只加边 不减边
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
