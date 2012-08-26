package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;

public class SentEmailFromDomainRule implements SentEmailRule {
    private String toDomain;

    public SentEmailFromDomainRule(String toDomain) {
        this.toDomain = toDomain;
    }

    public String getToDomain() {
        return toDomain;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (String to : sentEmailEvent.getTos()) {
            if (to != null && to.contains(this.toDomain)) { //TODO: toDomain, EmailHelper
                return false;
            }
        }

        return true;
    }
}
