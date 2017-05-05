package com.lhx.library.section;

///section信息
public class GridSectionInfo extends SectionInfo {

    ///列数
    public int numberOfColumns;

    ///item之间的间隔 px
    public int itemSpace;

    ///item和header之间的间隔 px
    public int itemHeaderSpace;

    ///item和footer之间的间隔 px
    public int itemFooterSpace;

    ///section偏移量
    public EdgeInsets sectionInsets;
}
