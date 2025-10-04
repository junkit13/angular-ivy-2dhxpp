package com.example.coreenvproxy.repository;

import com.example.coreenvproxy.entity.CepMonitorList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CepMonitorListRepository extends JpaRepository<CepMonitorList, String> {

    Page<CepMonitorList> findByAccountIdContainingIgnoreCase(String accountId, Pageable pageable);
}
