package com.nsoz.clan;

import com.nsoz.model.Char;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Member implements Comparable<Member> {

    private int id;
    private byte classId;
    private int level;
    private int type;
    private String name;
    private int pointClan;
    private int pointClanWeek;
    private boolean online;
    private Char p;
    @Getter
    @Setter
    private boolean saving;

    @Builder
    public Member(int id, int classId, int level, int type, String name, int pointClan, int pointClanWeek) {
        this.id = id;
        this.classId = (byte) classId;
        this.level = level;
        this.name = name;
        this.type = type;
        this.pointClan = pointClan;
        this.pointClanWeek = pointClanWeek;
    }

    public void setChar(Char p) {
        this.p = p;
        if (p != null) {
            this.classId = p.classId;
            this.level = p.level;
        }
    }

    public Char getChar() {
        return p;
    }

    public String getName() {
        return p != null ? p.name : name;
    }

    public byte getClassId() {
        return p != null ? p.classId : classId;
    }

    public int getLevel() {
        return p != null ? p.level : level;
    }

    public void addPointClan(int point) {
        this.pointClan += point;
    }

    public void addPointClanWeek(int point) {
        this.pointClanWeek += point;
    }

    @Override
    public int compareTo(Member him) {
        Integer mePointWeek = this.getPointClanWeek();
        Integer himPointWeek = him.getPointClanWeek();
        return mePointWeek.compareTo(himPointWeek);
    }
}
