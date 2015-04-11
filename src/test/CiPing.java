package test;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by yizhou on 15-2-19.
 */
public class CiPing {

    private static Map<String,Integer> KeyWord = new HashMap<String, Integer>();//所有词

    public static void main(String[] args ) throws Exception {

        IKAnalyzer analyzer = new IKAnalyzer(true);//将原始文档转化为矩阵形式的词文档
        FileWriter fw=new FileWriter("D:\\xxx.csv");
        BufferedWriter writer=new BufferedWriter(fw);
        InputStreamReader reader = new InputStreamReader(new FileInputStream("D:\\xx.csv"),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        while (line != null) {
            try{
                String tmp=line.split("\t")[8];
                line = tmp;
                if (line.split("\t").length>9)
                    System.out.println("sss");
            }
           catch(ArrayIndexOutOfBoundsException e){
               System.out.println(line.split("\t").length);
           }

            TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(line));
            tokenStream.addAttribute(CharTermAttribute.class);
            while (tokenStream.incrementToken()){
                String word = tokenStream.getAttribute(CharTermAttribute.class).toString();
                if(KeyWord.containsKey(word))
                    KeyWord.put(word,KeyWord.get(word)+1);
                else
                    KeyWord.put(word,1);
            }
            line = br.readLine();
        }
        for(Map.Entry<String, Integer> word :KeyWord.entrySet())
        {
            writer.write(word.getKey()+"\t"+word.getValue()+"\r\n");
        }
        writer.close();
        reader.close();


    }
}
