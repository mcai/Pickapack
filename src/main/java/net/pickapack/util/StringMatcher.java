package net.pickapack.util;

/**
 *
 * @author Min Cai
 */
public class StringMatcher {
    /**
     *
     * @param text
     * @param pattern
     * @param matchType
     * @return
     */
    public static boolean matches(String text, String pattern, StringMatchType matchType) {
        if (text == null || pattern == null) {
            return false;
        }

        switch (matchType) {
            case PLAIN:
                return text.contains(pattern);
            case WILDCARD:
                return wildCardMatch(text, pattern);
            case REGEX:
                return text.matches(pattern);
            default:
                throw new IllegalArgumentException();
        }
    }

    private static boolean wildCardMatch(String text, String pattern) {
        String[] cards = pattern.split("\\*");

        for (String card : cards) {
            int idx = text.indexOf(card);

            if (idx == -1) {
                return false;
            }

            text = text.substring(idx + card.length());
        }

        return true;
    }
}
