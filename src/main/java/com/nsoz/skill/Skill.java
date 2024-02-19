package com.nsoz.skill;

import org.json.simple.JSONObject;
import com.nsoz.model.Char;
import com.nsoz.option.SkillOption;

public class Skill {

    public static final byte SKILL_AUTO_USE = 0;
    public static final byte SKILL_CLICK_USE_ATTACK = 1;
    public static final byte SKILL_CLICK_USE_BUFF = 2;
    public static final byte SKILL_CLICK_NPC = 3;
    public static final byte SKILL_CLICK_LIVE = 4;

    public int id;
    public byte point;
    public byte level;
    public short manaUse;
    public int coolDown;
    public short dx;
    public short dy;
    public byte maxFight;
    public SkillOption[] options;
    public long lastTimeUseThisSkill;
    public SkillTemplate template;

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("id", this.template.id);
        obj.put("point", this.point);
        return obj;
    }

    public boolean isCooldown() {
        long currentTimeMillis = System.currentTimeMillis();
        long num = currentTimeMillis - lastTimeUseThisSkill;
        return num < (long) this.coolDown;
    }

    public static Skill findSkillByIdFrom(Char _char, int _templateId) {
        if (_char != null && _char.vSkill != null && !_char.vSkill.isEmpty()) {
            for (Skill skill : _char.vSkill) {
                if (skill.template.id == _templateId) {
                    return skill;
                }
            }
        }
        return null;
    }
}
