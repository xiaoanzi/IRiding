package com.app.iriding.util;

import com.app.iriding.model.CyclingPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.util.List;

/**
 * Created by ���� on 2015/5/30.
 */
public class SwitchJsonString {
    // ��CyclingPoint��������ת��ΪString
    public String toCyclingPointString(List<CyclingPoint> cyclingPoints){
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(cyclingPoints).getAsJsonArray();
        return myCustomArray.toString();
    }
}
