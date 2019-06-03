package com.robertreid.farm.system.barcodes.model;

public class Item {

    private String value;
    private Long date;

    public Item(String value, Long date) {
        this.value = value;
        this.date = date;
    }

    public Item() {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

}
