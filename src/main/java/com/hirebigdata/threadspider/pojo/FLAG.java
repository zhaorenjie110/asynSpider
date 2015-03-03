package com.hirebigdata.threadspider.pojo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/1/21.
 */
public class FLAG {
    boolean AboutFlag = false;
    boolean TopicFlag = false;
    boolean ColumnFlag = false;
    boolean FollowerFlag = false;
    boolean FolloweeFlag = false;
    boolean QuestionFlag = false;
    boolean AnswerFlag = false;

    private int questionCount = 0;
    private int answerCount = 0;

    public FLAG() {
    }

    public FLAG(int questionCount, int answerCount) {
        this.questionCount = questionCount;
        this.answerCount = answerCount;
    }

    public boolean isAboutFlag() {
        return AboutFlag;
    }
    public void setAboutFlag(boolean aboutFlag) {
        AboutFlag = aboutFlag;
    }
    public void setAboutFlag() {
        AboutFlag = true;
    }

    public boolean isTopicFlag() {
        return TopicFlag;
    }
    public void setTopicFlag(boolean topicFlag) {
        TopicFlag = topicFlag;
    }
    public void setTopicFlag() {
        TopicFlag = true;
    }

    public boolean isColumnFlag() {
        return ColumnFlag;
    }
    public void setColumnFlag(boolean columnFlag) {
        ColumnFlag = columnFlag;
    }
    public void setColumnFlag() {
        ColumnFlag = true;
    }

    public boolean isFollowerFlag() {
        return FollowerFlag;
    }
    public void setFollowerFlag(boolean followerFlag) {
        FollowerFlag = followerFlag;
    }
    public void setFollowerFlag() {
        FollowerFlag = true;
    }

    public boolean isFolloweeFlag() {
        return FolloweeFlag;
    }
    public void setFolloweeFlag(boolean followeeFlag) {
        FolloweeFlag = followeeFlag;
    }
    public void setFolloweeFlag() {
        FolloweeFlag = true;
    }

    public boolean isQuestionFlag() {
        return QuestionFlag;
    }
    public void setQuestionFlag() {
        if (questionCount == 0)
            QuestionFlag = true;
        else
            QuestionFlag = false;
    }

    public boolean isAnswerFlag() {
        return AnswerFlag;
    }

    public void setAnswerFlag() {
        if (answerCount == 0)
            AnswerFlag = true;
        else
            AnswerFlag = false;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
        if (questionCount == 0)
            QuestionFlag =true;
    }
    public void subQuestionCount(){
        questionCount--;
        if(questionCount == 0)
            QuestionFlag = true;
    }
    public int getAnswerCount() {
        return answerCount;
    }
    public void subAnswerCount(){
        answerCount--;
        if (answerCount == 0){
            AnswerFlag = true;
        }
    }
    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
        if (answerCount == 0)
            AnswerFlag = true;
    }
    public boolean check(){
        if(AboutFlag && TopicFlag && ColumnFlag && FollowerFlag && FolloweeFlag && QuestionFlag && AnswerFlag)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "AboutFlag:"+AboutFlag+" TopicFlag:"+TopicFlag+" ColumnFlag:"+ColumnFlag+" FollowerFlag:"+FollowerFlag+
                " FolloweeFlag:"+FolloweeFlag+" QuestionFlag:"+QuestionFlag+" AnswerFlag:"+AnswerFlag;
    }
}
