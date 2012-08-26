package net.pickapack.spider.noJs.crawler.qq.model;

import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Friend")
public class Friend {
    private long uin;
    private int categories;
    private String markName;
    private String nickName;

    public Friend(long uin) {
        this.uin = uin;
    }

    public long getUin() {
        return uin;
    }

    public int getCategories() {
        return categories;
    }

    public void setCategories(int categories) {
        this.categories = categories;
    }

    public String getMarkName() {
        return markName;
    }

    public void setMarkName(String markName) {
        this.markName = markName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
