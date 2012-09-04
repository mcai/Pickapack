package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class ReceivedEmailSubjectRule implements ReceivedEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String subject;

    public ReceivedEmailSubjectRule() {
    }

    public ReceivedEmailSubjectRule(StringMatchType matchType, String subject) {
        this.matchType = matchType;
        this.subject = subject;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return !StringMatcher.matches(receivedEmailEvent.getSubject(), this.subject, this.matchType);
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getSubject() {
        return subject;
    }
}
