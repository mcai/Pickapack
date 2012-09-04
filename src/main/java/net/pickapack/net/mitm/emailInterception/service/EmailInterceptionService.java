package net.pickapack.net.mitm.emailInterception.service;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.service.Service;

import java.util.List;

public interface EmailInterceptionService extends Service {
    List<EmailInterceptionTask> getEmailInterceptionTasks();

    EmailInterceptionTask getEmailInterceptionTaskById(long id);

    void addEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask);

    void removeEmailInterceptionTaskById(long id);

    void updateEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask);

    List<ReceivedEmailEvent> getReceivedEmailEvents();

    ReceivedEmailEvent getReceivedEmailEventById(long id);

    void addReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent);

    void removeReceivedEmailEventById(long id);

    void updateReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent);

    List<SentEmailEvent> getSentEmailEvents();

    SentEmailEvent getSentEmailEventById(long id);

    void addSentEmailEvent(SentEmailEvent sentEmailEvent);

    void removeSentEmailEventById(long id);

    void updateSentEmailEvent(SentEmailEvent sentEmailEvent);
}
