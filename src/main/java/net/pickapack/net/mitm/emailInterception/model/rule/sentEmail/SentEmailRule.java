package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.EmailInterceptionRule;

public interface SentEmailRule extends EmailInterceptionRule {
    boolean apply(SentEmailEvent sentEmailEvent);
}
