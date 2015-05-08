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
package net.pickapack.im.sink;

import java.io.Serializable;
import java.util.Set;

/**
 * Message sink.
 *
 * @author Min Cai
 */
public interface MessageSink extends Serializable {
    /**
     * Get the list of user IDs.
     *
     * @return the list of user IDs
     */
    Set<String> getUserIds();

    /**
     * Send a message.
     *
     * @param fromUserId the sender's user ID
     * @param toUserId   the receiver's user ID
     * @param message    the message to be sent
     */
    void send(String fromUserId, String toUserId, String message);

    /**
     * Receive a message for the specified user ID.
     *
     * @param userId the user ID
     * @return a newly arrived message for the specified user ID, if any exists
     */
    String receive(String userId);

    /**
     * Predefined server user ID.
     */
    public static String USER_ID_SERVER = "#server";
}
