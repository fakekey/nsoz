package com.nsoz.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Frame {

    public int[] idImg;
    public int[] dx;
    public int[] dy;
    public int[] onTop;
    public int[] flip;

    public JSONArray toJSONArray() {
        JSONArray arr = new JSONArray();
        for (int j = 0; j < idImg.length; j++) {
            JSONObject obj = new JSONObject();
            obj.put("dx", dx[j]);
            obj.put("dy", dy[j]);
            obj.put("id", idImg[j]);
            arr.add(obj);
        }

        return arr;
    }
}
