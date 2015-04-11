package test;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IKAnalyzerTest {

	private static Map<String,Map<Integer,Integer>> cipin = new HashMap<String,Map<Integer,Integer>>();
	private static Map<String,Double> keyword = new HashMap<String,Double>();


    public static void test() {
        String keyWord = "股东洗盘，赶紧跑" ;
		//创建IKAnalyzer中文分词对象  
        IKAnalyzer analyzer = new IKAnalyzer();  
        // 使用智能分词  
        analyzer.setUseSmart(false);
        // 打印分词结果  
        try {  
            printAnalysisResult(analyzer, keyWord);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    public static void main(String[] args) throws IOException {
        String str = "股东洗盘LE，高开高走,赶紧跑";
        StringReader reader = new StringReader(str);

        IKAnalyzer analyzer = new IKAnalyzer(false);
        TokenStream stream = analyzer.tokenStream("", reader);
        CharTermAttribute termAtt  = (CharTermAttribute)stream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offAtt  = (OffsetAttribute)stream.addAttribute(OffsetAttribute.class);
        // 循环打印出分词的结果，及分词出现的位置
        try {
            while(stream.incrementToken()){
                System.out.println(termAtt.toString() + "("+ offAtt.startOffset() + " " + offAtt.endOffset()+")");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    IKAnalysis("股东洗盘LE，赶紧跑");
    }

    public static String IKAnalysis(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            byte[] bt = str.getBytes();// str
            InputStream ip = new ByteArrayInputStream(bt);
            Reader read = new InputStreamReader(ip);
            IKSegmenter iks = new IKSegmenter(read, true);
            Lexeme t;
            while ((t = iks.next()) != null) {
                sb.append(t.getLexemeText() + " , ");

            }
            sb.delete(sb.length() - 1, sb.length());
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(sb.toString());
        return sb.toString();

    }

    //测试老师给的词汇计算情感效果如何
    public static void main22(String[] args) throws IOException {
        Map<String,Integer> keywords = new HashMap<String, Integer>();

        File basefilepath = new File("D:\\股吧\\guba\\motion");
        File [] filename = basefilepath.listFiles();
        for(int i=0;i<filename.length;i++){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename[i])); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    String[] words = line.split("\t");
                    keywords.put(words[0],Integer.parseInt(words[1]));
                    line = br.readLine();
                }
                reader.close();
            System.out.println(filename[i].getName());
            }


        File basepath = new File("D:\\股吧\\长文本处理（词典法）");
        File matrixpath = new File("D:\\股吧\\out人工标注");
        TezhenXuanze.Product_WordMatrix_Train(basepath,matrixpath);

        List<List<Double>> qingganzhi = new ArrayList<List<Double>>();
        File [] filepath = matrixpath.listFiles();
        for(int i=0;i<filepath.length;i++){
            Integer count=0;//用于统计在该类别下共有多少条句子 PS：以后若统计文档，只需更改一下count的位置即可（从while循环移到for循环下）
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            List<Double> classqinggan = new ArrayList<Double>();
            for(File filename1:files){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename1)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {

                    double tmp = 0.0;
                    count++;

                    for(String word:keywords.keySet()){
                        if(line.indexOf(word)!=-1)
                            tmp += keywords.get(word);
                    }
                    line = br.readLine();
                    classqinggan.add(tmp);
                }
                reader.close();
            }
//            qingganzhi.add(classqinggan);
            System.out.println(filepath[i].getName()+":"+classqinggan.size());
            int wordcount =0;
            int guanwangwu = 0;
            int kanduowu = 0;
            int kankongwu = 0;
            if(filepath[i].getName().toString().indexOf("空")!=-1){
                for(Double tmp:classqinggan){
                    if(tmp<0) wordcount++;
                    else if(tmp==0) guanwangwu++;
                    else kanduowu++;
                }
            }
            if(filepath[i].getName().toString().indexOf("多")!=-1){
                for(Double tag:classqinggan){
                    if(tag==0) guanwangwu++;
                    else if(tag>0) wordcount++;
                    else kankongwu++;
                }
            }
            if(filepath[i].getName().toString().indexOf("望")!=-1||filepath[i].getName().toString().indexOf("它")!=-1
                    ||filepath[i].getName().toString().indexOf("新")!=-1||filepath[i].getName().toString().indexOf("广")!=-1){
                for(Double tag:classqinggan){
                        if(tag==0) wordcount++;
                        else if(tag>0) kanduowu++;
                        else    kankongwu++;
                    }
                }
            System.out.println("句子数（去除其它）：" + (classqinggan.size()) + "正确分类数:" + wordcount +"正确率："+(double)wordcount/(classqinggan.size())+"看空错误："+kankongwu+"看多错误："+kanduowu+"其他错误："+guanwangwu);
        }

    }

//    从txt中读入文件
	private static List<TxtValue> txtinput(String pathname) throws IOException{
        List<TxtValue> tmp = new  ArrayList<TxtValue>();
        File filename = new File(pathname); // 要读取以上路径的input。txt文件  
        InputStreamReader reader = new InputStreamReader(  
                new FileInputStream(filename)); // 建立一个输入流对象reader  
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言  

        String line = br.readLine();
        while (line != null) {
            String[] tmpp = line.split("\t");
            TxtValue juzi = new TxtValue(tmpp[3], Integer.parseInt(tmpp[1]));
            tmp.add(juzi);
            line = br.readLine(); // 一次读入一行数据
        } 
        
        return tmp;
    }
    
	public static void main2(String[] args) throws IOException {
    
		//创建IKAnalyzer中文分词对象  
        IKAnalyzer analyzer = new IKAnalyzer();  
        // 使用智能分词  
        analyzer.setUseSmart(true);  
        // 打印分词结果  
        List<TxtValue> alltxt = new ArrayList<TxtValue>();
        for(int i=1;i<7;i++){
        	String pathname = "D:\\股吧\\guba\\hqx\\gupiao ("+i+").txt";
            alltxt.addAll(txtinput(pathname));
        }

        System.out.println("句子总数："+alltxt.size());

        try {  
        	for(TxtValue juzi : alltxt)
        			tongjicipin(analyzer, juzi.getJuzi(),juzi.getValue());
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        //使用现有标记好的词汇库
//            for(int j=1;j<7;j++){
//                String pathname = "D:\\guba\\motion\\m_"+j+".txt";
//                File filename = new File(pathname); // 要读取以上路径的input。txt文件
//                InputStreamReader reader = new InputStreamReader(
//                        new FileInputStream(filename)); // 建立一个输入流对象reader
//                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
//                String line = br.readLine();
//                while (line != null) {
//                    String[] tmpp = line.split("\t");
//                    keyword.put(tmpp[0],Double.parseDouble(tmpp[1])*j);
//                    line = br.readLine(); // 一次读入一行数据
//                }
//            }
        double max = 0.0;
        int tag=0;
        for(int i=0;i<350;i++){
        CountQX(i);
            int zhengque = 0;
            int alljz = 0;
            for(TxtValue juzi : alltxt)
                if (juzi.getValue() != 6) {
                    Double qx = 0.0;
                    int count = 0;
                    alljz++;
                    for (Map.Entry<String, Double> entry : keyword.entrySet()) {
                        if (juzi.getJuzi().indexOf(entry.getKey()) != -1) {
                            qx += entry.getValue();
                            count++;
                        }
                    }
    //                if (count != 0 && Math.abs(qx / (double) count - juzi.getValue()) < 1) zhengque++;
                    if (count != 0){
                        //计算否定词个数
//                        int counter = 0;
//                        int startposition = 0;
//                        while(startposition < juzi.getJuzi().length()){
//                            int search = juzi.getJuzi().indexOf("不", startposition);
//                            if(search != -1){
//                                counter++;
//                                startposition = search + "不".length();
//                            }else{
//                                break;
//                            }
//                        }
//                        startposition = 0;
//                        while(startposition < juzi.getJuzi().length()){
//                            int search = juzi.getJuzi().indexOf("非", startposition);
//                            if(search != -1){
//                                counter++;
//                                startposition = search + "非".length();
//                            }else{
//                                break;
//                            }
//                        }
//                        double tmp = 0;
//                        if(counter%2 == 0)
//                            tmp = qx / (double) count*-1;
//                        else
                        double tmp = qx / (double) count;

//  if(Math.abs(tmp-juzi.getValue())<0.01||(tmp>3&&juzi.getValue()>3)||(tmp<3&&juzi.getValue()<3))
                        if((tmp>0&&juzi.getValue()>3)||(tmp<0&&juzi.getValue()<3)||Math.abs(tmp+3-juzi.getValue())<0.01)
                            zhengque++;
                    }
                }
         //   System.out.println(zhengque+"ss"+alljz+"qq"+(double)zhengque/alljz);
            if((double)zhengque/alljz > max){
                max = (double)zhengque/alljz;
                tag = i;
            }
        }
        System.out.println("max:"+max+"tag:"+tag);
    }

    public static void CountQX(int mincount){
        keyword.clear();
        String output ="";
        for(Map.Entry<String,Map<Integer,Integer>> entry : cipin.entrySet()){
            output = "关键词："+entry.getKey();
            int all=0;
            double qx = 0.0;
            for(int i =1;i<7;i++){
                if(entry.getValue().containsKey(i)){
                    output += "\t" + entry.getValue().get(i);
                    all += entry.getValue().get(i);
                    qx += entry.getValue().get(i)*i;
                }
                else
                    output +="\t";
            }
            if(all>mincount&&((qx/all)>3.5||(qx/all)<2.5)){
            //    System.out.println(output+"\t"+qx/all);
                keyword.put(entry.getKey(),qx/all-3.0);
            }
        }

    }
    
    /** 
     * 打印出给定分词器的分词结果 
     *  
     * @param analyzer 
     *            分词器 
     * @param keyWord 
     *            关键词 
     * @throws Exception 
     */
    public static void printAnalysisResult(Analyzer analyzer, String keyWord)
            throws Exception {  
        System.out.println("["+keyWord+"]分词效果如下");  
        TokenStream tokenStream = analyzer.tokenStream("content",  
                new StringReader(keyWord));  
        tokenStream.addAttribute(CharTermAttribute.class);  
        while (tokenStream.incrementToken()) {  
            CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);  
            System.out.println(charTermAttribute.toString());
        }  
    } 
    
    public static void tongjicipin(Analyzer analyzer, String keyWord,Integer value)
            throws Exception {   
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(keyWord));  
        tokenStream.addAttribute(CharTermAttribute.class);  
        while (tokenStream.incrementToken()) { 
        	String tmp = tokenStream.getAttribute(CharTermAttribute.class).toString();
            Map<Integer, Integer> tmap = new HashMap<Integer, Integer>();
        	if(cipin.containsKey(tmp)){
                tmap.putAll(cipin.get(tmp));

                if(cipin.get(tmp).containsKey(value))
                    tmap.put(value,cipin.get(tmp).get(value)+1);
                else
                    tmap.put(value,1);
            }
            else{
                tmap.put(value, 1);
        }
            cipin.put(tmp,tmap);
        }
    }

    public static void tongjicipin(Analyzer analyzer, String keyWord,Map<String,Integer> cihui)
            throws Exception {
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(keyWord));
        tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            String tmp = tokenStream.getAttribute(CharTermAttribute.class).toString();
            if(cihui.containsKey(tmp))
                cihui.put(tmp,cihui.get(tmp)+1);
            else
                cihui.put(tmp,1);
        }
    }
}
