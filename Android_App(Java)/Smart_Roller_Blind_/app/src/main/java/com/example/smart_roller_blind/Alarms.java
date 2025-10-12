package com.example.smart_roller_blind;

public class Alarms {
    private int WD;
    private int h;
    private int m;
    private boolean pos;
    private boolean Act;

    public Alarms(int WD, int h, int m, boolean pos, boolean act) {
        this.WD = WD;
        this.h = h;
        this.m = m;
        this.pos = pos;
        Act = act;
    }

    public int getWD() {
        return WD;
    }

    public void setWD(int WD) {
        this.WD = WD;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public boolean getPos() {
        return pos;
    }

    public void setPos(boolean pos) {
        this.pos = pos;
    }

    public boolean getAct() {
        return Act;
    }

    public void setAct(boolean act) {
        Act = act;
    }

}
