package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.EmailInterceptionRule;

public interface ReceivedEmailRule extends EmailInterceptionRule {
    boolean apply(ReceivedEmailEvent receivedEmailEvent);
}

