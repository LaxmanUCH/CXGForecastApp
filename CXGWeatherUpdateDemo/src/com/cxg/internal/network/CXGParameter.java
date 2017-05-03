package com.cxg.internal.network;

/**
 * Created by CXG Pvt Ltd., Singapore.
 */

public class CXGParameter {

    private String key;
    private String value;

   public CXGParameter(String key,String value) {
        setKey(key);
        setValue(value);
    }
    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
