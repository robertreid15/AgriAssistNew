package com.robertreid.farm.system.barcodes.model;

public class EditItemObject {

    public String title;
    public String manufacturer;
    public String price;
    public String category;

    public EditItemObject(){

    }

    public EditItemObject(String title, String manufacturer, String price, String category) {
        //this.itemId = itemId;
        this.title = title;
        this.manufacturer = manufacturer;
        this.price = price;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


}
