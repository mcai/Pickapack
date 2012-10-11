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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Min Cai
 * @param <StateT>
 * @param <ConditionT>
 */
public class SimpleFiniteStateMachine<StateT, ConditionT> implements FiniteStateMachine<StateT,ConditionT> {
    private StateT state;

    private boolean settingStates = false;

    private Map<StateT, Map<ConditionT, Long>> numExecutions;

    /**
     *
     * @param state
     */
    public SimpleFiniteStateMachine(StateT state) {
        this.state = state;

        this.numExecutions = new LinkedHashMap<StateT, Map<ConditionT, Long>>();
    }

    /**
     *
     * @return
     */
    @Override
    public StateT getState() {
        return state;
    }

    /**
     *
     * @param sender
     * @param condition
     * @param params
     * @param state
     */
    @Override
    public void setState(Object sender, ConditionT condition, Params params, StateT state) {
        if(this.settingStates) {
            throw new IllegalArgumentException();
        }

        this.settingStates = true;

        this.state = state;

        this.settingStates = false;
    }

    @Override
    public Map<StateT, Map<ConditionT, Long>> getNumExecutions() {
        return numExecutions;
    }

    @Override
    public long getNumExecutionsByTransition(StateT state, ConditionT condition) {
        return this.numExecutions.containsKey(state) && this.numExecutions.get(state).containsKey(condition) ? this.numExecutions.get(state).get(condition) : 0L;
    }
}
