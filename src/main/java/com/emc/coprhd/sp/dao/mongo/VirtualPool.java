/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.dao.mongo;

import com.emc.coprhd.sp.json.vnxszier.ApplicationsList;
import com.emc.coprhd.sp.repository.DaoConstants.VirtualPools;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "#{contextProperties.getVirtualPoolsCollectionName()}")
public class VirtualPool {
    @Id
    private final String id;
    @Field(VirtualPools.ID)
    private final String vpId;
    @Field(VirtualPools.NAME)
    private final String name;
    @Field(VirtualPools.TARGET_RESPONSE_TIME)
    private final Double targetResponseTime;
    @Field(VirtualPools.WORKLOAD)
    private final ApplicationsList workload;

    public VirtualPool(
            final String id,
            final String vpId,
            final String name,
            final Double targetResponseTime,
            final ApplicationsList workload) {
        this.id = id;
        this.vpId = vpId;
        this.name = name;
        this.targetResponseTime = targetResponseTime;
        this.workload = workload;
    }

    public String getId() {
        return id;
    }

    public String getVpId() {
        return vpId;
    }

    public String getName() {
        return name;
    }

    public Double getTargetResponseTime() {
        return targetResponseTime;
    }

    public ApplicationsList getWorkload() {
        return workload;
    }

    @SuppressWarnings("PublicInnerClass")
    public static final class VirtualPoolBuilder {
        private String id;
        private String vpId;
        private String name;
        private Double targetResponseTime;
        private ApplicationsList workload;

        private VirtualPoolBuilder() {
        }

        public VirtualPoolBuilder withId(final String id) {
            this.id = id;
            return this;
        }

        public VirtualPoolBuilder withVpId(final String vpId) {
            this.vpId = vpId;
            return this;
        }

        public VirtualPoolBuilder withName(final String name) {
            this.name = name;
            return this;
        }

        public VirtualPoolBuilder withTargetResponseTime(final Double targetResponseTime) {
            this.targetResponseTime = targetResponseTime;
            return this;
        }

        public VirtualPoolBuilder withWorkload(final ApplicationsList workload) {
            this.workload = workload;
            return this;
        }

        public VirtualPool build() {
            return new VirtualPool(id, vpId, name, targetResponseTime, workload);
        }

        public static VirtualPoolBuilder aVirtualPool() {
            return new VirtualPoolBuilder();
        }
    }
}
