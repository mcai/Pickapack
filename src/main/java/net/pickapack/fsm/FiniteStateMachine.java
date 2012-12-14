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

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author Min Cai
 * @param <StateT>
 * @param <ConditionT>
 */
public interface FiniteStateMachine<StateT, ConditionT> extends Serializable {
    /**
     *
     * @return
     */
    StateT getState();

    /**
     *
     * @param sender
     * @param condition
     * @param params
     * @param state
     */
    void setState(Object sender, ConditionT condition, Params params, StateT state);

    /**
     *
     * @return
     */
    Map<StateT, Map<ConditionT, Long>> getNumExecutions();

    /**
     *
     *
     * @param state
     * @return
     */
    long getNumExecutionsByTransition(StateT state, ConditionT condition);
}
