package net.pickapack.service.event;

import net.pickapack.Pair;

import java.util.List;

public  class AfterItemsUpdatedEvent implements ServiceEvent {
    private Class<?> clz;
    private List<Pair<Long, Long>> idAndParentIds;

    public AfterItemsUpdatedEvent(Class<?> clz, List<Pair<Long, Long>> idAndParentIds) {
        this.clz = clz;
        this.idAndParentIds = idAndParentIds;
    }

    public Class<?> getClz() {
        return clz;
    }

    public List<Pair<Long, Long>> getIdAndParentIds() {
        return idAndParentIds;
    }
}
