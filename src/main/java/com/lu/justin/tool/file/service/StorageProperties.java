package com.lu.justin.tool.file.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

//@Configuration
@ConfigurationProperties("store")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "upload_dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
