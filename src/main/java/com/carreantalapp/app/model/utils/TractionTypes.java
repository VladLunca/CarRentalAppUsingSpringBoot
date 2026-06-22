package com.carreantalapp.app.model.utils;
public enum TractionTypes {
    FWD("FWD"),
    RWD("RWD"),
    AWD("AWD");

    private final String value;
    TractionTypes(String value) {
        this.value =value;
    }

    public String getValue() {
        return value;
    }

    public static TractionTypes fromDbValue(String value) {
        for (TractionTypes type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown traction type: " + value);
    }
}