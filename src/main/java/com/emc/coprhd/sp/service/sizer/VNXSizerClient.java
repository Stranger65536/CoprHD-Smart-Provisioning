/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.service.sizer;

import com.emc.coprhd.sp.transfer.vnx.sizer.request.VNXSizerRequest;
import com.emc.coprhd.sp.transfer.vnx.sizer.response.VNXSizerResponse;

@FunctionalInterface
public interface VNXSizerClient {
    VNXSizerResponse processSizerRequest(final VNXSizerRequest request);
}
