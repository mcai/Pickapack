package net.pickapack.model;

/**
 * With parent ID.
 *
 * @author Min Cai
 */
public interface WithParentId extends WithId {
    /**
     * Get the parent ID.
     *
     * @return the parent ID
     */
    long getParentId();
}
