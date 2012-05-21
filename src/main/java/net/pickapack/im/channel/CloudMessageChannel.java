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

import net.pickapack.action.Action1;
import net.pickapack.event.BlockingEventDispatcher;
import net.pickapack.im.event.CloudEvent;
import net.pickapack.im.sink.MessageSink;

public class CloudMessageChannel extends BasicMessageChannel {
    private BlockingEventDispatcher<CloudEvent> cloudEventDispatcher;

    public CloudMessageChannel(String userId, MessageSink sink) {
        super(userId, sink, 5000);

        this.cloudEventDispatcher = new BlockingEventDispatcher<CloudEvent>();

        this.addMessagingListener(new MessagingListener() {
            @Override
            public void messageReceived(String from, Object obj) {
                if (obj instanceof CloudEvent) {
                    cloudEventDispatcher.dispatch((CloudEvent) obj);
                }
            }
        });
    }

    public <CloudEventT extends CloudEvent> void addCloudEventListener(Class<CloudEventT> eventClass, Action1<CloudEventT> listener) {
        this.cloudEventDispatcher.addListener(eventClass, listener);
    }

    public <CloudEventT extends CloudEvent> void removeCloudEventListener(Class<CloudEventT> eventClass, Action1<CloudEventT> listener) {
        this.cloudEventDispatcher.removeListener(eventClass, listener);
    }
}