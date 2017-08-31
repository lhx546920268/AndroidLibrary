package com.lhx.library.section;

/**
 * 用于 listView RecyclerView 分成多块
 */

public class SectionInfo {

    ///section下标 section中行数 该section的起点
    public int section, numberItems, sectionBegin;

    ///是否存在 footer 和 header
    public boolean isExistHeader, isExistFooter;

    ///获取头部位置
    public int getHeaderPosition() {

        return sectionBegin;
    }

    ///获取底部位置
    public int getFooterPosition(){

        int footerPosition = sectionBegin;
        if(isExistHeader)
            footerPosition ++;

        return footerPosition + numberItems;
    }

    ///获取item的起始位置
    public int getItemPosition(){

        if(isExistHeader){

            return sectionBegin + 1;
        }else {

            return sectionBegin;
        }
    }

    public int getItemPosition(int position){
        return position - getItemPosition();
    }

    ///判断position是否是头部
    public boolean isHeaderForPosition(int position){

        return isExistHeader && position == getHeaderPosition();
    }

    ///判断position是否是底部
    public boolean isFooterForPosition(int position){

        return isExistFooter && position == getFooterPosition();
    }
}
