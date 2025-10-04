package com.example.coreenvproxy.model;

public class MonitorProfile {
    private final String accountId;
    private final String cifNumber;
    private final String idNumber;
    private final String name;

    public MonitorProfile(String accountId, String cifNumber, String idNumber, String name) {
        this.accountId = accountId;
        this.cifNumber = cifNumber;
        this.idNumber = idNumber;
        this.name = name;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCifNumber() {
        return cifNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public String getName() {
        return name;
    }
}
