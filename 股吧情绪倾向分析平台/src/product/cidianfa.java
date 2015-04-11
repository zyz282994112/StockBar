package product;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class cidianfa {


    //测试老师给的词汇计算情感效果如何
    public static void main(String[] args) throws IOException {
        Map<String,Integer> keywords = new HashMap<String, Integer>();


        File filename = new File("D:\\股吧\\词典法\\motion.txt");
        InputStreamReader rreader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
        BufferedReader brr = new BufferedReader(rreader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = brr.readLine();
        while (line != null) {
            String[] words = line.split("\t");
            keywords.put(words[0],Integer.parseInt(words[1]));
//            System.out.println(words[0]);
            line = brr.readLine();
        }
        rreader.close();
//        File basepath = new File("D:\\股吧\\词典法\\traininput");
        File matrixpath = new File("D:\\股吧\\词典法\\traininput");
        FileWriter fw=new FileWriter("D:\\股吧\\词典法\\trainoutput\\output.csv");
        File [] filepath = matrixpath.listFiles();
        BufferedWriter writer=new BufferedWriter(fw);
        int count=0;
        for(String word:keywords.keySet()){
                writer.write("c"+count+",");
            count++;
        }
        writer.write("result\r\n");
        for(int i=0;i<filepath.length;i++){

            File []files = filepath[i].listFiles();//存储子目录下所有文件
            for(File filename1:files){
                InputStreamReader reader = new InputStreamReader(new FileInputStream(filename1)); // 建立一个输入流对象reader
                BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
                line = br.readLine();
                while (line != null) {
//                    System.out.println(line);
                    for(String word:keywords.keySet()){
                        if(line.indexOf(word)!=-1)
                            writer.write("T,");
                        else
                            writer.write("F,");
                    }
                    writer.write(filepath[i].getName()+"\r\n");
                    line = br.readLine();
                    writer.flush();
                }
                reader.close();
            }


        }
        writer.close();
    }

}
