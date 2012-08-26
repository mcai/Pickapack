package net.pickapack.forumSprite.model.forum;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.forumSprite.service.ServiceManager;
import net.pickapack.model.ModelElement;

import java.util.Date;

@DatabaseTable(tableName = "ForumThread")
public class ForumThread implements ModelElement {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long parentId;

    @DatabaseField
    private long createTime;

    @DatabaseField
    private String label;

    @DatabaseField
    private String subject;

    @DatabaseField
    private long creationTime;

    @DatabaseField
    private long modifiedTime;

    @DatabaseField
    private boolean approved;

    @DatabaseField
    private boolean sticky;

    @DatabaseField
    private boolean closed;

    public ForumThread() {
    }

    public ForumThread(Forum parent, String label, String subject, long creationTime) {
        this.parentId = parent == null ? -1 : parent.getId();
        this.createTime = DateHelper.toTick(new Date());
        this.label = label;
        this.subject = subject;
        this.creationTime = creationTime;
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getTitle() {
        return subject;
    }

    public String getSubject() {
        return subject;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isSticky() {
        return sticky;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Forum getParent() {
        return this.parentId == -1 ? null : ServiceManager.getForumSpriteService().getForumById(this.parentId);
    }
}
