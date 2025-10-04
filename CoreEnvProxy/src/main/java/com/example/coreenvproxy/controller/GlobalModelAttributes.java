package com.example.coreenvproxy.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("menuItems")
    public List<String> menuItems() {
        return List.of("Monitor List", "Undetected Record", "View Last Transaction", "Environment", "Advance Setting");
    }
}
