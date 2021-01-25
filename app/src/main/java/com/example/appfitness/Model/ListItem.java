package com.example.appfitness.Model;

public class ListItem {
    private String name;
    private String description;
    private String btn_home;

    public ListItem(String name, String description, String btn_home) {
        this.name = name;
        this.description = description;
        this.btn_home = btn_home;
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

    public String getButton() {
        return btn_home;
    }

    public void setButton(String btn_home) {
        this.btn_home = btn_home;
    }

}
