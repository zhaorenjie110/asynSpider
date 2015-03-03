package com.hirebigdata.threadspider.Thread;

import com.hirebigdata.threadspider.pojo.FLAG;
import com.hirebigdata.threadspider.pojo.Resource;
import com.hirebigdata.threadspider.utils.HttpUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2015/1/20.
 */
public class DownThrad implements Callable{
    static final int HomePAGE = 1;
    static final int AboutPAGE = 2;
    static final int TopicPAGE = 3;
    static final int ColumnPAGE = 4;
    static final int FollowerPAGE = 5;
    static final int FolloweePAGE = 6;
    static final int QuestionListPAGE = 7;
    static final int AnswerListPAGE = 8;
    static final int QuestionPAGE = 9;
    static final int AnswerPAGE = 10;
    private static Map<String, String> header = new HashMap<String, String>();
    ConcurrentLinkedQueue<Resource> download;
    ConcurrentLinkedQueue<Resource> resources;
    HashMap<String, FLAG> userMap ;

    public DownThrad(ConcurrentLinkedQueue<Resource> download, ConcurrentLinkedQueue<Resource> resources, HashMap<String, FLAG> userMap) {
        this.download = download;
        this.resources = resources;
        this.userMap = userMap;
    }

    @Override
    public Object call() throws Exception {
        while (true){
            if (!download.isEmpty()) {
                Resource res = download.poll();
                int flag = res.getFlag();
                switch (flag) {
                    case HomePAGE://data：uid  下载 主页
                        DownLoad(res.getData(), "http://www.zhihu.com/people/" + res.getData(), 3, HomePAGE);
                        break;
                    case AboutPAGE://data: url_name have user_data_id url_name 下载详细信息页面
                        DownLoad(res.getUser_data_id(), "http://www.zhihu.com/people/" + res.getUrl_name() + "/about", 3, AboutPAGE);
                        System.out.println("aboutpage finish");
                        break;
                    case TopicPAGE:
                        DownLoad(res.getUser_data_id(),"http://www.zhihu.com/people/" + res.getUrl_name() + "/topics", 3, TopicPAGE);
                        break;
                    case ColumnPAGE:
                        DownLoad(res.getUser_data_id(),"http://www.zhihu.com/people/" + res.getUrl_name() + "/columns/followed", 3, ColumnPAGE);
                        break;
                    case FollowerPAGE:
                        DownLoad(res.getUser_data_id(),"http://www.zhihu.com/people/" + res.getUrl_name() + "/followers", 3, FollowerPAGE);
                        break;
                    case FolloweePAGE:
                        DownLoad(res.getUser_data_id(),"http://www.zhihu.com/people/" + res.getUrl_name() + "/followees", 3, FolloweePAGE);
                        break;
                    case QuestionListPAGE:
                        DownLoad(res.getUser_data_id(), "http://www.zhihu.com/people/" + res.getUrl_name() + "/asks?page=" + res.getData(), 3, QuestionListPAGE);
                        break;
                    case AnswerListPAGE:
                        DownLoad(res.getUser_data_id(),"http://www.zhihu.com/people/" + res.getUrl_name() + "/answers?page="+res.getData(), 3, AnswerListPAGE);
                        break;
                    case QuestionPAGE://data: question_id
                        DownLoadQandA(res.getUser_data_id(), "http://www.zhihu.com" + res.getData(), 5, QuestionPAGE, res);
                        break;
                    case AnswerPAGE://data: answer_id
                        DownLoadQandA(res.getUser_data_id(),"http://www.zhihu.com"+res.getData(),5,AnswerPAGE, res);
                        break;
                    default:
                        break;
                }
            }else
                Thread.sleep(100);
        }
    }


    private String DownLoad(String user_data_id, String url,int num, int flag){
        String html = DownLoad(url);
        int i = 0;
        while( html.equals("error") && i++ < num){
            html = DownLoad(url);
        }
        resources.add(new Resource(flag, html, user_data_id));
        return html;
    }

    private String DownLoadQandA(String user_data_id, String url,int num, int flag,Resource res){
        String html = DownLoad(url);
        int i = 0;
        while( html.equals("error") && i++ < num){
            html = DownLoad(url);
        }
        resources.add(new Resource(flag, html, user_data_id, res.getUrl_name(), res.getObj()));
        return html;
    }

    private String DownLoad(String url){
        String html = "";
        try {
            html = new HttpUtil().get(url, getHeader());
            if (html.length() > 4)
                return html;
            else
                return "error";
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error:"+url);
            return "error";
        } catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("->error "+url);
            return "error";
        }
    }


    public static Map<String, String> getHeader() {
        header.put("Origin", "http://www.zhihu.com");
        header.put("Host", "www.zhihu.com");
        header.put("Connection", "keep-alive");
        header.put("Accept-Encoding", "gzip,deflate,sdch");
        header.put("Referer", "http://www.zhihu.com/people/fenng/followees");
        // header.put("Content-Type",
        // "application/x-www-form-urlencoded; charset=UTF-8");
        header.put("Accept", "gzip, deflate");
        header.put("Accept-Language", "	zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        header.put(
                "cookie",
                "   q_c1=ccfd92e359e54617a0ff06a03ca42b92|1421637754000|1421637754000; z_c0=\"QUFDQWdQcEdBQUFYQUFBQVlRSlZUWG9GNUZRRlRoVk9JcjVkLXFBRWVJakdqTTUydUlhYWRnPT0=|1421637754|0ba1acefabd2db982d5be8996970d005128bb7b5\"; _xsrf=f3ac2e80dd818aa786427b5b0f777b38; __utma=51854390.879657291.1421637754.1421637754.1421637754.1; __utmb=51854390.3.10.1421637754; __utmc=51854390; __utmz=51854390.1421637754.1.1.utmcsr=zhihu.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=51854390.100--|2=registration_date=20150104=1^3=entry_date=20150104=1; __utmt=1");
        header.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:32.0) Gecko/20100101 Firefox/32.0");
        // header.put("X-Requested-With", "XMLHttpRequest");

        return header;
    }
}
