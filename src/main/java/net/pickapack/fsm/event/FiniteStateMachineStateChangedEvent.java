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
package net.pickapack.fsm.event;

import net.pickapack.Params;
import net.pickapack.fsm.FiniteStateMachine;

public class FiniteStateMachineStateChangedEvent extends FiniteStateMachineEvent {
    private FiniteStateMachine<?, ?> from;
    private Object condition;
    private Params params;

    public FiniteStateMachineStateChangedEvent(FiniteStateMachine<?, ?> from, Object condition, Params params) {
        this.from = from;
        this.condition = condition;
        this.params = params;
    }

    public FiniteStateMachine<?, ?> getFrom() {
        return from;
    }

    public Object getCondition() {
        return condition;
    }

    public Params getParams() {
        return params;
    }
}
