package com.example.coreenvproxy.service;

import com.example.coreenvproxy.entity.CepMonitorList;
import com.example.coreenvproxy.exception.LookupException;
import com.example.coreenvproxy.model.MonitorProfile;
import com.example.coreenvproxy.repository.CepMonitorListRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MonitorListService {

    private final CepMonitorListRepository repository;
    private final ProfileLookupService profileLookupService;
    private final CacheManager cacheManager;

    public MonitorListService(CepMonitorListRepository repository,
                              ProfileLookupService profileLookupService,
                              CacheManager cacheManager) {
        this.repository = repository;
        this.profileLookupService = profileLookupService;
        this.cacheManager = cacheManager;
    }

    public Page<CepMonitorList> findMonitorLists(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("accountId"));
        if (searchTerm != null && !searchTerm.isBlank()) {
            return repository.findByAccountIdContainingIgnoreCase(searchTerm.trim(), pageable);
        }
        return repository.findAll(pageable);
    }

    @Transactional
    public void deleteByAccountIds(List<String> accountIds) {
        if (accountIds == null || accountIds.isEmpty()) {
            return;
        }
        repository.deleteAllById(accountIds);
        Cache cache = cacheManager.getCache("monitorLists");
        if (cache != null) {
            accountIds.forEach(cache::evict);
        }
    }

    @Transactional
    public void updateEnvironment(String accountId, String envId) {
        Optional<CepMonitorList> optional = repository.findById(accountId);
        optional.ifPresent(record -> {
            record.setEnvId(envId);
            repository.save(record);
            cacheRecord(record);
        });
    }

    @Transactional
    public void addMonitorRecords(String envId, List<String> accountIds) {
        if (accountIds == null || accountIds.isEmpty()) {
            throw new LookupException("Account ID list cannot be empty");
        }
        List<MonitorProfile> profiles = profileLookupService.lookupProfiles(envId, accountIds);
        List<CepMonitorList> entities = profiles.stream()
                .map(profile -> new CepMonitorList(
                        profile.getAccountId(),
                        profile.getCifNumber(),
                        profile.getIdNumber(),
                        profile.getName(),
                        envId))
                .collect(Collectors.toList());
        repository.saveAll(entities);
        entities.forEach(this::cacheRecord);
    }

    private void cacheRecord(CepMonitorList record) {
        Cache cache = cacheManager.getCache("monitorLists");
        if (cache != null) {
            cache.put(record.getAccountId(), record);
        }
    }

    public List<CepMonitorList> getAllCached() {
        return repository.findAll();
    }
}
