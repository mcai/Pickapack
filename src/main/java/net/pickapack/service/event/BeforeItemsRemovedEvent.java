package net.pickapack.service.event;

import net.pickapack.Pair;

import java.util.List;

/**
 *
 * @author Min Cai
 */
public  class BeforeItemsRemovedEvent implements ServiceEvent {
    private Class<?> clz;
    private List<Pair<Long, Long>> idAndParentIds;

    /**
     *
     * @param clz
     * @param idAndParentIds
     */
    public BeforeItemsRemovedEvent(Class<?> clz, List<Pair<Long, Long>> idAndParentIds) {
        this.clz = clz;
        this.idAndParentIds = idAndParentIds;
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
    public List<Pair<Long, Long>> getIdAndParentIds() {
        return idAndParentIds;
    }
}
