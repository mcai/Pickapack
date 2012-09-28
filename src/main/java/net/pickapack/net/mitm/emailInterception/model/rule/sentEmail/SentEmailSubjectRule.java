package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class SentEmailSubjectRule implements SentEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String subject;

    public SentEmailSubjectRule() {
    }

    public SentEmailSubjectRule(StringMatchType matchType, String subject) {
        this.matchType = matchType;
        this.subject = subject;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        return !StringMatcher.matches(sentEmailEvent.getSubject(), this.subject, this.matchType);
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return String.format("SentEmailSubjectRule{matchType=%s, subject='%s'}", matchType, subject);
    }
}
