package net.pickapack.model;

import java.io.Serializable;
import java.lang.String;

public interface ModelElement extends Serializable {
    long getId();
    long getParentId();
    String getTitle();
    long getCreateTime();
}

