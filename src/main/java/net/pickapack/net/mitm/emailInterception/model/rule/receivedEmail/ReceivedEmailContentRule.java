package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class ReceivedEmailContentRule implements ReceivedEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String content;

    public ReceivedEmailContentRule() {
    }

    public ReceivedEmailContentRule(StringMatchType matchType, String content) {
        this.matchType = matchType;
        this.content = content;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        return !StringMatcher.matches(receivedEmailEvent.getContent(), this.content, this.matchType);
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("ReceivedEmailContentRule{matchType=%s, content='%s'}", matchType, content);
    }
}
