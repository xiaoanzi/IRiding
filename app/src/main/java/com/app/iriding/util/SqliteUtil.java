package com.app.iriding.util;

import com.activeandroid.query.Select;
import com.app.iriding.model.CyclingPoint;
import com.app.iriding.model.CyclingRecord;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ���� on 2015/5/30.
 */
public class SqliteUtil {
    // ��ȡ�����б�
    public List<CyclingRecord> selectAllCyclingRecord(){
        List<CyclingRecord> cyclingRecords = new Select().from(CyclingRecord.class).execute();
        return cyclingRecords;
    }

    //��ȡ�������м�¼
    public CyclingRecord selectSingleCyclingRecord(int id){
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
}