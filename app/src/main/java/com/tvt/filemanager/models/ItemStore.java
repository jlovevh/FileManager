package com.tvt.filemanager.models;

import java.util.Date;

public class ItemStore {

    private String name;
    private Date lastModifier;
    private String extent;
    private String path;

    public ItemStore() {
    }

    public ItemStore(String name, Date lastModifier, String extent, String path) {
        this.name = name;
        this.lastModifier = lastModifier;
        this.extent = extent;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(Date lastModifier) {
        this.lastModifier = lastModifier;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
