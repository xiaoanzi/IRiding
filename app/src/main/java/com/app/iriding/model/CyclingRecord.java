package com.app.iriding.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by ���� on 2015/5/30.
 */
@Table(name = "CyclingRecord")
public class CyclingRecord extends Model{
    @Column(name = "totalTime")
    private String totalTime;// ��ʱ��
    @Column(name = "restTime")
    private String restTime;// ��Ϣʱ��
    @Column(name = "distance")
    private String distance;// ����
    @Column(name = "averageSpeed")
    private String averageSpeed;// ƽ���ٶ�
    @Column(name = "maxSpeed")
    private String maxSpeed;// ����ٶ�
    @Column(name = "totalPoint")
    private String totalPoint;// �ܵļ�¼�ĵ�

    private List<LatLng> latLngs;

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getRestTime() {
        return restTime;
    }

    public void setRestTime(String restTime) {
        this.restTime = restTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
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
