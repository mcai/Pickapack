package net.pickapack.forumSprite.model.forum;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.forumSprite.service.ServiceManager;
import net.pickapack.model.ModelElement;

import java.lang.String;

@DatabaseTable(tableName = "Forum")
public class Forum implements ModelElement {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long parentId;

    @DatabaseField
    private String name;

    @DatabaseField
    private String description;

    @DatabaseField
    private String label;

    @DatabaseField
    private boolean moderated;

    @DatabaseField
    private long creationTime;

    @DatabaseField
    private long modifiedTime;

    public Forum() {
    }

    public Forum(Forum parent, String name, String label, boolean moderated, long creationTime) {
        this.parentId = parent == null ? -1 : parent.getId();
        this.name = name;
        this.label = label;
        this.moderated = moderated;
        this.creationTime = creationTime;
    }

    public long getId() {
        return id;
    }

    @Override
    public long getParentId() {
        return parentId;
    }

    @Override
    public String getTitle() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public boolean isModerated() {
        return moderated;
    }

    public long getCreateTime() {
        return creationTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Forum getParent() {
        return this.parentId == -1 ? null : ServiceManager.getForumSpriteService().getForumById(this.parentId);
    }
}
