package net.pickapack.fsm;

import net.pickapack.Params;
import net.pickapack.action.Action4;

/**
 *
 * @author Min Cai
 * @param <FiniteStateMachineT>
 * @param <ConditionT>
 * @param <ParamsT>
 */
public abstract class FiniteStateMachineAction<FiniteStateMachineT, ConditionT, ParamsT extends Params> implements Action4<FiniteStateMachineT, Object, ConditionT, ParamsT> {
    private String name;

    /**
     *
     * @param name
     */
    public FiniteStateMachineAction(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
