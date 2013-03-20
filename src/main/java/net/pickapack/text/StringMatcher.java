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
