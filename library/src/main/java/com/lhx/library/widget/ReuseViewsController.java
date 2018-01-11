package com.lhx.library.widget;

import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 视图重用控制器
 */

@SuppressWarnings("unchecked")
public class ReuseViewsController<Child extends View, Container extends ViewGroup>{

    //可见的图片
    private HashSet<Child> visibleViews = new HashSet<>();

    //可复用的view
    private HashSet<Child> reusedViews = new HashSet<>();


    /**
     * 添加子视图
     * @param parent 父视图
     * @param count 子视图
     * @param handler 配置子视图
     */
    public void addSubviews(Container parent, int count, OnSubviewHandler<Child, Container> handler){
        int childCount = parent.getChildCount();

        //把多余的子视图移除
        for(int i = count;i < childCount;i ++){
            Child child = (Child)parent.getChildAt(i);
            visibleViews.remove(child);
            reusedViews.add(child);
        }

        if(count < childCount){
            parent.removeViews(count, childCount - count);
        }

        childCount = parent.getChildCount();
        //重用已在parent上的子视图
        for(int i = 0;i < childCount;i ++){

            Child child = (Child)parent.getChildAt(i);
            handler.onLayout(parent, child, i);
        }

        //如果子视图不够，创建新的，或者在重用队列里面获取
        for(int i = childCount;i < count;i ++){
            Child child = getView(handler);
            handler.onLayout(parent, child, i);
            visibleViews.add(child);
            parent.addView(child);
            reusedViews.remove(child);
        }
    }



    //获取view
    public Child getView(OnSubviewHandler<Child, Container> handler){
        if(reusedViews.size() > 0){
            Iterator<Child> iterator = reusedViews.iterator();
            return iterator.next();
        }

        return handler.getSubviewNewInstance();
    }


    //子视图配置
    public interface OnSubviewHandler<Child, Container>{

        void onLayout(Container container, Child child, int index);

        //获取子视图新实例
        Child getSubviewNewInstance();
    }

}
