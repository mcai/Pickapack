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

import net.pickapack.im.sink.MessageSink;

/**
 * Basic message channel.
 *
 * @author Min Cai
 */
public class BasicMessageChannel extends AbstractMessageChannel {
    private boolean open;
    private String userId;

    /**
     * Create a basic message channel.
     *
     * @param userId                     the user ID
     * @param sink                       the message sink
     * @param checkReceivedMessagePeriod the period in seconds to check for received messages
     */
    public BasicMessageChannel(String userId, MessageSink sink, long checkReceivedMessagePeriod) {
        super(sink, checkReceivedMessagePeriod);
        this.userId = userId;
    }

    @Override
    public void open() {
        super.open();

        this.open = true;

        Thread threadReceive = new Thread() {
            @Override
            public void run() {
                receive();
            }
        };
        threadReceive.setDaemon(true);
        threadReceive.start();
    }

    @Override
    public void send(String to, Object obj) {
        if (this.open) {
            sendObj(this.userId, to, obj);
        }
    }

    private void receive() {
        while (this.open) {
            this.receiveBatch(this.userId);

            try {
                Thread.sleep(this.checkReceivedMessagePeriod);
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    @Override
    public void close() {
        super.close();
    }

    /**
     * Get a value indicating whether the channel is open or not.
     *
     * @return a value indicating whether the channel is open or not
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * Get the channel's user ID.
     *
     * @return the channel's user ID
     */
    public String getUserId() {
        return userId;
    }
}