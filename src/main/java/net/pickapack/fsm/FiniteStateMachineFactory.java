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

import java.util.HashMap;
import java.util.Map;

public class FiniteStateMachineFactory<StateT, ConditionT, FiniteStateMachineT extends FiniteStateMachine<StateT, ConditionT>> {
    private Map<StateT, StateTransitions<StateT, ConditionT, FiniteStateMachineT>> transitions;

    public FiniteStateMachineFactory() {
        this.transitions = new HashMap<StateT, StateTransitions<StateT, ConditionT, FiniteStateMachineT>>();
    }

    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> inState(StateT state) {
        if (!this.transitions.containsKey(state)) {
            this.transitions.put(state, new StateTransitions<StateT, ConditionT, FiniteStateMachineT>(this, state));
        }

        return this.transitions.get(state);
    }

    public void clear() {
        this.transitions.clear();
    }

    public void fireTransition(FiniteStateMachineT fsm, ConditionT condition, Params params) {
        if (this.transitions.containsKey(fsm.getState())) {
            this.transitions.get(fsm.getState()).fireTransition(fsm, condition, params);
        } else {
            throw new IllegalArgumentException("No handler registered for condition " + condition + " in state " + fsm.getState());
        }
    }

    void changeState(FiniteStateMachineT from, ConditionT condition, Params params, StateT newState) {
        if (from != newState) {
            from.setState(newState, condition, params);
        }
    }
}
