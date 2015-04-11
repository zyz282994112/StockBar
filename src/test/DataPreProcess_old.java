package test;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by yizhou on 14-4-9.
 */
public class DataPreProcess_old {
    private static List<String> KeyWord = new ArrayList<String>();
    private static Pattern pattern = Pattern.compile("[0-9]{6}");
    private static Pattern pattern1 = Pattern.compile("[0-9]{1,20}");

    public static String shaixuan(String line,String gupiao){
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

        else if(line.indexOf("公告日期")!=-1||(pattern.matcher(line).find()&&!(line.indexOf(gupiao)!=-1)))
            line=null;
        else if(line.length()>500)
            line=null;
        return line;
    }

    //    basefilepath为根目录，里面包含所有分类（如看多目录，看空目录等）filepath[]为一级子目录，每个filepath[i]目录里包含该分类下所有文本
//    输入的文本为未经分词的原始文本
    public static void Product_WordMatrix(File basefilepath,File writepath,Boolean ishuafen) throws IOException {
        IKAnalyzer analyzer = new IKAnalyzer(true);
        File [] filepath = basefilepath.listFiles();
        Integer count = 0;
        for(int i=0;i<filepath.length;i++){
            File []files = filepath[i].listFiles();//存储子目录下所有文件
            File path = new File(writepath.getPath()+"\\"+filepath[i].getName());//生成类别目录
            if(!path.exists())  path.mkdirs();
            int yiciguolv=0;
            for(File filename:files){
                File writefile=new File(path.getPath()+"\\"+filename.getName());//将原始文档转化为矩阵形式的词文档
                FileWriter fw=new FileWriter(writefile);
                BufferedWriter writer=new BufferedWriter(fw);
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"UTF-8"); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    count++;
                        line = shaixuan(line,filename.getName().substring(0,5));
                    if(line == null) {yiciguolv++;line=br.readLine();continue;}
                    List<String> juzis = new ArrayList<String>();
                    if(ishuafen)    Collections.addAll(juzis, line.split("[。？?!！;；]"));
                    else    juzis.add(line);
                        for(String juzi:juzis){
                            TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(juzi));
                            tokenStream.addAttribute(CharTermAttribute.class);
                            writer.write(count.toString()+"\t");//句子编号
                            while (tokenStream.incrementToken()){
                                String word = tokenStream.getAttribute(CharTermAttribute.class).toString();
                                writer.write(word + "\t");
                                if(!pattern1.matcher(word).find()&&!KeyWord.contains(word))
                                    KeyWord.add(word);
                            }
                            writer.write("\r\n");
                            writer.flush();
                        }

                    line = br.readLine();
                }
                writer.close();
                reader.close();
                }
            System.out.println("类别：" + filepath[i].getName() + "一次过滤数目:" + yiciguolv);
            }
        }

    public static int Test_WordMatrix(File basename,File writename) throws IOException {
        IKAnalyzer analyzer = new IKAnalyzer(true);
        Integer count = 0;
        int yiciguolv=0;
        File writefile = new File(writename.getPath()+"\\"+basename.getName());
        writefile.getParentFile().mkdirs();
        writefile.delete();
        writefile.createNewFile();
        FileWriter fw=new FileWriter(writefile);
        BufferedWriter writer=new BufferedWriter(fw);
        InputStreamReader reader = new InputStreamReader(new FileInputStream(basename),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        while (line != null) {
            count++;
            line = shaixuan(line,basename.getParentFile().getName());
            if(line == null) {yiciguolv++;line=br.readLine();continue;}
            List<String> juzis = new ArrayList<String>();
            Collections.addAll(juzis, line.split("[。？?!！;；]"));
            for(String juzi:juzis){
                TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(juzi));
                tokenStream.addAttribute(CharTermAttribute.class);
                writer.write(count.toString()+"\t");//句子编号
                while (tokenStream.incrementToken()){
                    String word = tokenStream.getAttribute(CharTermAttribute.class).toString();
                    writer.write(word + "\t");
                }
                writer.write("\r\n");
                writer.flush();
            }
            line = br.readLine();
        }
        writer.close();
        reader.close();

        System.out.println(basename.getName() + "一次过滤数目:" + yiciguolv);
        return count;
    }

    private static void Word2Num(File writepath,File boolpath,String name) throws IOException {

        File [] filepath = writepath.listFiles();
        File writefile=new File(boolpath.getPath()+"\\"+name+".csv");
        FileWriter fw=new FileWriter(writefile);
        BufferedWriter writer=new BufferedWriter(fw);
//      for(String word : KeyWord){writer.write(word+"\t"); }
        writer.write("NO,");
        for(int num =0;num<KeyWord.size();num++){ writer.write("c"+num+",");}
        writer.write("result");
        writer.write("\r\n");
        writer.flush();

        for(int i=0;i<filepath.length;i++){
            File[] files = filepath[i].listFiles();//存储子目录下所有文件
            for(File filename:files){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"UTF-8"); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    String[] wordstmp = line.split("\t");
                    List<String> words = new ArrayList<String>();
                    for(int ii=0;ii<wordstmp.length;ii++)
                        words.add(wordstmp[ii]);
                    writer.write(words.get(0)+",");
                    for(String word : KeyWord){
                        if(words.contains(word))
                            writer.write("T,");
                        else
                            writer.write("F,");
                    }
                    writer.write(filepath[i].getName());
                    writer.write("\r\n");
                    writer.flush();
                    line = br.readLine();
                }
                reader.close();
            }
        }
        writer.close();
    }

    private static void Word2Num_Test(File filename,File boolpath,String name) throws IOException {

        File writefile=new File(boolpath.getPath()+"\\"+name+".csv");
        FileWriter fw=new FileWriter(writefile);
        BufferedWriter writer=new BufferedWriter(fw);
//      for(String word : KeyWord){writer.write(word+"\t"); }
        writer.write("NO,");
        for(int num =0;num<KeyWord.size();num++){ writer.write("c"+num+",");}
        writer.write("result");
        writer.write("\r\n");
        writer.flush();

        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        while (line != null) {
            String[] wordstmp = line.split("\t");
            List<String> words = new ArrayList<String>();
            for(int ii=0;ii<wordstmp.length;ii++)
                words.add(wordstmp[ii]);
            writer.write(words.get(0)+",");
            for(String word : KeyWord){
                if(words.contains(word))
                    writer.write("T,");
                else
                    writer.write("F,");
            }
            writer.write(filename.getParentFile().getName());
            writer.write("\r\n");
            writer.flush();
            line = br.readLine();
        }
        reader.close();
        writer.close();
    }

    public static void deleteFile(File oldPath) {
        if (oldPath.isDirectory()) {
            File[] files = oldPath.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        }
        oldPath.delete();
    }

    public static void deleteFileexceptPath(File oldPath) {
        if (oldPath.isDirectory()) {
            File[] files = oldPath.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        }
    }

    public static void main(String []args) throws Exception
    {
        Train("D:\\股吧\\",200000);
        DataProcess("D:\\股吧\\","600000","05-31");
//        QingXulianghua("D:\\股吧\\","600000","2014-06-01","2014-06-04");
    }

    public static int  DataProcess(String pathname,String gupiao,String date) throws IOException{
        KeyWord.clear();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(pathname+"\\model\\keyword.txt"),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        while(line!=null){
            KeyWord.add(line);
            line=br.readLine();
        }
        File testinput = new File(pathname+"rowdata\\"+gupiao+"\\"+date+".txt");
        File testtmp = new File(pathname+"testtmp");
        File testoutput = new File(pathname+"testout");
        int count = Test_WordMatrix(testinput, testtmp);
        Word2Num_Test(new File(testtmp+"\\"+date+".txt"),testoutput,"test_filter");
        deleteFile(testtmp);
        return count;
    }

    public static void Train(String pathname,int savenum) throws Exception {

        File basepath = new File(pathname+"traininput");
        File matrixpath = new File(pathname+"traintmp");
        File shuchu = new File(pathname+"trainout");

        Product_WordMatrix(basepath, matrixpath, true);
        Word2Num(matrixpath,shuchu,"train_filter");
        Product_WordMatrix(basepath, matrixpath, false);
        Word2Num(matrixpath,shuchu,"train");
        deleteFile(matrixpath);
        File writefile=new File(pathname+"\\model\\keyword.txt");
        FileWriter fw=new FileWriter(writefile);
        BufferedWriter writer=new BufferedWriter(fw);
        for(String word : KeyWord){
                writer.write(word+"\r\n");
        }
        writer.flush();
        writer.close();
        DataClassify_old.Train(savenum);
    }

    public static void  QingXulianghua(String basepath,String gupiao,String startstr,String endstr)throws IOException{
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
            product.WebSpider spider = new product.WebSpider();

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
                int count = DataProcess(basepath,gupiao,str);
                double emotion = DataClassify_old.Run(true, basepath + "testout\\test_filter.csv");
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
