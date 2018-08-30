package com.lu.justin.tool.dao.dto;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

public class BaseDTO implements Serializable {

    public final static String SYS = "SYS";

    @Id
    protected String id;
    protected Date createdAt;
    protected String createdBy;
    protected Date updatedAt;
    protected String updatedBy;

    public void setBaseInfo() {
        this.createdAt = new Date();
        this.createdBy = SYS;
        this.updatedBy = SYS;
        this.updatedAt = (Date) this.createdAt.clone();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
