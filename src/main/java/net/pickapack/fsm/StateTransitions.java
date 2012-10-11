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

/**
 *
 * @author Min Cai
 * @param <StateT>
 * @param <ConditionT>
 * @param <FiniteStateMachineT>
 */
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

    /**
     *
     * @param onCompletedCallback
     * @return
     */
    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> setOnCompletedCallback(Action1<FiniteStateMachineT> onCompletedCallback) {
        this.onCompletedCallback = onCompletedCallback;
        return this;
    }

    /**
     *
     * @param conditions
     * @param transition
     * @param newState
     * @return
     */
    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onConditions(List<ConditionT> conditions, Action4<FiniteStateMachineT, Object, ConditionT, ? extends Params> transition, StateT newState) {
        return onConditions(conditions, transition, newState, null);
    }

    /**
     *
     * @param conditions
     * @param transition
     * @param newState
     * @param onCompletedCallback
     * @return
     */
    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onConditions(List<ConditionT> conditions, Action4<FiniteStateMachineT, Object, ConditionT, ? extends Params> transition, StateT newState, Action1<FiniteStateMachineT> onCompletedCallback) {
        for (ConditionT condition : conditions) {
            this.onCondition(condition, transition, newState, onCompletedCallback);
        }

        return this;
    }

    /**
     *
     * @param condition
     * @param transition
     * @param newState
     * @return
     */
    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onCondition(ConditionT condition, final Action4<FiniteStateMachineT, Object, ConditionT, ? extends Params> transition, final StateT newState) {
        return onCondition(condition, transition, newState, null);
    }

    /**
     *
     * @param condition
     * @param transition
     * @param newState
     * @param onCompletedCallback
     * @return
     */
    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onCondition(ConditionT condition, final Action4<FiniteStateMachineT, Object, ConditionT, ? extends Params> transition, final StateT newState, Action1<FiniteStateMachineT> onCompletedCallback) {
        if (this.perStateTransitions.containsKey(condition)) {
            throw new IllegalArgumentException("Transition of condition " + condition + " in state " + this.state + " has already been registered");
        }

        List<FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ? extends Params>> actions = new ArrayList<FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ? extends Params>>();
        actions.add(new FiniteStateMachineAction<FiniteStateMachineT, ConditionT, Params>(state + ": " + condition + " -> unamedAction/" + newState) {
            @Override
            @SuppressWarnings("unchecked")
            public void apply(FiniteStateMachineT fsm, Object sender, ConditionT eventType, Params params) {
                ((Action4<FiniteStateMachineT, Object, ConditionT, Params>) transition).apply(fsm, sender,  eventType, params);
            }
        });

        this.perStateTransitions.put(condition, new StateTransition(state, condition, newState, actions, onCompletedCallback));

        return this;
    }

    /**
     *
     * @param condition
     * @param actions
     * @param newState
     * @param onCompletedCallback
     * @return
     */
    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> onCondition(ConditionT condition, final List<FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ? extends Params>> actions, final StateT newState, Action1<FiniteStateMachineT> onCompletedCallback) {
        if (this.perStateTransitions.containsKey(condition)) {
            throw new IllegalArgumentException("Transition of condition " + condition + " in state " + this.state + " has already been registered");
        }

        this.perStateTransitions.put(condition, new StateTransition(state, condition, newState, actions, onCompletedCallback));

        return this;
    }

    /**
     *
     * @param condition
     * @return
     */
    public StateTransitions<StateT, ConditionT, FiniteStateMachineT> ignoreCondition(ConditionT condition) {
        return this.onCondition(condition, new Action4<FiniteStateMachineT, Object, ConditionT, Params>() {
            public void apply(FiniteStateMachineT from, Object sender, ConditionT condition, Params params) {
                from.getState();
            }
        }, null, null);
    }

    /**
     *
     */
    public void clear() {
        this.perStateTransitions.clear();
    }

    public Map<ConditionT, StateTransition> getPerStateTransitions() {
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

    /**
     *
     */
    public class StateTransition implements Function4<FiniteStateMachineT, Object, ConditionT, Params, StateT> {
        private StateT state;
        private ConditionT condition;
        private StateT newState;
        private List<FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ? extends Params>> actions;
        private Action1<FiniteStateMachineT> onCompletedCallback;

        /**
         *
         * @param state
         * @param condition
         * @param newState
         * @param actions
         * @param onCompletedCallback
         */
        public StateTransition(StateT state, ConditionT condition, StateT newState, List<FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ? extends Params>> actions, Action1<FiniteStateMachineT> onCompletedCallback) {
            this.state = state;
            this.condition = condition;
            this.newState = newState;
            this.actions = actions;
            this.onCompletedCallback = onCompletedCallback;
        }

        /**
         *
         * @param fsm
         * @param sender
         * @param condition
         * @param params
         * @return
         */
        @SuppressWarnings("unchecked")
        public StateT apply(FiniteStateMachineT fsm, Object sender, ConditionT condition, Params params) {
            for(FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ? extends Params> action : this.actions) {
                ((FiniteStateMachineAction<FiniteStateMachineT, ConditionT, Params>) action).apply(fsm, sender, condition, params);
            }

            if(!fsm.getNumExecutions().containsKey(state)) {
                fsm.getNumExecutions().put(state, new LinkedHashMap<ConditionT, Long>());
            }

            if(!fsm.getNumExecutions().get(state).containsKey(condition)) {
                fsm.getNumExecutions().get(state).put(condition, 0L);
            }

            fsm.getNumExecutions().get(state).put(condition, fsm.getNumExecutions().get(state).get(condition) + 1);

            if(this.newState == null) {
                return fsm.getState();
            }

            return this.newState;
        }

        /**
         *
         * @param onCompletedCallback
         * @return
         */
        public StateTransition setOnCompletedCallback(Action1<FiniteStateMachineT> onCompletedCallback) {
            this.onCompletedCallback = onCompletedCallback;
            return this;
        }

        /**
         *
         * @return
         */
        public List<FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ? extends Params>> getActions() {
            return actions;
        }

        /**
         *
         * @return
         */
        public StateT getState() {
            return state;
        }

        /**
         *
         * @return
         */
        public ConditionT getCondition() {
            return condition;
        }

        /**
         *
         * @return
         */
        public StateT getNewState() {
            return newState;
        }
    }
}
