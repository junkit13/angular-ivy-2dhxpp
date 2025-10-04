package com.example.coreenvproxy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CEP_Environments")
public class CepEnvironment {

    @Id
    @Column(name = "envId", length = 20)
    private String envId;

    public CepEnvironment() {
    }

    public CepEnvironment(String envId) {
        this.envId = envId;
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }
}
