package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.OrRule;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SentEmailOrRule implements OrRule, SentEmailRule {
    @ElementList
    private ArrayList<SentEmailRule> rules;

    public SentEmailOrRule() {
    }

    public SentEmailOrRule(SentEmailRule... rules) {
        this(Arrays.asList(rules));
    }

    public SentEmailOrRule(List<SentEmailRule> rules) {
        this.rules = new ArrayList<SentEmailRule>(rules);
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
