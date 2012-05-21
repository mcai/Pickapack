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
package net.pickapack.util.fsm;

import net.pickapack.util.action.Action1;
import net.pickapack.util.event.BlockingEventDispatcher;
import net.pickapack.util.fsm.event.EnterStateEvent;
import net.pickapack.util.fsm.event.ExitStateEvent;
import net.pickapack.util.fsm.event.FiniteStateMachineEvent;

import java.util.HashMap;
import java.util.Map;

public class BasicFiniteStateMachine<StateT, ConditionT> implements FiniteStateMachine<StateT,ConditionT> {
    private String name;
    private StateT state;

    private Map<Object, Object> properties;
    private BlockingEventDispatcher<FiniteStateMachineEvent> eventDispatcher;

    public BasicFiniteStateMachine(String name, StateT state) {
        this.name = name;
        this.state = state;

        this.properties = new HashMap<Object, Object>();
        this.eventDispatcher = new BlockingEventDispatcher<FiniteStateMachineEvent>();
    }

    public void put(Object key, Object value) {
        this.properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clz, Object key) {
        return (T) this.properties.get(key);
    }

    public <EventT extends FiniteStateMachineEvent> void addListener(Class<EventT> eventClass, Action1<EventT> listener) {
        this.eventDispatcher.addListener(eventClass, listener);
    }

    public <EventT extends FiniteStateMachineEvent> void removeListener(Class<EventT> eventClass, Action1<EventT> listener) {
        this.eventDispatcher.removeListener(eventClass, listener);
    }

    public String getName() {
        return name;
    }

    @Override
    public StateT getState() {
        return state;
    }

    @Override
    public void setState(StateT state, ConditionT condition, Object[] params) {
        this.eventDispatcher.dispatch(new ExitStateEvent(this, condition, params));
        this.state = state;
        this.eventDispatcher.dispatch(new EnterStateEvent(this, condition, params));
    }
}
