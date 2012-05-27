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
import net.pickapack.action.Action;
import net.pickapack.action.Action3;
import net.pickapack.action.Function2;
import net.pickapack.action.Function3;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StateTransitions<StateT, ConditionT, FiniteStateMachineT extends FiniteStateMachine<StateT, ConditionT>> {
    private Map<ConditionT, MyFunction3> perStateTransitions;
    private FiniteStateMachineFactory<StateT, ConditionT, FiniteStateMachineT> fsmFactory;
    private StateT state;

    StateTransitions(FiniteStateMachineFactory<StateT, ConditionT, FiniteStateMachineT> fsmFactory, StateT state) {
        this.fsmFactory = fsmFactory;
        this.state = state;
        this.perStateTransitions = new LinkedHashMap<ConditionT, MyFunction3>();
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onConditions(List<ConditionT> conditions, Action3<FiniteStateMachineT, ConditionT, Params> transition, StateT newState) {
        for (ConditionT condition : conditions) {
            this.onCondition(condition, transition, newState);
        }

        return this;
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onCondition(ConditionT condition, final Action3<FiniteStateMachineT, ConditionT, Params> transition, final StateT newState) {
        if (this.perStateTransitions.containsKey(condition)) {
            throw new IllegalArgumentException("Transition of condition " + condition + " in state " + this.state + " has already been registered");
        }

        this.perStateTransitions.put(condition, new MyFunction3(newState) {
            @Override
            public StateT apply(FiniteStateMachineT from, ConditionT condition, Params params) {
                transition.apply(from, condition, params);

                if(newState == null) {
                    return from.getState();
                }

                return newState;
            }
        });

        return this;
    }

    public abstract class MyFunction3 implements Function3<FiniteStateMachineT, ConditionT, Params, StateT> {
        private StateT newState;

        protected MyFunction3(StateT newState) {
            this.newState = newState;
        }

        public StateT getNewState() {
            return newState;
        }
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> ignoreCondition(ConditionT condition) {
        return this.onCondition(condition, new Action3<FiniteStateMachineT, ConditionT, Params>() {
            public void apply(FiniteStateMachineT from, ConditionT condition, Params params) {
                from.getState();
            }
        }, null);
    }

    public void clear() {
        this.perStateTransitions.clear();
    }

    Map<ConditionT, MyFunction3> getPerStateTransitions() {
        return perStateTransitions;
    }

    void fireTransition(FiniteStateMachineT fsm, ConditionT condition, Params params) {
        if (this.perStateTransitions.containsKey(condition)) {
            fsmFactory.changeState(fsm, condition, params, this.perStateTransitions.get(condition).apply(fsm, condition, params));
        } else {
            throw new IllegalArgumentException("Unexpected condition " + condition + " in state " + this.state + " is not among " + this.perStateTransitions.keySet());
        }
    }
}
