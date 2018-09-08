package com.lhx.library.activity;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * activity 堆栈
 */

public class ActivityStack {

    //当前显示的activity
    private static ArrayList<AppBaseActivity> activities = new ArrayList<>();

    /**
     * 添加activity
     * @param activity 要添加的activity
     */
    public static void addActivity(@NonNull AppBaseActivity activity){

        if(!activities.contains(activity)){
            activities.add(activity);
        }
    }

    /**
     * 删除activity
     * @param activity 要删除的activity
     */
    public static void removeActivity(@NonNull AppBaseActivity activity){
        activities.remove(activity);
    }

    /**
     * 获取对应名称的 activity
     * @param name 名称
     * @return activity
     */
    public static AppBaseActivity getActivity(String name){
        if(name != null){
            for(AppBaseActivity activity : activities){
                if(name.equals(activity.getName())){
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * 关闭所有activity到 root
     */
    public static void finishActivitiesToRoot(){
        //要关闭的activity
        Set<AppBaseActivity> closeActivities = new HashSet<>();

        for(int i = 0;i < activities.size();i ++){
            AppBaseActivity activity = activities.get(i);
            if(!activity.isTaskRoot()){
                closeActivities.add(activities.get(i));
            }
        }

        Iterator<AppBaseActivity> iterator = closeActivities.iterator();
        while (iterator.hasNext()){
            AppBaseActivity activity = iterator.next();
            activity.finish();
        }
    }

    /**
     * 关闭多个activity
     * @param toName 在这个activity名称之后的都关闭，不包括这个
     */
    public static void finishActivities(@NonNull String toName){
        finishActivities(toName, Integer.MAX_VALUE);
    }

    /**
     * 关闭多个activity
     * @param toName 在这个activity名称之后的都关闭，不包括这个
     * @param resultCode {@link android.app.Activity#setResult(int)}
     */
    public static void finishActivities(@NonNull String toName, int resultCode){
        int index = -1;
        for(int i = activities.size() - 1;i >= 0;i --){
            AppBaseActivity activity = activities.get(i);
            if(toName.equals(activity.getName())){
                index = i;
                break;
            }
        }

        if(index != -1 && index != activities.size() - 1){

            //要关闭的activity
            Set<AppBaseActivity> closeActivities = new HashSet<>();

            for(int i = index + 1;i < activities.size();i ++){
                closeActivities.add(activities.get(i));
            }

            Iterator<AppBaseActivity> iterator = closeActivities.iterator();
            while (iterator.hasNext()){
                AppBaseActivity activity = iterator.next();
                if(resultCode != Integer.MAX_VALUE){
                    activity.setResult(resultCode);
                }
                activity.finish();
            }
        }
    }
}
