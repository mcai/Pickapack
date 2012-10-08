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
package net.pickapack.event;

import net.pickapack.action.Action;

/**
 *
 * @author Min Cai
 */
public class CycleAccurateEvent implements Comparable<CycleAccurateEvent> {
    private Object sender;
    private Action action;
    private long scheduledTime;
    private long when;
    private long id;

    /**
     *
     * @param parent
     * @param sender
     * @param action
     * @param when
     */
    public CycleAccurateEvent(CycleAccurateEventQueue parent, Object sender, Action action, long when) {
        this.id = parent.currentId++;
        this.sender = sender;
        this.action = action;
        this.when = when;
    }

    @Override
    public int compareTo(CycleAccurateEvent otherEvent) {
        if (this.when < otherEvent.when) {
            return -1;
        }
        else if (this.when == otherEvent.when) {
            if (this.id < otherEvent.id) {
                return -1;
            }
            else if (this.id == otherEvent.id) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CycleAccurateEvent && this.id == ((CycleAccurateEvent) o).id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    /**
     *
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public long getScheduledTime() {
        return scheduledTime;
    }

    /**
     *
     * @param scheduledTime
     */
    public void setScheduledTime(long scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    /**
     *
     * @return
     */
    public long getWhen() {
        return when;
    }

    /**
     *
     * @param when
     */
    public void setWhen(long when) {
        this.when = when;
    }

    /**
     *
     * @return
     */
    public Action getAction() {
        return action;
    }

    /**
     *
     * @return
     */
    public Object getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s: %s", when, sender, action);
    }
}
