package com.nsoz.model;

import java.util.ArrayList;
import java.util.List;
import com.nsoz.item.Item;
import com.nsoz.skill.Skill;

@SuppressWarnings("unused")
public class Bijuu {

    private List<Skill> skills;
    private short[] potential;
    private Item[] equips;
    private int ppoint, spoint;

    public Bijuu() {
        this.skills = new ArrayList<>();
        this.potential = new short[5];
    }

}
