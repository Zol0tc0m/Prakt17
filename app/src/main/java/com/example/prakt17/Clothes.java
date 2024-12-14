package com.example.prakt17;

public class Clothes {
    private String id;
    private String name;
    private String description;
    private String price;
    private String imageBase64; // Фото в формате Base64

    public Clothes(String id, String name, String description, String price, String imageBase64) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageBase64 = imageBase64;
    }

    public String getId() {
        return id;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}