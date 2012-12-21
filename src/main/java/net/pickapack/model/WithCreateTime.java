package net.pickapack.model;

/**
 * With create time.
 *
 * @author Min Cai
 */
public interface WithCreateTime extends WithId {
    /**
     * Get the time when it is created.
     *
     * @return the time when it is created
     */
    long getCreateTime();
}
