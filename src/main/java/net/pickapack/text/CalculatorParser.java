package net.pickapack.text;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author Min Cai
 */
@BuildParseTree
public class CalculatorParser extends BaseParser<Object> {
    /**
     *
     * @return
     */
    public Rule Expression() {
        return Sequence(
                Term(),
                ZeroOrMore(AnyOf("+-"), Term())
        );
    }

    /**
     *
     * @return
     */
    public Rule Term() {
        return Sequence(
                Factor(),
                ZeroOrMore(AnyOf("*/"), Factor())
        );
    }

    /**
     *
     * @return
     */
    public Rule Factor() {
        return FirstOf(
                Number(),
                Sequence('(', Expression(), ')')
        );
    }

    /**
     *
     * @return
     */
    public Rule Number() {
        return OneOrMore(CharRange('0', '9'));
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String input = "1+2";
        CalculatorParser parser = Parboiled.createParser(CalculatorParser.class);
        ParsingResult<?> result = new ReportingParseRunner(parser.Expression()).run(input);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        System.out.println(parseTreePrintOut);
    }
}