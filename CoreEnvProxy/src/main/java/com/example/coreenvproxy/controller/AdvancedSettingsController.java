package com.example.coreenvproxy.controller;

import com.example.coreenvproxy.model.AdvancedSettingsForm;
import com.example.coreenvproxy.service.SettingService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class AdvancedSettingsController {

    private final SettingService settingService;

    public AdvancedSettingsController(SettingService settingService) {
        this.settingService = settingService;
    }

    @ModelAttribute("activeMenu")
    public String activeMenu() {
        return "Advance Setting";
    }

    @GetMapping("/advanced-settings")
    public String advancedSettings(Model model) {
        AdvancedSettingsForm form = new AdvancedSettingsForm();
        form.setHeaderTitle(settingService.getSettingValue(SettingService.HEADER_TITLE_KEY, "Core Environment Proxy (CEP)"));
        form.setDemoMode(settingService.isOn(SettingService.DEMO_MODE_KEY));
        form.setPassthroughMode(settingService.isOn(SettingService.PASSTHROUGH_KEY));
        form.setEsbDomain(settingService.getSettingValue(SettingService.ESB_DOMAIN_KEY, ""));
        form.setConnectionTimeout(settingService.getSettingValue(SettingService.CONNECTION_TIMEOUT_KEY, "5"));
        form.setReadTimeout(settingService.getSettingValue(SettingService.READ_TIMEOUT_KEY, "10"));
        form.setWriteTimeout(settingService.getSettingValue(SettingService.WRITE_TIMEOUT_KEY, "5"));
        form.setLookupUrl(settingService.getSettingValue(SettingService.LOOKUP_URL_KEY, ""));
        form.setLookupHeader(settingService.getSettingValue(SettingService.LOOKUP_HEADER_KEY, "{}"));
        form.setLookupBody(settingService.getSettingValue(SettingService.LOOKUP_BODY_KEY, "{}"));
        form.setLookupEnvKey(settingService.getSettingValue(SettingService.LOOKUP_ENV_KEY, "AppId"));
        form.setLookupAccountKey(settingService.getSettingValue(SettingService.LOOKUP_ACCOUNT_KEY, "ACCTID"));
        form.setLookupCifKey(settingService.getSettingValue(SettingService.LOOKUP_CIF_KEY, "CIFNUM"));
        form.setLookupIdKey(settingService.getSettingValue(SettingService.LOOKUP_ID_KEY, "IDNUM"));
        form.setLookupNameKey(settingService.getSettingValue(SettingService.LOOKUP_NAME_KEY, "NAME1"));
        model.addAttribute("form", form);
        return "advanced-settings";
    }

    @PostMapping("/advanced-settings")
    public String saveAdvancedSettings(@Valid @ModelAttribute("form") AdvancedSettingsForm form,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "advanced-settings";
        }

        Map<String, String> settings = new LinkedHashMap<>();
        settings.put(SettingService.HEADER_TITLE_KEY, form.getHeaderTitle().trim());
        settings.put(SettingService.DEMO_MODE_KEY, form.isDemoMode() ? "on" : "off");
        settings.put(SettingService.PASSTHROUGH_KEY, form.isPassthroughMode() ? "on" : "off");
        settings.put(SettingService.ESB_DOMAIN_KEY, valueOrEmpty(form.getEsbDomain()));
        settings.put(SettingService.CONNECTION_TIMEOUT_KEY, form.getConnectionTimeout().trim());
        settings.put(SettingService.READ_TIMEOUT_KEY, form.getReadTimeout().trim());
        settings.put(SettingService.WRITE_TIMEOUT_KEY, form.getWriteTimeout().trim());
        settings.put(SettingService.LOOKUP_URL_KEY, valueOrEmpty(form.getLookupUrl()));
        settings.put(SettingService.LOOKUP_HEADER_KEY, valueOrEmpty(form.getLookupHeader()));
        settings.put(SettingService.LOOKUP_BODY_KEY, valueOrEmpty(form.getLookupBody()));
        settings.put(SettingService.LOOKUP_ENV_KEY, valueOrEmpty(form.getLookupEnvKey()));
        settings.put(SettingService.LOOKUP_ACCOUNT_KEY, valueOrEmpty(form.getLookupAccountKey()));
        settings.put(SettingService.LOOKUP_CIF_KEY, valueOrEmpty(form.getLookupCifKey()));
        settings.put(SettingService.LOOKUP_ID_KEY, valueOrEmpty(form.getLookupIdKey()));
        settings.put(SettingService.LOOKUP_NAME_KEY, valueOrEmpty(form.getLookupNameKey()));

        settingService.updateSettings(settings);
        redirectAttributes.addFlashAttribute("successMessage", "Settings saved");
        return "redirect:/advanced-settings";
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
