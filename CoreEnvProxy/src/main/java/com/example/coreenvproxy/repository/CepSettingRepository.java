package com.example.coreenvproxy.repository;

import com.example.coreenvproxy.entity.CepSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CepSettingRepository extends JpaRepository<CepSetting, String> {
}
