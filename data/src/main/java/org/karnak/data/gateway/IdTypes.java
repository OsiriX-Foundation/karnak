package org.karnak.data.gateway;

public enum IdTypes {
    PID("pid"),
    EXTID("extid"),
    EXTID_IN_INSTANCE("extid");

    private String value;

    IdTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
