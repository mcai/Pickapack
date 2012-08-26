package net.pickapack.forumSprite.model.forum;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.forumSprite.service.ServiceManager;
import net.pickapack.model.ModelElement;

import java.util.Date;

@DatabaseTable(tableName = "ForumUserPermission")
public class ForumUserPermission implements ModelElement {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long createTime;

    @DatabaseField
    private long parentId;

    @DatabaseField
    private boolean read;

    @DatabaseField
    private boolean systemAdmin;

    @DatabaseField
    private boolean forumAdmin;

    @DatabaseField
    private boolean userAdmin;

    @DatabaseField
    private boolean moderator;

    @DatabaseField
    private boolean createThread;

    @DatabaseField
    private boolean createMessage;

    public ForumUserPermission() {
    }

    public ForumUserPermission(long parentId, boolean read, boolean systemAdmin, boolean forumAdmin, boolean userAdmin, boolean moderator, boolean createThread, boolean createMessage) {
        this.createTime = DateHelper.toTick(new Date());
        this.parentId = parentId;
        this.read = read;
        this.systemAdmin = systemAdmin;
        this.forumAdmin = forumAdmin;
        this.userAdmin = userAdmin;
        this.moderator = moderator;
        this.createThread = createThread;
        this.createMessage = createMessage;
    }

    public void setToFull() {
        this.read = true;
        this.systemAdmin = true;
        this.forumAdmin = true;
        this.userAdmin = true;
        this.moderator = true;
        this.createThread = true;
        this.createMessage = true;
    }

    public void setToNone() {
        this.read = false;
        this.systemAdmin = false;
        this.forumAdmin = false;
        this.userAdmin = false;
        this.moderator = false;
        this.createThread = false;
        this.createMessage = false;
    }

    public void setToReadonly() {
        this.read = true;
        this.systemAdmin = false;
        this.forumAdmin = false;
        this.userAdmin = false;
        this.moderator = false;
        this.createThread = false;
        this.createMessage = false;
    }

    public long getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getParentId() {
        return parentId;
    }

    @Override
    public String getTitle() {
        return getParent().getName() + "_permission";
    }

    public boolean isRead() {
        return read;
    }

    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    public boolean isForumAdmin() {
        return forumAdmin;
    }

    public boolean isUserAdmin() {
        return userAdmin;
    }

    public boolean isModerator() {
        return moderator;
    }

    public boolean isCreateThread() {
        return createThread;
    }

    public boolean isCreateMessage() {
        return createMessage;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setSystemAdmin(boolean systemAdmin) {
        this.systemAdmin = systemAdmin;
    }

    public void setForumAdmin(boolean forumAdmin) {
        this.forumAdmin = forumAdmin;
    }

    public void setUserAdmin(boolean userAdmin) {
        this.userAdmin = userAdmin;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public void setCreateThread(boolean createThread) {
        this.createThread = createThread;
    }

    public void setCreateMessage(boolean createMessage) {
        this.createMessage = createMessage;
    }

    public ForumUser getParent() {
        return this.parentId == -1 ? null : ServiceManager.getForumSpriteService().getForumUserById(this.parentId);
    }
}
