package net.pickapack.net.mitm.emailInterception.model.rule.sentEmail;

import net.pickapack.net.mitm.emailInterception.model.event.SentEmailEvent;
import net.pickapack.net.mitm.emailInterception.model.rule.common.AndRule;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SentEmailAndRule implements AndRule, SentEmailRule {
    @ElementList
    private ArrayList<SentEmailRule> rules;

    public SentEmailAndRule() {
    }

    public SentEmailAndRule(SentEmailRule... rules) {
        this(Arrays.asList(rules));
    }

    public SentEmailAndRule(List<SentEmailRule> rules) {
        this.rules = new ArrayList<SentEmailRule>(rules);
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

    @Override
    public String toString() {
        return String.format("SentEmailAndRule{rules=%s}", rules);
    }
}
