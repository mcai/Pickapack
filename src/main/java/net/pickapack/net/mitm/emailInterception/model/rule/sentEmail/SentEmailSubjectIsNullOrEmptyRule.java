package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;

public class SentEmailSubjectIsNullOrEmptyRule implements SentEmailRule {
    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        return sentEmailEvent.getSubject() != null && !sentEmailEvent.getSubject().isEmpty();
    }
}
