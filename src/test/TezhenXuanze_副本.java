package test;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Math.log;
import static java.lang.Math.pow;

/**
 * Created by yizhou on 14-4-9.
 */
public class TezhenXuanze_副本 {
    private static Map<String,Integer> WordList = new HashMap<String, Integer>();//所有词
    private static Map<String,Integer> ClassList = new HashMap<String,Integer>();//所有分类
//    private static Map<String,Map<Integer,Double>> WordFeatureValue = new HashMap<String, Map<Integer, Double>>();//用来存放所有类的互信息
    private static Map<String,Double> WordFeatureValue = new HashMap<String, Double>();
    private static List<String> KeyWord = new ArrayList<String>();
    private static int wordnum=0;
    private static int classnum=0;
    private static Pattern pattern = Pattern.compile("[0-9]{6}");
    private static Pattern pattern1 = Pattern.compile("[0-9]{1,10}");
    //    basefilepath为根目录，里面包含所有分类（如看多目录，看空目录等）filepath[]为一级子目录，每个filepath[i]目录里包含该分类下所有文本
//    输入的文本为未经分词的原始文本
    public static void Product_WordMatrix(File basefilepath,File writepath) throws IOException {
        IKAnalyzer analyzer = new IKAnalyzer(true);
        File [] filepath = basefilepath.listFiles();
        Integer count = 0;
        classnum = filepath.length;
        for(int i=0;i<classnum;i++){
            ClassList.put(filepath[i].getName(),i);
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            File path = new File(writepath.getPath()+"\\"+filepath[i].getName());//生成类别目录
            if(!path.exists())  path.mkdirs();
            int dayux00=0;
            for(File filename:files){
                File writefile=new File(path.getPath()+"\\"+filename.getName());//将原始文档转化为矩阵形式的词文档
                FileWriter fw=new FileWriter(writefile);
                BufferedWriter writer=new BufferedWriter(fw);

                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    count++;
                    line = shaixuan(line,filename.getName());
                    if(line == null) {dayux00++;line=br.readLine();continue;}
                    String[] lines = line.split("[。？?]");
//                    for(int it=0;it<lines.length;it++){ //长句子分为短句子
//                        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(lines[it]));
                    TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));

                        tokenStream.addAttribute(CharTermAttribute.class);
                        while (tokenStream.incrementToken()){
                            String word = tokenStream.getAttribute(CharTermAttribute.class).toString();
                            writer.write(word + "\t");
                            if(!pattern1.matcher(word).find()) {
                                WordList.put(word, wordnum);
                                wordnum++;
                            }
                        }
                        writer.write(count.toString());
                        writer.write("\r\n");
                        writer.flush();
//                    }

                    line = br.readLine();
                }
                writer.close();
                reader.close();
                }
            System.out.println("类别：" + filepath[i].getName() + "过滤数目:" + dayux00+":"+count);
            }
        }

    public static String shaixuan(String line,String filename){
        line = line.replaceAll("[+~$`^=|<>～`$^+=|<>￥×]","");
        if(line.indexOf("评级")!=-1){
            line=line.replaceAll("[“”\"的]", "");
            if(line.indexOf("评级")>5)
                line = line.substring(line.indexOf("评级")-6,line.indexOf("评级"));
            else
                line.substring(0,line.indexOf("评级"));
        }
        else if(line.indexOf("投资建议")!=-1){
            if(line.indexOf("风险提示")!=-1)
                line = line.substring(line.indexOf("投资建议"),line.indexOf("风险提示"));
            else
                line = line.substring(line.indexOf("投资建议"));

        }

        else if(line.indexOf("研报")!=-1&&line.indexOf("评级")==-1)
            line=null;

        else if(line.indexOf("公告日期")!=-1||(pattern.matcher(line).find()&&!(line.indexOf(filename.substring(0,5))!=-1)))
            line=null;

        return line;
    }

    public static void Product_WordMatrix_Test(File basefilepath,File writepath) throws IOException {
        IKAnalyzer analyzer = new IKAnalyzer(true);
        File [] filepath = basefilepath.listFiles();
        Integer count = 0;
        for(int i=0;i<filepath.length;i++){
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            File path = new File(writepath.getPath()+"\\"+filepath[i].getName());//生成类别目录
            if(!path.exists())  path.mkdirs();
            int dayux00=0;
            for(File filename:files){
                File writefile=new File(path.getPath()+"\\"+filename.getName());//将原始文档转化为矩阵形式的词文档
                FileWriter fw=new FileWriter(writefile);
                BufferedWriter writer=new BufferedWriter(fw);

                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    count++;
                    line = shaixuan(line,filename.getName());
                    if(line == null) {dayux00++;line=br.readLine();continue;}
                    String[] lines = line.split("[。？?]");
//                    for(int it=0;it<lines.length;it++){ //长句子分为短句子
//                        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(lines[it]));
                    TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));

                        tokenStream.addAttribute(CharTermAttribute.class);
                        while (tokenStream.incrementToken()){
                            String word = tokenStream.getAttribute(CharTermAttribute.class).toString();
                            writer.write(word + "\t");

                        }
                        writer.write(count.toString());
                        writer.write("\r\n");
                        writer.flush();
//                    }

                    line = br.readLine();
                }
                writer.close();
                reader.close();
            }
            System.out.println("测试类别："+filepath[i].getName()+"\t过滤数目:"+dayux00);
        }
    }



    public static void Product_ZiMatrix(File basefilepath,File writepath) throws IOException {
        File [] filepath = basefilepath.listFiles();
        classnum = filepath.length;
        for(int i=0;i<classnum;i++){
            ClassList.put(filepath[i].getName(),i);
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            File path = new File(writepath.getPath()+"\\"+filepath[i].getName());//生成类别目录
            if(!path.exists())  path.mkdirs();
            int dayux00=0;
            for(File filename:files){
                File writefile=new File(path.getPath()+"\\"+filename.getName());//将原始文档转化为矩阵形式的词文档
                FileWriter fw=new FileWriter(writefile);
                BufferedWriter writer=new BufferedWriter(fw);

                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {

                    if(line.indexOf("公告日期")!=-1||(pattern.matcher(line).find()&&!(line.indexOf(filename.getName().substring(0,5))!=-1))||line.length()>140) {line = br.readLine();dayux00++;continue;} //过滤
                     char[] tokenStream = line.replaceAll("[\\p{P}+~$`^=|<>～`$^+=|<>￥×0-9]","").toCharArray();

                    for(int ii=0;ii<tokenStream.length;ii++)
                    if(true){
                        String word = String.valueOf(tokenStream[ii]);
                        writer.write(word+"\t");
                        if (!WordList.containsKey(word)&&!word.equals("")){
                            WordList.put(word,wordnum);
                            wordnum++;
                        }
                    }
                    writer.write("\r\n");
                    line = br.readLine();
                    writer.flush();
                }
                writer.close();
                reader.close();
            }
//            System.out.println("类别：" + filepath[i].getName() + "\t过滤数目:" + dayux00);
        }
    }

    public static void Product_ZiMatrix_Test(File basefilepath,File writepath) throws IOException {
        File [] filepath = basefilepath.listFiles();
        for(int i=0;i<filepath.length;i++){
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            File path = new File(writepath.getPath()+"\\"+filepath[i].getName());//生成类别目录
            if(!path.exists())  path.mkdirs();
            int dayux00=0;
            for(File filename:files){
                File writefile=new File(path.getPath()+"\\"+filename.getName());//将原始文档转化为矩阵形式的词文档
                FileWriter fw=new FileWriter(writefile);
                BufferedWriter writer=new BufferedWriter(fw);

                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {

                    if(line.indexOf("公告日期")!=-1||(pattern.matcher(line).find()&&!(line.indexOf(filename.getName().substring(0,5))!=-1))||line.length()>500) {line = br.readLine();dayux00++;continue;} //过滤
                    char[] tokenStream = line.replaceAll("[\\p{P}+~$`^=|<>～`$^+=|<>￥×0-9]","").toCharArray();


                    for(int ii=0;ii<tokenStream.length;ii++){
                        String word = String.valueOf(tokenStream[ii]);
                        writer.write(word+"\t");
                    }
                    writer.write("\r\n");
                    line = br.readLine();
                    writer.flush();
                }
                writer.close();
                reader.close();
            }
//            System.out.println("测试类别："+filepath[i].getName()+"\t过滤数目:"+dayux00);
        }
    }



    //得到一个三维的矩阵【wordnum】【class】【isnot】用于记录每个词项在不同类中的文档频率,
    // isnot的0维存储某词出现在某类别中的次数，1维存储该类别中某词没有出现的次数
    public static int[][][] GetWordFeature(File basefilepath) throws IOException{
        int [][][]matrix = new int[wordnum][classnum][2];
        File [] filepath = basefilepath.listFiles();


        for(int i=0;i<classnum;i++){
            Integer count=0;//用于统计在该类别下共有多少条句子 PS：以后若统计文档，只需更改一下count的位置即可（从while循环移到for循环下）
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            for(File filename:files){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    count++;
                    String[] words = line.split("\t");
                    for(String word:words){
                     if (WordList.containsKey(word))    matrix[WordList.get(word)][i][0]++;
                    }
                    line = br.readLine();
                }
                reader.close();
            }
            for(int j=0;j<wordnum;j++)
                matrix[j][i][1] = count - matrix[j][i][0];

        }
        return matrix;
    }


    //从matrix中获取互信息(or卡方)值，存入WordFeatureValue中
    public static void GetFeatureValue(File basepath) throws IOException
    {
        int[][][]matrix = GetWordFeature(basepath);
        System.out.println("================获得矩阵OK===================");
        for(Map.Entry<String,Integer> entry:WordList.entrySet()) {
//                Map<Integer,Double> sum = new HashMap<Integer, Double>();
            Double sum = new Double(0);
                for (int i = 0; i < classnum; i++) {
                    int num1 = matrix[entry.getValue()][i][0];
                    int num2 = matrix[entry.getValue()][i][1];
                    int num3 = 0;
                    int num4 = 0;
                    for (int j = 0; j < classnum; j++)
                        if (j != i) {
                            num3 += matrix[entry.getValue()][j][0];
                            num4 += matrix[entry.getValue()][j][1];
                        }//统计非本类中出现词与没出现词的个数

//                   sum +=  calIG(num1, num2, num3, num4);//信息增益计算公式
//                    sum +=  calhuxinxi(num1, num2, num3, num4);//互信息计算公式
                        sum +=  calkafang(num1, num2, num3, num4);//卡方计算公式
                }
                WordFeatureValue.put(entry.getKey(), sum);//把词项在各个类别中的互信息和作为这个词的得分
        }
    }

    //互信息计算公式
    public static double calhuxinxi(int num1,int num2,int num3,int num4) {
        int N=num1+num2+num3+num4;
        int N1dian=num1+num2;
        int Ndian1=num1+num3;
        int N0dian=num3+num4;
        int Ndian0=num2+num4;
        double a = num1 * 1.0 * Math.log(N * (num1 + 1) * 1.0 / (N1dian * Ndian1));
        double b = num3 * 1.0 * Math.log(N * (num3 + 1) * 1.0 / (N0dian * Ndian1));
        double c = num2 * 1.0 * Math.log(N * (num2 + 1) * 1.0 / (N1dian * Ndian0));
        double d = num4 * 1.0 * Math.log(N * (num4 + 1) * 1.0 / (N0dian * Ndian0));
        double e = (N * Math.log(2));
        return (a+b+c+d)/e;
    }

    //卡方计算公式
    public static double calkafang(int A,int B,int C,int D) {

        return pow(A*D-B*C,2)/((A+B)*(C+D));
    }

    //信息增益计算公式
    public static double calIG(int A,int B,int C,int D) {
        double PCi = (double)(A+C)/(A+B+C+D);
        double PCi_t = (double)A/(A+B);
        double PCi_tf = (double)C/(C+D);
        double P_t = (double)(A+B)/(A+B+C+D);

        return -PCi*log(PCi)/log(2) + P_t*PCi_t*log(PCi_t)/log(2) + (1-P_t)*PCi_tf*log(PCi_tf)/log(2);
    }

    private static void Word2Num(File basefilepath,File writepath) throws IOException {

        File [] filepath = basefilepath.listFiles();
        classnum = filepath.length;
        for(int i=0;i<classnum;i++){
            String result = filepath[i].getName();
            ClassList.put(filepath[i].getName(),i);
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            File path = new File(writepath.getPath()+"\\"+filepath[i].getName());//生成类别目录
            if(!path.exists())  path.mkdirs();
            File writefile=new File(path.getPath()+"\\"+i+".txt");
            FileWriter fw=new FileWriter(writefile);
            BufferedWriter writer=new BufferedWriter(fw);
            for(String word : KeyWord){
                writer.write(word+"\t");
            }
            writer.write("编号\tresult");
            writer.write("\r\n");
            writer.flush();

            for(File filename:files){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    String[] wordstmp = line.split("\t");
                    List<String> words = new ArrayList<String>();
                    for(int ii=0;ii<wordstmp.length;ii++)
                        words.add(wordstmp[ii]);
                    for(String word : KeyWord){
                        if(words.contains(word))
                            writer.write("T\t");
                        else
                            writer.write("F\t");
                    }
                    writer.write(words.get(words.size()-1)+"\t");

                    writer.write(filepath[i].getName());
                    writer.write("\r\n");
                    line = br.readLine();
                    writer.flush();
                }
                reader.close();
            }
            writer.close();
        }

    }


    public static void main(String []args) throws IOException
    {
        File matrixpath = new File("D:\\股吧\\gubashuchu");
        File basepath = new File("D:\\股吧\\gubatmp");


        Product_WordMatrix(basepath, matrixpath);
        GetFeatureValue(matrixpath);

        List<Map.Entry<String,Double>> arrayList = new ArrayList<Map.Entry<String,Double>>(WordFeatureValue.entrySet());
        Collections.sort(arrayList, new Comparator<Map.Entry<String,Double>>(){
            public int compare(Map.Entry<String,Double> o1,Map.Entry<String,Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //选择特征词
        int i=0;
//        int count =8600;//选择特征词数量
        int count =WordList.size();
        for(Map.Entry<String,Double> entry: arrayList){
//            System.out.println(entry.getKey()+":"+entry.getValue());
            KeyWord.add(entry.getKey());
            i++;
            if(i==count) break;
        }


        //转换为矩阵表
        File shuchu = new File("D:\\股吧\\gubanum");
        Word2Num(matrixpath,shuchu);

        //检验结果
        File testinput = new File("D:\\股吧\\gubatest");
        File testtmp = new File("D:\\股吧\\testtmp");
        File testoutput = new File("D:\\股吧\\testoutput");
        Product_WordMatrix_Test(testinput,testtmp);
        Word2Num(testtmp,testoutput);

    }

    public static void yongzihuafen(String []args) throws IOException
    {
        File basepath = new File("D:\\股吧\\gubatmp");
        File matrixpath = new File("D:\\股吧\\gubashuchu2");

        Product_ZiMatrix(basepath, matrixpath);
        GetFeatureValue(matrixpath);

        List<Map.Entry<String,Double>> arrayList = new ArrayList<Map.Entry<String,Double>>(WordFeatureValue.entrySet());
        Collections.sort(arrayList, new Comparator<Map.Entry<String,Double>>(){
            public int compare(Map.Entry<String,Double> o1,Map.Entry<String,Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //选择特征词
        int i=0;
//        int count =8600;//选择特征词数量
        int count =WordList.size();
        for(Map.Entry<String,Double> entry: arrayList){
//            System.out.println(entry.getKey()+":"+entry.getValue());
            KeyWord.add(entry.getKey());
            i++;
            if(i==count) break;
        }


        //转换为矩阵表
        File shuchu = new File("D:\\股吧\\gubanum2");
        Word2Num(matrixpath,shuchu);

        //检验结果
        File testinput = new File("D:\\股吧\\gubatest");
        File testtmp = new File("D:\\股吧\\testtmp2");
        File testoutput = new File("D:\\股吧\\testoutput2");
        Product_ZiMatrix_Test(testinput, testtmp);
        Word2Num(testtmp,testoutput);

    }


}
