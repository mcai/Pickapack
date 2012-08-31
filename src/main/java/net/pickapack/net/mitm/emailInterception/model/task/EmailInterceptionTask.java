package net.pickapack.net.mitm.emailInterception.model.task;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import net.pickapack.dateTime.DateHelper;
import net.pickapack.model.ModelElement;
import net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail.ReceivedEmailRule;
import net.pickapack.net.mitm.emailInterception.model.rule.sentEmail.SentEmailRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<ReceivedEmailRule> receivedEmailRules;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<SentEmailRule> sentEmailRules;

    public EmailInterceptionTask() {
    }

    public EmailInterceptionTask(List<ReceivedEmailRule> receivedEmailRules, List<SentEmailRule> sentEmailRules) {
        this.createTime = DateHelper.toTick(new Date());
        this.receivedEmailRules = new ArrayList<ReceivedEmailRule>(receivedEmailRules);
        this.sentEmailRules = new ArrayList<SentEmailRule>(sentEmailRules);
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
        return ""; //TODO
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

    public List<ReceivedEmailRule> getReceivedEmailRules() {
        return receivedEmailRules;
    }

    public List<SentEmailRule> getSentEmailRules() {
        return sentEmailRules;
    }
}
