package com.sajawal.imdb;

public class Executive_Production_Company {
    String search;
    String name;
    String logo;
    String description;
    String founded;
    String id;
    public Executive_Production_Company(){


    }

    public Executive_Production_Company(String search, String name, String logo, String description, String founded, String id) {
        this.search = search;
        this.name = name;
        this.logo = logo;
        this.description = description;
        this.founded = founded;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFounded() {
        return founded;
    }

    public void setFounded(String founded) {
        this.founded = founded;
    }
}
