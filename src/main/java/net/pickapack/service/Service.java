package net.pickapack.service;

import net.pickapack.service.event.ServiceEvent;
import net.pickapack.event.BlockingEventDispatcher;

import java.io.Serializable;

public interface Service extends Serializable {
    void stop();
    BlockingEventDispatcher<ServiceEvent> getBlockingEventDispatcher();
}


