/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.util;

public enum RuntimeUtils {
    ;

    public static String enterMethodMessage() {
        return getMethodName() + ".enter";
    }

    public static String exitMethodMessage() {
        return getMethodName() + ".exit";
    }

    private static String getMethodName() {
        final StackTraceElement e = Thread.currentThread().getStackTrace()[3];
        final String s = e.getClassName();
        return s.substring(s.lastIndexOf('.') + 1, s.length())
                + '.' + e.getMethodName();
    }
}
