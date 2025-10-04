package com.example.coreenvproxy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "CEP_MonitorLists")
public class CepMonitorList {

    @Id
    @Column(name = "accountId", length = 12)
    private String accountId;

    @Column(name = "cifNum", length = 20)
    private String cifNum;

    @Column(name = "idNum", length = 25)
    private String idNum;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "envId", length = 20)
    private String envId;

    public CepMonitorList() {
    }

    public CepMonitorList(String accountId, String cifNum, String idNum, String name, String envId) {
        this.accountId = accountId;
        this.cifNum = cifNum;
        this.idNum = idNum;
        this.name = name;
        this.envId = envId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCifNum() {
        return cifNum;
    }

    public void setCifNum(String cifNum) {
        this.cifNum = cifNum;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnvId() {
        return envId;
    }

    public void setEnvId(String envId) {
        this.envId = envId;
    }
}
