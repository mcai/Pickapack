package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;

public class ReceivedEmailAttachmentNameRule implements ReceivedEmailRule {
    private String attachmentName;

    public ReceivedEmailAttachmentNameRule() {
    }

    public ReceivedEmailAttachmentNameRule(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        for (String attachmentName : receivedEmailEvent.getAttachmentNames()) {
            if (attachmentName != null && attachmentName.equals(this.attachmentName)) {
                return false;
            }
        }

        return true;
    }
}
