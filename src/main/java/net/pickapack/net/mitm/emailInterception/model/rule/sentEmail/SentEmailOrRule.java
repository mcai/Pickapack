package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.OrRule;

import java.util.List;

public class SentEmailOrRule implements OrRule, SentEmailRule {
    private List<SentEmailRule> rules;

    public SentEmailOrRule(List<SentEmailRule> rules) {
        this.rules = rules;
    }

    @Override
    public boolean apply(SentEmailEvent sentEmailEvent) {
        for (SentEmailRule rule : this.rules) {
            if (rule.apply(sentEmailEvent)) {
                return true;
            }
        }

        return false;
    }

    public List<SentEmailRule> getRules() {
        return rules;
    }
}
