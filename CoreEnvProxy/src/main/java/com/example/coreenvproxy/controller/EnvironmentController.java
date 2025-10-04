package com.example.coreenvproxy.controller;

import com.example.coreenvproxy.entity.CepEnvironment;
import com.example.coreenvproxy.service.EnvironmentService;
import com.example.coreenvproxy.service.SettingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class EnvironmentController {

    private final EnvironmentService environmentService;
    private final SettingService settingService;

    public EnvironmentController(EnvironmentService environmentService,
                                 SettingService settingService) {
        this.environmentService = environmentService;
        this.settingService = settingService;
    }

    @ModelAttribute("activeMenu")
    public String activeMenu() {
        return "Environment";
    }

    @GetMapping("/environments")
    public String environments(Model model) {
        model.addAttribute("environments", environmentService.getAllEnvironments());
        return "environment";
    }

    @PostMapping("/environments")
    public String saveEnvironments(@RequestParam(value = "envIds", required = false) List<String> envIds,
                                   RedirectAttributes redirectAttributes) {
        if (envIds == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide at least one environment");
            return "redirect:/environments";
        }
        Set<String> uniqueIds = envIds.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (uniqueIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide at least one environment");
            return "redirect:/environments";
        }
        List<CepEnvironment> environments = uniqueIds.stream()
                .map(CepEnvironment::new)
                .collect(Collectors.toList());
        environmentService.saveAll(environments);

        String defaultEnv = settingService.getSettingValue(SettingService.DEFAULT_ENV_KEY, "");
        if (!uniqueIds.contains(defaultEnv) && !uniqueIds.isEmpty()) {
            settingService.updateSetting(SettingService.DEFAULT_ENV_KEY, uniqueIds.iterator().next());
        }

        redirectAttributes.addFlashAttribute("successMessage", "Environments saved");
        return "redirect:/environments";
    }
}
