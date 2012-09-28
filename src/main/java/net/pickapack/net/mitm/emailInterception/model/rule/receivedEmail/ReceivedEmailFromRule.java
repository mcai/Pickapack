package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class ReceivedEmailFromRule implements ReceivedEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String from;

    public ReceivedEmailFromRule() {
    }

    public ReceivedEmailFromRule(StringMatchType matchType, String from) {
        this.matchType = matchType;
        this.from = from;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return !StringMatcher.matches(receivedEmailEvent.getFrom(), this.from, this.matchType);
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return String.format("ReceivedEmailFromRule{matchType=%s, from='%s'}", matchType, from);
    }
}
