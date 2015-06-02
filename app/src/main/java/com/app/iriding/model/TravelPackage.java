package com.app.iriding.model;

/**
 * Created by 王海 on 2015/6/3.
 */
public class TravelPackage {
    private String title;
    private String departure;// 出发地
    private String destination;// 目的地
    private int alreadyDay;// 已经进行多少天
    private int forecastDay;// 预计多少天
    private String cover;// 封面
    private int isDone;// 0表示进行中 1表示已完成

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getAlreadyDay() {
        return alreadyDay;
    }

    public void setAlreadyDay(int alreadyDay) {
        this.alreadyDay = alreadyDay;
    }

    public int getForecastDay() {
        return forecastDay;
    }

    public void setForecastDay(int forecastDay) {
        this.forecastDay = forecastDay;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }
}
