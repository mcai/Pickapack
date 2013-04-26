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
package net.pickapack.im.channel;

/**
 * Message channel.
 *
 * @author Min Cai
 */
public interface MessageChannel {
    /**
     * Open the channel.
     */
    void open();

    /**
     * Send an object to the specified receiver.
     *
     * @param to  the receiver
     * @param obj the object to send
     */
    void send(String to, Object obj);

    /**
     * Close the channel.
     */
    void close();

    /**
     * Add the specified messaging listener to the channel.
     *
     * @param listener the messaging listener to be added
     */
    void addMessagingListener(MessagingListener listener);

    /**
     * Remove the specified messaging listener from the channel.
     *
     * @param listener the messaging listener to be removed
     */
    void removeMessagingListener(MessagingListener listener);
}
