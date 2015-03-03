package com.hirebigdata.threadspider.test;

import com.hirebigdata.threadspider.utils.Mongo;

/**
 * Created by Administrator on 2015/1/20.
 */
public class Test1 {
    public static void main(String[] args){
        String DBname = "scrapy";
        new Mongo().insertUserID(DBname,"zhihu_user_data_ids","b1afcdcc11d6c68f9aeb8b558113b302");
    }
}
