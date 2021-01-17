package com.pdp.bkresv2.model;

import java.io.Serializable;

public class Project implements Serializable {
    private String _id;
    private String name;
    private String description;
    private int status;
    private String id_user;

    public Project() {
    }

    public Project(String _id, String name, String description, int status, String id_user) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.id_user = id_user;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    @Override
    public String toString() {
        return "Project{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id_user=" + id_user +
                '}';
    }
}
