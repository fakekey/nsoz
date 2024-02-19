package com.nsoz.model;

import java.util.Vector;
import com.nsoz.item.Item;

public class Trader {

    public boolean isLock;
    public Char player;
    public int coinTradeOrder;
    public Vector<Item> itemTradeOrder;
    public boolean accept;

    public Trader(Char p) {
        this.player = p;
    }

    public Char getChar() {
        return this.player;
    }
}
