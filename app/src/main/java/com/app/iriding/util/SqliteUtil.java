package com.app.iriding.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.app.iriding.model.CyclingPoint;
import com.app.iriding.model.CyclingRecord;
import com.app.iriding.model.StatisticalRecords;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王海 on 2015/5/30.
 */
public class SqliteUtil {
    // 得到所有的骑行记录
    public List<CyclingRecord> selectAllCyclingRecord(){
        List<CyclingRecord> cyclingRecords = new Select().from(CyclingRecord.class).execute();
        return cyclingRecords;
    }

    // 统计总的骑行纪录
    public StatisticalRecords selectTopCyclingRecord(){
        StatisticalRecords sr = new StatisticalRecords();
        Cursor c = ActiveAndroid.getDatabase().rawQuery("SELECT COUNT(*) as sFrequency, Sum(distance) as sDistance," +
                " Sum(totalTime) as sTotalTime, MAX(maxSpeed) as sMaxSpeed, MAX(distance) as sMaxDistance, " +
                "MAX(totalTime) as sMaxTotalTime FROM " + new CyclingRecord().getTableName(), null);
        c.moveToFirst();
        sr.setsFrequency(c.getInt(c.getColumnIndex("sFrequency")));
        sr.setsDistance(c.getDouble(c.getColumnIndex("sDistance")));
        sr.setsTotalTime(c.getLong(c.getColumnIndex("sTotalTime")));
        sr.setsMaxSpeed(c.getDouble(c.getColumnIndex("sMaxSpeed")));
        sr.setsMaxDistance(c.getDouble(c.getColumnIndex("sMaxDistance")));
        sr.setsMaxTotalTime(c.getLong(c.getColumnIndex("sMaxTotalTime")));
        c.close();
        return sr;
    }

    // 统计月的骑行纪录
    public StatisticalRecords selectMonthCyclingRecord(String month){
        month = month + "%";
        StatisticalRecords sr = new StatisticalRecords();
        try {
            Cursor c = ActiveAndroid.getDatabase().rawQuery("SELECT COUNT(*) as sFrequency, Sum(distance) as sDistance," +
                    " Sum(totalTime) as sTotalTime FROM " + new CyclingRecord().getTableName() + " where mdateTimeStr like ?", new String[]{month});
            c.moveToFirst();
            sr.setsFrequency(c.getInt(c.getColumnIndex("sFrequency")));
            sr.setsDistance(c.getDouble(c.getColumnIndex("sDistance")));
            sr.setsTotalTime(c.getLong(c.getColumnIndex("sTotalTime")));
            c.close();
        }catch (Exception e){
            Log.e("XXX", e.toString());
        }
        return sr;
    }

    // 得到倒数n个骑行记录
    public List<CyclingRecord> selectLastCyclingRecord(int start,int length){
        List<CyclingRecord> cyclingRecords = new Select("Id,totalTimeStr,distance,mdateTimeStr").from(CyclingRecord.class)
                .limit(""+start+", "+length+"")
                .orderBy("Id DESC")
                .execute();
        return cyclingRecords;
    }

    // 得到某一次的骑行记录
    public CyclingRecord selectSingleCyclingRecord(long id){
        CyclingRecord cyclingRecord = new Select().from(CyclingRecord.class)
                .where("Id = ?", id)
                .executeSingle();
        Gson gson = new Gson();
        List<CyclingPoint> cyclingPoints = gson.fromJson(cyclingRecord.getTotalPoint(), new TypeToken<List<CyclingPoint>>() {}.getType());
        List<LatLng> latLngs = new ArrayList<LatLng>();
        int cSize = cyclingPoints.size();
        for (int i = 0; i < cSize; i++){
            LatLng lng = new LatLng(cyclingPoints.get(i).getLatitude(), cyclingPoints.get(i).getLongitude());
            latLngs.add(lng);
        }
        cyclingRecord.setLatLngs(latLngs);
        return cyclingRecord;
    }

    // 得到某一天的纪录
    public List<CyclingRecord> selectDateCyclingRecord(String calendarDay){
        List<CyclingRecord> cyclingRecords = new Select("Id,totalTimeStr,distance,mdateTimeStr")
                .from(CyclingRecord.class)
                .where("mdateTimeStr like ?", calendarDay + "%")
                .orderBy("Id DESC")
                .execute();
        return cyclingRecords;
    }
}
