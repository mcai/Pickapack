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
import net.pickapack.action.*;

import java.util.*;

public class StateTransitions<StateT, ConditionT, FiniteStateMachineT extends FiniteStateMachine<StateT, ConditionT>> {
    private Map<ConditionT, StateTransition> perStateTransitions;
    private FiniteStateMachineFactory<StateT, ConditionT, FiniteStateMachineT> fsmFactory;
    private StateT state;
    private Action1<FiniteStateMachineT> onCompletedCallback;

    StateTransitions(FiniteStateMachineFactory<StateT, ConditionT, FiniteStateMachineT> fsmFactory, StateT state) {
        this.fsmFactory = fsmFactory;
        this.state = state;
        this.perStateTransitions = new LinkedHashMap<ConditionT, StateTransition>();
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> setOnCompletedCallback(Action1<FiniteStateMachineT> onCompletedCallback) {
        this.onCompletedCallback = onCompletedCallback;
        return this;
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onConditions(List<ConditionT> conditions, Action4<FiniteStateMachineT, Object, ConditionT, Params> transition, StateT newState) {
        return onConditions(conditions, transition, newState, null);
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onConditions(List<ConditionT> conditions, Action4<FiniteStateMachineT, Object, ConditionT, Params> transition, StateT newState, Action1<FiniteStateMachineT> onCompletedCallback) {
        for (ConditionT condition : conditions) {
            this.onCondition(condition, transition, newState, onCompletedCallback);
        }

        return this;
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onCondition(ConditionT condition, final Action4<FiniteStateMachineT, Object, ConditionT, Params> transition, final StateT newState) {
        return onCondition(condition, transition, newState, null);
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onCondition(ConditionT condition, final Action4<FiniteStateMachineT, Object, ConditionT, Params> transition, final StateT newState, Action1<FiniteStateMachineT> onCompletedCallback) {
        if (this.perStateTransitions.containsKey(condition)) {
            throw new IllegalArgumentException("Transition of condition " + condition + " in state " + this.state + " has already been registered");
        }

        this.perStateTransitions.put(condition, new StateTransition(newState, transition, onCompletedCallback));

        return this;
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> ignoreCondition(ConditionT condition) {
        return this.onCondition(condition, new Action4<FiniteStateMachineT, Object, ConditionT, Params>() {
            public void apply(FiniteStateMachineT from, Object sender, ConditionT condition, Params params) {
                from.getState();
            }
        }, null, null);
    }

    public void clear() {
        this.perStateTransitions.clear();
    }

    Map<ConditionT, StateTransition> getPerStateTransitions() {
        return perStateTransitions;
    }

    void fireTransition(FiniteStateMachineT fsm, Object sender, ConditionT condition, Params params) {
        if (this.perStateTransitions.containsKey(condition)) {
            StateTransition stateTransition = this.perStateTransitions.get(condition);
            fsmFactory.changeState(fsm, sender, condition, params, stateTransition.apply(fsm, sender, condition, params));
            if(stateTransition.onCompletedCallback != null) {
                stateTransition.onCompletedCallback.apply(fsm);
            }

            if(this.onCompletedCallback != null) {
                this.onCompletedCallback.apply(fsm);
            }
        } else {
            throw new IllegalArgumentException("Unexpected condition " + condition + " in state " + this.state + " is not among " + this.perStateTransitions.keySet());
        }
    }

    public class StateTransition implements Function4<FiniteStateMachineT, Object, ConditionT, Params, StateT> {
        private final StateT newState;
        private Map<FiniteStateMachineT, Integer> numExecutionsPerFsm;
        private long numExecutions;
        private final Action4<FiniteStateMachineT, Object, ConditionT, Params> transition;
        private Action1<FiniteStateMachineT> onCompletedCallback;

        public StateTransition(StateT newState, Action4<FiniteStateMachineT, Object, ConditionT, Params> transition, Action1<FiniteStateMachineT> onCompletedCallback) {
            this.newState = newState;
            this.numExecutionsPerFsm = new HashMap<FiniteStateMachineT, Integer>();
            this.transition = transition;
            this.onCompletedCallback = onCompletedCallback;
        }

        public StateT apply(FiniteStateMachineT fsm, Object sender, ConditionT condition, Params params) {
            this.transition.apply(fsm, sender, condition, params);

            if(!this.numExecutionsPerFsm.containsKey(fsm)) {
                 this.numExecutionsPerFsm.put(fsm, 0);
            }
            this.numExecutionsPerFsm.put(fsm, this.numExecutionsPerFsm.get(fsm) + 1);

            this.numExecutions++;

            if(this.newState == null) {
                return fsm.getState();
            }

            return this.newState;
        }

        public StateTransition setOnCompletedCallback(Action1<FiniteStateMachineT> onCompletedCallback) {
            this.onCompletedCallback = onCompletedCallback;
            return this;
        }

        public int getNumExecutionsPerFsm(FiniteStateMachineT fsm) {
            return this.numExecutionsPerFsm.containsKey(fsm) ? this.numExecutionsPerFsm.get(fsm) : 0;
        }

        public StateT getNewState() {
            return newState;
        }

        public long getNumExecutions() {
            return numExecutions;
        }
    }
}
