package com.example.coreenvproxy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CEP_Setting")
public class CepSetting {

    @Id
    @Column(name = "settingId", length = 50)
    private String settingId;

    @Column(name = "settingValue", length = 1000)
    private String settingValue;

    public CepSetting() {
    }

    public CepSetting(String settingId, String settingValue) {
        this.settingId = settingId;
        this.settingValue = settingValue;
    }

    public String getSettingId() {
        return settingId;
    }

    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }
}
