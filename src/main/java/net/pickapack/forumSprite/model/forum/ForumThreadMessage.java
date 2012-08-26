package net.pickapack.forumSprite.model.forum;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.forumSprite.service.ServiceManager;
import net.pickapack.model.ModelElement;

import java.util.Date;

@DatabaseTable(tableName = "ForumThreadMessage")
public class ForumThreadMessage implements ModelElement {
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
    private String body;

    @DatabaseField
    private ForumThreadMessageAccessType accessType;

    @DatabaseField
    private String userId;

    @DatabaseField
    private boolean anonymous;

    public ForumThreadMessage() {
    }

    public ForumThreadMessage(ForumThread parent, String label, String subject, long creationTime, ForumThreadMessageAccessType accessType, String userId, boolean anonymous) {
        this.createTime = DateHelper.toTick(new Date());
        this.label = label;
        this.parentId = parent == null ? -1 : parent.getId();
        this.subject = subject;
        this.creationTime = creationTime;
        this.accessType = accessType;
        this.userId = userId;
        this.anonymous = anonymous;
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String getTitle() {
        return subject;
    }

    public long getCreateTime() {
        return createTime;
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

    public String getBody() {
        return body;
    }

    public ForumThreadMessageAccessType getAccessType() {
        return accessType;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isAnonymous() {
        return anonymous;
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

    public void setBody(String body) {
        this.body = body;
    }

    public void setAccessType(ForumThreadMessageAccessType accessType) {
        this.accessType = accessType;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public ForumThread getParent() {
        return this.parentId == -1 ? null : ServiceManager.getForumSpriteService().getForumThreadById(this.parentId);
    }
}
