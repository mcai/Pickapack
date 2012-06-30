package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.AndRule;

import java.util.Arrays;
import java.util.List;

public class ReceivedEmailAndRule implements AndRule, ReceivedEmailRule {
    private List<ReceivedEmailRule> rules;

    public ReceivedEmailAndRule() {
    }

    public ReceivedEmailAndRule(ReceivedEmailRule... rules) {
        this(Arrays.asList(rules));
    }

    public ReceivedEmailAndRule(List<ReceivedEmailRule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        for (ReceivedEmailRule rule : this.rules) {
            if (!rule.apply(receivedEmailEvent)) {
                return false;
            }
        }

        return true;
    }

    public List<ReceivedEmailRule> getRules() {
        return rules;
    }
}
