package com.emc.coprhd.sp.service.common;

import org.springframework.beans.factory.BeanNameAware;

public interface FallBackable extends BeanNameAware {
    String getName();

    void initialize();
}
