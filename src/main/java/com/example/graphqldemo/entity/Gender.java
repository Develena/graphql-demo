package com.example.graphqldemo.entity;

public enum Gender {

    M("M"),
    F("F")
    ;

    private String value;

    Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
