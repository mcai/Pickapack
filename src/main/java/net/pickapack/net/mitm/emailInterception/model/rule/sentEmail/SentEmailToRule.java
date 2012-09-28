package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class SentEmailToRule implements SentEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String to;

    public SentEmailToRule() {
    }

    public SentEmailToRule(StringMatchType matchType, String to) {
        this.matchType = matchType;
        this.to = to;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (String to : sentEmailEvent.getTos()) {
            if(StringMatcher.matches(to, this.to, this.matchType)) {
                return false;
            }
        }

        return true;
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("SentEmailToRule{matchType=%s, to='%s'}", matchType, to);
    }
}
