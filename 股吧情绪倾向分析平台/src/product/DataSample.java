package product;

import java.io.*;
import java.util.Random;

/**
 * Created by yizhou on 14-12-16.
 */
public class DataSample {

    public static void main(String[] args) throws IOException {
        Sample("D:\\股吧\\Sample\\input\\rowdata.txt",2,3);

    }

    //训练集比例为：radio=radiofenzi/radiofenmu
    public static void Sample(String path,int radiofenzi,int radiofenmu) throws  IOException{
        File input = new File(path);
        FileWriter outtrain=new FileWriter("D:\\股吧\\traininput\\train.csv");
        FileWriter outtest=new FileWriter("D:\\股吧\\testinput\\test.csv");
        Random random = new Random();
        InputStreamReader rreader = new InputStreamReader(new FileInputStream(input)); // 建立一个输入流对象reader
        BufferedReader brr = new BufferedReader(rreader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        String line = brr.readLine();
        while (line != null) {
            if(random.nextInt(radiofenmu)<radiofenzi)
                outtrain.write(line+"\r\n");
            else
                outtest.write(line+"\r\n");

            line = brr.readLine();
        }
        outtest.close();
        outtrain.close();



    }
}
