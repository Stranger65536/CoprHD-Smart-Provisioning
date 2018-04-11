/*
 * Copyright 1994-2018 EMC Corporation. All rights reserved.
 */
package com.emc.coprhd.sp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @SuppressWarnings("SameReturnValue")
    @GetMapping(ContextPaths.ROOT)
    public String mainPage() {
        return "redirect:/pool-manager";
    }
}
