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

import java.util.concurrent.PriorityBlockingQueue;

public final class CycleAccurateEventQueue {
    private long currentCycle;
    private PriorityBlockingQueue<CycleAccurateEvent> events;

    public CycleAccurateEventQueue() {
        this.events = new PriorityBlockingQueue<CycleAccurateEvent>();
    }

    public void advanceOneCycle() {
        while (!this.events.isEmpty()) {
            CycleAccurateEvent event = this.events.peek();

            if (event.getWhen() > this.currentCycle) {
                break;
            }

            event.getAction().apply();
            this.events.remove(event);
        }

        this.currentCycle++;
    }

    public CycleAccurateEventQueue schedule(Object sender, Action action, int delay) {
        this.schedule(new CycleAccurateEvent(sender, action, this.currentCycle + delay));
        return this;
    }

    private void schedule(CycleAccurateEvent event) {
        this.events.add(event);
    }

    public void resetCurrentCycle() {
        for (CycleAccurateEvent event : this.events) {
            event.setWhen(event.getWhen() - this.currentCycle);
        }

        this.currentCycle = 0;
    }

    public long getCurrentCycle() {
        return this.currentCycle;
    }

    @Override
    public String toString() {
        return String.format("CycleAccurateEventQueue{currentCycle=%d, events=%s}", currentCycle, events);
    }
}
