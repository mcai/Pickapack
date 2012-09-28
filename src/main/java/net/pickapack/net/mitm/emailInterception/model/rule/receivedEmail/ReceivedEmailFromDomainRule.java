package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.mail.EmailHelper;
import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class ReceivedEmailFromDomainRule implements ReceivedEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String fromDomain;

    public ReceivedEmailFromDomainRule() {
    }

    public ReceivedEmailFromDomainRule(StringMatchType matchType, String fromDomain) {
        this.matchType = matchType;
        this.fromDomain = fromDomain;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return !StringMatcher.matches(EmailHelper.getEmailDomain(receivedEmailEvent.getFrom()), this.fromDomain, this.matchType);
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getFromDomain() {
        return fromDomain;
    }

    @Override
    public String toString() {
        return String.format("ReceivedEmailFromDomainRule{matchType=%s, fromDomain='%s'}", matchType, fromDomain);
    }
}
