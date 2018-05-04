package com.lhx.library.util;

import java.util.List;

/**
 * 数据工具类
 */

public class ListUtil {

    public static <T> T get(List<T> list, int index){
        if(list != null && index < list.size()){
            return list.get(index);
        }

        return null;
    }
}
