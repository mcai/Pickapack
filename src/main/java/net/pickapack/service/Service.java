package net.pickapack.service;

import net.pickapack.service.event.ServiceEvent;
import net.pickapack.event.BlockingEventDispatcher;

import java.io.Serializable;

/**
 *
 * @author Min Cai
 */
public interface Service extends Serializable {
    /**
     *
     */
    void stop();
    /**
     *
     * @return
     */
    BlockingEventDispatcher<ServiceEvent> getBlockingEventDispatcher();
}


