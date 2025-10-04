package com.example.coreenvproxy.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdvancedSettingsForm {

    @NotBlank
    private String headerTitle;

    private boolean demoMode;

    private boolean passthroughMode;

    private String esbDomain;

    @NotBlank
    @Pattern(regexp = "\\d+", message = "Connection timeout must be numeric")
    private String connectionTimeout;

    @NotBlank
    @Pattern(regexp = "\\d+", message = "Read timeout must be numeric")
    private String readTimeout;

    @NotBlank
    @Pattern(regexp = "\\d+", message = "Write timeout must be numeric")
    private String writeTimeout;

    private String lookupUrl;
    private String lookupHeader;
    private String lookupBody;
    private String lookupEnvKey;
    private String lookupAccountKey;
    private String lookupCifKey;
    private String lookupIdKey;
    private String lookupNameKey;

    public boolean isDemoMode() {
        return demoMode;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public void setDemoMode(boolean demoMode) {
        this.demoMode = demoMode;
    }

    public boolean isPassthroughMode() {
        return passthroughMode;
    }

    public void setPassthroughMode(boolean passthroughMode) {
        this.passthroughMode = passthroughMode;
    }

    public String getEsbDomain() {
        return esbDomain;
    }

    public void setEsbDomain(String esbDomain) {
        this.esbDomain = esbDomain;
    }

    public String getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(String connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(String readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(String writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public String getLookupUrl() {
        return lookupUrl;
    }

    public void setLookupUrl(String lookupUrl) {
        this.lookupUrl = lookupUrl;
    }

    public String getLookupHeader() {
        return lookupHeader;
    }

    public void setLookupHeader(String lookupHeader) {
        this.lookupHeader = lookupHeader;
    }

    public String getLookupBody() {
        return lookupBody;
    }

    public void setLookupBody(String lookupBody) {
        this.lookupBody = lookupBody;
    }

    public String getLookupEnvKey() {
        return lookupEnvKey;
    }

    public void setLookupEnvKey(String lookupEnvKey) {
        this.lookupEnvKey = lookupEnvKey;
    }

    public String getLookupAccountKey() {
        return lookupAccountKey;
    }

    public void setLookupAccountKey(String lookupAccountKey) {
        this.lookupAccountKey = lookupAccountKey;
    }

    public String getLookupCifKey() {
        return lookupCifKey;
    }

    public void setLookupCifKey(String lookupCifKey) {
        this.lookupCifKey = lookupCifKey;
    }

    public String getLookupIdKey() {
        return lookupIdKey;
    }

    public void setLookupIdKey(String lookupIdKey) {
        this.lookupIdKey = lookupIdKey;
    }

    public String getLookupNameKey() {
        return lookupNameKey;
    }

    public void setLookupNameKey(String lookupNameKey) {
        this.lookupNameKey = lookupNameKey;
    }
}
