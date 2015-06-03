package com.app.iriding.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by 王海 on 2015/5/30.
 */
@Table(name = "CyclingRecord")
public class CyclingRecord extends Model{
    @Column(name = "totalTime")
    private long totalTime;// 总时间(毫秒)
    @Column(name = "restTime")
    private long restTime;// 歇息时间(毫秒)
    @Column(name = "totalTimeStr")
    private String totalTimeStr;// 总时间(HH:mm:ss)
    @Column(name = "restTimeStr")
    private String restTimeStr;// 歇息时间(HH:mm:ss)
    @Column(name = "distance")
    private double distance;// 总距离
    @Column(name = "averageSpeed")
    private double averageSpeed;// 平均速度
    @Column(name = "maxSpeed")
    private double maxSpeed;// 最高速度
    @Column(name = "mdateTime")
    private long mdateTime;// 日期(秒)
    @Column(name = "mdateTimeStr")
    private String mdateTimeStr;// 日期的string类型
    @Column(name = "totalPoint")
    private String totalPoint;// 绘图的点

    private List<LatLng> latLngs;

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getRestTime() {
        return restTime;
    }

    public void setRestTime(long restTime) {
        this.restTime = restTime;
    }

    public String getTotalTimeStr() {
        return totalTimeStr;
    }

    public void setTotalTimeStr(String totalTimeStr) {
        this.totalTimeStr = totalTimeStr;
    }

    public String getRestTimeStr() {
        return restTimeStr;
    }

    public void setRestTimeStr(String restTimeStr) {
        this.restTimeStr = restTimeStr;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getMdateTimeStr() {
        return mdateTimeStr;
    }

    public void setMdateTimeStr(String mdateTimeStr) {
        this.mdateTimeStr = mdateTimeStr;
    }

    public long getMdateTime() {
        return mdateTime;
    }

    public void setMdateTime(long mdateTime) {
        this.mdateTime = mdateTime;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(String totalPoint) {
        this.totalPoint = totalPoint;
    }

    public List<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(List<LatLng> latLngs) {
        this.latLngs = latLngs;
    }
}
