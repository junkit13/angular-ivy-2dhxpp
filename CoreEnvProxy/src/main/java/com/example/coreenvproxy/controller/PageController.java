package com.example.coreenvproxy.controller;

import com.example.coreenvproxy.repository.UserGroupRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final UserGroupRepository userGroupRepository;

    public PageController(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("userGroups", userGroupRepository.findAll());
        return "home";
    }
}
