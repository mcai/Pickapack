package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;

public class SentEmailAttachmentNameRule implements SentEmailRule {
    private String attachmentName;

    public SentEmailAttachmentNameRule() {
    }

    public SentEmailAttachmentNameRule(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (String attachmentName : sentEmailEvent.getAttachmentNames()) {
            if (attachmentName != null && attachmentName.equals(this.attachmentName)) {
                return false;
            }
        }

        return true;
    }
}
