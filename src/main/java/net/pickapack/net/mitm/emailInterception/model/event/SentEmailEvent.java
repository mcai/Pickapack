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
    private long parentId;

    @DatabaseField
    private long createTime;

    @DatabaseField
    private String email;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> tos;

    @DatabaseField
    private String subject;

    @DatabaseField
    private String content;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> attachmentNames;

    private String result;

    public SentEmailEvent() {
    }

    public SentEmailEvent(EmailInterceptionTask parent, long id, String email, List<String> tos, String subject, String content, List<String> attachmentNames, String result) {
        this.parentId = parent.getId();
        this.id = id;
        this.createTime = DateHelper.toTick(new Date());
        this.email = email;
        this.tos = new ArrayList<String>(tos);
        this.subject = subject;
        this.content = content;
        this.attachmentNames = new ArrayList<String>(attachmentNames);
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    @Override
    public String getTitle() {
        return "sent email event #" + id + " @ " + DateHelper.toString(this.createTime);
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

    public String getResult() {
        return result;
    }

    public EmailInterceptionTask getParent() {
        return ServiceManager.getEmailInterceptionService().getEmailInterceptionTaskById(this.parentId);
    }
}
