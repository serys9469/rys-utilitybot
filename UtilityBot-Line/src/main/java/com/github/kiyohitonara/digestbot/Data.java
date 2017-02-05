package com.github.kiyohitonara.digestbot;

public class Data {
    private String content;
    private String item;
    private int money;
    private int total;
    private int job;
    private String color;
    private String day;
    private int love;
    private int rank;
    private String sign;

    public Data(String content, String item, int money, int total, int job, String color, String day, int love, int rank, String sign) {
        this.content = content;
        this.item = item;
        this.money = money;
        this.total = total;
        this.job = job;
        this.color = color;
        this.day = day;
        this.love = love;
        this.rank = rank;
        this.sign = sign;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getJob() {
        return job;
    }

    public void setJob(int job) {
        this.job = job;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getLove() {
        return love;
    }

    public void setLove(int love) {
        this.love = love;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "Data{" +
                "content='" + content + '\'' +
                ", item='" + item + '\'' +
                ", money=" + money +
                ", total=" + total +
                ", job=" + job +
                ", color='" + color + '\'' +
                ", day='" + day + '\'' +
                ", love=" + love +
                ", rank=" + rank +
                ", sign='" + sign + '\'' +
                '}';
    }
}
