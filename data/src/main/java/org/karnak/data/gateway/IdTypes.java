package org.karnak.data.gateway;

public enum IdTypes {
    PID("pid"),
    EXTID("extid"),
    ADD_EXTID("extid");

    private String value;

    IdTypes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
