package net.pickapack.net.mitm.emailInterception.service;

import com.j256.ormlite.dao.Dao;
import net.pickapack.model.ModelElement;
import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.service.AbstractService;

import java.util.Arrays;
import java.util.List;

public class EmailInterceptionServiceImpl extends AbstractService implements EmailInterceptionService {
    private Dao<EmailInterceptionTask, Long> emailInterceptionTasks;
    private Dao<ReceivedEmailEvent, Long> receivedEmailEvents;
    private Dao<SentEmailEvent, Long> sentEmailEvents;

    @SuppressWarnings("unchecked")
    public EmailInterceptionServiceImpl(){
        super(ServiceManager.DATABASE_URL, Arrays.<Class<? extends ModelElement>>asList(EmailInterceptionTask.class, ReceivedEmailEvent.class, SentEmailEvent.class));

        this.emailInterceptionTasks = createDao(EmailInterceptionTask.class);
        this.receivedEmailEvents = createDao(ReceivedEmailEvent.class);
        this.sentEmailEvents = createDao(SentEmailEvent.class);
    }

    @Override
    public List<EmailInterceptionTask> getEmailInterceptionTasks() {
        return this.getAllItems(this.emailInterceptionTasks);
    }

    @Override
    public EmailInterceptionTask getEmailInterceptionTaskById(long id) {
        return this.getItemById(this.emailInterceptionTasks, id);
    }

    @Override
    public void addEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask) {
        this.addItem(this.emailInterceptionTasks, EmailInterceptionTask.class, emailInterceptionTask);
    }

    @Override
    public void removeEmailInterceptionTaskById(long id) {
        this.removeItemById(this.emailInterceptionTasks, EmailInterceptionTask.class, id);
    }

    @Override
    public void updateEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask) {
        this.updateItem(this.emailInterceptionTasks, EmailInterceptionTask.class, emailInterceptionTask);
    }

    @Override
    public List<ReceivedEmailEvent> getReceivedEmailEvents() {
        return this.getAllItems(this.receivedEmailEvents);
    }

    @Override
    public ReceivedEmailEvent getReceivedEmailEventById(long id) {
        return this.getItemById(this.receivedEmailEvents, id);
    }

    @Override
    public void addReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent) {
        this.addItem(this.receivedEmailEvents, ReceivedEmailEvent.class, receivedEmailEvent);
    }

    @Override
    public void removeReceivedEmailEventById(long id) {
        this.removeItemById(this.receivedEmailEvents, ReceivedEmailEvent.class, id);
    }

    @Override
    public void updateReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent) {
        this.updateItem(this.receivedEmailEvents, ReceivedEmailEvent.class, receivedEmailEvent);
    }

    @Override
    public List<SentEmailEvent> getSentEmailEvents() {
        return this.getAllItems(this.sentEmailEvents);
    }

    @Override
    public SentEmailEvent getSentEmailEventById(long id) {
        return this.getItemById(this.sentEmailEvents, id);
    }

    @Override
    public void addSentEmailEvent(SentEmailEvent sentEmailEvent) {
        this.addItem(this.sentEmailEvents, SentEmailEvent.class, sentEmailEvent);
    }

    @Override
    public void removeSentEmailEventById(long id) {
        this.removeItemById(this.sentEmailEvents, SentEmailEvent.class, id);
    }

    @Override
    public void updateSentEmailEvent(SentEmailEvent sentEmailEvent) {
        this.updateItem(this.sentEmailEvents, SentEmailEvent.class, sentEmailEvent);
    }
}
