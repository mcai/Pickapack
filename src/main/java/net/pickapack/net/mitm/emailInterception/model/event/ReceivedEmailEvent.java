package net.pickapack.net.mitm.emailInterception.model.event;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.model.ModelElement;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "ReceivedEmailEvent")
public class ReceivedEmailEvent implements ModelElement, EmailInterceptionEvent {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long parentId;

    @DatabaseField
    private long createTime;

    @DatabaseField
    private long receiveTime;

    @DatabaseField
    private String email;

    @DatabaseField
    private String from;

    @DatabaseField
    private String subject;

    @DatabaseField
    private String content;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> attachmentNames;

    public ReceivedEmailEvent(EmailInterceptionTask parent, String email, String from, String subject, String content, List<String> attachmentNames) {
        this.parentId = parent.getId();
        this.createTime = DateHelper.toTick(new Date());
        this.email = email;
        this.from = from;
        this.subject = subject;
        this.content = content;
        this.attachmentNames = new ArrayList<String>(attachmentNames);
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    @Override
    public String getTitle() {
        return ""; //TODO
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getEmail() {
        return email;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public List<String> getAttachmentNames() {
        return attachmentNames;
    }
}
