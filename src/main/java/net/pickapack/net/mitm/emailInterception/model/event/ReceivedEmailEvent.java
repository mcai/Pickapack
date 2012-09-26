package net.pickapack.net.mitm.emailInterception.model.event;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.model.ModelElement;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.net.mitm.emailInterception.service.ServiceManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "ReceivedEmailEvent")
public class ReceivedEmailEvent implements ModelElement, EmailInterceptionEvent {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String no;

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

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> tos;

    @DatabaseField
    private String subject;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String content;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> attachmentNames;

    public ReceivedEmailEvent() {
    }

    public ReceivedEmailEvent(EmailInterceptionTask parent, String no, String email, String from, List<String> tos, String subject, String content, List<String> attachmentNames) {
        this.tos = new ArrayList<String>(tos);
        this.parentId = parent.getId();
        this.no = no;
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

    public String getNo() {
        return no;
    }

    public long getParentId() {
        return parentId;
    }

    @Override
    public String getTitle() {
        return no;
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

    public List<String> getTos() {
        return tos;
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

    public EmailInterceptionTask getParent() {
        return ServiceManager.getEmailInterceptionService().getEmailInterceptionTaskById(this.parentId);
    }

    @Override
    public String toString() {
        return String.format("[%s] ReceivedEmailEvent{no=%s, receiveTime=%s, email='%s', from='%s', tos=%s, subject='%s', content='%s', attachmentNames=%s}", DateHelper.toString(createTime), no, DateHelper.toString(receiveTime), email, from, tos, subject, content, attachmentNames);
    }
}
