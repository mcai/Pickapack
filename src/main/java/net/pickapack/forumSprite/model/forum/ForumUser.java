package net.pickapack.forumSprite.model.forum;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.model.ModelElement;

import java.util.Date;

@DatabaseTable(tableName = "ForumUser")
public class ForumUser implements ModelElement {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long createTime;

    @DatabaseField
    private String name;

    @DatabaseField
    private String password;

    @DatabaseField
    private String email;

    @DatabaseField
    private boolean emailVisible;

    @DatabaseField
    private boolean threadSubscribe;

    @DatabaseField
    private long lastLoginTime;

    @DatabaseField
    private long lastPostTime;

    public ForumUser() {
    }

    public ForumUser(String name, String password) {
        this.createTime = DateHelper.toTick(new Date());
        this.name = name;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    @Override
    public long getParentId() {
        return -1;
    }

    @Override
    public String getTitle() {
        return name;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public long getLastLoginTime() {

        return lastLoginTime;
    }

    public long getLastPostTime() {
        return lastPostTime;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVisible() {
        return emailVisible;
    }

    public boolean isThreadSubscribe() {
        return threadSubscribe;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailVisible(boolean emailVisible) {
        this.emailVisible = emailVisible;
    }

    public void setThreadSubscribe(boolean threadSubscribe) {
        this.threadSubscribe = threadSubscribe;
    }

    public void setLastPostTime(long lastPostTime) {
        this.lastPostTime = lastPostTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
