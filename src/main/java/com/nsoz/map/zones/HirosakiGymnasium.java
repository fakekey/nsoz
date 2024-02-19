package com.nsoz.map.zones;

import com.nsoz.ability.AbilityCustom;
import com.nsoz.bot.Bot;
import com.nsoz.bot.attack.AttackAround;
import com.nsoz.bot.move.MoveWithinCustom;
import com.nsoz.constants.SkillName;
import com.nsoz.fashion.FashionCustom;
import com.nsoz.map.Map;
import com.nsoz.map.TileMap;
import com.nsoz.model.Char;
import com.nsoz.skill.SkillFactory;

public class HirosakiGymnasium extends Gymnasium {

    public HirosakiGymnasium(int id, TileMap tilemap, Map map) {
        super(id, tilemap, map);
    }

    @Override
    public void initBot() {
        Bot bot = Bot.builder().id(-11111).name("Cô Toyotomi").level(50).typePk(Char.PK_DOSAT).build();
        bot.setDefault();
        FashionCustom fashionCustom = FashionCustom.builder().head((short) 44).body((short) 45).leg((short) 46).weapon((short) -1).build();
        bot.setFashionStrategy(fashionCustom);
        AbilityCustom abilityCustom = AbilityCustom.builder().hp(1000).mp(1000).damage(1000).damage2(900).miss(10).exactly(100).fatal(100).build();
        bot.setAbilityStrategy(abilityCustom);
        MoveWithinCustom move = MoveWithinCustom.builder().minX(744).maxX(1176).minY(100).maxY(240).build();
        bot.setMove(move);
        AttackAround attackAround = new AttackAround();
        attackAround.addSkill(SkillFactory.getInstance().newSkill(SkillName.CHIEU_CHOUKOUHIRENZO, 1));
        attackAround.addSkill(SkillFactory.getInstance().newSkill(SkillName.CHIEU_CHOUKOU_SHURIKEN, 1));
        bot.setAttack(attackAround);
        bot.setAbility();
        bot.setFashion();
        bot.recovery();
        bot.setXY((short) 1000, (short) 240);
        setBot(bot);
        join(bot);
    }

}
