package net.pickapack.service.event;

import java.util.List;

public class AfterItemsAddedEvent implements ServiceEvent {
    private Class<?> clz;
    private List<Long> itemIds;

    public AfterItemsAddedEvent(Class<?> clz, List<Long> itemIds) {
        this.clz = clz;
        this.itemIds = itemIds;
    }

    public Class<?> getClz() {
        return clz;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }
}
