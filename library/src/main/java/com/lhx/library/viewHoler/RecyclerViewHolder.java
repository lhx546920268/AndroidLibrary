package com.lhx.library.viewHoler;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * recyclerView holder
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    //保存view的集合
    private SparseArray<View> mViews;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getItemView(){
        return (T)this.itemView;
    }

    /**
     * 通过id 获取view
     * @param id view的id
     * @param <T> 视图类型
     * @return 对应的view
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int id){

        T view = null;
        if(mViews == null){
            mViews = new SparseArray<>();
        }else {
           view = (T)mViews.get(id);
        }

        if(view == null){
            view = (T)itemView.findViewById(id);
            mViews.put(id, view);
        }

        return view;
    }
}
