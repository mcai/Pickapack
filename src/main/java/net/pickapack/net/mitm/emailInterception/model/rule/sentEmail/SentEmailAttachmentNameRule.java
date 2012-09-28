package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class SentEmailAttachmentNameRule implements SentEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String attachmentName;

    public SentEmailAttachmentNameRule() {
    }

    public SentEmailAttachmentNameRule(StringMatchType matchType, String attachmentName) {
        this.matchType = matchType;
        this.attachmentName = attachmentName;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (String attachmentName : sentEmailEvent.getAttachmentNames()) {
            if(StringMatcher.matches(attachmentName, this.attachmentName, this.matchType)) {
                return false;
            }
        }

        return true;
    }

    public StringMatchType getMatchType() {
        return matchType;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    @Override
    public String toString() {
        return String.format("SentEmailAttachmentNameRule{matchType=%s, attachmentName='%s'}", matchType, attachmentName);
    }
}
