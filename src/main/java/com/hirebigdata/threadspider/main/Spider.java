package com.hirebigdata.threadspider.main;

import com.hirebigdata.threadspider.Thread.DealThread;
import com.hirebigdata.threadspider.Thread.DownThrad;
import com.hirebigdata.threadspider.pojo.FLAG;
import com.hirebigdata.threadspider.pojo.Resource;
import com.hirebigdata.threadspider.utils.Mongo;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015/1/20.
 */
public class Spider {
    static final String DBname = Mongo.getMongoDBname();
    ConcurrentLinkedQueue<Resource> resources = new ConcurrentLinkedQueue<Resource>();
    ConcurrentLinkedQueue<Resource> download = new ConcurrentLinkedQueue<Resource>();
    HashMap<String, FLAG> userMap = new HashMap<String, FLAG>();

    public void MangerThread(){
        ExecutorService pool = Executors.newFixedThreadPool(5);
        DealThread dealt1 = new DealThread(download, resources, userMap);
        DownThrad  downt1 = new DownThrad(download, resources, userMap);
//        DealThread dealt2 = new DealThread(download, resources, userMap);
        DownThrad  downt2 = new DownThrad(download, resources, userMap);
        pool.submit(dealt1);
        pool.submit(downt1);
        pool.submit(downt2);
        pool.shutdown();
        String user_data_id = new Mongo().getUserid(DBname, "zhihu_user_data_ids");
        inputUid(user_data_id);
        try {
            while(true) {
                if(userMap.get(user_data_id).check()){
                    userMap.remove(user_data_id);
                    user_data_id = new Mongo().getUserid(DBname, "zhihu_user_data_ids");
                    inputUid(user_data_id);
                }else {
//                    System.out.println(userMap.get(user_data_id).toString());
                    Thread.sleep(2000);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void inputUid(String user_data_id){
        new Mongo().startCrawl(DBname, "zhihu_user_data_ids", user_data_id);//开始爬取
        userMap.put(user_data_id,new FLAG());
        download.add(new Resource(1, user_data_id));
        System.out.print("\n" + user_data_id);
    }

    public static void main(String[] args){
        new Spider().MangerThread();
    }
}
