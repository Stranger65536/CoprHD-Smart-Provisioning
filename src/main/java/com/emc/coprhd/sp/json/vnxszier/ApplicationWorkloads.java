/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.json.vnxszier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;

@SuppressWarnings("DuplicateStringLiteralInspection")
public class ApplicationWorkloads {
    private static final String WRITE_SIZE_KB = "WriteSizeKB";
    private static final String SKEW = "Skew";
    private static final String ALLOCATED_GB = "AllocatedGB";
    private static final String READ_SIZE_KB = "ReadSizeKB";
    private static final String CACHE_HIT_READ_PCT = "CacheHitReadPct";
    private static final String RANDOM_READ_PCT = "RandomReadPct";
    private static final String NUMBER_LUNS = "NumberLUNs";
    private static final String RANDOM_WRITE_PCT = "RandomWritePct";
    private static final String RESPONSE_TIME_MS = "ResponseTimeMS";
    private static final String APP_NAME = "appName";
    private static final String SEQUENTIAL_READ_PCT = "SequentialReadPct";
    private static final String TYPE = "type";
    private static final String THIN_LUN = "thinLUN";
    private static final String PERFORMANCE_IOPS = "PerformanceIOPS";
    private static final String B_THIN_LUN = "bThinLUN";
    private static final String CONCURRENT_PROCESSES = "ConcurrentProcesses";
    private static final String CACHE_HIT_WRITE_PCT = "CacheHitWritePct";
    private static final String SEQUENTIAL_WRITE_PCT = "SequentialWritePct";
    private static final String DESCRIPTION = "description";
    private static final String ID = "id";
    private static final String B_FASTCACHE = "bFASTCache";
    private static final String DEDUPLICATION = "deduplication";

    @JsonProperty(SKEW)
    private final BigDecimal skew;
    @JsonProperty(WRITE_SIZE_KB)
    private final Integer writeSizeKb;
    @JsonProperty(ALLOCATED_GB)
    private final Long allocatedGb;
    @JsonProperty(READ_SIZE_KB)
    private final Integer readSizeKb;
    @JsonProperty(CACHE_HIT_READ_PCT)
    private final Integer cacheHitReadPercent;
    @JsonProperty(RANDOM_READ_PCT)
    private final Integer randomReadPercent;
    @JsonProperty(NUMBER_LUNS)
    private final Integer numberOfLuns;
    @JsonProperty(RANDOM_WRITE_PCT)
    private final Integer randomWritePercent;
    @JsonProperty(RESPONSE_TIME_MS)
    private final Integer responseTime;
    @JsonProperty(TYPE)
    private final Integer type;
    @JsonProperty(APP_NAME)
    private final String applicationName;
    @JsonProperty(SEQUENTIAL_READ_PCT)
    private final Integer sequentialReadPercent;
    @JsonProperty(THIN_LUN)
    private final Boolean thinLun;
    @JsonProperty(PERFORMANCE_IOPS)
    private final Integer targetIops;
    @JsonProperty(B_THIN_LUN)
    private final Boolean bThinLun;
    @JsonProperty(CONCURRENT_PROCESSES)
    private final Integer concurrentProcesses;
    @JsonProperty(CACHE_HIT_WRITE_PCT)
    private final BigDecimal cacheHitWritePercent;
    @JsonProperty(SEQUENTIAL_WRITE_PCT)
    private final Integer sequentialWritePercent;
    @JsonProperty(DESCRIPTION)
    private final String description;
    @JsonProperty(ID)
    private final Integer id;
    @JsonProperty(B_FASTCACHE)
    private final Boolean fastCache;
    @JsonProperty(DEDUPLICATION)
    private final Integer deduplication;

    @JsonCreator
    public ApplicationWorkloads(
            @JsonProperty(SKEW) final BigDecimal skew,
            @JsonProperty(WRITE_SIZE_KB) final Integer writeSizeKb,
            @JsonProperty(ALLOCATED_GB) final Long allocatedGb,
            @JsonProperty(READ_SIZE_KB) final Integer readSizeKb,
            @JsonProperty(CACHE_HIT_READ_PCT) final Integer cacheHitReadPercent,
            @JsonProperty(RANDOM_READ_PCT) final Integer randomReadPercent,
            @JsonProperty(NUMBER_LUNS) final Integer numberOfLuns,
            @JsonProperty(RANDOM_WRITE_PCT) final Integer randomWritePercent,
            @JsonProperty(RESPONSE_TIME_MS) final Integer responseTime,
            @JsonProperty(TYPE) final Integer type,
            @JsonProperty(APP_NAME) final String applicationName,
            @JsonProperty(SEQUENTIAL_READ_PCT) final Integer sequentialReadPercent,
            @JsonProperty(THIN_LUN) final Boolean thinLun,
            @JsonProperty(PERFORMANCE_IOPS) final Integer targetIops,
            @JsonProperty(B_THIN_LUN) final Boolean bThinLun,
            @JsonProperty(CONCURRENT_PROCESSES) final Integer concurrentProcesses,
            @JsonProperty(CACHE_HIT_WRITE_PCT) final BigDecimal cacheHitWritePercent,
            @JsonProperty(SEQUENTIAL_WRITE_PCT) final Integer sequentialWritePercent,
            @JsonProperty(DESCRIPTION) final String description,
            @JsonProperty(ID) final Integer id,
            @JsonProperty(B_FASTCACHE) final Boolean fastCache,
            @JsonProperty(DEDUPLICATION) final Integer deduplication) {
        this.skew = skew;
        this.writeSizeKb = writeSizeKb;
        this.allocatedGb = allocatedGb;
        this.readSizeKb = readSizeKb;
        this.cacheHitReadPercent = cacheHitReadPercent;
        this.randomReadPercent = randomReadPercent;
        this.numberOfLuns = numberOfLuns;
        this.randomWritePercent = randomWritePercent;
        this.responseTime = responseTime;
        this.type = type;
        this.applicationName = applicationName;
        this.sequentialReadPercent = sequentialReadPercent;
        this.thinLun = thinLun;
        this.targetIops = targetIops;
        this.bThinLun = bThinLun;
        this.concurrentProcesses = concurrentProcesses;
        this.cacheHitWritePercent = cacheHitWritePercent;
        this.sequentialWritePercent = sequentialWritePercent;
        this.description = description;
        this.id = id;
        this.fastCache = fastCache;
        this.deduplication = deduplication;
    }

    public BigDecimal getSkew() {
        return skew;
    }

    public Integer getWriteSizeKb() {
        return writeSizeKb;
    }

    public Long getAllocatedGb() {
        return allocatedGb;
    }

    public Integer getReadSizeKb() {
        return readSizeKb;
    }

    public Integer getCacheHitReadPercent() {
        return cacheHitReadPercent;
    }

    public Integer getRandomReadPercent() {
        return randomReadPercent;
    }

    public Integer getNumberOfLuns() {
        return numberOfLuns;
    }

    public Integer getRandomWritePercent() {
        return randomWritePercent;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public Integer getType() {
        return type;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Integer getSequentialReadPercent() {
        return sequentialReadPercent;
    }

    public Boolean getThinLun() {
        return thinLun;
    }

    public Integer getTargetIops() {
        return targetIops;
    }

    public Boolean getbThinLun() {
        return bThinLun;
    }

    public Integer getConcurrentProcesses() {
        return concurrentProcesses;
    }

    public BigDecimal getCacheHitWritePercent() {
        return cacheHitWritePercent;
    }

    public Integer getSequentialWritePercent() {
        return sequentialWritePercent;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }

    public Boolean getFastCache() {
        return fastCache;
    }

    public Integer getDeduplication() {
        return deduplication;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(skew)
                .append(writeSizeKb)
                .append(allocatedGb)
                .append(readSizeKb)
                .append(cacheHitReadPercent)
                .append(randomReadPercent)
                .append(numberOfLuns)
                .append(randomWritePercent)
                .append(responseTime)
                .append(type)
                .append(applicationName)
                .append(sequentialReadPercent)
                .append(thinLun)
                .append(targetIops)
                .append(bThinLun)
                .append(concurrentProcesses)
                .append(cacheHitWritePercent)
                .append(sequentialWritePercent)
                .append(description)
                .append(id)
                .append(fastCache)
                .append(deduplication)
                .toHashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }

        final ApplicationWorkloads other = (ApplicationWorkloads) o;

        return new EqualsBuilder()
                .append(skew, other.skew)
                .append(writeSizeKb, other.writeSizeKb)
                .append(allocatedGb, other.allocatedGb)
                .append(readSizeKb, other.readSizeKb)
                .append(cacheHitReadPercent, other.cacheHitReadPercent)
                .append(randomReadPercent, other.randomReadPercent)
                .append(numberOfLuns, other.numberOfLuns)
                .append(randomWritePercent, other.randomWritePercent)
                .append(responseTime, other.responseTime)
                .append(type, other.type)
                .append(applicationName, other.applicationName)
                .append(sequentialReadPercent, other.sequentialReadPercent)
                .append(thinLun, other.thinLun)
                .append(targetIops, other.targetIops)
                .append(bThinLun, other.bThinLun)
                .append(concurrentProcesses, other.concurrentProcesses)
                .append(cacheHitWritePercent, other.cacheHitWritePercent)
                .append(sequentialWritePercent, other.sequentialWritePercent)
                .append(description, other.description)
                .append(id, other.id)
                .append(fastCache, other.fastCache)
                .append(deduplication, other.deduplication)
                .isEquals();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("skew", skew)
                .add("writeSizeKb", writeSizeKb)
                .add("allocatedGb", allocatedGb)
                .add("readSizeKb", readSizeKb)
                .add("cacheHitReadPercent", cacheHitReadPercent)
                .add("randomReadPercent", randomReadPercent)
                .add("numberOfLuns", numberOfLuns)
                .add("randomWritePercent", randomWritePercent)
                .add("responseTime", responseTime)
                .add("type", type)
                .add("applicationName", applicationName)
                .add("sequentialReadPercent", sequentialReadPercent)
                .add("thinLun", thinLun)
                .add("targetIops", targetIops)
                .add("bThinLun", bThinLun)
                .add("concurrentProcesses", concurrentProcesses)
                .add("cacheHitWritePercent", cacheHitWritePercent)
                .add("sequentialWritePercent", sequentialWritePercent)
                .add("description", description)
                .add("id", id)
                .add("fastCache", fastCache)
                .add("deduplication", deduplication)
                .toString();
    }
}
