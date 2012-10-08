package net.pickapack.text;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.common.StringUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import java.util.Scanner;

import static org.parboiled.support.ParseTreeUtils.printNodeTree;

/**
 *
 * @author Min Cai
 */
@BuildParseTree
public class TimeParser extends BaseParser<Object> {
    /**
     *
     * @return
     */
    public Rule Time() {
        return FirstOf(Time_HH_MM_SS(), Time_HHMMSS(), Time_HMM());
    }

    Rule Time_HH_MM_SS() {
        return Sequence(
                OneOrTwoDigits(), ':',
                TwoDigits(),
                FirstOf(Sequence(':', TwoDigits()), push(0)),
                EOI,
                swap3() && push(convertToTime(popAsInt(), popAsInt(), popAsInt()))
        );
    }

    Rule Time_HHMMSS() {
        return Sequence(
                TwoDigits(),
                FirstOf(
                        Sequence(
                                TwoDigits(),
                                FirstOf(TwoDigits(), push(0))
                        ),
                        pushAll(0, 0)
                ),
                EOI,
                swap3() && push(convertToTime(popAsInt(), popAsInt(), popAsInt()))
        );
    }

    Rule Time_HMM() {
        return Sequence(
                OneDigit(),
                FirstOf(TwoDigits(), push(0)),
                EOI,
                swap() && push(convertToTime(popAsInt(), popAsInt()))
        );
    }

    Rule OneOrTwoDigits() {
        return FirstOf(TwoDigits(), OneDigit());
    }

    Rule OneDigit() {
        return Sequence(Digit(), push(Integer.parseInt(matchOrDefault("0"))));
    }

    Rule TwoDigits() {
        return Sequence(Sequence(Digit(), Digit()), push(Integer.parseInt(matchOrDefault("0"))));
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    /**
     *
     * @return
     */
    protected Integer popAsInt() {
        return (Integer) pop();
    }

    /**
     *
     * @param hours
     * @param minutes
     * @return
     */
    protected String convertToTime(Integer hours, Integer minutes) {
        return convertToTime(hours, minutes, 0);
    }

    /**
     *
     * @param hours
     * @param minutes
     * @param seconds
     * @return
     */
    protected String convertToTime(Integer hours, Integer minutes, Integer seconds) {
        return String.format("%s h, %s min, %s s",
                hours != null ? hours : 0,
                minutes != null ? minutes : 0,
                seconds != null ? seconds : 0);
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        TimeParser parser = Parboiled.createParser(TimeParser.class);

        while (true) {
            System.out.print("Enter a time expression (hh:mm(:ss)?, hh(mm(ss)?)? or h(mm)?, single RETURN to exit)!\n");
            String input = new Scanner(System.in).nextLine();
            if (StringUtils.isEmpty(input)) break;

            ParsingResult<?> result = new RecoveringParseRunner(parser.Time()).run(input);

            System.out.println(input + " = " + result.parseTreeRoot.getValue() + '\n');
        }
    }
}
