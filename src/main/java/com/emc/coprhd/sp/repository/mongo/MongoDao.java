/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.repository.mongo;

import com.emc.coprhd.sp.dao.mongo.VirtualPool;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDao extends MongoRepository<VirtualPool, String> {
    VirtualPool findById(final String id);
}
