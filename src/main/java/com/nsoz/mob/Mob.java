package com.nsoz.mob;

import com.nsoz.constants.MobName;
import com.nsoz.bot.Bot;
import com.nsoz.constants.EffectIdName;
import com.nsoz.constants.EffectTypeName;
import com.nsoz.constants.ItemName;
import com.nsoz.constants.ItemOptionName;
import com.nsoz.item.Item;
import com.nsoz.map.zones.Zone;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.nsoz.event.Event;
import com.nsoz.event.KoroKing;
import com.nsoz.event.LunarNewYear;
import com.nsoz.model.Figurehead;
import com.nsoz.model.Friend;
import com.nsoz.model.Char;
import com.nsoz.effect.Effect;
import com.nsoz.task.GloryTask;
import com.nsoz.map.item.ItemMap;
import com.nsoz.constants.MapName;
import com.nsoz.store.ItemStore;
import com.nsoz.model.RandomItem;
import com.nsoz.option.SkillOptionName;
import com.nsoz.task.TaskOrder;
import com.nsoz.map.world.Territory;
import com.nsoz.constants.TaskName;
import com.nsoz.convert.Converter;
import com.nsoz.event.Halloween;
import com.nsoz.event.Noel;
import com.nsoz.item.ItemFactory;
import com.nsoz.util.NinjaUtils;
import com.nsoz.lib.RandomCollection;
import com.nsoz.map.item.ItemMapFactory;
import com.nsoz.party.Group;
import com.nsoz.server.Server;
import com.nsoz.store.StoreManager;
import com.nsoz.util.Log;

import java.util.Vector;
import lombok.Getter;
import lombok.Setter;

public class Mob {

    public static final byte YEN = 0;
    public static final byte ITEM = 1;
    public static final byte ITEM_TASK = 2;
    public static final byte SUSHI = 3;
    public static final byte BOSS = 4;
    public static final byte EQUIP = 5;
    public static final byte LANG_CO = 6;
    public static final byte VDMQ = 8;
    public static final byte EVENT = 7;
    public static final byte LANG_TRUYEN_THUYET = 9;
    public static final byte CHIEN_TRUONG = 10;
    public static final byte CHIA_KHOA_CO_QUAN = 11;
    public static final byte LAM_THAO_DUOC = 12;
    public static final byte BOSS_LDGT = 13;
    public static final byte LANH_DIA_GIA_TOC = 14;
    public static final byte BI_MA = 15;

    public int id;
    public boolean isDisable;
    public boolean isDontMove;
    public boolean isFire;
    public boolean isIce;
    public boolean isWind;
    public byte sys;
    public int hp;
    public int maxHP;
    public int originalHp;
    public short level;
    public short x;
    public short y;
    public int resFire, resIce, resWind;
    public MobTemplate template;
    public byte status;
    public byte levelBoss;
    public boolean isBoss;
    public long lastTimeAttack;
    public long attackDelay = 2000;
    public int recoveryTimeCount;
    public Vector<Integer> chars;
    public Vector<Integer> attackedChars;
    public boolean isDead;
    public int damageOnPlayer, damageOnPlayer2;
    public int damageOnMob, damageOnMob2;
    public short idSkill_atk = -1;
    public byte typeTool = -1;
    @Setter
    public Zone zone;
    public ItemMap itemMap;
    public boolean isBusyAttackSokeOne;
    public boolean isCantRespawn;
    @Setter
    @Getter
    private boolean isBeast;
    public Lock lock = new ReentrantLock();

    public Object mobMeFocus = null;
    public boolean isCantAttack = false;

    public Hashtable<Byte, Effect> effects = new Hashtable<>();

    public Mob(short templateId, boolean isBoss) {
        this.id = -1;
        this.template = MobManager.getInstance().find(templateId);
        this.isBoss = isBoss;
        this.hp = 0;
        this.maxHP = 0;
        this.isDisable = false;
        this.isDontMove = false;
        this.isFire = false;
        this.isIce = false;
        this.isWind = false;
        this.sys = (byte) NinjaUtils.nextInt(1, 3);
        this.status = 4;
        this.levelBoss = 0;
        this.zone = null;
    }

    public Mob(int id, short templateId, int hp, short level, short x, short y, boolean isBeast, boolean isBoss) {
        this.id = id;
        this.template = MobManager.getInstance().find(templateId);
        this.originalHp = hp;
        this.level = level;
        this.x = x;
        this.y = y;
        this.isDisable = false;
        this.isDontMove = false;
        this.status = 5;
        this.isBoss = isBoss;
        this.levelBoss = 0;
        this.isFire = this.isIce = this.isWind = false;
        this.isDead = false;
        this.chars = new Vector<>();
        this.attackedChars = new Vector<>();
        setBeast(isBeast);
        setClass();
        if (templateId == MobName.HEO_RUNG || templateId == MobName.HEO_MOI || zone.tilemap.isThatThuAi()) {
            this.isCantRespawn = true;
        }
        setLevelBoss();
        setHP();
        setDamage();
        setResistance();
    }

    public Mob(int id, short templateId, int hp, short level, short x, short y, boolean isBeast, boolean isBoss, Zone zone) {
        this.id = id;
        this.template = MobManager.getInstance().find(templateId);
        this.originalHp = hp;
        this.level = level;
        this.x = x;
        this.y = y;
        this.isDisable = false;
        this.isDontMove = false;
        this.status = 5;
        this.isBoss = isBoss;
        this.levelBoss = 0;
        this.isFire = this.isIce = this.isWind = false;
        this.isDead = false;
        this.chars = new Vector<>();
        this.attackedChars = new Vector<>();
        setZone(zone);
        setBeast(isBeast);
        setClass();
        if (templateId == MobName.HEO_RUNG || templateId == MobName.HEO_MOI || zone.tilemap.isThatThuAi()) {
            this.isCantRespawn = true;
        }
        setLevelBoss();
        setHP();
        setDamage();
        setResistance();
    }

    public void setResistance() {
        this.resFire = this.resIce = this.resWind = 0;
        switch (this.sys) {
            case 1: // Hoả hệ
                this.resWind += this.level;
                this.resIce -= this.level;
                break;
            case 2: // Băng hệ
                this.resFire += this.level;
                this.resWind -= this.level;
                break;
            case 3: // Phong hệ
                this.resIce += this.level;
                this.resFire -= this.level;
                break;
        }
        if (this.levelBoss == 1) {
            this.resFire *= 2;
            this.resIce *= 2;
            this.resWind *= 2;
        } else if (this.levelBoss == 2) {
            this.resFire *= 3;
            this.resIce *= 3;
            this.resWind *= 3;
        } else if (this.levelBoss == 3) {
            this.resFire *= 4;
            this.resIce *= 4;
            this.resWind *= 4;
        }
    }

    public void setClass() {
        this.sys = (byte) NinjaUtils.nextInt(1, 3);
    }

    public void setLevelBoss() {
        if (template.id == MobName.HEO_RUNG || template.id == MobName.HEO_MOI) {
            this.levelBoss = 2;
        } else if (template.id == MobName.MOC_NHAN) {
            this.levelBoss = 0;
        } else if (zone.tilemap.isDungeo()) {
            this.levelBoss = 0;
            if (zone.tilemap.id == 116) {
                if (this.id == 82 || this.id == 85) {
                    this.levelBoss = 1;
                }
            } else {
                if (template.id == MobName.NINJA_HAC_AM || template.id == MobName.THIEN_VUONG || template.id == MobName.NGAN_LANG_VUONG) {
                    this.levelBoss = 2;
                }
            }
        } else if (zone.tilemap.isDungeoClan()) {
            this.levelBoss = 0;
            if (this.template.id == MobName.BAO_QUAN) {
                this.levelBoss = 2;
            }
        } else if (zone.tilemap.id == MapName.DIA_DAO_CHIKATOYA || zone.tilemap.id == MapName.THAT_THU_AI) {
            this.levelBoss = 0;
        } else {
            if (this.levelBoss == 3) {
                return;
            }
            if (isBeast) {
                this.levelBoss = 3;
            } else if (zone.numberChief < 1 && NinjaUtils.nextInt(100) == 1 && this.level >= 10 && !this.isBoss) {
                this.levelBoss = 2;
                zone.numberChief++;
            } else if (zone.numberElitez < 2 && NinjaUtils.nextInt(50) == 1 && this.level >= 10 && !this.isBoss) {
                this.levelBoss = 1;
                zone.numberElitez++;
            } else {
                this.levelBoss = 0;
            }
        }
    }

    public void setDamage() {
        this.damageOnPlayer = (int) (this.level + (Math.pow(this.level, 2) / 4));
        if (this.isBoss) {
            this.damageOnPlayer *= 20;
        } else if (this.levelBoss == 1) {
            this.damageOnPlayer *= 2;
        } else if (this.levelBoss == 2) {
            this.damageOnPlayer *= 3;
        } else if (this.levelBoss == 3) {
            this.damageOnPlayer *= 2.5;
        }
        this.damageOnPlayer2 = this.damageOnPlayer - this.damageOnPlayer / 10;
    }

    public void setHP() {
        if (this.levelBoss == 1) {
            this.hp = this.maxHP = this.originalHp * 10;
        } else if (this.levelBoss == 2) {
            this.hp = this.maxHP = this.originalHp * 100;
        } else if (this.levelBoss == 3) {
            this.hp = this.maxHP = this.originalHp * 200;
        } else {
            this.hp = this.maxHP = this.originalHp;
        }
        if (template.id == MobName.HEO_RUNG || template.id == MobName.HEO_MOI) {
            this.hp = this.maxHP = this.originalHp;
        }
        if (this.maxHP < 0) {
            this.hp = this.maxHP = Integer.MAX_VALUE;
        }
    }

    public void recovery() {
        this.itemMap = null;
        this.isDead = false;
        setClass();
        setLevelBoss();
        setResistance();
        setHP();
        setDamage();
        this.status = 5;
        this.isFire = false;
        this.isIce = false;
        this.isWind = false;
        this.isDontMove = false;
        this.isDisable = false;
        this.effects.clear();
    }

    public void die() {
        switch (this.levelBoss) {
            case 3:
                break;
            case 2:
                zone.numberChief--;
                break;
            case 1:
                zone.numberElitez--;
                break;
            default:
                break;
        }
        if (zone.numberChief < 0) {
            zone.numberChief = 0;
        }
        if (zone.numberElitez < 0) {
            zone.numberElitez = 0;
        }
        this.hp = 0;
        this.status = 0;
        this.recoveryTimeCount = 12;
        if (isBeast) {
            this.recoveryTimeCount = 300;
        }
        if (this.zone.tilemap.isChienTruong()) {
            this.recoveryTimeCount = 300;
            if (this.template.id == MobName.BACH_LONG_TRU || this.template.id == MobName.HAC_LONG_TRU) {
                this.recoveryTimeCount += 300;
            }
        } else if (this.template.id == MobName.HOP_BI_AN) {
            this.recoveryTimeCount = 65;
        } else if (this.template.id == MobName.NGUOI_TUYET || this.template.id == MobName.CHUOT_CANH_TY) {
            this.recoveryTimeCount = 900;
        }
        this.isDead = true;
        this.chars.clear();
        this.attackedChars.clear();
        Vector<Byte> removeEffect = new Vector<>();
        for (Entry<Byte, Effect> entry : this.effects.entrySet()) {
            Effect eff = entry.getValue();
            if (eff != null) {
                removeEffect.add(entry.getKey());
            }
        }
        for (byte b : removeEffect) {
            this.effects.remove(b);
            if (b == 1) {
                zone.setFire(this, false);
            } else if (b == 2) {
                zone.setIce(this, false);
            } else if (b == 3) {
                zone.setWind(this, false);
            } else if (b == 14) {
                zone.setMove(this, false);
            } else if (b == 0) {
                zone.setDisable(this, false);
            }
        }
    }

    public int randomItemID() {
        int itemID = RandomItem.ITEM.next();
        if (!isBoss && itemID == ItemName.DA_CAP_1) {
            itemID = this.level / 15;
            itemID = itemID > ItemName.DA_CAP_5 ? ItemName.DA_CAP_5 : itemID;
        } else if (itemID == ItemName.BINH_HP_CUC_TIEU) {
            if (this.level < 10) {
                itemID = ItemName.BINH_HP_CUC_TIEU;
            } else if (this.level < 30) {
                itemID = ItemName.BINH_HP_TIEU;
            } else if (this.level < 40) {
                itemID = ItemName.BINH_HP_VUA;
            } else if (this.level < 70) {
                itemID = ItemName.BINH_HP_LON;
            } else if (this.level < 90) {
                itemID = ItemName.BINH_HP_CUC_LON;
            } else {
                itemID = ItemName.BINH_HP_CAO_CAP;
            }
        } else if (itemID == ItemName.BINH_MP_CUC_TIEU) {
            if (this.level < 10) {
                itemID = ItemName.BINH_MP_CUC_TIEU;
            } else if (this.level < 30) {
                itemID = ItemName.BINH_MP_TIEU;
            } else if (this.level < 40) {
                itemID = ItemName.BINH_MP_VUA;
            } else if (this.level < 70) {
                itemID = ItemName.BINH_MP_LON;
            } else if (this.level < 90) {
                itemID = ItemName.BINH_MP_CUC_LON;
            } else {
                itemID = ItemName.BINH_MP_CAO_CAP;
            }
        }
        return itemID;
    }

    public void dropItem(Char owner, byte type) {
        try {
            if (zone.getNumberItem() > 100) {
                return;
            }
            Item itm = null;
            int itemId = 0;
            if (type == ITEM) {
                itemId = randomItemID();
            } else if (type == LANG_CO) {
                itemId = RandomItem.LANG_CO.next();
                if (this.levelBoss == 1 && NinjaUtils.nextInt(1000) == 0) {
                    itemId = ItemName.HARLEY_DAVIDSON;
                }
            } else if (type == LANG_TRUYEN_THUYET) {
                itemId = RandomItem.LANG_TRUYEN_THUYET.next();
                if (this.levelBoss == 1 && NinjaUtils.nextInt(1000) == 0) {
                    itemId = ItemName.HARLEY_DAVIDSON;
                }
            } else if (type == VDMQ) {
                itemId = RandomItem.VDMQ.next();
                if (itemId == ItemName.PHAN_THAN_LENH && this.level < 90) {
                    return;
                }
            } else if (type == LANH_DIA_GIA_TOC) {
                itemId = RandomItem.LANH_DIA_GIA_TOC.next();
            } else if (type == BOSS_LDGT) {
                itemId = RandomItem.BOSS_LDGT.next();
            } else if (type == CHIEN_TRUONG) {
                itemId = 846;// chìa khóa
            } else if (type == CHIA_KHOA_CO_QUAN) {
                itemId = ItemName.CHIA_KHOA_LANH_DIA_GIA_TOC;// chìa khóa
            } else if (type == LAM_THAO_DUOC) {
                itemId = ItemName.LAM_THAO_DUOC;// lam thảo dược
            } else if (type == YEN) {
                itemId = ItemName.YEN;
            } else if (type == ITEM_TASK) {
                itemId = owner.getIdItemTask(template.id);
                if (itemId == -1) {
                    return;
                }
            } else if (type == EVENT) {
                itemId = Event.getCurrentEvent().randomItemID();
                if (itemId == -1) {
                    return;
                }
            } else if (type == BI_MA) {
                Halloween halloween = (Halloween) Event.getCurrentEvent();
                itemId = halloween.randomItemID2();
            } else if (type == SUSHI) {
                itemId = ItemName.SUSHI;
            } else if (type == BOSS) {
                if (zone.map.id == 167) {
                    itemId = RandomItem.BOSS_LDGT.next();
                } else if (zone.map.id >= 162) {
                    itemId = RandomItem.BOSS_LANG_TRUYEN_THUYET.next();
                } else if (template.id == MobName.HOA_KY_LAN || template.id == MobName.TU_HA_MA_THAN || template.id == MobName.CHUOT_CANH_TY
                        || template.id == 227 || template.id == MobName.KING_HEO || template.id == MobName.MY_HAU_TUONG
                        || template.id == MobName.HOA_KY_LAN_2) {
                    itemId = RandomItem.BOSS_EVENT.next();
                } else {
                    if (this.level >= 90) {
                        itemId = RandomItem.BOSS_VDMQ.next();
                    } else {
                        itemId = RandomItem.BOSS.next();
                    }
                }
            } else if (type == EQUIP) {
                int levelMin = this.level / 10 * 10;
                int levelMax = levelMin + 9;
                List<ItemStore> list = StoreManager.getInstance().getListEquipmentWithLevelRange(levelMin, levelMax);
                if (list.isEmpty()) {
                    return;
                }
                int rd = NinjaUtils.nextInt(list.size());
                ItemStore itemStore = list.get(rd);
                if (itemStore == null) {
                    return;
                }
                itm = Converter.getInstance().toItem(itemStore, Converter.RANDOM_OPTION);
                int n = NinjaUtils.nextInt(itm.options.size() - 1);
                for (int i = 0; i < n; i++) {
                    int index = NinjaUtils.nextInt(itm.options.size());
                    itm.options.remove(index);
                }
                if (n > 0) {
                    itm.yen = 5;
                }
            }
            Item item = null;
            if (type == EQUIP) {
                item = itm;
            } else {
                item = ItemFactory.getInstance().newItem(itemId);
            }
            if (item.id < 12 && !this.isBoss && this.levelBoss == 0) {
                item.isLock = true;
            }
            if (item.template.type == 25) {
                item.isLock = true;
            }
            if (item.id == ItemName.SUSHI) {
                if (owner.selectedSkill != null) {
                    item.setQuantity(owner.selectedSkill.options[0].param);
                }
            } else if (item.id == ItemName.YEN) {
                if (type == LANG_CO) {
                    item.setQuantity(10000);
                } else {
                    item.setQuantity(NinjaUtils.nextInt(this.level * 15, (this.level + 10) * 15));
                }
                if (this.isBoss) {
                    item.setQuantity(50000);
                } else if (this.levelBoss == 1) {
                    item.setQuantity(item.getQuantity() * 20);
                } else if (this.levelBoss == 2) {
                    item.setQuantity(item.getQuantity() * 30);
                }
                if (!this.isBoss && (this.levelBoss == 1 || this.levelBoss == 2)) {
                    if (owner != null) {
                        owner.addYen(item.getQuantity());
                        owner.serverMessage("Bạn nhận được " + item.getQuantity() + " yên.");

                        if (owner.gloryTask != null && owner.gloryTask.type == GloryTask.NHAT_YEN) {
                            owner.gloryTask.updateProgress(item.getQuantity());
                        }

                        return;
                    }
                }
            } else {
                item.setQuantity(1);
            }
            if (type == EVENT || type == BI_MA) {
                if (owner != null) {
                    if (owner.getSlotNull() > 0) {
                        owner.addItemToBag(item);
                        return;
                    }
                }
            }

            item.expire = -1;

            if (type == CHIA_KHOA_CO_QUAN || type == LAM_THAO_DUOC) {
                item.expire = System.currentTimeMillis() + (600000 * 3); //
            }
            short x = this.x;
            short y = this.y;
            if (type != SUSHI) {
                x = (short) NinjaUtils.nextInt(this.x - 20, this.x + 20);
            }
            int temp = 0;
            if (x < 50) {
                x = 50;
            } else if (x > (temp = (zone.tilemap.tmw * 24))) {
                x = (short) (temp - 50);
            }
            y = zone.tilemap.collisionY(x, (short) (this.y / 24 * 24));

            ItemMap itemMap = ItemMapFactory.getInstance().builder().id(zone.numberDropItem++).x(x).y(y).build();
            itemMap.setOwnerID(owner.id);
            if (type == BOSS_LDGT) {
                itemMap.setOwnerID(-1);
            }
            if (item != null) {
                itemMap.setItem(item);
                zone.addItemMap(itemMap);
                if (type == SUSHI || type == ITEM_TASK) {
                    this.itemMap = itemMap;
                } else {
                    zone.getService().addItemMap(itemMap);
                }
            }
        } catch (Exception e) {
            Log.error("mob drop item err", e);
        }
    }

    private void attack() {
        try {
            lock.lock();
            Figurehead[] buNhins = zone.getBuNhins();
            for (int j = 0; j < buNhins.length; j++) {
                Figurehead buNhin = buNhins[j];
                int distance = NinjaUtils.getDistance(this.x, this.y, buNhin.x, buNhin.y);
                if ((this.isBoss && distance > 300) || (!this.isBoss && distance > 300)) {
                    continue;
                }
                zone.getService().npcAttackBuNhin(this, j);
                return;
            }
            Vector<Char> list = new Vector<Char>();
            Vector<Char> attackedChars = getAttackedChars();
            Vector<Char> chars = getChars();
            for (Char _char : attackedChars) {
                if (_char.isCleaned) {
                    continue;
                }
                if (_char.isInvisible()) {
                    continue;
                }
                if (_char.isNhanBan) {
                    continue;
                }
                if (_char.isModeCreate) {
                    continue;
                }
                int distance = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                if ((this.isBoss && distance > 600) || (!this.isBoss && distance > 300)) {
                    continue;
                }
                list.add(_char);
            }
            if (list.isEmpty()) {
                for (Char _char : chars) {
                    if (_char.isCleaned) {
                        removeCharIfExist(_char.id);
                        continue;
                    }
                    if (_char.isInvisible()) {
                        removeCharIfExist(_char.id);
                        continue;
                    }
                    if (_char.isNhanBan) {
                        removeCharIfExist(_char.id);
                        continue;
                    }
                    if (_char.isModeCreate) {
                        removeCharIfExist(_char.id);
                        continue;
                    }
                    int distance = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                    if ((this.isBoss && distance > 600) || (!this.isBoss && distance > 300)) {
                        removeCharIfExist(_char.id);
                        continue;
                    }
                    list.add(_char);
                }
                if (!list.isEmpty()) {
                    int rand = NinjaUtils.nextInt(list.size());
                    Char pl = list.get(rand);
                    attack(pl);
                    removeCharIfExist(pl.id);
                }
                return;
            }
            int rand = NinjaUtils.nextInt(list.size());
            Char pl = list.get(rand);
            attack(pl);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public void thunuoiAttack(Char owner) {
        if (this.mobMeFocus == null || this.isCantAttack) {
            return;
        }
        if (this.mobMeFocus instanceof Mob) {
            Mob m = (Mob) this.mobMeFocus;
            Mob find = owner.zone.findMobLiveByID(m.id);
            if (find == null || !m.equals(find) || find.isDead || NinjaUtils.getDistance(find.x, find.y, owner.x, owner.y) > 200
                    || !owner.isMeCanAttackNpc(find)) {
                this.mobMeFocus = null;
                return;
            }
            mobMeAttack(find, owner);
        } else if (this.mobMeFocus instanceof Char) {
            Char c = (Char) this.mobMeFocus;
            Char find = owner.zone.findCharById(c.id);
            if (find == null || !c.equals(find) || find.isDead || NinjaUtils.getDistance(find.x, find.y, owner.x, owner.y) > 200
                    || !owner.isMeCanAttackOtherPlayer(find)) {
                this.mobMeFocus = null;
                return;
            }
            mobMeAttack(find, owner);
        }
    }

    public void mobMeAttack(Char pl, Char owner) {
        if (pl != null && !pl.isDead) {
            if (System.currentTimeMillis() - this.lastTimeAttack < this.attackDelay) {
                return;
            }
            Char originChar = owner.getOriginChar();
            if (originChar.mapId != 138) {
                if (!originChar.isTiThi && (owner.isVillage() || owner.isSchool())) {
                    return;
                }
            }

            if (owner.isNonCombatState()) {
                return;
            }

            if (originChar.hieuChien >= 15 && !originChar.isBot()) {
                owner.serverDialog("Điểm hiếu chiến quá cao, không thể tiếp tục thi triển thuật này lên người.");
                mobMeFocus = null;
                return;
            }

            try {
                pl.lock.lock();
                int dmgOrigin = NinjaUtils.nextInt(this.damageOnPlayer2, this.damageOnPlayer);
                int dameHp = dmgOrigin;
                int dameFatal = 0;
                boolean isFatal = (owner.fatal > 950 ? 950 : owner.fatal) > NinjaUtils.nextInt(1000);
                if (isFatal) {
                    dameFatal += dmgOrigin;
                }
                int kstcm = pl.options[79] + pl.options[121]; // Kháng st chí mạng
                kstcm = kstcm > 100 ? 100 : kstcm;
                dameFatal -= dameFatal * kstcm / 100;
                dameHp += dameFatal;

                if (pl.isFire) {
                    dameHp += dmgOrigin;
                }

                dameHp -= pl.dameDown;
                switch (sys) {
                    case 1:
                        dameHp -= pl.options[48];
                        dameHp -= pl.resFire;
                        break;

                    case 2:
                        dameHp -= pl.options[49];
                        dameHp -= pl.resIce;
                        break;

                    case 3:
                        dameHp -= pl.options[50];
                        dameHp -= pl.resWind;
                        break;
                }
                switch (sys) {
                    case 1:
                        dameHp -= dameHp * pl.options[127] / 100; // Kháng st hệ hoả %
                        break;

                    case 2:
                        dameHp -= dameHp * pl.options[130] / 100; // Kháng st hệ băng %
                        break;

                    case 3:
                        dameHp -= dameHp * pl.options[131] / 100; // Kháng st hệ phong %
                        break;
                }
                if (isFatal && kstcm <= 90) {
                    dameHp -= dameHp * pl.options[46] / 100; // Chịu st khi bị cm -#%
                }
                dameHp -= dameHp * (pl.options[63] + pl.options[98]) / 100; // Miễn giảm sát thương trang bị
                if (pl.isReductionDame) { // Skill phượng hoàng, miễn giảm sát thương 5s
                    dameHp -= dameHp * pl.options[136] / 100;
                }
                Effect eff2 = pl.getEm().findByID(EffectIdName.HIEU_UNG_LONG_DEN_NGOI_SAO_MIEN_GIAM_SAT_THUONG_20_PERCENT);
                if (eff2 != null) {
                    dameHp -= dameHp * eff2.param / 100;
                }
                int exactly = NinjaUtils.nextInt(owner.exactly + 100);
                int miss = NinjaUtils.nextInt(pl.miss + 100);
                boolean isMiss = miss > exactly;

                if (pl.isMiss) {
                    isMiss = true;
                }

                if (pl.optionsSupportSkill[SkillOptionName.NE_TRANH_HOAN_TOAN_TAN_CONG_ADD_POINT_PERCENT] > 0) {
                    int rand = NinjaUtils.nextInt(100);
                    if (rand <= pl.optionsSupportSkill[SkillOptionName.NE_TRANH_HOAN_TOAN_TAN_CONG_ADD_POINT_PERCENT]) {
                        isMiss = true;
                    }
                }

                if (isMiss) {
                    dameHp = 0;
                } else {
                    if (dameHp <= 0) {
                        dameHp = 1;
                    }
                }
                int dameMp = 0;
                if (pl.isShieldMana) {
                    Effect eff = pl.getEm().findByType(EffectTypeName.HIEU_UNG_MANA_SHIELD);
                    if (eff != null) {
                        if ((float) (pl.mp / (float) pl.maxMP * 100f) >= 10) {
                            dameMp = dameHp * eff.param / 100;
                            if (dameMp >= pl.mp) {
                                dameMp = pl.mp;
                            }
                            dameHp -= dameMp;
                            pl.addMp(-dameMp);
                            pl.getService().updateMp();
                        }
                    }
                }
                if (isFatal) {
                    dameHp *= -1;
                    dameMp *= -1;
                }
                this.lastTimeAttack = System.currentTimeMillis();
                owner.zone.getService().mobMeAttack(owner, pl, this);
                owner.zone.getService().attackCharacter(dameHp, dameMp, pl);

                if ((pl.taskId == TaskName.NV_LAY_NUOC_HANG_SAU || pl.taskId == TaskName.NV_LAY_NUOC_HANG_SAU || pl.taskId == TaskName.NV_HAI_NAM)
                        && pl.isCatchItem) {
                    pl.isFailure = true;
                }

                if (pl.isTiThi && pl.hp - Math.abs(dameHp) <= 0) {
                    Char pl2 = pl.zone.findCharById(pl.playerTiThiId);
                    int num2 = pl.hp;
                    pl.testEnd(num2, pl2);
                } else {
                    pl.addHp(-Math.abs(dameHp));
                }

                owner.zone.getService().loadHP(owner);
                pl.zone.getService().loadHP(pl);
                owner.getService().updateHp();
                pl.getService().updateHp();

                if (pl.hp <= 0) {
                    if (owner.zone.tilemap.isChienTruong()) {
                        short pointAdd = 5;
                        int range = pl.nDead - pl.nKill;
                        if (range > 50) {
                            pointAdd -= 1;
                        }
                        if (range > 100) {
                            pointAdd -= 1;
                        }
                        if (range > 150) {
                            pointAdd -= 1;
                        }
                        if (range > 200) {
                            pointAdd -= 1;
                        }
                        if (range > 250) {
                            pointAdd -= 1;
                        }
                        owner.nKill += 1;
                        pl.nDead += 1;
                        owner.addWarPoint(pointAdd);
                    }
                    if ((owner.getOriginChar().typePk == 2 || owner.getOriginChar().typePk == 3) && owner.getOriginChar().gloryTask != null) {
                        if (owner.getOriginChar().gloryTask.type == GloryTask.CUU_SAT_NGUOI_KHAC && Math.abs(this.level - pl.level) <= 10
                                && !owner.getOriginChar().gloryTask.isExistCharacterId(pl.id)) {
                            owner.getOriginChar().gloryTask.updateProgress(1);
                            owner.getOriginChar().gloryTask.addCharacterId(pl.id);
                        }
                    }
                    if (pl.enemies != null) {
                        if (owner.getOriginChar().typePk == 3) {
                            owner.getOriginChar().addPointPk(1);
                        } else if (owner.getOriginChar().typePk == 2 || owner.getOriginChar().killCharId == pl.id) {
                            owner.getOriginChar().addPointPk(2);
                        }
                        if (!owner.isBot()) {
                            pl.enemies.put(owner.name, new Friend(owner.name, (byte) 0));
                        }
                    }
                    if (pl.hieuChien > 0) {
                        long expLevel = NinjaUtils.getExp(pl.level);
                        long expNext = Server.exps[pl.level];
                        long expD = (expNext * ((pl.hieuChien > 5) ? 5 : pl.hieuChien) / 50);
                        if (pl.exp - expD >= expLevel) {
                            pl.exp -= expD;
                            pl.expDown = 0;
                        } else {
                            expD -= (pl.exp - expLevel);
                            pl.exp = expLevel;
                            pl.expDown += expD;
                            long fiftyPercent = (expNext / 2);
                            if (pl.expDown > fiftyPercent) {
                                pl.expDown = fiftyPercent;
                            }
                        }
                        pl.addPointPk(-1);
                    }
                    owner.getService().serverMessage("Bạn vừa đánh trọng thương " + pl.name);
                    pl.getService().serverMessage("Bạn bị " + owner.name + " đánh trọng thương");
                    pl.startDie();
                }
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
            } finally {
                pl.lock.unlock();
            }
        }
    }

    public void mobMeAttack(Mob m, Char owner) {
        if (m != null && !m.isDead) {
            if (System.currentTimeMillis() - this.lastTimeAttack < this.attackDelay) {
                return;
            }
            try {
                m.lock.lock();
                if (owner.isMobSameParty(m)) {
                    return;
                }
                if (m.template.id == MobName.BOSS_TUAN_LOC || m.template.id == MobName.QUAI_VAT) {
                    if (m.chars.get(0) != this.id) {
                        return;
                    }
                }
                if (Event.isEvent() && (owner.getEventPoint().getPoint(LunarNewYear.MYSTERY_BOX_LEFT) <= 0 && m.template.id == MobName.HOP_BI_AN
                        && owner.mob == null)) {
                    return;
                }
                if (owner.clan == null && m.template.id == MobName.NGUOI_TUYET) {
                    return;
                }

                if (m.template.id == MobName.BAO_QUAN && owner.getEm().findByID((byte) 23) == null) {
                    return;
                }

                if (m.template.id == MobName.KORO_KING && owner.getIndexItemByIdInBag(ItemName.VIEN_THUOC_THAN_KY) == -1) {
                    return;
                }

                if (m.template.id == MobName.CHUOT_CANH_TY) {
                    if (owner.fashion[2] == null || (owner.fashion[2] != null && owner.fashion[2].template.id != ItemName.AO_NGU_THAN
                            && owner.fashion[2].template.id != ItemName.AO_TAN_THOI)) {
                        return;
                    }
                }

                int miss = NinjaUtils.nextInt(m.level + 10);
                int exactly = NinjaUtils.nextInt(owner.exactly + 100);
                boolean isMiss = miss > exactly;

                if (isMiss) {
                    this.lastTimeAttack = System.currentTimeMillis();
                    owner.zone.getService().mobMeAttack(owner, m, this);
                    owner.zone.getService().attackMonster(-1, false, m);
                } else {
                    int dmgOrigin = NinjaUtils.nextInt(this.damageOnMob2, this.damageOnMob);
                    int dameHit = dmgOrigin;

                    if (m.effects != null) {
                        if (m.effects.get(EffectTypeName.HIEU_UNG_BONG) != null) {
                            dameHit += dmgOrigin;
                        }
                    }
                    boolean isFatal = (owner.fatal > 950 ? 950 : owner.fatal) > NinjaUtils.nextInt(1000);
                    if (isFatal) {
                        dameHit += dmgOrigin;
                    }

                    switch (this.sys) {
                        case 1: // Hoả hệ
                            dameHit -= m.resFire;
                            break;
                        case 2: // Băng hệ
                            dameHit -= m.resIce;
                            break;
                        case 3: // Phong hệ
                            dameHit -= m.resWind;
                            break;
                    }

                    if (m.template.id == MobName.NGUOI_TUYET) {
                        dameHit = 1;
                    }
                    dameHit = dameHit > 0 ? dameHit : 1;
                    int preHP = m.hp;
                    if (m.template.id == MobName.BU_NHIN) {
                        m.addHp(-(m.maxHP / 5));
                    } else {
                        m.addHp(-dameHit);
                    }
                    if (m.hp < 0) {
                        m.hp = 0;
                    }
                    int nextHP = m.hp;
                    int hp = Math.abs(nextHP - preHP);
                    if (m.template.id != MobName.BOSS_TUAN_LOC && m.template.id != MobName.QUAI_VAT) {
                        owner.addExp(m, hp);
                    }
                    if (m.hp <= 0) {
                        m.die();
                    }
                    if (m.isDead) {
                        Char killer = owner.getOriginChar();
                        m.dead(killer);
                    } else {
                        if (m.id != 0 && !owner.isNhanBan) {
                            m.addCharId(this.id);
                        }
                    }

                    if (m.template.id == MobName.HOP_BI_AN && m.isDead) {
                        owner.zone.getService().addEffectAuto((byte) 8, (short) m.x, m.y, (byte) 0, (short) 1);
                    }
                    this.lastTimeAttack = System.currentTimeMillis();
                    owner.zone.getService().mobMeAttack(owner, m, this);
                    owner.zone.getService().attackMonster(dameHit, isFatal, m);

                    if (owner.zone.tilemap.isDungeoClan()) {
                        Territory.checkEveryAttack(owner);
                    }
                }
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
            } finally {
                m.lock.unlock();
            }
        }
    }

    public void attack(Char pl) {
        if (pl != null && !pl.isDead) {
            try {
                pl.lock.lock();

                int level = this.level > 0 ? this.level : 1;
                int exactly = NinjaUtils.nextInt((level * 10) + 100);
                int miss = NinjaUtils.nextInt(pl.miss + 100);
                boolean isMiss = miss > exactly;
                if (pl.isMiss) {
                    isMiss = true;
                }

                if (pl.optionsSupportSkill[SkillOptionName.NE_TRANH_HOAN_TOAN_TAN_CONG_ADD_POINT_PERCENT] > 0) {
                    int rand = NinjaUtils.nextInt(100);
                    if (rand <= pl.optionsSupportSkill[SkillOptionName.NE_TRANH_HOAN_TOAN_TAN_CONG_ADD_POINT_PERCENT]) {
                        isMiss = true;
                    }
                }

                if (pl.optionsSupportSkill[69] > 0 && !isMiss) { // đóng băng kunai 6x
                    int rand = NinjaUtils.nextInt(100);
                    if (rand <= pl.optionsSupportSkill[69]) {
                        Effect eff = new Effect(6, pl.optionsSupportSkill[70], 0);
                        eff.addTime(pl.options[44] * 1000);
                        byte type = eff.template.type;
                        Effect effDongBang = effects.get(type);
                        if (effDongBang != null) {
                            if (effDongBang.getTimeRemaining() > effDongBang.getTimeLength()) {
                                return;
                            }
                        }
                        effects.put(type, eff);
                        zone.setIce(this, true);
                    }
                }

                int dameHp = NinjaUtils.nextInt(this.damageOnPlayer2, this.damageOnPlayer);

                if (zone.tilemap.isDungeo()) {
                    dameHp *= 2;
                } else if (zone.tilemap.isDungeoClan()) {
                    dameHp = pl.hp * 80 / 100;
                    if (this.isBoss) {
                        dameHp *= 20;
                    } else if (this.levelBoss == 1) {
                        dameHp *= 2;
                    } else if (this.levelBoss == 2) {
                        dameHp *= 3;
                    }
                } else if (zone.tilemap.isDauTruong() || zone.tilemap.isLoiDai()) {
                    dameHp = dameHp * 10 / 100;
                }

                if (pl.isFire) {
                    dameHp += dameHp;
                }
                dameHp -= pl.dameDown;
                switch (sys) {
                    case 1:
                        dameHp -= pl.options[48];
                        dameHp -= pl.resFire;
                        break;

                    case 2:
                        dameHp -= pl.options[49];
                        dameHp -= pl.resIce;
                        break;

                    case 3:
                        dameHp -= pl.options[50];
                        dameHp -= pl.resWind;
                        break;
                }
                switch (sys) {
                    case 1:
                        dameHp -= dameHp * pl.options[127] / 100; // Kháng st hệ hoả %
                        break;

                    case 2:
                        dameHp -= dameHp * pl.options[130] / 100; // Kháng st hệ băng %
                        break;

                    case 3:
                        dameHp -= dameHp * pl.options[131] / 100; // Kháng st hệ phong %
                        break;
                }
                dameHp -= dameHp * (pl.options[63] + pl.options[98]) / 100; // Miễn giảm sát thương trang bị
                if (pl.isReductionDame) { // Skill phượng hoàng, miễn giảm sát thương 5s
                    dameHp -= dameHp * pl.options[136] / 100;
                }
                Effect eff2 = pl.getEm().findByID(EffectIdName.HIEU_UNG_LONG_DEN_NGOI_SAO_MIEN_GIAM_SAT_THUONG_20_PERCENT);
                if (eff2 != null) {
                    dameHp -= dameHp * eff2.param / 100;
                }
                if (zone.tilemap.isDungeoClan()) {
                    int effectId = -1;
                    int downTimeEffectId = -1;
                    int randEffectId = NinjaUtils.nextInt(3);
                    if (zone.tilemap.id == 84 || ((zone.tilemap.id == 90 || zone.tilemap.id == 167) && randEffectId == 1)) {
                        effectId = 5;
                        downTimeEffectId = 40;
                    } else if (zone.tilemap.id == 85 || ((zone.tilemap.id == 90 || zone.tilemap.id == 167) && randEffectId == 2)) {
                        effectId = 7;
                        downTimeEffectId = 42;
                    } else if (zone.tilemap.id == 86 || ((zone.tilemap.id == 90 || zone.tilemap.id == 167) && randEffectId == 3)) {
                        effectId = 6;
                        downTimeEffectId = 41;
                    }

                    int randEffect = NinjaUtils.nextInt(100);
                    if (effectId != -1 && downTimeEffectId != -1
                            && (randEffect < 10 || (randEffect < 50 && zone.tilemap.id == 90) || (randEffect < 20 && zone.tilemap.id == 167))) {
                        Effect eff = new Effect(effectId, 3000, 0);
                        eff.addTime(-pl.options[downTimeEffectId] * 1000);
                        switch (effectId) {
                            case 5: { // Bỏng
                                int skill45 = pl.optionsSupportSkill[SkillOptionName.GIAM_THOI_GIAN_BI_BONG_SUB_0_POINT_GIAY] * 100;
                                eff.addTime(-skill45);
                                Effect effQuat40 = pl.getEm().findByType(EffectTypeName.GIAM_TRU_THOI_GIAN_CHO_MINH_VA_DONG_DOI);
                                if (effQuat40 != null) {
                                    int decreaseTime = 0;
                                    if (effQuat40.param == Effect.EFF_ME) {
                                        decreaseTime = Effect.GIAM_3_GIAY_THOI_GIAN_BI_BONG;
                                    } else if (effQuat40.param == Effect.EFF_FRIEND) {
                                        decreaseTime = Effect.GIAM_2_GIAY_THOI_GIAN_BI_BONG_CHO_DONG_DOI;
                                    }
                                    eff.addTime(-decreaseTime);
                                }
                                break;
                            }
                            case 6: { // Đóng băng
                                int skill45 = pl.optionsSupportSkill[SkillOptionName.GIAM_THOI_GIAN_BI_DONG_BANG_SUB_0_POINT_GIAY] * 100;
                                eff.addTime(-skill45);
                                Effect effQuat40 = pl.getEm().findByType(EffectTypeName.GIAM_TRU_THOI_GIAN_CHO_MINH_VA_DONG_DOI);
                                if (effQuat40 != null) {
                                    int decreaseTime = 0;
                                    if (effQuat40.param == Effect.EFF_ME) {
                                        decreaseTime = Effect.GIAM_2_GIAY_THOI_GIAN_BI_DONG_BANG;
                                    } else if (effQuat40.param == Effect.EFF_FRIEND) {
                                        decreaseTime = Effect.GIAM_1_GIAY_THOI_GIAN_BI_DONG_BANG_CHO_DONG_DOI;
                                    }
                                    eff.addTime(-decreaseTime);
                                }
                                break;
                            }
                            case 7: { // Choáng
                                int skill45 = pl.optionsSupportSkill[SkillOptionName.GIAM_THOI_GIAN_BI_CHOANG_SUB_0_POINT_GIAY] * 100;
                                eff.addTime(-skill45);
                                Effect effQuat40 = pl.getEm().findByType(EffectTypeName.GIAM_TRU_THOI_GIAN_CHO_MINH_VA_DONG_DOI);
                                if (effQuat40 != null) {
                                    int decreaseTime = 0;
                                    if (effQuat40.param == Effect.EFF_ME) {
                                        decreaseTime = Effect.GIAM_1_GIAY_THOI_GIAN_BI_CHOANG;
                                    } else if (effQuat40.param == Effect.EFF_FRIEND) {
                                        decreaseTime = Effect.GIAM_0_5_GIAY_THOI_GIAN_BI_CHOANG_CHO_DONG_DOI;
                                    }
                                    eff.addTime(-decreaseTime);
                                }
                                break;
                            }
                        }
                        pl.getEm().setEffect(eff);
                    }
                }

                if (!isMiss) { // Phản dmg
                    int preHP = this.hp;
                    int reactDame = pl.reactDame;

                    if (pl.options[135] > 0) { // Skill phượng hoàng phản dmg 20% máu hiện tại
                        if (NinjaUtils.nextInt(100) < pl.options[135]) {
                            reactDame = hp * 20 / 100;
                            zone.getService().addEffect(pl.mob, 64, 5, 5, 0);
                        }
                    }

                    if (reactDame > 0) {
                        int reactDame2 = reactDame - reactDame / 10;
                        int dmgOrigin = NinjaUtils.nextInt(reactDame2, reactDame);
                        int dmgHit = dmgOrigin;
                        boolean isFatal = (pl.fatal > 950 ? 950 : pl.fatal) > NinjaUtils.nextInt(1000);
                        if (this.effects != null) {
                            if (this.effects.get(EffectTypeName.HIEU_UNG_BONG) != null) {
                                dmgHit += dmgOrigin;
                            }
                        }
                        if (isFatal) {
                            dmgHit += dmgOrigin;
                        }
                        addHp(-dmgHit);
                        zone.getService().attackMonster(dmgHit, isFatal, this);
                    }

                    int nextHP = this.hp;
                    int hp = Math.abs(nextHP - preHP);
                    pl.addExp(this, hp);

                    if (this.hp <= 0) {
                        this.die();
                    }
                    if (this.isDead) {
                        Char killer = pl.getOriginChar();
                        this.dead(killer);
                    }
                }

                if (isMiss) {
                    dameHp = 0;
                } else {
                    if (dameHp <= 0) {
                        dameHp = 1;
                    }
                }
                int dameMp = 0;
                if (pl.isShieldMana) {
                    Effect eff = pl.getEm().findByType(EffectTypeName.HIEU_UNG_MANA_SHIELD);
                    if (eff != null) {
                        if ((float) (pl.mp / (float) pl.maxMP * 100f) >= 10) {
                            dameMp = dameHp * eff.param / 100;
                            if (dameMp >= pl.mp) {
                                dameMp = pl.mp;
                            }
                            dameHp -= dameMp;
                            pl.addMp(-dameMp);
                            pl.getService().updateMp();
                        }
                    }
                }

                if ((pl.taskId == TaskName.NV_LAY_NUOC_HANG_SAU || pl.taskId == TaskName.NV_LAY_NUOC_HANG_SAU || pl.taskId == TaskName.NV_HAI_NAM)
                        && pl.isCatchItem) {
                    pl.isFailure = true;
                }

                pl.addHp(-dameHp);
                pl.zone.getService().loadHP(pl);
                pl.getService().updateHp();
                if (pl.hp <= 0) {
                    pl.startDie();
                }

                pl.getService().npcAttackMe(this, dameHp, dameMp);
                zone.getService().npcAttackPlayer(this, pl);
            } catch (Exception e) {
                Log.error(e.getMessage(), e);
            } finally {
                pl.lock.unlock();
            }
        }
    }

    public void dead(Char killer) {
        if (killer != null) {
            if (zone != null) {
                zone.mobDead(this, killer);
            }
            int dLevel = Math.abs(this.level - killer.level);
            if (Event.isKoroKing() && dLevel <= 10) {
                if (NinjaUtils.nextInt(2000) == -1) {
                    ((KoroKing) Event.getCurrentEvent()).bornKoroKing(this);
                }
                if (NinjaUtils.nextInt(2000) == 1) {
                    ((KoroKing) Event.getCurrentEvent()).infection(killer);
                }
            }
            if (killer.taskOrders != null) {
                for (TaskOrder task : killer.taskOrders) {
                    if (task.isComplete()) {
                        continue;
                    }
                    if (task.killId == this.template.id) {
                        if (task.taskId == TaskOrder.TASK_DAY) {
                            task.updateTask(1);
                        }
                        if (task.taskId == TaskOrder.TASK_BOSS) {
                            if (this.levelBoss == 3) {
                                task.updateTask(1);
                            }
                        }
                    }

                }
            }
            if (killer.taskMain != null) {
                if (killer.taskId != TaskName.NV_BAT_KHA_THI
                        || ((killer.taskMain.index == 1 && this.levelBoss == 1) || (killer.taskMain.index == 2 && this.levelBoss == 2))) {
                    killer.updateTaskKillMonster(this);
                    Group group = killer.getGroup();
                    if (group != null) {
                        List<Char> chars = group.getCharsInZone(killer.mapId, zone.id);
                        for (Char _char : chars) {
                            if (_char != null && _char != killer && !_char.isDead) {
                                if (_char.taskMain != null) {
                                    if (_char.taskMain.taskId == killer.taskMain.taskId && _char.taskMain.index == killer.taskMain.index) {
                                        _char.updateTaskKillMonster(this);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.template.id == MobName.NGUOI_TUYET) {
                if (killer.clan != null) {
                    for (Char mem : killer.clan.getOnlineMembers()) {
                        mem.serverMessage(killer.name + " đã tiêu diệt người tuyết");
                        mem.getEventPoint().addPoint(Noel.TOP_KILL_SNOWMAN, 100);
                    }
                }
            } else if (this.template.id == MobName.HOP_BI_AN) {
                killer.addBossVuiXuan(this.x, this.y);
            } else if (this.template.id == MobName.QUAI_VAT) {
                killer.rewardVuiXuan();
            } else if (this.template.id == MobName.CHUOT_CANH_TY) {
                // killer.addEventPoint(1, Events.TOP_CHUOT);
            } else if (this.template.id == MobName.BOSS_TUAN_LOC) {
                killer.getEventPoint().addPoint(Noel.TOP_KILL_REINDEER_KING, 1);
                killer.addExp(8000000);
                if (killer.getSlotNull() == 0) {
                    return;
                }
                RandomCollection<Integer> rc = RandomItem.VUA_TUAN_LOC;
                int itemId = rc.next();
                Item itm = ItemFactory.getInstance().newItem(itemId);
                killer.addItemToBag(itm);
            } else if (this.template.id != MobName.BU_NHIN) {
                if (this.isBoss) {
                    if (this.template.id == MobName.KORO_KING) {
                        int itemIndex = killer.getIndexItemByIdInBag(ItemName.VIEN_THUOC_THAN_KY);
                        killer.removeItem(itemIndex, 1, true);
                        killer.addExp(5000000);
                        if (killer.getSlotNull() > 0) {
                            RandomCollection<Integer> rc = RandomItem.BUA_MAY_MAN;
                            int itemId = rc.next();
                            Item itm = ItemFactory.getInstance().newItem(itemId);
                            itm.initExpire();
                            killer.addItemToBag(itm);
                        }
                        return;
                    }
                    if (zone.tilemap.isNormal()) {
                        if (killer.mapId >= 162) {
                            for (int i = 0; i < 20; i++) {
                                dropItem(killer, Mob.BOSS);
                            }
                        } else {
                            for (int i = 0; i < 20; i++) {
                                dropItem(killer, Mob.BOSS);
                            }
                        }
                    } else if (zone.tilemap.id == 167) {
                        for (int i = 0; i < 20; i++) {
                            dropItem(killer, Mob.BOSS_LDGT);
                        }
                    }
                    for (int i = 0; i < 10; i++) {
                        dropItem(killer, Mob.YEN);
                    }
                    if (Event.isVietnameseWomensDay() || Event.isInternationalWomensDay()) {
                        for (int i = 0; i < 20; i++) {
                            dropItem(killer, Mob.EVENT);
                        }
                    }
                } else {
                    int idItem = killer.getIdItemTask(this.template.id);
                    if (idItem != -1 && (NinjaUtils.nextInt(3) == 0 || this.template.id == MobName.HEO_RUNG)) {
                        dropItem(killer, Mob.ITEM_TASK);
                    }
                    if (zone.tilemap.isLangCo()) {
                        if (NinjaUtils.nextInt(8) == 0) {
                            dropItem(killer, Mob.LANG_CO);
                        }
                    } else if (zone.tilemap.isLangTruyenThuyet()) {
                        if (NinjaUtils.nextInt(12) == 0) {
                            this.dropItem(killer, Mob.LANG_TRUYEN_THUYET);
                        }
                    } else if (zone.tilemap.isChienTruong()) {
                        if (NinjaUtils.nextInt(100) == 0) {
                            dropItem(killer, Mob.CHIEN_TRUONG);
                        }
                    } else if (zone.tilemap.isDungeoClan() && zone.isLastBossWasBorn && this.levelBoss == 1) {
                        dropItem(killer, Mob.CHIA_KHOA_CO_QUAN);
                    } else if (zone.tilemap.isDungeoClan() && this.template.id == 81) {
                        if (NinjaUtils.nextInt(10) == 0) {
                            dropItem(killer, Mob.LAM_THAO_DUOC);
                        }
                    } else if (zone.tilemap.isDungeoClan() && (this.template.id == MobName.BAO_QUAN || this.template.id == MobName.TU_HA_MA_THAN)) {
                        for (int i = 0; i < 20; i++) {
                            dropItem(killer, Mob.BOSS_LDGT);
                        }
                    } else if (dLevel <= 7) {
                        int[] percents = {14, 10, 1, 75};
                        byte[] types = {Mob.YEN, Mob.ITEM, Mob.EQUIP, -1};
                        int index = NinjaUtils.randomWithRate(percents, 100);
                        byte type = types[index];
                        if (type == -1) {
                            if (zone.tilemap.isVDMQ()) {
                                if (killer.isTNP && NinjaUtils.nextInt(250) < 5) {
                                    type = Mob.VDMQ;
                                }
                                if (killer.isKNP && NinjaUtils.nextInt(250) == 0) {
                                    type = Mob.VDMQ;
                                }
                            }
                            if (zone.tilemap.isDungeoClan()) {
                                if (NinjaUtils.nextInt(100) < 20) {
                                    type = Mob.LANH_DIA_GIA_TOC;
                                }
                            }
                        }
                        if (type != -1) {
                            dropItem(killer, type);
                        }
                    }
                    if (Event.isEvent()) {
                        int distance = 5;
                        int percentage = 10;
                        if (killer.isTNP || killer.isKNP) {
                            distance = 10;
                        }
                        if (killer.isTNP) {
                            percentage += 5;
                        } else if (killer.isKNP) {
                            percentage += 3;
                        }
                        if (zone.tilemap.isLangCo() || zone.tilemap.isVDMQ()) {
                            percentage += 2;
                        }
                        if (dLevel <= distance) {
                            int r = NinjaUtils.nextInt(100);
                            if (r < percentage) {
                                dropItem(killer, EVENT);
                            }
                        }
                        if (killer.isBiMa && Event.isHalloween()) {
                            if (dLevel <= 10) {
                                int r = NinjaUtils.nextInt(100);
                                if (r <= 5) {
                                    dropItem(killer, BI_MA);
                                }
                            }
                        }
                    }
                    if ((this.levelBoss == 1 || this.levelBoss == 2) && template.id != MobName.HEO_RUNG && template.id != MobName.HEO_MOI) {
                        this.dropItem(killer, Mob.YEN);
                    }
                }
            }
            // isHuman
            if (killer.gloryTask != null) {
                if (Math.abs(killer.level - this.level) <= 10) {
                    if (this.levelBoss == 1) {
                        if (killer.gloryTask.type == GloryTask.TIEU_DIET_TINH_ANH) {
                            killer.gloryTask.updateProgress(1);
                        }
                    } else if (this.levelBoss == 2) {
                        if (killer.gloryTask.type == GloryTask.TIEU_DIET_THU_LINH) {
                            killer.gloryTask.updateProgress(1);
                        }
                    }
                }
            }
            if (killer.isModeRemove) {
                zone.waitingListDelete.add(this);
            } else {
                if (zone.tilemap.isDungeoClan()) {
                    if (this.template.id == MobName.LAM_THAO) {
                        zone.addMobForWatingListRespawn(this);
                    }
                } else if (!zone.tilemap.isDungeo()) {
                    if (!this.isBoss || this.template.id == MobName.HOP_BI_AN) {
                        zone.addMobForWatingListRespawn(this);
                    } else {
                        if (this.template.id == MobName.BOSS_TUAN_LOC || this.template.id == MobName.QUAI_VAT) {
                            killer.mob = null;
                        } else {
                            zone.waitingListDelete.add(this);
                        }
                    }
                }
            }
        }
    }

    public void addCharId(int charId) {
        if (!chars.contains(charId)) {
            chars.add(charId);
        }
    }

    public boolean checkExist(int charId) {
        for (int id : chars) {
            if (id == charId) {
                return true;
            }
        }
        return false;
    }

    public Vector<Char> getChars() {
        Vector<Char> chars = new Vector<>();
        Vector<Integer> clone = (Vector<Integer>) this.chars.clone();
        for (int id : clone) {
            Char _char = zone.findCharById(id);
            if (_char != null) {
                chars.addElement(_char);
            } else if (_char == null) {
                removeCharIfExist(id);
            }
        }
        return chars;
    }

    public void removeCharIfExist(int id) {
        if (chars.contains(id)) {
            chars.remove(chars.indexOf(id));
        }
    }

    public void addAttackedCharId(int charId) {
        if (!attackedChars.contains(charId)) {
            attackedChars.add(charId);
        }
    }

    public Vector<Char> getAttackedChars() {
        Vector<Char> chars = new Vector<>();
        Vector<Integer> clone = (Vector<Integer>) this.attackedChars.clone();
        for (int id : clone) {
            Char _char = zone.findCharById(id);
            if (_char != null) {
                chars.addElement(_char);
            }
        }
        return chars;
    }

    public Char randomChar() {
        Char _char = null;
        Vector<Integer> chars = (Vector<Integer>) this.chars.clone();
        do {
            int size = chars.size();
            if (size == 0) {
                break;
            }
            int index = NinjaUtils.nextInt(size);
            int id = chars.get(index);
            Char tmp = zone.findCharById(id);
            if (tmp == null) {
                chars.remove(index);
            } else {
                if (!tmp.isCleaned && !tmp.isDead && !tmp.isInvisible()) {
                    int distance = NinjaUtils.getDistance(this.x, this.y, tmp.x, tmp.y);
                    if ((this.isBoss && distance > 600) || (!this.isBoss && distance > 300)) {
                        continue;
                    }
                    _char = tmp;
                    break;
                }
            }
        } while (_char == null);
        return _char;
    }

    public void update() {
        if (!this.isDead) {
            if (template.id != MobName.BACH_LONG_TRU && template.id != MobName.HAC_LONG_TRU) {
                if (template.id != MobName.BOSS_TUAN_LOC && template.id != MobName.NGUOI_TUYET && template.id != MobName.HOP_BI_AN
                        && template.id != MobName.QUAI_VAT) {
                    List<Char> list = zone.getChars();
                    if (list.size() > 0) {
                        int add = 0;
                        if (isBoss) {
                            add = 100;
                        }
                        for (Char _char : list) {
                            if (_char.isDead) {
                                continue;
                            }
                            if ((_char.faction == 0 && zone.tilemap.id == 99) || (_char.faction == 1 && zone.tilemap.id == 103)
                                    || _char.faction == 2) {
                                continue;
                            }
                            if (template.type == 4) {
                                int range = NinjaUtils.getDistance(this.x, this.y, _char.x, _char.y);
                                if (range < template.rangeMove + 50 + add) {
                                    if (!_char.isInvisible()) {
                                        addCharId(_char.id);
                                    }
                                }
                            } else {
                                if (this.y == _char.y && Math.abs(this.x - _char.x) < template.rangeMove + 20 + add) {
                                    if (!_char.isInvisible()) {
                                        addCharId(_char.id);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!isIce && !isWind && !isDisable && template.id != MobName.BU_NHIN && template.id != MobName.MOC_NHAN
                        && template.id != MobName.THAO_DUOC && chars.size() > 0) {
                    if (System.currentTimeMillis() - this.lastTimeAttack > this.attackDelay) {
                        this.lastTimeAttack = System.currentTimeMillis();
                        attack();
                    }
                }
            }
            Effect eff5 = effects.get(EffectTypeName.LUA_VO_HINH);
            if (eff5 != null) {
                thieuDot(eff5);
            }
            Vector<Byte> removeEffect = new Vector<>();
            for (Entry<Byte, Effect> entry : this.effects.entrySet()) {
                Effect eff = entry.getValue();
                if (eff == null || eff.isExpired()) {
                    removeEffect.add(entry.getKey());
                }
            }
            for (byte b : removeEffect) {
                this.effects.remove(b);
                if (b == 1) {
                    zone.setFire(this, false);
                } else if (b == 2) {
                    zone.setIce(this, false);
                } else if (b == 3) {
                    zone.setWind(this, false);
                } else if (b == 14) {
                    zone.setMove(this, false);
                } else if (b == 0) {
                    zone.setDisable(this, false);
                }
            }
        }
    }

    public void thieuDot(Effect eff5) {
        lock.lock();
        try {
            int charId = eff5.param2;
            int damage = eff5.param;
            int damage2 = damage - damage / 10;
            int dmgOrigin = NinjaUtils.nextInt(damage2, damage);

            zone.getService().callEffectNpc(this);
            int preHP = this.hp;
            Char p = zone.findCharById(charId);
            if (p == null) {
                return;
            }

            int dmgHit = dmgOrigin;
            boolean isFatal = (p.fatal > 950 ? 950 : p.fatal) > NinjaUtils.nextInt(1000);
            if (this.effects != null) {
                if (this.effects.get(EffectTypeName.HIEU_UNG_BONG) != null) {
                    dmgHit += dmgOrigin;
                }
            }
            if (isFatal) {
                dmgHit += dmgOrigin;
            }

            if (this.template.id == MobName.NGUOI_TUYET) {
                if (p.clan != null) {
                    dmgHit = 1;
                } else {
                    dmgHit = 0;
                }
            } else if (this.zone.tilemap.isDungeoClan() && this.hp - dmgHit <= 0) {
                dmgHit = 0;
            }

            if (this.template.id == MobName.BU_NHIN) {
                addHp(-(this.maxHP / 5));
            } else {
                addHp(-dmgHit);
            }
            zone.getService().attackMonster(dmgHit, isFatal, this);
            int nextHP = this.hp;
            int hp = Math.abs(nextHP - preHP);
            p.addExp(this, hp);

            if (this.hp <= 0) {
                this.die();
            }
            if (this.isDead) {
                Char killer = p.getOriginChar();
                this.dead(killer);
            }

            if (zone.tilemap.isDungeoClan()) {
                Territory.checkEveryAttack(p);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addHp(int add) {
        this.hp += add;
    }
}
