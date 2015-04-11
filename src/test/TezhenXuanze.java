
package test;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by yizhou on 14-4-9.
 */
public class TezhenXuanze {
    private static List<String> KeyWord = new ArrayList<String>();
    private static Pattern pattern = Pattern.compile("[0-9]{6}");
    private static Pattern pattern1 = Pattern.compile("[0-9]{1,20}");
    //    basefilepath为根目录，里面包含所有分类（如看多目录，看空目录等）filepath[]为一级子目录，每个filepath[i]目录里包含该分类下所有文本
//    输入的文本为未经分词的原始文本
    public static void Product_WordMatrix_Train(File basefilepath,File writepath) throws IOException {
        IKAnalyzer analyzer = new IKAnalyzer(true);
        File[] filepath = basefilepath.listFiles();
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
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                String line = br.readLine();
                while (line != null) {
                    count++;
                    line = shaixuan(line,filename.getName());
                    if(line == null) {yiciguolv++;line=br.readLine();continue;}
                    TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));
//                    String[] lines = line.split("[。？?]");
//                    for(int it=0;it<lines.length;it++){ //长句子分为短句子
//                        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(lines[it]));
                        tokenStream.addAttribute(CharTermAttribute.class);
                        while (tokenStream.incrementToken()){
                            String word = tokenStream.getAttribute(CharTermAttribute.class).toString();
                            writer.write(word + "\t");
                            if(!pattern1.matcher(word).find()&&!KeyWord.contains(word))
                                KeyWord.add(word);
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
            System.out.println("类别：" + filepath[i].getName() + "一次过滤数目:" + yiciguolv);
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
//        else if(line.length()>500)
//            line=null;
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
            int yiciguolv=0;
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
                    if(line == null) {yiciguolv++;line=br.readLine();continue;}
                    TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));
//                    String[] lines = line.split("[。？?]");
//                    for(int it=0;it<lines.length;it++){ //长句子分为短句子
//                        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(lines[it]));
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
            System.out.println("测试类别："+filepath[i].getName()+"\t一次过滤数目:"+yiciguolv);
        }
    }


    private static void Word2Num(File writepath,File boolpath,String name) throws IOException {

        File [] filepath = writepath.listFiles();
        File writefile=new File(boolpath.getPath()+"\\"+name+".csv");
        FileWriter fw=new FileWriter(writefile);
        BufferedWriter writer=new BufferedWriter(fw);
        //            for(String word : KeyWord){writer.write(word+"\t"); }
        for(int num =0;num<KeyWord.size();num++){ writer.write("c"+num+",");}
        writer.write("bianhao,result");
        writer.write("\r\n");
        writer.flush();

        for(int i=0;i<filepath.length;i++){
            File[] files = filepath[i].listFiles();//存储子目录下所有文件
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
                            writer.write("T,");
                        else
                            writer.write("F,");
                    }
                    writer.write(words.get(words.size()-1)+",");

                    writer.write(filepath[i].getName());
                    writer.write("\r\n");
                    line = br.readLine();
                    writer.flush();
                }
                reader.close();
            }
        }
        writer.close();
    }


    public static void main(String []args) throws IOException
    {
        File basepath = new File("D:\\股吧\\traininput");
        File matrixpath = new File("D:\\股吧\\traintmp");
        File shuchu = new File("D:\\股吧\\trainout");

        Product_WordMatrix_Train(basepath, matrixpath);
        Word2Num(matrixpath,shuchu,"train");

        //检验结果
        File testinput = new File("D:\\股吧\\testinput");
        File testtmp = new File("D:\\股吧\\testtmp");
        File testoutput = new File("D:\\股吧\\testout");
        Product_WordMatrix_Test(testinput,testtmp);
        Word2Num(testtmp,testoutput,"test");

    }

}
