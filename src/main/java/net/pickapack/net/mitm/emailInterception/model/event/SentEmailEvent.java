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

@DatabaseTable(tableName = "SentEmailEvent")
public class SentEmailEvent implements ModelElement, EmailInterceptionEvent {
    @DatabaseField(id = true)
    private long id;

    @DatabaseField
    private String no;

    @DatabaseField
    private long parentId;

    @DatabaseField
    private long createTime;

    @DatabaseField(dataType = DataType.STRING_BYTES)
    private String email;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> tos;

    @DatabaseField(dataType = DataType.STRING_BYTES)
    private String subject;

    @DatabaseField(dataType = DataType.STRING_BYTES)
    private String content;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> attachmentNames;

    @DatabaseField(dataType = DataType.STRING_BYTES)
    private String result;

    public SentEmailEvent() {
    }

    public SentEmailEvent(EmailInterceptionTask parent, String no, String email, List<String> tos, String subject, String content, String result) {
        this.parentId = parent == null ? -1 : parent.getId();
        this.no = no;
        this.createTime = DateHelper.toTick(new Date());
        this.email = email;
        this.tos = new ArrayList<String>(tos);
        this.subject = subject;
        this.content = content;
        this.result = result;
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

    public String getEmail() {
        return email;
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

    public void setAttachmentNames(List<String> attachmentNames) {
        this.attachmentNames = new ArrayList<String>(attachmentNames);
    }

    public String getResult() {
        return result;
    }

    public EmailInterceptionTask getParent() {
        return ServiceManager.getEmailInterceptionService().getEmailInterceptionTaskById(this.parentId);
    }

    @Override
    public String toString() {
        return String.format("[%s] SentEmailEvent{no=%s, email='%s', tos=%s, subject='%s', content='%s', attachmentNames=%s, result='%s'}", DateHelper.toString(createTime), no, email, tos, subject, content, attachmentNames, result);
    }
}
