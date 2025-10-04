package com.example.coreenvproxy.repository;

import com.example.coreenvproxy.entity.CepEnvironment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CepEnvironmentRepository extends JpaRepository<CepEnvironment, String> {
}
