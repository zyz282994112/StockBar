package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yizhou on 14-4-3.
 * 只针对东方财富股吧
 */
public class WebSpider_old {

    private  List<String> alllinks = new ArrayList<String>();
    private  List<String> alljuzi = new ArrayList<String>();
    private  BufferedWriter writer  = null;

    public static void main(String[] args) throws IOException {

        SpiderRunAll("600000","2014-06-01","2014-06-4",1,15);

    }

    public  void testtimecount_chengjiaoliang(String[] args) throws IOException{

        Map<String,Integer> timecount = new HashMap<String, Integer>();
        String url="";
        Elements links = new Elements();
        for(int i=1;i<81;i++){
            url = "http://guba.eastmoney.com/list,600155,f_"+i+".html";
        Document doc = Jsoup.connect(url).timeout(60000).get();
            links.addAll(doc.select(".articleh"));
        }
        for (Element link : links) {
            if(timecount.containsKey(link.child(4).text()))
                timecount.put(link.child(4).text(),timecount.get(link.child(4).text())+Integer.parseInt(link.child(1).text())+1);
            else
                timecount.put(link.child(4).text(),Integer.parseInt(link.child(1).text())+1);
        }

        for(Map.Entry<String, Integer> entry : timecount.entrySet()){
            System.out.println(entry.getKey()+","+entry.getValue());
        }

    }

    public  int SpiderRun(String gupiao,String time,int s_yeshu,int e_yeshu) throws IOException {

        alljuzi.clear();
        alllinks.clear();
        GetAllLink(gupiao, s_yeshu, e_yeshu, time);
        int count = alllinks.size();
        for(int i=0;i<count;i++){
            alljuzi.addAll(getjuzi(alllinks.get(i)));
        }
        count = alljuzi.size();
        System.out.println("句子总数：" + count);
        //爬取数据
        File dest = new File("D:\\股吧\\rowdata\\"+gupiao);
        dest.getParentFile().mkdirs();
        dest.mkdir();
        File destname = new File(dest.getPath()+"\\"+time+".txt");
        if(destname.exists()){
            destname.delete();
            destname.createNewFile();
        }
        writer = new BufferedWriter(new FileWriter(destname));
        for(int i=0;i<count;i++)
            writer.write(alljuzi.get(i) + "\r\n");
        writer.close();

        return count;
    }

    private  List<String> getjuzi(String url) throws IOException {
        List<String> juzis = new ArrayList<String>();
        Document doc = Jsoup.connect(url).timeout(60000).get();
        Elements links= doc.select("#zwconbody");
        links.addAll(doc.select(".zwlitext"));
        for (Element link : links) {
            if(link.text().trim().length()>0)
              juzis.add(link.text());
        }
        return juzis;
    }
      
    private  void GetAllLink(String gupiao,int s_yeshu,int e_yeshu,String time) throws IOException {
        String url = "";
        for(int i=s_yeshu;i<=e_yeshu;i++){
            url = "http://guba.eastmoney.com/list,"+gupiao+",f_"+i+".html";
            alllinks.addAll(GetUrlLink(url,time));
        }
    }

    private  List<String> GetUrlLink(String url,String time) throws IOException {
        List<String>   urllinks = new ArrayList<String>();
        Document doc = Jsoup.connect(url).timeout(600000).get();
        Elements links = doc.select(".articleh");
        for (Element link : links) {
            if(link.child(4).text().trim().equals(time))
                    urllinks.add(link.child(2).select("a").attr("abs:href"));
        }
        return urllinks;
    }

    public static void SpiderRunAll(String gupiao,String startstr,String endstr,int begin,int fend){

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date starttime = df.parse(startstr);
            Date endtime = df.parse(endstr);
            Long start = starttime.getTime();
            Long end = endtime.getTime();

            long result = (end - start) / (24 * 60 * 60 * 1000);

            Calendar startTime = Calendar.getInstance();
//            System.out.println("现在时间"+startTime.getTime().toString());
            startTime.clear();
            startTime.setTime(starttime);
            WebSpider_old spider = new WebSpider_old();

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
                spider.SpiderRun(gupiao, str, begin,fend);
                startTime.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
