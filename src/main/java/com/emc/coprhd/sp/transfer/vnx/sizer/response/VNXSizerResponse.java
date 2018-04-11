/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.transfer.vnx.sizer.response;

import com.emc.coprhd.sp.json.vnxszier.ResponseBody;
import com.emc.coprhd.sp.json.vnxszier.ResponseError;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

public class VNXSizerResponse {
    private static final String ERROR_LEVEL = "errorLevel";
    private static final String RESPONCE_BODY = "responceBody";
    private static final String RESPONCE_ERROR_BODY = "responceErrorBody";

    private final Long errorLevel;
    private final List<ResponseBody> responseBody;
    private final List<ResponseError> responseErrorBody;

    @JsonCreator
    public VNXSizerResponse(
            @JsonProperty(ERROR_LEVEL) final Long errorLevel,
            @JsonProperty(RESPONCE_BODY) final List<ResponseBody> responseBody,
            @JsonProperty(RESPONCE_ERROR_BODY) final List<ResponseError> responseErrorBody) {
        this.errorLevel = errorLevel;
        this.responseBody = responseBody;
        this.responseErrorBody = responseErrorBody;
    }

    public Long getErrorLevel() {
        return errorLevel;
    }

    public List<ResponseBody> getResponseBody() {
        return responseBody;
    }

    public List<ResponseError> getResponseErrorBody() {
        return responseErrorBody;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errorLevel", errorLevel)
                .add("responseBody", responseBody)
                .add("responseErrorBody", responseErrorBody)
                .toString();
    }
}
