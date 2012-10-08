package net.pickapack.service.event;

import java.util.List;

/**
 *
 * @author Min Cai
 */
public class AfterItemsAddedEvent implements ServiceEvent {
    private Class<?> clz;
    private List<Long> itemIds;

    /**
     *
     * @param clz
     * @param itemIds
     */
    public AfterItemsAddedEvent(Class<?> clz, List<Long> itemIds) {
        this.clz = clz;
        this.itemIds = itemIds;
    }

    /**
     *
     * @return
     */
    public Class<?> getClz() {
        return clz;
    }

    /**
     *
     * @return
     */
    public List<Long> getItemIds() {
        return itemIds;
    }
}
