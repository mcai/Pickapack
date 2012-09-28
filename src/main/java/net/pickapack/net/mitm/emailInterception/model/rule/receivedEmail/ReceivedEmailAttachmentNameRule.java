package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.util.StringMatchType;
import net.pickapack.util.StringMatcher;
import org.simpleframework.xml.Attribute;

public class ReceivedEmailAttachmentNameRule implements ReceivedEmailRule {
    @Attribute
    private StringMatchType matchType;

    @Attribute
    private String attachmentName;

    public ReceivedEmailAttachmentNameRule() {
    }

    public ReceivedEmailAttachmentNameRule(StringMatchType matchType, String attachmentName) {
        this.matchType = matchType;
        this.attachmentName = attachmentName;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        for (String attachmentName : receivedEmailEvent.getAttachmentNames()) {
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
        return String.format("ReceivedEmailAttachmentNameRule{matchType=%s, attachmentName='%s'}", matchType, attachmentName);
    }
}
