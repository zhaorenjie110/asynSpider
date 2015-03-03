package com.hirebigdata.threadspider.Thread;

import com.hirebigdata.threadspider.pojo.*;
import com.hirebigdata.threadspider.utils.Mongo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2015/1/20.
 */
public class DealThread implements Callable{
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

    ConcurrentLinkedQueue<Resource> download;
    ConcurrentLinkedQueue<Resource> resources;
    HashMap<String, FLAG> userMap ;

    public DealThread(ConcurrentLinkedQueue<Resource> download, ConcurrentLinkedQueue<Resource> resources, HashMap<String, FLAG> userMap) {
        this.download = download;
        this.resources = resources;
        this.userMap = userMap;
    }

    @Override
    public Object call() throws Exception {
        while(true) {
            if (!resources.isEmpty()) {
                Resource res = resources.poll();
                int flag = res.getFlag();
                switch (flag) {
                    case HomePAGE:
                        dealHomePage(res);
                        break;
                    case AboutPAGE:
                        dealAboutPage(res);
                        break;
                    case TopicPAGE:
                        dealTopicPage(res);
                        break;
                    case ColumnPAGE:
                        dealColumnPage(res);
                        break;
                    case FollowerPAGE:
                        dealFollowerPage(res);
                        break;
                    case FolloweePAGE:
                        dealFolloweePage(res);
                        break;
                    case QuestionListPAGE:
                        dealQuestionListPage(res);
                        break;
                    case AnswerListPAGE:
                        dealAnswerListPage(res);
                        break;
                    case QuestionPAGE:
                        dealQuestionPage(res);
                        break;
                    case AnswerPAGE:
                        dealAnswerPage(res);
                        break;
                    default:
                        break;
                }
            }else
                Thread.sleep(100);
        }
    }
    private void dealHomePage(Resource res){
        ZhihuUser zhihuUser = new ZhihuUser();
        Document page = Jsoup.parse(res.getData());
        String s = page.getElementsByTag("meta").get(5).attr("content");
        res.setUrl_name(new String(s.getBytes(), 45, s.length() - 45)); //首先得到url_name 添加下载需求
        download.add(new Resource(AboutPAGE,res.getUrl_name(),res.getUser_data_id(),res.getUrl_name()));
        System.out.println("request aboutpage");

        Elements els = page.getElementsByAttributeValue("class", "profile-navbar clearfix").first().getElementsByClass("item");
        zhihuUser.setQuestions_count(els.get(1).getElementsByTag("span").first().text());//question count
        zhihuUser.setAnswers_count(els.get(2).getElementsByTag("span").first().text());//answer count
        zhihuUser.setPosts_count(els.get(3).getElementsByTag("span").first().text());
        zhihuUser.setCollections_count(els.get(4).getElementsByTag("span").first().text());
        zhihuUser.setLogs_count(els.get(5).getElementsByTag("span").first().text());
        zhihuUser.setUrl_name(res.getUrl_name());
        zhihuUser.setUser_data_id(res.getUser_data_id());
        userMap.get(res.getUser_data_id()).setAnswerCount(Integer.parseInt(zhihuUser.getAnswers_count()));
        userMap.get(res.getUser_data_id()).setQuestionCount(Integer.parseInt(zhihuUser.getQuestions_count()));

        Element e = page.getElementsByAttributeValue("href", "/people/" + res.getUrl_name() + "/columns/followed").first();
        if (e != null) {
            String columns_count = e.text();
            zhihuUser.setFollow_columns_count(columns_count);
            if(Integer.parseInt(columns_count.replace(" 个专栏","")) >= 1)//有专栏 提出页面下载需求
                download.add(new Resource(ColumnPAGE, res.getUrl_name(), res.getUser_data_id(), res.getUrl_name()));
        }else
            userMap.get(res.getUser_data_id()).setColumnFlag();
        e = page.getElementsByAttributeValue("href", "/people/" + res.getUrl_name() + "/topics").first();
        if(e != null) {
            String topics_count = e.text();
            zhihuUser.setFollow_topics_count(topics_count);
            if(Integer.parseInt(topics_count.replace(" 个话题","")) >= 1)//有话题 提出页面下载需求
                download.add(new Resource(TopicPAGE,res.getUrl_name(),res.getUser_data_id(),res.getUrl_name()));
            else
                userMap.get(res.getUser_data_id()).setTopicFlag();
        }
        els = page.getElementsByAttributeValue("class","zg-gray-normal");
        e = els.first();
        String followee_count = e.parent().getElementsByTag("strong").first().text();
        zhihuUser.setFollowee_count(followee_count);
        if(Integer.parseInt(followee_count) >= 1)
            download.add(new Resource(FolloweePAGE,res.getUrl_name(),res.getUser_data_id(),res.getUrl_name()));
        else
            userMap.get(res.getUser_data_id()).setFolloweeFlag();
        e = els.get(1);
        String follower_count = e.parent().getElementsByTag("strong").first().text();
        zhihuUser.setFollower_count(follower_count);
        if(Integer.parseInt(follower_count) >= 1)
            download.add(new Resource(FollowerPAGE,res.getUrl_name(),res.getUser_data_id(),res.getUrl_name()));
        else
            userMap.get(res.getUser_data_id()).setFollowerFlag();
        e = els.get(2);
        zhihuUser.setPersonal_page_view_count(e.getElementsByTag("strong").first().text());
        zhihuUser.setUser__xsrf_value(page.getElementsByAttributeValue("name","_xsrf").first().attr("value"));
        e = page.getElementsByAttributeValue("class","zm-profile-section-list zg-clear").first();
        if (e != null) {
            els = e.getElementsByAttributeValue("class", "item");
            for (Element element : els) {
                Skilled_topic skilled_topic = new Skilled_topic();
                skilled_topic.setName(element.getElementsByAttributeValue("class", "zg-gray-darker").text());
                skilled_topic.setUrl(element.attr("data-url-token"));
                skilled_topic.setVote(element.getElementsByAttributeValue("class", "zg-icon vote").first().parent().text());
                skilled_topic.setComment(element.getElementsByAttributeValue("class", "zg-icon comment").first().parent().text());
                zhihuUser.getSkilled_topics().add(skilled_topic);
            }
        }
        for(int i = 1 ; i <= Math.ceil(Double.parseDouble(zhihuUser.getQuestions_count())/20.0) ; i++){
            download.add(new Resource(QuestionListPAGE,""+i,res.getUser_data_id(),res.getUrl_name()));
        }
        for(int i = 1 ; i <= Math.ceil(Double.parseDouble(zhihuUser.getAnswers_count())/20.0) ; i++){
            download.add(new Resource(AnswerListPAGE,""+i,res.getUser_data_id(),res.getUrl_name()));
        }
        new Mongo().upsertUser(zhihuUser);
    }

    private void dealAboutPage(Resource res){
        ZhihuUserDetil zhihuUserDetil = new ZhihuUserDetil();
        zhihuUserDetil.setUser_data_id(res.getUser_data_id());
        Document page = Jsoup.parse(res.getData());
        try{
            Element e = page.getElementsByAttributeValue("class","title-section ellipsis").first().getElementsByAttributeValue("class", "name").first();
            zhihuUserDetil.setName(e.text());//得到名字
            e = page.getElementsByAttributeValue("class","bio").first();//得到简介
            if(e!= null)
                zhihuUserDetil.setBio(e.text());
            e = page.getElementsByAttributeValue("class","icon icon-profile-male").first();
            if(e != null){//得到性别
                zhihuUserDetil.setGender("icon icon-profile-male");
            }else{
                e = page.getElementsByAttributeValue("class","icon icon-profile-female").first();
                if(e != null)
                    zhihuUserDetil.setGender("icon icon-profile-female");
            }
            e = page.getElementsByAttributeValue("class", "business item").first();
            if(e != null){
                zhihuUserDetil.setBusiness(e.attr("title"));//行业
                Element el = e.getElementsByTag("a").first();
                if(el != null)
                    zhihuUserDetil.setBusiness_topic_url(el.attr("href"));//行业url
            }
            e = page.getElementsByAttributeValue("class","zm-profile-header-user-weibo").first();
            if(e != null)
                zhihuUserDetil.setWeibo_url(e.attr("href"));
            e = page.getElementsByAttributeValue("class","fold-item").first();
            if (e != null){
                zhihuUserDetil.setDescription(e.getElementsByAttributeValue("class","content").first().text());//介绍
            }
            Elements els = page.getElementsByAttributeValue("class","zm-profile-module-desc");
            Elements es = els.first().getElementsByTag("strong");
            zhihuUserDetil.setVote_count(es.first().text());
            zhihuUserDetil.setThank_count(es.get(1).text());
            zhihuUserDetil.setFav_count(es.get(2).text());
            zhihuUserDetil.setShare_count(es.get(3).text());
            e = els.get(1);
            for(Element employ : e.getElementsByTag("li")) {
                zhihuUserDetil.getEmployments().add(employ.attr("data-title")+" - "+employ.attr("data-sub-title"));
            }
            e = els.get(2);
            for(Element loction: e.getElementsByTag("li")){
                zhihuUserDetil.getLocations().add(loction.attr("data-title"));
            }
            e = els.get(3);
            for(Element edu : e.getElementsByTag("li")){
                zhihuUserDetil.getEducations().add(edu.attr("data-title")+" - "+edu.attr("data-sub-title"));
            }
            new Mongo().upsertUserDetil(zhihuUserDetil);
            userMap.get(res.getUser_data_id()).setAboutFlag();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    private void dealTopicPage(Resource res){
        ZhihuUserTopic zhihuUserTopic = new ZhihuUserTopic();
        Document page = Jsoup.parse(res.getData());
        Elements els= page.getElementById("zh-profile-topic-list").getElementsByAttributeValue("class","zm-profile-section-main");
        for(Element e : els){
            Topic topic = new Topic();
            topic.setUrl(e.getElementsByTag("a").get(1).attr("href"));
            topic.setName(e.getElementsByTag("a").get(1).text());
            topic.setContent(e.getElementsByAttributeValue("class", "zm-editable-content").first().text());
            topic.setAnswers_count(e.getElementsByAttributeValue("class", "zg-link-gray").first().text());
            zhihuUserTopic.getTopics().add(topic);
        }
        new Mongo().upsertUserTopic(res.getUser_data_id(), zhihuUserTopic);
        userMap.get(res.getUser_data_id()).setTopicFlag();
    }

    private void dealColumnPage(Resource res){
        ZhihuUserColumn zhihuUserColumn = new ZhihuUserColumn();
        Document page = Jsoup.parse(res.getData());
        Elements els = page.getElementsByAttributeValue("class","zm-profile-section-item zg-clear");
        for ( Element e : els){
            Column column = new Column();
            column.setUrl(e.getElementsByAttributeValue("class", "zm-list-avatar-link").first().attr("href"));
            column.setName(e.getElementsByAttributeValue("class", "zm-profile-section-main").first().getElementsByAttributeValue("href", column.getUrl()).first().text());
            column.setDescription(e.getElementsByAttributeValue("class", "description").html());
            column.setMeta(e.getElementsByAttributeValue("class", "meta").text());
            //            new Mongo().pushUserColumn(User_data_id, column);
            zhihuUserColumn.getColumns().add(column);
        }
        new Mongo().upsertUserColumn(res.getUser_data_id(), zhihuUserColumn);
        userMap.get(res.getUser_data_id()).setColumnFlag();
    }
    private void dealFollowerPage(Resource res){
        ZhihuUserFollower zhihuUserFollower = new ZhihuUserFollower();
        Document page = Jsoup.parse(res.getData());
        Elements els = page.getElementById("zh-profile-follows-list").getElementsByAttributeValue("data-follow","m:button");
        for(Element e: els){
            zhihuUserFollower.getFollowers().add(e.attr("data-id"));
        }
        new Mongo().upsertUserFollower(res.getUser_data_id(),zhihuUserFollower);
        userMap.get(res.getUser_data_id()).setFollowerFlag();
    }
    private void dealFolloweePage(Resource res){
        ZhihuUserFollowee zhihuUserFollowee = new ZhihuUserFollowee();
        Document page = Jsoup.parse(res.getData());
        Elements els = page.getElementById("zh-profile-follows-list").getElementsByAttributeValue("data-follow","m:button");
        for(Element e: els){
            zhihuUserFollowee.getFollowees().add(e.attr("data-id"));
        }
        new Mongo().upsertUserFollowee(res.getUser_data_id(), zhihuUserFollowee);
        userMap.get(res.getUser_data_id()).setFolloweeFlag();
    }
    private void dealQuestionListPage(Resource res){
        Document page = Jsoup.parse(res.getData());
        Elements allQuestionElements = page
                .getElementsByAttributeValue("id", "zh-profile-ask-list")
                .first().getElementsByAttributeValue("class", "zm-profile-section-item zg-clear");
        for (Element e : allQuestionElements) {
            Question question = new Question();
            question.setQuestion_id(e.getElementsByAttributeValue("class", "question_link").first().attr("href").trim());
            question.setTitle(e.getElementsByAttributeValue("class", "question_link").first().text().trim());
            Element element = e.getElementsByAttributeValue("class", "meta zg-gray").first();
            String[] str = element.text().split(" • ");
            question.setAnswer_count(str[1]);
            question.setFollower_count(str[2]);
            download.add(new Resource(QuestionPAGE,question.getQuestion_id(),res.getUser_data_id(),res.getUrl_name(), question));
        }
    }
    private void dealAnswerListPage(Resource res){
        Document page = Jsoup.parse(res.getData());
        Elements allAnswersElements = page
                .getElementsByAttributeValue("id", "zh-profile-answer-list")
                .first().getElementsByAttributeValue("class", "zm-item");
        for (Element e : allAnswersElements) {
            Answer answer = new Answer();
            answer.setAnswer_id(e.getElementsByAttributeValue("class", "question_link").attr("href"));
            answer.setAnswer_title(e.getElementsByAttributeValue("class", "question_link").text());
            answer.setAnswer_vote_up(getIntVoteFromString(e.getElementsByAttributeValue("class", "zm-item-vote-count").attr("data-votecount")));
            download.add(new Resource(AnswerPAGE,answer.getAnswer_id(),res.getUser_data_id(),res.getUrl_name(),answer));
        }
    }
    private void dealQuestionPage(Resource res){
        Question question = (Question) res.getObj();
        Document questionpage = Jsoup.parse(res.getData());
        Element element = questionpage.getElementsByAttributeValue("class", "zm-tag-editor zg-section").first();
        if (element != null) {
            for (Element el : element.getElementsByAttributeValue("class", "zm-item-tag")) {
                question.getTags().add(el.text());//爬取tag
            }
            element = questionpage.getElementsByAttributeValue("class", "zg-gray-normal").get(2);
            question.setView_count(element.getElementsByTag("strong").get(0).text());
        }
        new Mongo().pushUserQuestion(res.getUser_data_id(),question);//单个返回
        userMap.get(res.getUser_data_id()).subQuestionCount();
    }
    private void dealAnswerPage(Resource res){
        Answer answer = (Answer) res.getObj();
        Document page = Jsoup.parse(res.getData());
        Element el = page.getElementsByAttributeValue("class", "zm-tag-editor-labels zg-clear").first();
        if (el != null) {
            for (Element element : el.getElementsByTag("a")) {
                answer.getAnswer_tags().add(element.text());
            }
        }
        Element an = page.getElementById("zh-question-answer-wrap");
        el = an.getElementsByAttributeValue("class", "answer-date-link meta-item").first();
        if (el != null) {
            answer.setAnswer_time(getTime(el.text()));
        } else {
            el = an.getElementsByAttributeValue("class", "answer-date-link last_updated meta-item").first();
            answer.setAnswer_time(getTime(el.text()));
        }//获得时间
        el = an.getElementsByAttributeValue("class", "zu-question-my-bio").first();
        if (el != null)
            answer.setAnswer_bio(el.text());//获得特殊的简介
        el = an.getElementsByAttributeValue("data-action", "/answer/content").first();
        if(el !=null)
            answer.setAnswer_content(el.html());//获得回答全文
        else
            answer.setAnswer_content(an.getElementById("answer-status").text());
        new Mongo().pushUserAnswer(res.getUser_data_id(), answer);//单个返回
        userMap.get(res.getUser_data_id()).subAnswerCount();
    }

    private String getIntVoteFromString(String vote) {
        int v = 0;
        try {
            v = Integer.parseInt(vote);
        } catch (NumberFormatException n) {
            if (vote.contains("K")) {
                v = Integer.parseInt(vote.replace("K", "").trim());
                v = v * 1000;
            }
            if (vote.contains("W")) {
                v = Integer.parseInt(vote.replace("W", "").trim());
                v = v * 10000;
            }
            if (vote.contains("M")) {
                v = (int)Double.parseDouble(vote.replace("M", "").trim());
                v = v * 100000;
            }
        }
        return ""+v;
    }
    public String getTime(String time) {
        Format f = new SimpleDateFormat("yyyy-MM-dd");
        if(time.indexOf(":") > 0)
            time = f.format(new Date());
        return time;
    }

}
