package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;

public class SentEmailSubjectRule implements SentEmailRule {
    private String subject;

    public SentEmailSubjectRule() {
    }

    public SentEmailSubjectRule(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        return sentEmailEvent.getSubject() == null || !sentEmailEvent.getSubject().contains(this.subject);
    }
}
