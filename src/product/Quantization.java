package product;

import java.io.*;

/**
 * Created by yizhou on 14-12-17.
 */
public class Quantization {

    public static Integer sum(int a,int b, int c){
        return a+b+c;
    }

    public static void main( String[] args ) throws Exception {
        String gupiao="000858";
        FileWriter fw=new FileWriter("G:\\股吧\\stock\\"+gupiao+"\\"+gupiao+"(合并日期).csv");
        BufferedWriter writer=new BufferedWriter(fw);
        InputStreamReader reader = new InputStreamReader(new FileInputStream("G:\\股吧\\stock\\"+gupiao+"\\"+gupiao+"(label).csv"),"UTF-8"); // 建立一个输入流对象reader
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言


        Integer[] zaosheng = new Integer[3];//依次为点击量和、回复量和、贴子数
        Integer[] kanduo = new Integer[3];
        Integer[] kankong = new Integer[3];
        for(int i=0;i<3;i++){
            zaosheng[i]=0;
            kanduo[i]=0;
            kankong[i]=0;
        }

        writer.write("date,zaoshengclick,zaoshengreply,zaoshengnum," +
                "kanduoclick,kanduoreply,kanduonum,kankongclick,kankongreply,kankongnum," +
                "allclick,allreply,allnum,SSI,BI,DIS\r\n");
        String line = br.readLine();
        String tag=new String(line.split(",")[0].split(" ")[0]);
        while (line != null) {
             String[] zhibiao = line.split(",");
             zhibiao[0]=zhibiao[0].split(" ")[0];
            if(!tag.contentEquals(zhibiao[0])) {
                writer.write(tag + "," + zaosheng[0] + "," + zaosheng[1] + "," + zaosheng[2] + ","
                        + kanduo[0] + "," + kanduo[1] + "," + kanduo[2] + ","
                        + kankong[0] + "," + kankong[1] + "," + kankong[2] + ","
                        + sum(zaosheng[0], kanduo[0], kankong[0]) + ","
                        + sum(zaosheng[1], kanduo[1], kankong[1]) + ","
                        + sum(zaosheng[2], kanduo[2], kankong[2]) + ","
                        + (kanduo[2] - kankong[2]) + ","
                        + Math.log((1 + kanduo[2] + 0.000001) / (1 + kankong[2] + 0.000001)) + ","
                        + Math.abs(1 - Math.abs((kanduo[2] - kankong[2] + 0.000001) / (kanduo[2] + kankong[2] + 0.000001)))
                        + "\r\n");
                for(int i=0;i<3;i++){
                    zaosheng[i]=0;
                    kanduo[i]=0;
                    kankong[i]=0;
                }
            }
                 if(zhibiao[3].contentEquals("zaosheng")){
                     zaosheng[0]+=Integer.parseInt(zhibiao[1]);
                     zaosheng[1]+=Integer.parseInt(zhibiao[2]);
                     zaosheng[2]+=1;
                 }
                 else if(zhibiao[3].contentEquals("kanduo")){
                     kanduo[0]+=Integer.parseInt(zhibiao[1]);
                     kanduo[1]+=Integer.parseInt(zhibiao[2]);
                     kanduo[2]+=1;
                 }
                 else if(zhibiao[3].contentEquals("kankong")){
                     kankong[0]+=Integer.parseInt(zhibiao[1]);
                     kankong[1]+=Integer.parseInt(zhibiao[2]);
                     kankong[2]+=1;
                 }
            tag=zhibiao[0];
            line=br.readLine();
        }
        writer.write(tag+","+zaosheng[0]+","+zaosheng[1]+","+zaosheng[2]+","
                +kanduo[0]+","+kanduo[1]+","+kanduo[2]+","
                +kankong[0]+","+kankong[1]+","+kankong[2]+ ","+ sum(zaosheng[0], kanduo[0], kankong[0]) + ","
                + sum(zaosheng[1], kanduo[1], kankong[1]) + ","
                + sum(zaosheng[2], kanduo[2], kankong[2]) + ","
                + (kanduo[2] - kankong[2]) + ","
                + Math.log((1 + kanduo[2] + 0.000001) / (1 + kankong[2] + 0.000001)) + ","
                + Math.abs(1 - Math.abs((kanduo[2] - kankong[2] + 0.000001) / (kanduo[2] + kankong[2] + 0.000001)))+"\r\n");
        writer.close();
    }



}
