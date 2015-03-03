package com.hirebigdata.threadspider.test;

import com.hirebigdata.threadspider.Thread.DealThread;
import com.hirebigdata.threadspider.Thread.DownThrad;
import com.hirebigdata.threadspider.pojo.FLAG;
import com.hirebigdata.threadspider.pojo.Resource;
import com.hirebigdata.threadspider.utils.Mongo;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015/1/20.
 */
public class TestSpiderOne {
    static final String DBname = "scrapy";
    ConcurrentLinkedQueue<Resource> resources = new ConcurrentLinkedQueue<Resource>();
    ConcurrentLinkedQueue<Resource> download = new ConcurrentLinkedQueue<Resource>();
    HashMap<String, FLAG> userMap = new HashMap<String, FLAG>();
    public void MangerThread(){
        ExecutorService pool = Executors.newFixedThreadPool(5);
        DealThread dealt1 = new DealThread(download, resources, userMap);
        DownThrad  downt1 = new DownThrad(download, resources, userMap);
        DealThread dealt2 = new DealThread(download, resources, userMap);
        DownThrad  downt2 = new DownThrad(download, resources, userMap);
        DealThread dealt3 = new DealThread(download, resources, userMap);
        pool.submit(dealt1);
        pool.submit(downt1);
        pool.submit(dealt2);
        pool.submit(downt2);
        pool.submit(dealt3);
        pool.shutdown();
        String uid = new Mongo().getUserid(DBname, "zhihu_user_data_ids");
        new Mongo().startCrawl(DBname, "zhihu_user_data_ids", uid);//开始爬取
        download.add(new Resource(1, uid));
        System.out.println(uid);



    }


    public static void main(String[] args){
        new TestSpiderOne().MangerThread();
    }
}
