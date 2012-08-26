package net.pickapack.net.mitm.emailInterception.service;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.task.EmailInterceptionTask;
import net.pickapack.service.Service;

import java.sql.SQLException;
import java.util.List;

public interface EmailInterceptionService extends Service {
    List<EmailInterceptionTask> getEmailInterceptionTasks() throws SQLException;

    EmailInterceptionTask getEmailInterceptionTaskById(long id) throws SQLException;

    void addEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask) throws SQLException;

    void removeEmailInterceptionTaskById(long id) throws SQLException;

    void updateEmailInterceptionTask(EmailInterceptionTask emailInterceptionTask) throws SQLException;

    List<ReceivedEmailEvent> getReceivedEmailEvents() throws SQLException;

    ReceivedEmailEvent getReceivedEmailEventById(long id) throws SQLException;

    void addReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent) throws SQLException;

    void removeReceivedEmailEventById(long id) throws SQLException;

    void updateReceivedEmailEvent(ReceivedEmailEvent receivedEmailEvent) throws SQLException;

    List<SentEmailEvent> getSentEmailEvents() throws SQLException;

    SentEmailEvent getSentEmailEventById(long id) throws SQLException;

    void addSentEmailEvent(SentEmailEvent sentEmailEvent) throws SQLException;

    void removeSentEmailEventById(long id) throws SQLException;

    void updateSentEmailEvent(SentEmailEvent sentEmailEvent) throws SQLException;
}
