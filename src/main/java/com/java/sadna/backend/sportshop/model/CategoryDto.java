package com.java.sadna.backend.sportshop.model;

public class CategoryDto implements BaseDto {

    private final Long id;
    private final String name;
    private final String iconFilename;

    public CategoryDto(Long id, String name, String iconFilename) {
        this.id = id;
        this.name = name;
        this.iconFilename = iconFilename;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconFilename() {
        return iconFilename;
    }
}
