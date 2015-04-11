package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yizhou on 14-5-26.
 */
public class jisuan {
    private static Map<String,Integer[]> bianhao = new HashMap<String, Integer[]>();//所有词

    public static void main(String []args) throws IOException{

        String filename="G:\\课程\\毕业设计\\统计方法\\保留所有特征词\\精抽取泛抽取\\划分错误.txt";
        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = br.readLine();
        while (line != null) {

            String[] ss= line.split("\t");
            Integer[] tmp = new Integer[2];
            tmp[0]=1;
            tmp[1]=0;
           if(bianhao.containsKey(ss[1])){

               tmp[0]+=bianhao.get(ss[1])[0];
               if(ss[0].contains("0"))
                   tmp[1]+= bianhao.get(ss[1])[1]+1;
           }
            else {
               if(ss[0].contains("0"))
                   tmp[1]+=1;
           }
            bianhao.put(ss[1],tmp);

            line = br.readLine();

        }

        for(String key: bianhao.keySet()){
            if(bianhao.get(key)[0]-bianhao.get(key)[1]>bianhao.get(key)[1] )
                System.out.println(key + ",1");
                else
                    System.out.println(key + ",0");
        }

    }
}
