package com.nsoz.bot;

import com.nsoz.ability.AbilityCustom;
import com.nsoz.fashion.FashionCustom;
import com.nsoz.map.Map;
import com.nsoz.map.item.ItemMap;
import com.nsoz.map.item.ItemMapFactory;
import com.nsoz.map.zones.Zone;
import com.nsoz.model.Char;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import com.nsoz.util.TimeUtils;

public class Principal extends Bot {

    private int head;
    private int body;
    private int leg;
    private long lastTime, lastTimeDropItem, lastTimeChat, initTime;

    public Principal(int id, String name, int head, int body, int leg) {
        super(id, name, 150, Char.PK_NORMAL, (byte) 0);
        this.lastTime = this.lastTimeDropItem = this.initTime = System.currentTimeMillis();
        this.head = head;
        this.body = body;
        this.leg = leg;
    }

    @Override
    public void setDefault() {
        super.setDefault();
        FashionCustom fashionCustom = FashionCustom.builder().head((short) head).body((short) body).leg((short) leg).weapon((short) -1).build();
        setFashionStrategy(fashionCustom);
        AbilityCustom abilityCustom = AbilityCustom.builder().hp(2000000).build();
        setAbilityStrategy(abilityCustom);
        setAbility();
        setFashion();
    }

    @Override
    public void updateEveryHalfSecond() {
        super.updateEveryHalfSecond();
        long now = System.currentTimeMillis();
        if (now >= initTime + 60 * 60 * 1000) {
            zone.getService().chat(this.id, "Hôm nay đến đây thôi, tạm biệt các con, hẹn các con ngày mai");
            outZone();
            return;
        }
        if (TimeUtils.canDoWithTime(lastTime, 60000)) {
            lastTime = now;
            zone.getService().chat(this.id, "Ta đi khu khác phát tiếp đây, tạm biệt các con");
            Map map = zone.map;
            Zone z = map.rand();
            outZone();
            Log.info(String.format("%s, map: %s, khu %d", this.name, map.tilemap.name, z.id));
            z.join(this);
            return;
        }
        if (TimeUtils.canDoWithTime(lastTimeDropItem, 10000)) {
            lastTimeDropItem = now;
            int q = NinjaUtils.nextInt(1, 2);
            for (int i = 0; i < q; i++) {
                ItemMap item = ItemMapFactory.getInstance().builder().id(zone.numberDropItem++).type(ItemMapFactory.ENVELOPE)
                        .x((short) (x + (i * 6 * (i % 2 == 0 ? 1 : -1)))).y(y).build();
                zone.addItemMap(item);
                zone.getService().addItemMap(item);
            }
            zone.getService().addEffectAuto((byte) 0, (short) x, y, (byte) 0, (short) 5);
        }
        if (TimeUtils.canDoWithTime(lastTimeChat, 5000)) {
            lastTimeChat = now;
            zone.getService().chat(this.id,
                    (String) NinjaUtils.randomObject("Chúc mừng năm mới 2024", "Xuân này hơn hẳn mấy xuân qua.", "Vạn sự an khang vạn sự lành.",
                            "Phúc lộc đưa nhau đến từng nhà. Vài lời cung chúc tân niên mới.",
                            "Vạn sự như ý, tỉ sự như mơ, triệu triệu bất ngờ, không chờ cũng đến.", "Hoa đào nở, chim én về, mùa xuân lại đến.",
                            "Chúc một năm mới: nghìn sự như ý, vạn sự như mơ, triệu sự bất ngờ, tỷ lần hạnh phúc.",
                            "Chúc năm mới sức khỏe dẻo dai, công việc thuận lợi thăng tiến dài dài, phi những nước đại tiến tới thành công."));
        }
    }
}
