package com.nsoz.effect;

import org.json.simple.JSONArray;
import com.nsoz.model.Frame;
import com.nsoz.model.ImageInfo;

public class EffectAutoTemp {
    public ImageInfo[] imgInfo;
    public Frame[] frameEffAuto;
    public short[] frameRunning;

    public String toJson() {
        JSONArray effAutoData = new JSONArray();
        JSONArray sprites = new JSONArray();
        for (ImageInfo inf : imgInfo) {
            sprites.add(inf.toJSONObject());
        }
        JSONArray frames = new JSONArray();
        for (Frame fra : frameEffAuto) {
            frames.add(fra.toJSONArray());
        }
        JSONArray running = new JSONArray();
        for (short fra : frameRunning) {
            running.add(fra);
        }
        effAutoData.add(sprites);
        effAutoData.add(frames);
        effAutoData.add(running);

        return effAutoData.toJSONString();
    }
}
