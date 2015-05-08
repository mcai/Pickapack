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

/**
 * Instant message.
 *
 * @author Min Cai
 */
public class InstantMessage {
    private Long id;
    private String fromUserId;
    private String body;

    /**
     * Create an instant message.
     *
     * @param fromUserId the sender's user ID
     * @param body       the body of the message
     */
    public InstantMessage(String fromUserId, String body) {
        this.id = currentId++;
        this.fromUserId = fromUserId;
        this.body = body;
    }

    /**
     * Get the ID of the message.
     *
     * @return the ID of the message
     */
    public Long getId() {
        return id;
    }

    /**
     * Get the sender's user ID.
     *
     * @return the sender's user ID
     */
    public String getFromUserId() {
        return fromUserId;
    }

    /**
     * Get the body of the message.
     *
     * @return the body of the message
     */
    public String getBody() {
        return body;
    }

    private static Long currentId = 0L;
}