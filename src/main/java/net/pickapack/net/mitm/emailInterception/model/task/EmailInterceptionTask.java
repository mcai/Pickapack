package net.pickapack.net.mitm.emailInterception.model.task;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.model.ModelElement;
import net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail.ReceivedEmailRule;
import net.pickapack.net.mitm.emailInterception.model.rule.sentEmail.SentEmailRule;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.Date;

@Root
@DatabaseTable(tableName = "EmailInterceptionTask")
public class EmailInterceptionTask implements ModelElement {
    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private long createTime;

    @DatabaseField
    private long beginTime;

    @DatabaseField
    private long endTime;

    @Attribute
    @DatabaseField
    private int port;

    @Element
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ReceivedEmailRule receivedEmailRule;

    @Element
    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private SentEmailRule sentEmailRule;

    public EmailInterceptionTask() {
    }

    public EmailInterceptionTask(ReceivedEmailRule receivedEmailRule, SentEmailRule sentEmailRule) {
        this.createTime = DateHelper.toTick(new Date());
        this.receivedEmailRule = receivedEmailRule;
        this.sentEmailRule = sentEmailRule;
        this.port = 3737;
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
        return "email interception task #" + id + " @ " + DateHelper.toString(this.beginTime) + " - " + DateHelper.toString(this.endTime);
    }

    public long getCreateTime() {
        return createTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ReceivedEmailRule getReceivedEmailRule() {
        return receivedEmailRule;
    }

    public SentEmailRule getSentEmailRule() {
        return sentEmailRule;
    }
}
