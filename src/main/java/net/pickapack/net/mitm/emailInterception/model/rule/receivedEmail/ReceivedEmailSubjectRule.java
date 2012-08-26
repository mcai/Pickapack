package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;

public class ReceivedEmailSubjectRule implements ReceivedEmailRule {
    private String subject;

    public ReceivedEmailSubjectRule(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return receivedEmailEvent.getSubject() == null || !receivedEmailEvent.getSubject().contains(this.subject);
    }
}
