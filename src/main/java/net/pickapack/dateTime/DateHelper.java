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
package net.pickapack.dateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Min Cai
 */
public class DateHelper {
    /**
     *
     * @return
     */
    public static long toTick() {
        return toTick(new Date());
    }

    /**
     *
     * @param time
     * @return
     */
    public static long toTick(Date time) {
        return time.getTime();
    }

    /**
     *
     * @param tick
     * @return
     */
    public static Date fromTick(long tick) {
        return new Date(tick);
    }

    /**
     *
     * @param tick
     * @return
     */
    public static String toString(long tick) {
        return toString(fromTick(tick));
    }

    /**
     *
     * @param date
     * @return
     */
    public static String toString(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
    }

    /**
     *
     * @param date
     * @return
     */
    public static String toFileNameString(Date date) {
        return new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(date);
    }
}
