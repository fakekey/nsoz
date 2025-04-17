package com.nsoz.effect;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import com.nsoz.constants.EffectIdName;
import com.nsoz.constants.EffectTypeName;
import com.nsoz.constants.ItemName;
import com.nsoz.item.Equip;
import com.nsoz.item.ItemTemplate;
import com.nsoz.map.MapService;
import com.nsoz.model.Char;
import com.nsoz.network.Message;
import com.nsoz.network.Service;
import com.nsoz.util.Log;

public class EffectManager {

    private final Char player;

    private final List<Effect> list;
    private boolean[] statusEffectWithID;
    private boolean[] statusEffectWithType;

    public EffectManager(Char player) {
        this.player = player;
        byte size = Byte.MAX_VALUE;
        this.list = new ArrayList<>();
        this.statusEffectWithID = new boolean[size];
        this.statusEffectWithType = new boolean[size];
    }

    public void add(Effect effect) {
        synchronized (list) {
            list.add(effect);
        }
    }

    public void remove(Effect effect) {
        synchronized (list) {
            list.remove(effect);
        }
    }

    public void setStatus(Effect eff, boolean status) {
        statusEffectWithID[eff.template.id] = status;
        statusEffectWithType[eff.template.type] = status;
    }

    public Effect findByID(int id) {
        synchronized (list) {
            if (statusEffectWithID[id]) {
                for (Effect eff : list) {
                    if (eff.template.id == id) {
                        return eff;
                    }
                }
            }
        }
        return null;
    }

    public Effect findByType(byte type) {
        synchronized (list) {
            if (statusEffectWithType[type]) {
                for (Effect eff : list) {
                    if (eff.template.type == type) {
                        return eff;
                    }
                }
            }
        }
        return null;
    }

    public void update() {
        List<Effect> listExpired = filter(e -> e.isExpired());
        if (!listExpired.isEmpty()) {
            boolean isSetAbility = false;
            for (Effect eff : listExpired) {
                removeEffect(eff);
                player.getService().removeEffect(eff);
                if (player.zone != null) {
                    player.zone.getService().playerRemoveEffect(player, eff);
                }
                byte type = eff.template.type;
                if (type == 7 || type == 15 || type == 13 || type == 20 || type == 21 || type == 22 || type == 23) {
                    isSetAbility = true;
                }
            }
            if (isSetAbility) {
                player.setAbility();
            }
        }
    }

    public byte[] getData() {
        Message ms = new Message();
        try {
            DataOutputStream ds = ms.writer();
            synchronized (list) {
                ds.writeByte(list.size()); // num eff
                for (Effect eff : list) {
                    ds.writeByte(eff.template.id);
                    ds.writeInt(eff.getTimeStart());
                    ds.writeInt(eff.getTimeLength());
                    ds.writeShort(eff.param);
                }
            }
            ds.flush();
            return ms.getData();
        } catch (IOException ex) {
            Log.error(ex.getMessage(), ex);
        } finally {
            ms.cleanup();
        }
        return new byte[] {0};
    }

    public List<Effect> filter(Predicate<Effect> predicate) {
        synchronized (list) {
            return list.stream().filter(predicate).collect(Collectors.toList());
        }
    }

    public boolean statusWithID(int id) {
        return statusEffectWithID[id];
    }

    public boolean statusWithType(int type) {
        return statusEffectWithType[type];
    }

    public void addTime(int id, long time) {
        Effect e = findByID(id);
        if (e != null) {
            e.addTime(time);
        }
    }

    public void setEffect(Effect effect) {
        byte type = effect.template.type;
        int id = effect.template.id;
        effect(effect, true);
        if (id == 40 || id == 41) {
            add(effect);
            player.getService().addEffect(effect);
            player.zone.getService().playerAddEffect(player, effect);
            return;
        }
        if (id == 34) {
            int index = getIndexById((byte) 34);
            if (index > -1) {
                player.getService().removeEffect(effect);
                player.zone.getService().playerRemoveEffect(player, effect);
            } else {
                add(effect);
            }
            player.getService().addEffect(effect);
            player.zone.getService().playerAddEffect(player, effect);
            return;
        }
        if (id == 36 || id == 37 || id == 38 || id == 39 || id == 42) {
            Effect eff = findByID(EffectIdName.HIEU_UNG_LONG_DEN_CA_CHEP_NUA_GIAY_HOI_PHUC_300_HP_MP);
            if (eff != null) {
                player.getService().removeEffect(eff);
                player.zone.getService().playerRemoveEffect(player, eff);
                removeEffect(eff);
            }
            eff = findByID(EffectIdName.HIEU_UNG_LONG_DEN_NGOI_SAO_MIEN_GIAM_SAT_THUONG_20_PERCENT);
            if (eff != null) {
                player.getService().removeEffect(eff);
                player.zone.getService().playerRemoveEffect(player, eff);
                removeEffect(eff);
            }
            eff = findByID(EffectIdName.HIEU_UNG_LONG_DEN_TRON_THEM_30_PERCENT_KINH_NGHIEM_KHI_DANH_QUAI);
            if (eff != null) {
                player.getService().removeEffect(eff);
                player.zone.getService().playerRemoveEffect(player, eff);
                removeEffect(eff);
            }
            eff = findByID(EffectIdName.HIEU_UNG_LONG_DEN_ONG_TRANG_20_PERCENT_BO_QUA_KHANG_CUA_DOI_PHUONG);
            if (eff != null) {
                player.getService().removeEffect(eff);
                player.zone.getService().playerRemoveEffect(player, eff);
                removeEffect(eff);
            }
            eff = findByID(EffectIdName.HIEU_UNG_PET_BI_RE_HANH_NUA_GIAY_HOI_PHUC_400_HP_MP);
            if (eff != null) {
                player.getService().removeEffect(eff);
                player.zone.getService().playerRemoveEffect(player, eff);
                removeEffect(eff);
            }
            add(effect);
            player.getService().addEffect(effect);
            player.zone.getService().playerAddEffect(player, effect);
            return;
        }
        int index = getIndexByType(type);
        if (index > -1) {
            synchronized (list) {
                list.set(index, effect);
            }
            player.getService().editEffect(effect);
            player.zone.getService().playerEditEffect(player, effect);
        } else {
            if (effect.template.type == 0 && effect.template.id != 36) {
                synchronized (list) {
                    for (Effect eff : list) {
                        player.getService().removeEffect(eff);
                        player.zone.getService().playerRemoveEffect(player, eff);
                    }
                    list.add(0, effect);
                    for (Effect eff : list) {
                        player.getService().addEffect(eff);
                        player.zone.getService().playerAddEffect(player, eff);
                    }
                }
            } else {
                add(effect);
                player.getService().addEffect(effect);
                player.zone.getService().playerAddEffect(player, effect);
            }
        }
    }

    public void removeEffect(Effect eff) {
        effect(eff, false);
        remove(eff);
    }

    public void effect(Effect eff, boolean is) {
        byte type = eff.template.type;
        byte id = eff.template.id;
        setStatus(eff, is);
        switch (id) {
            case EffectIdName.THIEN_NHAN_PHU:
                player.isTNP = is;
                break;
            case EffectIdName.KHAI_NHAN_PHU:
                player.isKNP = is;
                break;
            case EffectIdName.HIEU_UNG_BI_MA:
                player.isBiMa = is;
                break;
            case EffectIdName.HIEU_UNG_LONG_DEN_TRON_THEM_30_PERCENT_KINH_NGHIEM_KHI_DANH_QUAI:
                player.isEffExp = is;
                break;
            case EffectIdName.HIEU_UNG_LONG_DEN_NGOI_SAO_MIEN_GIAM_SAT_THUONG_20_PERCENT:
                player.isEffDameDown = is;
                break;
            case EffectIdName.HIEU_UNG_LONG_DEN_ONG_TRANG_20_PERCENT_BO_QUA_KHANG_CUA_DOI_PHUONG:
                player.isEffSkipResistance = is;
                break;
            case EffectIdName.TRANG_THAI_PHI_CHIEN_DAU:
                player.isLockFire = is;
                break;
        }
        switch (type) {
            case EffectTypeName.HIEU_UNG_BONG:
                player.isFire = is;
                break;
            case EffectTypeName.HIEU_UNG_DONG_BANG:
                player.isIce = is;
                break;
            case EffectTypeName.HIEU_UNG_CHOANG:
                player.isWind = is;
                break;
            case EffectTypeName.LUA_VO_HINH:
                player.isEffBong = is;
                break;
            case EffectTypeName.HIEU_UNG_MANA_SHIELD:
                player.isShieldMana = is;
                break;
            case EffectTypeName.HIEU_UNG_TANG_NE_TRANH:
                player.isMiss = is;
                if (is) {
                    player.incrMiss = eff.param;
                } else {
                    player.incrMiss = 0;
                }
                break;
            case EffectTypeName.TRANG_THAI_TANG_HINH:
                player.isInvisible = is;
                break;
            case EffectTypeName.TRANG_THAI_AN_THAN:
                player.isHide = is;
                break;
            case EffectTypeName.GIAM_TRU_MAU_VA_TANG_TAN_CONG: // Cung 35
                if (is) {
                    player.incrDame2 = eff.param;
                } else {
                    player.incrDame2 = 0;
                }
                break;
            case EffectTypeName.TRANG_THAI_BI_TROI:
                player.isDontMove = is;
                break;
            case EffectTypeName.TANG_KHANG_CHO_MINH_VA_DONG_DOI: // Quạt 35
                if (is) {
                    player.incrDame = eff.param2;
                    player.incrRes1 = eff.param;
                } else {
                    player.incrDame = 0;
                    player.incrRes1 = 0;
                }
                break;
            case EffectTypeName.HIEU_UNG_X_KINH_NGHIEM:
                if (is) {
                    player.incrExp = eff.param;
                } else {
                    player.incrExp = 0;
                }
                break;
            case EffectTypeName.HIEU_UNG_TANG_CHINH_XAC:
                if (is) {
                    player.incrExactly = eff.param;
                } else {
                    player.incrExactly = 0;
                }
                break;
            case EffectTypeName.HIEU_UNG_TANG_SUC_MANH: // Long lực đan
                if (is) {
                    player.incrDame3 = eff.param;
                } else {
                    player.incrDame3 = 0;
                }
                break;
            case EffectTypeName.HIEU_UNG_TANG_KHANG:
                if (is) {
                    player.incrRes2 = eff.param;
                } else {
                    player.incrRes2 = 0;
                }
                break;
            case EffectTypeName.HIEU_UNG_TANG_MAU:
                if (is) {
                    player.incrHP = eff.param;
                } else {
                    player.incrHP = 0;
                }
                break;
        }
    }

    public void setEffectPet() {
        Equip equip = player.equipment[ItemTemplate.TYPE_THUNUOI];
        switch (equip.id) {
            case ItemName.LONG_DEN_TRON:
                Effect eff = new Effect(EffectIdName.HIEU_UNG_LONG_DEN_TRON_THEM_30_PERCENT_KINH_NGHIEM_KHI_DANH_QUAI,
                        (equip.expire - System.currentTimeMillis()), equip.options.get(0).param);
                setEffect(eff);
                break;

            case ItemName.LONG_DEN_CA_CHEP:
                Effect eff2 = new Effect(EffectIdName.HIEU_UNG_LONG_DEN_CA_CHEP_NUA_GIAY_HOI_PHUC_300_HP_MP,
                        (equip.expire - System.currentTimeMillis()), equip.options.get(0).param);
                setEffect(eff2);
                break;

            case ItemName.LONG_DEN_NGOI_SAO:
                Effect eff3 = new Effect(EffectIdName.HIEU_UNG_LONG_DEN_NGOI_SAO_MIEN_GIAM_SAT_THUONG_20_PERCENT,
                        (equip.expire - System.currentTimeMillis()), equip.options.get(0).param);
                setEffect(eff3);
                break;

            case ItemName.LONG_DEN_MAT_TRANG:
                Effect eff4 = new Effect(EffectIdName.HIEU_UNG_LONG_DEN_ONG_TRANG_20_PERCENT_BO_QUA_KHANG_CUA_DOI_PHUONG,
                        (equip.expire - System.currentTimeMillis()), equip.options.get(0).param);
                setEffect(eff4);
                break;

            case ItemName.BI_RE_HANH:
            case ItemName.BI_RE_HANH2:
                Effect eff5 = new Effect(EffectIdName.HIEU_UNG_PET_BI_RE_HANH_NUA_GIAY_HOI_PHUC_400_HP_MP,
                        (equip.expire - System.currentTimeMillis()), 400);
                setEffect(eff5);
                break;

            default:
                List<Effect> list = filter(e -> Arrays.stream(new int[] {36, 37, 38, 39, 42}).boxed().toList().contains((int) e.template.id));
                if (!list.isEmpty()) {
                    for (Effect effect : list) {
                        if (effect.template.id == 36 || effect.template.id == 37 || effect.template.id == 38 || effect.template.id == 39
                                || effect.template.id == 42) {
                            remove(effect);
                            player.getService().removeEffect(effect);
                            player.zone.getService().playerRemoveEffect(player, effect);
                        }
                    }
                }
                break;
        }
    }

    public void clearScrAllEffect(Service service, MapService mapService, Char p) {
        synchronized (list) {
            for (Effect eff : list) {
                service.removeEffect(eff);
                mapService.playerRemoveEffect(p, eff);
            }
        }
    }

    public void displayAllEffect(Service service, MapService mapService, Char p) {
        synchronized (list) {
            for (Effect eff : list) {
                if (service != null) {
                    service.addEffect(eff);
                }
                if (mapService != null && p != null) {
                    mapService.playerAddEffect(p, eff);
                }
            }
        }
    }

    public JSONArray toJSONArray() {
        JSONArray effects = new JSONArray();
        synchronized (list) {
            for (Effect eff : list) {
                int type = eff.template.type;
                int effId = eff.template.id;
                if (effId == 36 || effId == 37 || effId == 38 || effId == 39) {
                    continue;
                }
                if (type == 0 || type == 18 || type == 25 || type == 26 || type == 28) {
                    effects.add(eff.toJSONObject());
                }
            }
        }
        return effects;
    }

    public void clear() {
        synchronized (list) {
            list.clear();
        }
    }

    public EffectManager clone(Char p) {
        EffectManager em = new EffectManager(p);
        synchronized (list) {
            list.forEach(e -> {
                Effect newEffect = new Effect(e.template.id, e.getStartAt(), e.getEndAt(), e.getParam());
                newEffect.param2 = e.getParam2();
                em.add(newEffect);
            });
        }
        return em;
    }

    private int getIndexById(byte id) {
        int index = 0;
        synchronized (list) {
            for (Effect e : list) {
                if (e.template.id == id) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    private int getIndexByType(byte type) {
        int index = 0;
        synchronized (list) {
            for (Effect e : list) {
                if (e.template.type == type) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }
}
