package com.nsoz.item;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.ItemOptionName;
import com.nsoz.lib.ParseData;
import com.nsoz.option.ItemOption;
import com.nsoz.util.NinjaUtils;

public class Mount extends Item {

    public static HashMap<Integer, Integer> mountOptions = new HashMap<Integer, Integer>();

    static {
        mountOptions.put(ItemOptionName.HP_TOI_DA_ADD_POINT_TYPE_1, 50);
        mountOptions.put(ItemOptionName.MP_TOI_DA_ADD_POINT_TYPE_1, 50);
        mountOptions.put(ItemOptionName.CHINH_XAC_ADD_POINT_TYPE_1, 10);
        mountOptions.put(ItemOptionName.TAN_CONG_KHI_DANH_CHI_MANG_POINT_PERCENT_TYPE_1, 5);
        mountOptions.put(ItemOptionName.NE_DON_ADD_POINT_TYPE_1, 10);
        mountOptions.put(ItemOptionName.CHI_MANG_ADD_POINT_TYPE_1, 10);
        mountOptions.put(ItemOptionName.KHANG_HOA_ADD_POINT_TYPE_1, 5);
        mountOptions.put(ItemOptionName.KHANG_BANG_ADD_POINT_TYPE_1, 5);
        mountOptions.put(ItemOptionName.KHANG_PHONG_ADD_POINT_TYPE_1, 5);
        mountOptions.put(ItemOptionName.TAN_CONG_POINT_TYPE_1, 100);
        mountOptions.put(ItemOptionName.CHIU_SAT_THUONG_CHO_CHU_ADD_POINT_TYPE_1, 50);
    }

    public Mount(int id) {
        super(id);
    }

    public Mount(JSONObject obj) {
        super(obj);
    }

    @Override
    public void load(JSONObject obj) {
        ParseData parse = new ParseData(obj);
        loadHeader(parse);
        this.template = ItemManager.getInstance().getItemTemplate(this.id);
        this.upgrade = parse.getByte("level");
        this.sys = parse.getByte("sys");
        this.yen = parse.getInt("yen");
        JSONArray ability = parse.getJSONArray("options");
        int size2 = ability.size();
        this.options = new ArrayList<>();
        for (int c = 0; c < size2; c++) {
            JSONArray jAbility = (JSONArray) ability.get(c);
            int templateId = Integer.parseInt(jAbility.get(0).toString());
            int param = Integer.parseInt(jAbility.get(1).toString());
            this.options.add(new ItemOption(templateId, param));
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        saveHeader(obj);
        obj.put("sys", this.sys);
        obj.put("yen", this.yen);
        obj.put("level", this.upgrade);
        JSONArray options = new JSONArray();
        for (ItemOption option : this.options) {
            JSONArray ability = new JSONArray();
            ability.add(option.optionTemplate.id);
            ability.add(option.param);
            options.add(ability);
        }
        obj.put("options", options);
        return obj;
    }

    public void fixMount() {
        if ((this.id == ItemName.HUYET_SAC_HUNG_LANG || this.id == ItemName.XICH_NHAN_NGAN_LANG || this.id == ItemName.PHUONG_HOANG_BANG
                || this.id == ItemName.LAN_SU_VU) && this.isForever() && this.sys > 1) {
            fixMountOption();
        }
    }

    public void fixMountOption() {
        for (ItemOption option : this.options) {
            if (Mount.mountOptions.get(option.optionTemplate.id) == null) {
                continue;
            }
            if (!NinjaUtils.inArray(option.optionTemplate.id, 6, 7, 10, 67, 69, 68, 70, 71, 72, 73, 74)) {
                continue;
            }
            int originValue = Mount.mountOptions.get(option.optionTemplate.id);
            int level = this.upgrade + 1;
            int correctValue = originValue + originValue * (level / 10) + (originValue * 2 * this.sys);
            if (correctValue != option.param) {
                option.param = correctValue;
            }
        }
    }

}
