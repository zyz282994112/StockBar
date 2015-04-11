package product;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by yizhou on 14-4-9.
 */
public class DataPreProcess {
    private static List<String> KeyWord = new ArrayList<String>();

    //    basefilepath为根目录，里面包含所有分类（如看多目录，看空目录等）filepath[]为一级子目录，每个filepath[i]目录里包含该分类下所有文本
    //    输入的文本为未经分词的原始文本
    //tag=0 train/ tag=1 test/ tag=2 process
    public static void Product_WordMatrix(File rowdata,File fencidata,int tag) throws IOException {
        IKAnalyzer analyzer = new IKAnalyzer(true);//将原始文档转化为矩阵形式的词文档
        FileWriter fw=new FileWriter(fencidata);
        BufferedWriter writer=new BufferedWriter(fw);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(rowdata),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        while (line != null) {
            if(tag!=2){
                writer.write(line.split("\t")[0]+"\t");
                line=line.split("\t")[1];
            }
            else if(!line.contains("look")) {line=br.readLine();continue;}
            //    line = shaixuan(line,filename.getName().substring(0,5));
            //  if(line == null) {yiciguolv++;line=br.readLine();continue;}
            TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));
            tokenStream.addAttribute(CharTermAttribute.class);
            while (tokenStream.incrementToken()){
                String word = tokenStream.getAttribute(CharTermAttribute.class).toString();
                writer.write(word + "\t");
                if(tag==0&&!KeyWord.contains(word))
                    KeyWord.add(word);
            }
            line = br.readLine();
            writer.write("\r\n");
        }
        writer.close();
        reader.close();
    }

    //tag=0 train/ tag=1 test/ tag=2 process
    private static int Word2Num(File fencidata,File vecdata,int tag) throws IOException {

        FileWriter fw=new FileWriter(vecdata);
        BufferedWriter writer=new BufferedWriter(fw);
//      for(String word : KeyWord){writer.write(word+"\t"); }
        for(int num =0;num<KeyWord.size();num++){ writer.write("c"+num+",");}
        writer.write("result");
        writer.write("\r\n");
        writer.flush();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(fencidata),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        int count=0;
        while (line != null) {
            List<String> words=new ArrayList<String>();
            Collections.addAll(words, line.split("\t"));

            for(String word : KeyWord){
                if(words.contains(word))
                    writer.write("T,");
                else
                    writer.write("F,");
            }
            if(tag!=2)
                writer.write(words.get(0) + "\r\n");
            else{
                if(count%3==0)writer.write("zaosheng\r\n");//此处顺序与训练集中类别出现顺序一致！
                if(count%3==1)writer.write("kanduo\r\n");
                if(count%3==2)writer.write("kankong\r\n");
            }
            count++;
            line = br.readLine();
        }
        reader.close();
        writer.close();
        return count;
    }

    public static void ceshi() throws Exception{
//        PreProcess("D:\\股吧\\","train");

        String Classifier= "libsvm";
        Integer savenum = 0;
//        File writefile=new File("D:\\股吧\\model\\SVMsavenumRecord.txt");
//        File writefile=new File("D:\\股吧\\model\\SVMsavenumRecord(quchong).txt");
        File writefile=new File("D:\\股吧\\model\\SVMsavenumRecord(66%).txt");
        FileWriter fw=new FileWriter(writefile);
        BufferedWriter writer=new BufferedWriter(fw);

//        for(savenum = 1000;savenum<11000;savenum+=500){
            for(savenum = 1000;savenum<8435;savenum+=500){
                System.out.println("当前：" + savenum);
            String[] option = DataClassify.SelectModel(Classifier,savenum);//实验其他算法注释掉此句，换一个OPTION即可
            if(option!=null)System.out.println("参数加载成功");
//            String tmp = DataClassify.tmptest("D:\\股吧\\trainout\\train.csv","libsvm",savenum.toString(),option);
//            String tmp = DataClassify.tmptest("D:\\股吧\\trainout\\train(quchong).csv","libsvm(quchong)",savenum.toString(),option);
            String tmp = DataClassify.tmptest2("D:\\股吧\\trainout\\train_66%.csv","D:\\股吧\\testout\\test.csv.arff","libsvm(66%)",savenum.toString(),option);
            writer.write(tmp);
            writer.flush();

        }
        writer.close();



//        String[] option = DataClassify.SelectModel(Classifier,savenum);//实验其他算法注释掉此句，换一个OPTION即可
//        DataClassify.Train("D:\\股吧\\trainout\\train.csv","libsvm",option);

//        DataClassify.pinggu(true,"libsvm","D:\\股吧\\trainout\\train.csv","D:\\股吧\\testout\\test.csv");


        //评估

//        DataClassify.Run(false,"test.txt",option,"liblinear");
//        DataProcess("D:\\股吧\\","click000725","",2);
    }

    public static void main(String []args) throws Exception{
//        String[] option = DataClassify.SelectModel("libsvm",9000);//实验其他算法注释掉此句，换一个OPTION即可
//        DataClassify.Train("D:\\股吧\\trainout\\train.csv","libsvm9000",option);


        File root =new File("D:\\股吧\\testinput");
        for(File var:root.listFiles()){
            DataProcess("D:\\股吧\\",var.getName().split(".csv")[0],"",2);
            DataClassify.Run(false,var.getName(),null,"libsvm9000");
        }
//        DataClassify.Run(false,"click.csv",null,"libsvm9000");
    }

    public static int  DataProcess(String pathname,String gupiao,String date,int tag) throws IOException{
        KeyWord.clear();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(pathname+"\\model\\keyword.txt"),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        while(line!=null){
            KeyWord.add(line);
            line=br.readLine();
        }
        File testinput = new File(pathname+"testinput\\"+gupiao+date+".csv");
        File testtmp = new File(pathname+"testtmp\\"+gupiao+date+".csv");
        File testoutput = new File(pathname+"testout\\"+gupiao+date+".csv");
        Product_WordMatrix(testinput, testtmp,tag);
        int count = Word2Num(testtmp,testoutput,tag);
//        deleteFile(testtmp);
        return count;
    }

    public static void PreProcess(String pathname,String name) throws Exception {

        File basepath = new File(pathname+"traininput\\"+name+".csv");
        File matrixpath = new File(pathname+"traintmp\\"+name+".csv");
        File shuchu = new File(pathname+"trainout\\"+name+".csv");

        Product_WordMatrix(basepath, matrixpath, 0);
        Word2Num(matrixpath,shuchu,0);
//      deleteFile(matrixpath);
        File writefile=new File(pathname+"\\model\\keyword.txt");
        FileWriter fw=new FileWriter(writefile);
        BufferedWriter writer=new BufferedWriter(fw);
        for(String word : KeyWord){
                writer.write(word+"\r\n");
        }
        writer.close();
    }

    public static void  QingXulianghua(String basepath,String gupiao,String startstr,String endstr)throws IOException{//待修改
        try {
            File writefile=new File(basepath+"\\evaluation\\"+gupiao+"_"+startstr+"_"+endstr+".txt");
            FileWriter fw=new FileWriter(writefile);
            BufferedWriter writer=new BufferedWriter(fw);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date starttime = df.parse(startstr);
            Date endtime = df.parse(endstr);
            Long start = starttime.getTime();
            Long end = endtime.getTime();

            long result = (end - start) / (24 * 60 * 60 * 1000);

            Calendar startTime = Calendar.getInstance();
            startTime.clear();
            startTime.setTime(starttime);
            WebSpider spider = new WebSpider();

            for (int i = 0; i < (int)result+1;i++) {
                String str ="";
                if(startTime.get(Calendar.MONTH)<9)
                    str ="0"+(startTime.get(Calendar.MONTH)+1) + "-";
                else
                    str =(startTime.get(Calendar.MONTH)+1) + "-";
                if(startTime.get(Calendar.DAY_OF_MONTH)<10)
                    str = str+"0"+startTime.get(Calendar.DAY_OF_MONTH);
                else
                    str = str+startTime.get(Calendar.DAY_OF_MONTH);
                System.out.println("时间"+str);
//                spider.SpiderRun(gupiao, str, begin,fend);
                int count= DataProcess(basepath,gupiao,str,2);
                double emotion = DataClassify.Run(true,basepath+"testout\\test_filter.csv",null,"liblinear");
                writer.write(str+"\t"+count+"\t"+emotion+"\r\n");
                startTime.add(Calendar.DAY_OF_YEAR, 1);
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
