package com.nsoz.model;

import org.json.simple.JSONObject;

public class ImageInfo {

    public int id;
    public int x0;
    public int y0;
    public int w;
    public int h;

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("w", w);
        obj.put("x", x0);
        obj.put("h", h);
        obj.put("y", y0);
        obj.put("id", id);
        return obj;
    }
}
