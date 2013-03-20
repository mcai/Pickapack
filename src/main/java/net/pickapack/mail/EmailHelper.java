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
package net.pickapack.mail;

import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Min Cai
 */
public class EmailHelper {
    private static Random random = new Random();

    /**
     *
     * @return
     */
    public static String generateEmailPrefix() {
        return (char) (97 + random.nextInt(122 - 97)) + UUID.randomUUID().toString().substring(0, 5 + random.nextInt(10)).replaceAll("-", "");
    }

    /**
     *
     * @param email
     * @return
     */
    public static String getEmailDomain(String email) {
        int i = email.indexOf("@");
        return i == -1 ? null : email.substring(i + 1);
    }

    /**
     *
     * @param email
     * @return
     */
    public static String getUserId(String email) {
        int i = email.indexOf("@");
        return i == -1 ? null : email.substring(0, i);
    }
}
