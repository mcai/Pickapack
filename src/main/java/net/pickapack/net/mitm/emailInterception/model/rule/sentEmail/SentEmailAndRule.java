package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.AndRule;

import java.util.List;

public class SentEmailAndRule implements AndRule, SentEmailRule {
    private List<SentEmailRule> rules;

    public SentEmailAndRule(List<SentEmailRule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (SentEmailRule rule : this.rules) {
            if (!rule.apply(sentEmailEvent)) {
                return false;
            }
        }

        return true;
    }

    public List<SentEmailRule> getRules() {
        return rules;
    }
}
