package com.lhx.library.util;

import android.graphics.Point;

/**
 * 数学有关的工具类
 */

public class MathUtil {

    /**
     * 获取圆上的坐标
     * @param center 圆心坐标
     * @param radius 半径
     * @param arc 要获取坐标的弧度 0 - 360
     * @return 坐标
     */
    public static Point pointInCircle(Point center, int radius, float arc){

        int x = (int)(center.x + Math.cos(arc * Math.PI / 180) * radius);
        int y = (int)(center.y + Math.sin(arc * Math.PI / 180) * radius);

        return new Point(x, y);
    }
}
