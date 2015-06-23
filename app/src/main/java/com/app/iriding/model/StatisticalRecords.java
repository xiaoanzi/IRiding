package com.app.iriding.model;

/**
 * Created by wang on 2015/6/22.
 */
public class StatisticalRecords {
    private long sTotalTime;// 总时间
    private double sDistance;// 总里程
    private int sFrequency;// 总次数
    private double sMaxSpeed;// 最高速度
    private long sMaxTotalTime;// 一次最长时间
    private double sMaxDistance;// 一次最长里程

    public long getsTotalTime() {
        return sTotalTime;
    }

    public void setsTotalTime(long sTotalTime) {
        this.sTotalTime = sTotalTime;
    }

    public double getsDistance() {
        return sDistance;
    }

    public void setsDistance(double sDistance) {
        this.sDistance = sDistance;
    }

    public int getsFrequency() {
        return sFrequency;
    }

    public void setsFrequency(int sFrequency) {
        this.sFrequency = sFrequency;
    }

    public double getsMaxSpeed() {
        return sMaxSpeed;
    }

    public void setsMaxSpeed(double sMaxSpeed) {
        this.sMaxSpeed = sMaxSpeed;
    }

    public long getsMaxTotalTime() {
        return sMaxTotalTime;
    }

    public void setsMaxTotalTime(long sMaxTotalTime) {
        this.sMaxTotalTime = sMaxTotalTime;
    }

    public double getsMaxDistance() {
        return sMaxDistance;
    }

    public void setsMaxDistance(double sMaxDistance) {
        this.sMaxDistance = sMaxDistance;
    }
}
