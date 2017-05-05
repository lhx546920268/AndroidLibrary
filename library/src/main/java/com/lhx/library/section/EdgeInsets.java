package com.lhx.library.section;

///上下左右偏移量
public class EdgeInsets {

    public int left, top, right, bottom;

    public EdgeInsets() {
        this.left = 0;
        this.top = 0;
        this.right = 0;
        this.bottom = 0;
        ;
    }

    public EdgeInsets(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
}
