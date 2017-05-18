package com.lhx.library.util;

import java.util.Random;

/**
 * 字符串功能类
 */

public class StringUtil {

    /**
     * 获取一段随机数字
     * @param length 数字长度
     * @return 随机数
     */
    public static String getRandomNumber(int length){

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i < length;i ++){
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }
}
