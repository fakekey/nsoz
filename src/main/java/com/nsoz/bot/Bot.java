package com.nsoz.bot;

import com.nsoz.ability.AbilityFromEquip;
import com.nsoz.bot.attack.AttackTarget;
import com.nsoz.fashion.FashionFromEquip;
import com.nsoz.item.Equip;
import com.nsoz.item.Item;
import com.nsoz.item.Mount;
import com.nsoz.model.Char;
import com.nsoz.network.NoService;
import com.nsoz.network.Service;
import com.nsoz.server.ServerManager;
import com.nsoz.util.Callback;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class Bot extends Char {

    @Setter
    @Getter
    private IAttack attack;

    @Setter
    @Getter
    private IMove move;

    @Override
    public void outZone() {
        if (attack instanceof AttackTarget) {
            ((AttackTarget) attack).close();
        }
        super.outZone();
    }

    public Bot(int id) {
        super(id);
    }

    @Builder
    public Bot(int id, String name, int level, byte typePk, byte classId) {
        super(id);
        this.name = name;
        this.level = level;
        this.typePk = typePk;
        this.classId = classId;
    }

    public void setDefault() {
        this.bag = new Item[0];
        this.box = new Item[0];
        this.equipment = new Equip[16];
        this.fashion = new Equip[16];
        this.mount = new Mount[5];
        this.bijuu = new Item[5];
    }

    public void recovery() {
        this.hp = this.maxHP;
        this.mp = this.maxMP;
        this.isDead = false;
    }

    public void loadCurrentSkill() {
        selectSkill(onCSkill[0]);
    }

    public void setUp() {
        loadDisplay();
        load();
        loadCurrentSkill();
        this.setAbilityStrategy(new AbilityFromEquip());
        setAbility();
        this.hp = this.maxHP;
        this.mp = this.maxMP;
        this.setFashionStrategy(new FashionFromEquip());
        setFashion();
        ServerManager.putBot(this);
    }

    public Service getService() {
        return NoService.getInstance();
    }

    @Override
    public void addMp(int add, Callback... callbacks) {}

    @Override
    public void updateEveryHalfSecond() {
        try {
            super.updateEveryHalfSecond();
        } finally {
            if (attack != null) {
                attack.attack(this);
            }
            if (move != null) {
                move.move(this);
            }
        }
    }
}
