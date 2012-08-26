package net.pickapack.spider.noJs.crawler.qq.model;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "FriendCategory")
public class FriendCategory {
    private long index;
    private String name;

    public FriendCategory(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
