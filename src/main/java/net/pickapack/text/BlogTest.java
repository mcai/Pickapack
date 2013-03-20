/*******************************************************************************
 * Copyright (c) 2010-2012 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the PickaPack library.
 *
 * PickaPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PickaPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PickaPack. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package net.pickapack.text;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.parserunners.ParseRunner;
import org.parboiled.parserunners.TracingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

/**
 *
 * @author Min Cai
 */
@BuildParseTree
public class BlogTest extends BaseParser<Object> {
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        parse("Person($expand=location($current))");
    }

    private static BlogTest parser = Parboiled.createParser(BlogTest.class);
    /**
     *
     */
    public static boolean RESULT_TREE_ON = true;
    /**
     *
     */
    public static boolean STDOUT = true;

    /**
     *
     * @param str
     */
    public static void parse(String str) {
        ParseRunner runner = new TracingParseRunner(parser.Query());
        ParsingResult<?> result = runner.run(str);
        if (result.hasErrors()) {
            String errorMessage = printParseErrors(result);
            if (STDOUT) {
                System.out.println("\nParse Errors:\n" + errorMessage);
            }
        }
        if (RESULT_TREE_ON) {
            System.out.println(str + " ===>  " + ParseTreeUtils.printNodeTree(result));
        }
        if (STDOUT) {
            System.out.println("Parse String = " + str);
            QueryAst query = (QueryAst) result.resultValue;
            query.print();
        }

    }

    Rule Query() {
        // This matches the Person($expand=location($current=true))
        return Sequence(
                Word(),
                matchedQueryName(),
                Optional(
                        Sequence(
                                "(",
                                Expand(),
                                ")"
                        )
                )
        );
    }

    /**
     *
     * @return
     */
    protected boolean matchedQueryName() {
        QueryAst query = new QueryAst();
        query.name = match();
        return push(query);
    }

    Rule Word() {
        return OneOrMore(
                LetterOrDigit()
        );
    }

    Rule LetterOrDigit() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }

    Rule Expand() {
        // This matches the $expand=location($current=true)
        return Sequence(
                "$expand=",
                Word(),
                matchedExpand(),
                Optional(
                        "(",
                        Optional(
                                Current()
                        ),
                        ")"
                ),
                popExpandAst()
        );
    }

    /**
     *
     * @return
     */
    protected boolean matchedExpand() {
        ExpandAst expand = new ExpandAst();
        expand.name = match();
        return push(expand);
    }

    /**
     *
     * @return
     */
    protected boolean popExpandAst() {
        ExpandAst expand = (ExpandAst) pop();
        QueryAst query = (QueryAst) peek();
        query.expand = expand;
        return true;
    }

    Rule Current() {
        // This matches the $current
        return Sequence(
                "$current",
                currentSucceeded()
        );
    }

    /**
     *
     * @return
     */
    protected boolean currentSucceeded() {
        ExpandAst prop = (ExpandAst) peek();
        prop.current = true;
        return true;
    }

    static class QueryAst {
        public String name;
        public ExpandAst expand;

        public void print() {
            System.out.println("Query: name=" + name);
            if (expand == null) {
                System.out.println("expand == null");
            } else {
                expand.print();
            }
        }
    }

    static class ExpandAst {
        public String name;
        public boolean current;

        public void print() {
            System.out.println("Expand: name=" + name + " current=" + current);
        }
    }
}