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
import net.pickapack.JsonSerializationHelper;
import net.pickapack.im.sink.InstantMessage;
import net.pickapack.im.sink.MessageSink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Min Cai
 */
public abstract class AbstractMessageChannel implements MessageChannel {
    /**
     *
     */
    protected MessageSink sink;
    /**
     *
     */
    protected long checkReceivedMessagePeriod;
    private final List<MessagingListener> listeners;

    /**
     *
     * @param sink
     * @param checkReceivedMessagePeriod
     */
    public AbstractMessageChannel(MessageSink sink, long checkReceivedMessagePeriod) {
        this.checkReceivedMessagePeriod = checkReceivedMessagePeriod;
        this.sink = sink;

        this.listeners = new ArrayList<MessagingListener>();
    }

    /**
     *
     * @param listener
     */
    @Override
    public void addMessagingListener(MessagingListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }

    /**
     *
     * @param listener
     */
    @Override
    public void removeMessagingListener(MessagingListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }

    /**
     *
     * @param fromUserId
     * @param toUserId
     * @param obj
     */
    protected void sendObj(String fromUserId, String toUserId, Object obj) {
        this.sink.send(fromUserId, toUserId, JsonSerializationHelper.serialize(new JsonSerializationHelper.ObjectWrapper(obj.getClass().getName(), obj)));
    }

    /**
     *
     * @param message
     */
    public void send(Object message) {
        this.send(MessageSink.USER_ID_SERVER, message);
    }

    /**
     *
     * @param instantMessage
     */
    protected void fireMessageReceived(InstantMessage instantMessage) {
        JsonSerializationHelper.ObjectWrapper objectWrapper = JsonSerializationHelper.deserialize(JsonSerializationHelper.ObjectWrapper.class, instantMessage.getBody());

        for (MessagingListener listener : listeners) {
            listener.messageReceived(instantMessage.getFromUserId(), objectWrapper.getObj());
        }
    }

    /**
     *
     * @param userId
     * @return
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
     *
     * @param userId
     */
    protected void receiveBatch(String userId) {
        for (; receiveOne(userId); ) ;
    }

    /**
     *
     */
    @Override
    public void open() {
    }

    /**
     *
     */
    @Override
    public void close() {
        this.listeners.clear();
    }
}