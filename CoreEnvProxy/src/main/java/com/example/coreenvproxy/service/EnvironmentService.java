package com.example.coreenvproxy.service;

import com.example.coreenvproxy.entity.CepEnvironment;
import com.example.coreenvproxy.repository.CepEnvironmentRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnvironmentService {

    private final CepEnvironmentRepository repository;
    private final CacheManager cacheManager;

    public EnvironmentService(CepEnvironmentRepository repository, CacheManager cacheManager) {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    @Cacheable("environments")
    public List<CepEnvironment> getAllEnvironments() {
        return repository.findAll(Sort.by("envId"));
    }

    @Transactional
    public void saveAll(List<CepEnvironment> environments) {
        repository.deleteAll();
        repository.saveAll(environments);
        refreshCache();
    }

    @Transactional
    public void deleteByIds(List<String> envIds) {
        envIds.forEach(repository::deleteById);
        refreshCache();
    }

    private void refreshCache() {
        Cache cache = cacheManager.getCache("environments");
        if (cache != null) {
            cache.put(SimpleKey.EMPTY, new ArrayList<>(repository.findAll(Sort.by("envId"))));
        }
    }
}
