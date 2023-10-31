package com.ly.wvp.widget.progress;

/**
 * 颜色分段bean类
 */
public class SectionBean {
    /**
     * 颜色 RGB值
     */
    private int color;
    /**
     * 起点
     */
    private int origin;
    /**
     * 终点
     */
    private int terminus;

    /**
     * 是否要跳过
     */
    private boolean isSkip;

    public SectionBean(int color, int origin, int terminus, boolean isSkip) {
        this.color = color;
        this.origin = origin;
        this.terminus = terminus;
        this.isSkip = isSkip;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getOrigin() {
        return origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }

    public int getTerminus() {
        return terminus;
    }

    public void setTerminus(int terminus) {
        this.terminus = terminus;
    }

    public boolean isSkip() {
        return isSkip;
    }

    public void setSkip(boolean skip) {
        isSkip = skip;
    }
}
