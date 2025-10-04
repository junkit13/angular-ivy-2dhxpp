package com.example.coreenvproxy.controller;

import com.example.coreenvproxy.entity.CepEnvironment;
import com.example.coreenvproxy.entity.CepMonitorList;
import com.example.coreenvproxy.exception.LookupException;
import com.example.coreenvproxy.service.EnvironmentService;
import com.example.coreenvproxy.service.MonitorListService;
import com.example.coreenvproxy.service.SettingService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping
public class HomeController {

    private static final int PAGE_SIZE = 200;
    private final SettingService settingService;
    private final EnvironmentService environmentService;
    private final MonitorListService monitorListService;

    public HomeController(SettingService settingService,
                          EnvironmentService environmentService,
                          MonitorListService monitorListService) {
        this.settingService = settingService;
        this.environmentService = environmentService;
        this.monitorListService = monitorListService;
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "search", required = false) String search,
                       Model model) {
        int currentPage = Math.max(page, 0);
        String headerTitle = settingService.getSettingValue(SettingService.HEADER_TITLE_KEY, "Core Environment Proxy (CEP)");
        String passthroughValue = settingService.getSettingValue(SettingService.PASSTHROUGH_KEY, "off");
        boolean passthroughOn = "on".equalsIgnoreCase(passthroughValue);
        String defaultEnv = settingService.getSettingValue(SettingService.DEFAULT_ENV_KEY, "");
        String defaultEnvDisplay = StringUtils.hasText(defaultEnv) ? defaultEnv : "Not set";
        String passthroughDisplay = StringUtils.hasText(passthroughValue) ? passthroughValue.trim() : "off";
        List<CepEnvironment> environments = environmentService.getAllEnvironments();
        Page<CepMonitorList> monitorPage = monitorListService.findMonitorLists(search, currentPage, PAGE_SIZE);

        model.addAttribute("headerTitle", headerTitle);
        model.addAttribute("passthroughOn", passthroughOn);
        model.addAttribute("passthroughValue", passthroughDisplay);
        model.addAttribute("defaultEnv", defaultEnv);
        model.addAttribute("defaultEnvDisplay", defaultEnvDisplay);
        model.addAttribute("environments", environments);
        model.addAttribute("monitorPage", monitorPage);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", monitorPage.getTotalPages());
        model.addAttribute("monitoringEnabled", !passthroughOn);
        model.addAttribute("activeMenu", "Monitor List");

        return "home";
    }

    @PostMapping("/settings/passthrough")
    public String updatePassthrough(@RequestParam("value") String value,
                                    RedirectAttributes redirectAttributes) {
        settingService.updateSetting(SettingService.PASSTHROUGH_KEY, value);
        redirectAttributes.addFlashAttribute("successMessage", "Passthrough mode updated");
        return "redirect:/";
    }

    @PostMapping("/settings/default-environment")
    public String updateDefaultEnvironment(@RequestParam("defaultEnv") String defaultEnv,
                                           RedirectAttributes redirectAttributes) {
        if (!StringUtils.hasText(defaultEnv)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Default environment cannot be empty");
            return "redirect:/";
        }
        settingService.updateSetting(SettingService.DEFAULT_ENV_KEY, defaultEnv);
        redirectAttributes.addFlashAttribute("successMessage", "Default environment updated");
        return "redirect:/";
    }

    @PostMapping("/monitor/update-environment")
    public String updateMonitorEnvironment(@RequestParam("accountId") String accountId,
                                           @RequestParam("envId") String envId,
                                           RedirectAttributes redirectAttributes) {
        monitorListService.updateEnvironment(accountId, envId);
        redirectAttributes.addFlashAttribute("successMessage", "Monitor record updated");
        return "redirect:/";
    }

    @PostMapping("/monitor/delete")
    public String deleteMonitorRecords(@RequestParam(value = "selectedAccounts", required = false) List<String> accountIds,
                                       RedirectAttributes redirectAttributes) {
        if (accountIds == null || accountIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one record to delete");
            return "redirect:/";
        }
        monitorListService.deleteByAccountIds(accountIds);
        redirectAttributes.addFlashAttribute("successMessage", "Selected monitor records deleted");
        return "redirect:/";
    }

    @PostMapping("/monitor/add")
    public String addMonitorRecords(@RequestParam("envId") String envId,
                                    @RequestParam("mode") String mode,
                                    @RequestParam(value = "accountId", required = false) String accountId,
                                    @RequestParam(value = "uploadFile", required = false) MultipartFile file,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (!StringUtils.hasText(envId)) {
                throw new LookupException("Environment is required");
            }
            List<String> accountIds = new ArrayList<>();
            if (!"file".equals(mode)) {
                if (!StringUtils.hasText(accountId)) {
                    throw new LookupException("Account ID is required");
                }
                accountIds.add(accountId.trim());
            } else {
                accountIds = parseAccountIdsFromFile(file);
            }
            monitorListService.addMonitorRecords(envId, accountIds);
            redirectAttributes.addFlashAttribute("successMessage", "Monitor records added");
        } catch (LookupException | IOException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/";
    }

    private List<String> parseAccountIdsFromFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new LookupException("Please select a file to upload");
        }
        Set<String> ids = new LinkedHashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("[\\s,;]+");
                for (String token : tokens) {
                    if (StringUtils.hasText(token)) {
                        ids.add(token.trim());
                    }
                }
                if (ids.size() > 1000) {
                    throw new LookupException("Maximum Record 1000");
                }
            }
        }
        if (ids.isEmpty()) {
            throw new LookupException("Uploaded file does not contain any account IDs");
        }
        return new ArrayList<>(ids);
    }
}
