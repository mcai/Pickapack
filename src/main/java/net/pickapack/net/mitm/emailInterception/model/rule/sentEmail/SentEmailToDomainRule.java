package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.mail.EmailHelper;
import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class SentEmailToDomainRule implements SentEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String toDomain;

    public SentEmailToDomainRule() {
    }

    public SentEmailToDomainRule(StringMatchType matchType, String toDomain) {
        this.matchType = matchType;
        this.toDomain = toDomain;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (String to : sentEmailEvent.getTos()) {
            if(StringMatcher.matches(EmailHelper.getEmailDomain(to), this.toDomain, this.matchType)) {
                return false;
            }
        }

        return true;
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getToDomain() {
        return toDomain;
    }

    @Override
    public String toString() {
        return String.format("SentEmailToDomainRule{matchType=%s, toDomain='%s'}", matchType, toDomain);
    }
}
