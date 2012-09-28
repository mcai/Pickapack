package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class SentEmailContentRule implements SentEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String content;

    public SentEmailContentRule() {
    }

    public SentEmailContentRule(StringMatchType matchType, String content) {
        this.matchType = matchType;
        this.content = content;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        return !StringMatcher.matches(sentEmailEvent.getContent(), this.content, this.matchType);
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("SentEmailContentRule{matchType=%s, content='%s'}", matchType, content);
    }
}
