package com.example.coreenvproxy.config;

import com.example.coreenvproxy.entity.CepEnvironment;
import com.example.coreenvproxy.entity.CepSetting;
import com.example.coreenvproxy.repository.CepEnvironmentRepository;
import com.example.coreenvproxy.repository.CepSettingRepository;
import com.example.coreenvproxy.service.SettingService;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.example.coreenvproxy.service.SettingService.HEADER_TITLE_KEY;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CepSettingRepository settingRepository;
    private final CepEnvironmentRepository environmentRepository;

    public DataInitializer(CepSettingRepository settingRepository,
                           CepEnvironmentRepository environmentRepository) {
        this.settingRepository = settingRepository;
        this.environmentRepository = environmentRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        settingRepository.findById("header").ifPresent(legacy -> {
            if (!settingRepository.existsById(HEADER_TITLE_KEY)) {
                settingRepository.save(new CepSetting(HEADER_TITLE_KEY, legacy.getSettingValue()));
            }
            settingRepository.delete(legacy);
        });

        Map<String, String> defaults = Map.ofEntries(
                Map.entry(SettingService.HEADER_TITLE_KEY, "Core Environment Proxy (CEP)"),
                Map.entry(SettingService.PASSTHROUGH_KEY, "off"),
                Map.entry(SettingService.DEFAULT_ENV_KEY, "DEV"),
                Map.entry(SettingService.DEMO_MODE_KEY, "off"),
                Map.entry(SettingService.CONNECTION_TIMEOUT_KEY, "5"),
                Map.entry(SettingService.READ_TIMEOUT_KEY, "10"),
                Map.entry(SettingService.WRITE_TIMEOUT_KEY, "5"),
                Map.entry(SettingService.LOOKUP_ENV_KEY, "AppId"),
                Map.entry(SettingService.LOOKUP_ACCOUNT_KEY, "ACCTID"),
                Map.entry(SettingService.LOOKUP_CIF_KEY, "CIFNUM"),
                Map.entry(SettingService.LOOKUP_ID_KEY, "IDNUM"),
                Map.entry(SettingService.LOOKUP_NAME_KEY, "NAME1"),
                Map.entry(SettingService.LOOKUP_URL_KEY, ""),
                Map.entry(SettingService.LOOKUP_HEADER_KEY, "{}"),
                Map.entry(SettingService.LOOKUP_BODY_KEY, "{}"),
                Map.entry(SettingService.ESB_DOMAIN_KEY, "")
        );

        defaults.forEach((key, value) -> {
            if (!settingRepository.existsById(key)) {
                settingRepository.save(new CepSetting(key, value));
            }
        });

        if (environmentRepository.count() == 0) {
            environmentRepository.saveAll(List.of(
                    new CepEnvironment("DEV"),
                    new CepEnvironment("SIT"),
                    new CepEnvironment("UAT"),
                    new CepEnvironment("PROD")
            ));
        }
    }
}
