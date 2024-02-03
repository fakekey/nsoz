package com.nsoz.bot.attack;

import com.nsoz.model.Char;
import com.nsoz.network.Message;
import com.nsoz.server.Config;
import com.nsoz.skill.Skill;
import com.nsoz.util.Log;
import com.nsoz.util.NinjaUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.nsoz.bot.Bot;
import com.nsoz.bot.IAttack;
import com.nsoz.constants.CMD;
import com.nsoz.constants.EffectIdName;
import com.nsoz.constants.SkillName;
import com.nsoz.effect.Effect;
import com.nsoz.map.zones.Zone;

public class AttackTarget implements IAttack {

    private Char target;
    public Thread comboThread = null;
    public boolean running = false;
    private ReadWriteLock lock;
    private boolean isComboing = false;
    private Message cmd;
    private boolean useHideOnce = false;

    public AttackTarget(Char target) {
        this.target = target;
        this.lock = new ReentrantReadWriteLock();
        cmd = new Message(CMD.PLAYER_ATTACK_PLAYER);
        try {
            cmd.writer().writeInt(target.id);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
    }

    public void callClone(Bot me) {
        Skill skill90 = null;
        switch (me.classId) {
            case 1:
                skill90 = Skill.findSkillByIdFrom(me, SkillName.NHAN_THUAT_KAGE_BUNSHIN_1);
                break;
            case 2:
                skill90 = Skill.findSkillByIdFrom(me, SkillName.NHAN_THUAT_KAGE_BUNSHIN_2);
                break;
            case 3:
                skill90 = Skill.findSkillByIdFrom(me, SkillName.NHAN_THUAT_KAGE_BUNSHIN_3);
                break;
            case 4:
                skill90 = Skill.findSkillByIdFrom(me, SkillName.NHAN_THUAT_KAGE_BUNSHIN_4);
                break;
            case 5:
                skill90 = Skill.findSkillByIdFrom(me, SkillName.NHAN_THUAT_KAGE_BUNSHIN_5);
                break;
            case 6:
                skill90 = Skill.findSkillByIdFrom(me, SkillName.NHAN_THUAT_KAGE_BUNSHIN_6);
                break;
            default:
                break;
        }
        useSkillBuff(me, skill90);
    }

    public boolean useSkillBuff(Bot me, Skill skill) {
        if (skill != null && !skill.isCooldown()) {
            me.selectSkill((short) skill.template.id);
            me.useSkillBuff((byte) (target.x > me.x ? 1 : -1), skill);
            return true;
        }
        return false;
    }

    public boolean useSkillAttack(Bot me, Skill skill) {
        if (skill != null && !skill.isCooldown()) {
            me.selectSkill((short) skill.template.id);
            doAttack(me);
            return true;
        }
        return false;
    }

    public Skill findBestSkillAttack(Bot me) {
        me.vSkillFight.sort((o1, o2) -> (Integer.valueOf(o2.level).compareTo((Integer.valueOf(o1.level)))));
        for (Skill skill : me.vSkillFight) {
            if (!skill.isCooldown() && skill.template.type == Skill.SKILL_CLICK_USE_ATTACK) {
                int num = 0;
                if (me.classId == 0 || me.classId == 1 || me.classId == 3 || me.classId == 5) {
                    num = 40;
                }
                int num2 = me.x - skill.dx;
                int num3 = me.x + skill.dx;
                int num4 = me.y - skill.dy - num;
                int num5 = me.y + skill.dy;
                if (num2 <= target.x && target.x <= num3 && num4 <= target.y && target.y <= num5) {
                    return skill;
                }
            }
        }
        return null;
    }

    public void combo(Bot me) {
        if (isComboing || target.isInvisible()) {
            return;
        }
        try {
            switch (me.classId) {
                case 1: { // Kiếm
                    Skill skill35 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_RAIKOU);
                    Skill skill40 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_HIHEBUN);
                    if (useSkillBuff(me, skill35)) {
                        isComboing = true;
                        long l1 = System.currentTimeMillis();
                        long l2 = l1 + 5000L;
                        if (skill40 != null) {
                            while (true) {
                                if (System.currentTimeMillis() > l2) {
                                    if (useSkillAttack(me, skill40)) {
                                        Thread.sleep(500L);
                                    }
                                    break;
                                }
                            }
                        }
                    } else if (useSkillAttack(me, skill40)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    }
                    isComboing = false;
                    break;
                }
                case 2: { // Tiêu
                    Skill skill35 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_HOSHITAMA);
                    Skill skill40 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_HINOTAMA);
                    if (!useHideOnce) {
                        if ((float) me.hp <= ((float) me.maxHP * 3f / 4f)) {
                            if (useSkillBuff(me, skill35)) {
                                me.isDontMove = true;
                                isComboing = true;
                                long l1 = System.currentTimeMillis();
                                long l2 = l1 + 5000L;
                                if (skill40 != null) {
                                    while (true) {
                                        if (System.currentTimeMillis() > l2) {
                                            if (useSkillAttack(me, skill40)) {
                                                Thread.sleep(500L);
                                            }
                                            useHideOnce = true;
                                            me.isDontMove = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (useSkillBuff(me, skill35)) {
                            me.isDontMove = true;
                            isComboing = true;
                            long l1 = System.currentTimeMillis();
                            long l2 = l1 + 5000L;
                            if (skill40 != null) {
                                while (true) {
                                    if (System.currentTimeMillis() > l2) {
                                        if (useSkillAttack(me, skill40)) {
                                            Thread.sleep(500L);
                                        }
                                        me.isDontMove = false;
                                        break;
                                    }
                                }
                            }
                        } else if (useSkillAttack(me, skill40)) {
                            isComboing = true;
                            Thread.sleep(500L);
                        }
                    }
                    isComboing = false;
                    break;
                }
                case 3: { // Kunai
                    Skill skill35 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_HIBIKOU);
                    Skill skill40 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_KOGOERU);
                    if (useSkillAttack(me, skill35)) {
                        isComboing = true;
                        if (skill40 != null && !skill40.isCooldown()) {
                            long l1 = System.currentTimeMillis() + 2000;
                            while (true) {
                                int distance = NinjaUtils.getDistance(me.x, me.y, target.x, target.y);
                                if (distance <= 48 || System.currentTimeMillis() >= l1) {
                                    useSkillAttack(me, skill40);
                                    break;
                                }
                                Thread.sleep(500L);
                            }
                        }
                    } else if (useSkillAttack(me, skill40)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    }
                    isComboing = false;
                    break;
                }
                case 4: { // Cung
                    Skill skill25 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_SOGEKIHEI);
                    Skill skill35 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_JOUTENHITOMI);
                    Skill skill40 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_KOGOSA);
                    Skill skill60 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_TOTAAIGO);
                    if ((float) me.hp >= (float) me.maxHP * 90f / 100f) {
                        if (useSkillBuff(me, skill35)) {
                            isComboing = true;
                            Thread.sleep(500L);
                        }
                    }
                    if (useSkillBuff(me, skill25)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    } else if (useSkillAttack(me, skill40)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    } else if (useSkillBuff(me, skill60)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    }
                    isComboing = false;
                    break;
                }
                case 5: { // Đao
                    Skill skill35 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_AISUBAAGU);
                    Skill skill40 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_HAYATETO);
                    if (useSkillAttack(me, skill35)) {
                        isComboing = true;
                        if (skill40 != null) {
                            Thread.sleep(500L);
                            useSkillAttack(me, skill40);
                            Thread.sleep(500L);
                        }
                    } else if (useSkillAttack(me, skill40)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    }
                    isComboing = false;
                    break;
                }
                case 6: { // Quạt
                    Skill skill15 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_SUISHOU);
                    Skill skill35 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_HAYATEMI);
                    Skill skill40 = Skill.findSkillByIdFrom(me, SkillName.CHIEU_BOUSOUHAYATE);
                    if ((float) me.hp <= (float) me.maxHP * 90f / 100f) {
                        if (useSkillBuff(me, skill15)) {
                            isComboing = true;
                            Thread.sleep(500L);
                        }
                    }
                    if (useSkillBuff(me, skill35)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    } else if (useSkillBuff(me, skill40)) {
                        isComboing = true;
                        Thread.sleep(500L);
                    }
                    isComboing = false;
                    break;
                }
                default:
                    break;
            }
        } catch (Exception e) {
            isComboing = false;
            close();
        }
    }

    public void initComboThread(Bot me) {
        this.comboThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        long l1 = System.currentTimeMillis();
                        lock.readLock().lock();
                        me.lock.lock();
                        try {
                            combo(me);
                        } finally {
                            me.lock.unlock();
                            lock.readLock().unlock();
                        }
                        long l2 = System.currentTimeMillis() - l1;
                        if (l2 >= 20L) {
                            continue;
                        }
                        try {
                            Thread.sleep(20L - l2);
                        } catch (InterruptedException e) {
                            close();
                        }
                    } catch (Exception e2) {
                        close();
                    }
                }
            }
        });
        this.running = true;
        this.comboThread.setName("Bot thiên địa bảng: " + me.name);
        this.comboThread.start();
    }

    @Override
    public void attack(Bot me) {
        callClone(me);
        Effect nonStart = me.getEm().findByID(EffectIdName.TRANG_THAI_PHI_CHIEN_DAU);
        if (nonStart != null) {
            return;
        }
        if (this.comboThread == null) {
            initComboThread(me);
            return;
        }
        if (me.isDead || target.isDead) {
            return;
        }
        if (me.isCC()) {
            return;
        }
        if (isComboing) {
            return;
        }
        if (!target.isInvisible()) {
            Skill mainSkill = findBestSkillAttack(me);
            useSkillAttack(me, mainSkill);
        }
    }

    public void doAttack(Bot me) {
        if (cmd == null) {
            return;
        }
        Message ms = new Message(CMD.PLAYER_ATTACK_PLAYER, cmd.getData());
        try {
            me.attackCharacter(ms);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        } finally {
            ms.cleanup();
        }
    }

    public void close() {
        this.running = false;
        if (this.comboThread != null && this.comboThread.isAlive()) {
            this.comboThread.interrupt();
        }
        this.comboThread = null;
        if (cmd != null) {
            cmd.cleanup();
            cmd = null;
        }
    }
}
