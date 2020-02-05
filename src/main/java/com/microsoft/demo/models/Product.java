package com.microsoft.demo.models;

public class Product {

    private int id;

    private String name;

    private String description;

    private int version;

    public Product()
    {

    }

    public Product(int id, String name, String desc, int v)
    {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.version = v;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
