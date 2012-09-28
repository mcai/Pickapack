package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;

public class ReceivedEmailSubjectIsNullOrEmptyRule implements ReceivedEmailRule {
    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        String subject = receivedEmailEvent.getSubject();
        return subject != null && !subject.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("ReceivedEmailSubjectIsNullOrEmptyRule");
    }
}
