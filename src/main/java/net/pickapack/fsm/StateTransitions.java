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
import net.pickapack.action.Function2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateTransitions<StateT, ConditionT, FiniteStateMachineT extends FiniteStateMachine<StateT, ConditionT>> {
    private Map<ConditionT, Function2<FiniteStateMachineT, Params, StateT>> perStateTransitions;
    private FiniteStateMachineFactory<StateT, ConditionT, FiniteStateMachineT> fsmFactory;
    private StateT state;

    StateTransitions(FiniteStateMachineFactory<StateT, ConditionT, FiniteStateMachineT> fsmFactory, StateT state) {
        this.fsmFactory = fsmFactory;
        this.state = state;
        this.perStateTransitions = new HashMap<ConditionT, Function2<FiniteStateMachineT, Params, StateT>>();
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onConditions(List<ConditionT> conditions, Function2<FiniteStateMachineT, Params, StateT> transition) {
        for (ConditionT condition : conditions) {
            this.onCondition(condition, transition);
        }

        return this;
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onCondition(ConditionT condition, Function2<FiniteStateMachineT, Params, StateT> transition) {
        if (this.perStateTransitions.containsKey(condition)) {
            throw new IllegalArgumentException("Transition of condition " + condition + " in state " + this.state + " has already been registered");
        }

        this.perStateTransitions.put(condition, transition);

        return this;
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> ignoreCondition(ConditionT condition) {
        return this.onCondition(condition, new Function2<FiniteStateMachineT, Params, StateT>() {
            public StateT apply(FiniteStateMachineT from, Params params) {
                return from.getState();
            }
        });
    }

    public void clear() {
        this.perStateTransitions.clear();
    }

    void fireTransition(FiniteStateMachineT fsm, ConditionT condition, Params params) {
        if (this.perStateTransitions.containsKey(condition)) {
            fsmFactory.changeState(fsm, condition, params, this.perStateTransitions.get(condition).apply(fsm, params));
        } else {
            throw new IllegalArgumentException("Unexpected condition " + condition + " in state " + this.state + " is not among " + this.perStateTransitions.keySet());
        }
    }
}
