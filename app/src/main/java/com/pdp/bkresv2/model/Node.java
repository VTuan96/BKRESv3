package com.pdp.bkresv2.model;

import org.json.JSONException;

import java.io.Serializable;

public class Node implements Serializable {
    private String _id;
    private String name;
    private String description;
    private int status;
    private String id_server_gen;
    private String id_gateway_server_gen;
    private String id_project;
    private String id_gateway;
    private String id_protocol;
    private String id_communication;
    private String key;
    private int alive;

    public Node() {
        _id = "";
        name = "";
        description = "";
        id_server_gen = "";
        id_gateway_server_gen = "";
        id_project = "";
        id_gateway = "";
        id_protocol = "";
        id_communication = "";
        key = "";
    }

    public Node(String _id, String name, String description, int status, String id_server_gen, String id_gateway_server_gen, String id_project, String id_gateway, String id_protocol, String id_communication, String key, int alive) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.id_server_gen = id_server_gen;
        this.id_gateway_server_gen = id_gateway_server_gen;
        this.id_project = id_project;
        this.id_gateway = id_gateway;
        this.id_protocol = id_protocol;
        this.id_communication = id_communication;
        this.key = key;
        this.alive = alive;
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

    public String getId_server_gen() {
        return id_server_gen;
    }

    public void setId_server_gen(String id_server_gen) {
        this.id_server_gen = id_server_gen;
    }

    public String getId_gateway_server_gen() {
        return id_gateway_server_gen;
    }

    public void setId_gateway_server_gen(String id_gateway_server_gen) {
        this.id_gateway_server_gen = id_gateway_server_gen;
    }

    public String getId_project() {
        return id_project;
    }

    public void setId_project(String id_project) {
        this.id_project = id_project;
    }

    public String getId_gateway() {
        return id_gateway;
    }

    public void setId_gateway(String id_gateway) {
        this.id_gateway = id_gateway;
    }

    public String getId_protocol() {
        return id_protocol;
    }

    public void setId_protocol(String id_protocol) {
        this.id_protocol = id_protocol;
    }

    public String getId_communication() {
        return id_communication;
    }

    public void setId_communication(String id_communication) {
        this.id_communication = id_communication;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getAlive() {
        return alive;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {
        return "Node{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id_server_gen=" + id_server_gen +
                ", id_gateway_server_gen=" + id_gateway_server_gen +
                ", id_project=" + id_project +
                ", id_gateway=" + id_gateway +
                ", id_protocol=" + id_protocol +
                ", id_communication=" + id_communication +
                ", key='" + key + '\'' +
                ", alive=" + alive +
                '}';
    }
}
