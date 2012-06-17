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
package net.pickapack.fsm;

import net.pickapack.Params;
import net.pickapack.action.Action1;
import net.pickapack.event.BlockingEventDispatcher;
import net.pickapack.fsm.event.EnterStateEvent;
import net.pickapack.fsm.event.ExitStateEvent;
import net.pickapack.fsm.event.FiniteStateMachineEvent;

import java.util.Map;

public class BasicFiniteStateMachine<StateT, ConditionT> extends Params implements FiniteStateMachine<StateT,ConditionT> {
    private String name;
    private StateT state;

    private BlockingEventDispatcher<FiniteStateMachineEvent> eventDispatcher;

    public BasicFiniteStateMachine(String name, StateT state) {
        this.name = name;
        this.state = state;

        this.eventDispatcher = new BlockingEventDispatcher<FiniteStateMachineEvent>();
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

    private boolean settingStates = false;

    @Override
    public void setState(Object sender, ConditionT condition, Params params, StateT state) {
        if(this.settingStates) {
            throw new IllegalArgumentException();
        }

        this.settingStates = true;

        this.eventDispatcher.dispatch(new ExitStateEvent(this, sender, condition, params));
        this.state = state;
        this.eventDispatcher.dispatch(new EnterStateEvent(this, sender, condition, params));

        this.settingStates = false;
    }

    public void dump(FiniteStateMachineFactory<StateT, ConditionT, BasicFiniteStateMachine<StateT, ConditionT>> fsmFactory) {
        for(StateT state : fsmFactory.transitions.keySet()) {
            System.out.println(state);

            StateTransitions<StateT, ConditionT, BasicFiniteStateMachine<StateT, ConditionT>> stateTransitions = fsmFactory.transitions.get(state);
            Map<ConditionT, StateTransitions<StateT, ConditionT, BasicFiniteStateMachine<StateT, ConditionT>>.StateTransition> perStateTransitions = stateTransitions.getPerStateTransitions();
            for(ConditionT condition : perStateTransitions.keySet()) {
                StateTransitions<StateT, ConditionT, BasicFiniteStateMachine<StateT, ConditionT>>.StateTransition stateTransition = perStateTransitions.get(condition);
                System.out.printf("  -> %s:  %s/%s [%d] %n", condition, stateTransition.getActions(), stateTransition.getNewState(), stateTransition.getNumExecutionsPerFsm(this));
            }

            System.out.println();
        }
    }
}
