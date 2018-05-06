/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.core;

import com.emc.coprhd.sp.model.ClusterNode;

import java.util.List;

public interface ClusterStateService {
    String getNodeAddress(final ClusterNode node);

    List<ClusterNode> getAvailableNodes();
}
