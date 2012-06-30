package net.pickapack.net.mitm.emailInterception.model.rule.receivedEmail;

import net.pickapack.net.mitm.emailInterception.model.event.ReceivedEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.OrRule;

import java.util.Arrays;
import java.util.List;

public class ReceivedEmailOrRule implements OrRule, ReceivedEmailRule {
    private List<ReceivedEmailRule> rules;

    public ReceivedEmailOrRule() {
    }

    public ReceivedEmailOrRule(ReceivedEmailRule... rules) {
        this(Arrays.asList(rules));
    }

    public ReceivedEmailOrRule(List<ReceivedEmailRule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean apply(ReceivedEmailEvent receivedEmailEvent) {
        for (ReceivedEmailRule rule : this.rules) {
            if (rule.apply(receivedEmailEvent)) {
                return true;
            }
        }

        return false;
    }

    public List<ReceivedEmailRule> getRules() {
        return rules;
    }
}
