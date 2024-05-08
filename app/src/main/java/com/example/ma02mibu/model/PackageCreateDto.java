package com.example.ma02mibu.model;

public class PackageCreateDto {
    private String name;
    private String description;
    private boolean availableToBuy;
    private boolean visible;

    public PackageCreateDto(){
        name="";
        description="";
    }
    public PackageCreateDto(String name, String description, boolean availableToBuy, boolean visible) {
        this.name = name;
        this.description = description;
        this.availableToBuy = availableToBuy;
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAvailableToBuy() {
        return availableToBuy;
    }

    public void setAvailableToBuy(boolean availableToBuy) {
        this.availableToBuy = availableToBuy;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
