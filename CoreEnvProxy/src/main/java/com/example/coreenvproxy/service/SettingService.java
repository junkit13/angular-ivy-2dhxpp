package com.example.coreenvproxy.service;

import com.example.coreenvproxy.entity.CepSetting;
import com.example.coreenvproxy.repository.CepSettingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingService {

    public static final String HEADER_TITLE_KEY = "header_title";
    public static final String PASSTHROUGH_KEY = "Passthrough";
    public static final String DEFAULT_ENV_KEY = "defaultEnv";
    public static final String DEMO_MODE_KEY = "demoMode";
    public static final String LOOKUP_ENV_KEY = "LookupEnvId";
    public static final String LOOKUP_ACCOUNT_KEY = "LookupAcctId";
    public static final String LOOKUP_CIF_KEY = "LookupCifNum";
    public static final String LOOKUP_NAME_KEY = "LookupName";
    public static final String LOOKUP_ID_KEY = "LookupIdNum";
    public static final String LOOKUP_URL_KEY = "lookup_profile_URL";
    public static final String LOOKUP_HEADER_KEY = "lookup_profile_request_header";
    public static final String LOOKUP_BODY_KEY = "lookup_profile_request_body";
    public static final String CONNECTION_TIMEOUT_KEY = "conn_timeout";
    public static final String READ_TIMEOUT_KEY = "read_timeout";
    public static final String WRITE_TIMEOUT_KEY = "write_timeout";
    public static final String ESB_DOMAIN_KEY = "ESBdomain";

    private final CepSettingRepository repository;
    private final CacheManager cacheManager;

    public SettingService(CepSettingRepository repository, CacheManager cacheManager) {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void warmCache() {
        Cache cache = cacheManager.getCache("settings");
        if (cache != null) {
            repository.findAll().forEach(setting -> cache.put(setting.getSettingId(), setting.getSettingValue()));
        }
    }

    @Cacheable(value = "settings", key = "#settingId")
    public String getSettingValue(String settingId) {
        Optional<CepSetting> optionalSetting = repository.findById(settingId);
        return optionalSetting.map(CepSetting::getSettingValue).orElse(null);
    }

    public String getSettingValue(String settingId, String defaultValue) {
        String value = getSettingValue(settingId);
        return value != null ? value : defaultValue;
    }

    public boolean isOn(String settingId) {
        return "on".equalsIgnoreCase(getSettingValue(settingId));
    }

    public Map<String, String> getSettings(List<String> keys) {
        Map<String, String> values = new HashMap<>();
        for (String key : keys) {
            values.put(key, getSettingValue(key));
        }
        return values;
    }

    @Transactional
    public void updateSetting(String key, String value) {
        repository.save(new CepSetting(key, value));
        Cache cache = cacheManager.getCache("settings");
        if (cache != null) {
            cache.put(key, value);
        }
    }

    @Transactional
    public void updateSettings(Map<String, String> settings) {
        settings.forEach(this::updateSetting);
    }
}
