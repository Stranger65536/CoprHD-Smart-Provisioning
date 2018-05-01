/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.exception;

public class StartupException extends RuntimeException {
    public StartupException() {
    }

    public StartupException(final String message) {
        super(message);
    }

    public StartupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StartupException(final Throwable cause) {
        super(cause);
    }

    public StartupException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
