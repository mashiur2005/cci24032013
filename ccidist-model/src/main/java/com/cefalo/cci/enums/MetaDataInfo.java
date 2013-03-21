package com.cefalo.cci.enums;

public enum MetaDataInfo {
    DC_IDENTIFIER("dc:identifier"),
    DC_CREATOR("dc:creator"),
    DC_TITLE("dc:title"),
    DC_LANGUAGE("dc:language"),
    DC_ISSUE("dc:issue"),
    DC_DEVICE("dc:device"),
    DC_DATE("dc:date"),
    DC_META("meta");

    private String value;
    private MetaDataInfo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
