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

import net.pickapack.dateTime.DateHelper;
import net.pickapack.im.sink.InstantMessage;
import net.pickapack.im.sink.MessageSink;
import net.pickapack.io.serialization.JsonSerializationHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Abstract message channel.
 *
 * @author Min Cai
 */
public abstract class AbstractMessageChannel implements MessageChannel {
    /**
     * Message sink.
     */
    protected MessageSink sink;

    /**
     * The period in seconds to check for received messages.
     */
    protected long checkReceivedMessagePeriod;

    private final List<MessagingListener> listeners;

    /**
     * Create an abstract message channel.
     *
     * @param sink                       the message sink
     * @param checkReceivedMessagePeriod the period in seconds to check for received messages
     */
    public AbstractMessageChannel(MessageSink sink, long checkReceivedMessagePeriod) {
        this.checkReceivedMessagePeriod = checkReceivedMessagePeriod;
        this.sink = sink;

        this.listeners = new ArrayList<MessagingListener>();
    }

    @Override
    public void addMessagingListener(MessagingListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeMessagingListener(MessagingListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }

    /**
     * Send an object from the specified sender to the receiver.
     *
     * @param fromUserId the sender's user ID
     * @param toUserId   the receiver's user ID
     * @param obj        the object to be sent
     */
    protected void sendObj(String fromUserId, String toUserId, Object obj) {
        this.sink.send(fromUserId, toUserId, JsonSerializationHelper.serialize(new JsonSerializationHelper.ObjectWrapper(obj.getClass().getName(), obj)));
    }

    /**
     * Send an object.
     *
     * @param message the object to be sent
     */
    public void send(Object message) {
        this.send(MessageSink.USER_ID_SERVER, message);
    }

    /**
     * Fire an instant message is received.
     *
     * @param instantMessage the instant message that is received
     */
    protected void fireMessageReceived(InstantMessage instantMessage) {
        JsonSerializationHelper.ObjectWrapper objectWrapper = JsonSerializationHelper.deserialize(JsonSerializationHelper.ObjectWrapper.class, instantMessage.getBody());

        for (MessagingListener listener : listeners) {
            listener.messageReceived(instantMessage.getFromUserId(), objectWrapper.getObj());
        }
    }

    /**
     * Receive one message for the specified user ID.
     *
     * @param userId the user ID
     * @return a value indicating whether a message has been received for the specified user
     */
    protected boolean receiveOne(String userId) {
        String str;
        if ((str = this.sink.receive(userId)) != null) {
            this.fireMessageReceived(JsonSerializationHelper.deserialize(InstantMessage.class, str));
            return true;
        }

        System.out.printf("[%s Message Channel] No new message for user %s\n", DateHelper.toString(new Date()), userId);

        return false;
    }

    /**
     * Receive a batch of messages for the specified user ID.
     *
     * @param userId the user ID
     */
    protected void receiveBatch(String userId) {
        for (; receiveOne(userId); ) ;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
        this.listeners.clear();
    }
}