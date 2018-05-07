/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.srm;

import com.emc.coprhd.sp.service.common.AbstractFallBackable;
import com.emc.coprhd.sp.transfer.srm.DiskInfo;
import com.emc.coprhd.sp.transfer.srm.SRMPoolInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import static com.emc.coprhd.sp.util.RuntimeUtils.enterMethodMessage;
import static com.emc.coprhd.sp.util.RuntimeUtils.exitMethodMessage;

@Service
public class SRMStubClientImpl extends AbstractFallBackable<SRMClient> implements SRMClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(SRMStubClientImpl.class);
    private static final int TIERS_NUMBER = 5;
    private static final Random RANDOM = new SecureRandom();
    private static final double RELATIVE_RANGE = 0.1;
    private static final List<DiskInfo> NO_DISKS = null;
    private static final double UTILIZATION = 80;
    private static final double LATENCY = 15;
    private static final double IOPS = 1000;
    private static final double USED_CAPACITY = 50;

    @Override
    @SuppressWarnings("SynchronizationOnStaticField")
    public SRMPoolInfo getStoragePoolInfoByName(final String poolName) {
        LOGGER.debug("{} poolName: {}", enterMethodMessage(), poolName);
        final int baseTier = Math.abs(poolName.hashCode() % 5) + 1;
        final int tier = isUseUtilizedPool()
                ? (baseTier + TIERS_NUMBER / 2) % TIERS_NUMBER + 1
                : baseTier;
        final double utilization = Math.abs(randomMeanWithRelativeRange(UTILIZATION / tier, RELATIVE_RANGE));
        final double latency = Math.abs(randomMeanWithRelativeRange(LATENCY / tier, RELATIVE_RANGE));
        final double iops = Math.abs(randomMeanWithRelativeRange(IOPS / tier, RELATIVE_RANGE));
        final double capacity = Math.abs(randomMeanWithRelativeRange(USED_CAPACITY / tier, RELATIVE_RANGE));
        final SRMPoolInfo result = new SRMPoolInfo(poolName, utilization, latency, iops, capacity, NO_DISKS);
        LOGGER.debug("{} poolName: {}, info: {}", exitMethodMessage(), poolName, result);
        return result;
    }

    @Override
    @PostConstruct
    public void initialize() {
    }

    @SuppressWarnings("MagicNumber")
    private static boolean isUseUtilizedPool() {
        return LocalTime.now().getSecond() > 30;
    }

    private static double randomMeanWithRelativeRange(final double mean, final double relativeRange) {
        return mean + RANDOM.nextDouble() * mean * relativeRange / 2;
    }
}