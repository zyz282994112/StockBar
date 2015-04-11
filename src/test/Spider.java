package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yizhou on 14-4-3.
 * 只针对东方财富股吧
 */
public class Spider {

    private static List<String> alllinks = new ArrayList<String>();
    private static List<String> alljuzi = new ArrayList<String>();
    private static BufferedWriter writer  = null;

    public static void main(String[] args) throws IOException {

        String gupiao="600155";
        GetAllLink(gupiao,2);
        int count = alllinks.size();
        System.out.println("连接数"+count);
        for(int i=0;i<count;i++){
            alljuzi.addAll(getjuzi(alllinks.get(i)));
        }

        Map<String,Integer> cihui = new HashMap<String, Integer>();
        IKAnalyzer analyzer = new IKAnalyzer();
        // 使用智能分词
        analyzer.setUseSmart(true);
        count = alljuzi.size();
        System.out.println("句子总数：" + count);
        //爬取数据
        File dest = new File("D:\\guba\\"+gupiao+".txt");
        writer = new BufferedWriter(new FileWriter(dest));
        for(int i=0;i<count;i++)
            writer.write(alljuzi.get(i) + "\r\n");
        writer.close();

        try {
            for(String juzi : alljuzi)
                IKAnalyzerTest.tongjicipin(analyzer, juzi,cihui);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Map.Entry<String, Integer> entry : cihui.entrySet()){
            if(entry.getValue()>300)
                System.out.println(entry.getKey()+":"+entry.getValue());
        }


    }

    public static void testtimecount_chengjiaoliang(String[] args) throws IOException{

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



    private static List<String> getjuzi(String url) throws IOException {
        List<String> juzis = new ArrayList<String>();
        Document doc = Jsoup.connect(url).timeout(60000).get();
        Elements links = doc.select("#zwconttbt");
        links.addAll(doc.select("#zwconbody"));
//        links.addAll(doc.select(".zwlitext"));
        for (Element link : links) {
              juzis.add(link.text());
        }
        return juzis;
    }
      
    private static void GetAllLink(String gupiao,int size) throws IOException {
        String url = "";
        for(int i=0;i<size;i++){
            url = "http://guba.eastmoney.com/list,"+gupiao+",5,f_"+i+1+".html";
            alllinks.addAll(GetUrlLink(url));
        }
    }

    private static List<String> GetUrlLink(String url) throws IOException {
        List<String> urllinks = new ArrayList<String>();
        Document doc = Jsoup.connect(url).timeout(600000).get();
        Elements links = doc.select(".articleh").select("a");
        for (Element link : links) {
            if(link.attr("abs:href").contains("news"))
                urllinks.add(link.attr("abs:href"));
        }

        return urllinks;
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}
