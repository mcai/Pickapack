package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;

public class ReceivedEmailFromDomainRule implements ReceivedEmailRule {
    private String fromDomain;

    public ReceivedEmailFromDomainRule(String fromDomain) {
        this.fromDomain = fromDomain;
    }

    public String getFromDomain() {
        return fromDomain;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return receivedEmailEvent.getFrom() == null || !receivedEmailEvent.getFrom().contains(this.fromDomain); //TODO: fromDomain, EmailHelper
    }
}
