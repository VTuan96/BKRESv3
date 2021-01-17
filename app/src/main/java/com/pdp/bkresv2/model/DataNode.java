package com.pdp.bkresv2.model;

import java.io.Serializable;

public class DataNode implements Serializable {
    private int _id;
    private String data;
    private int id_node;

    public DataNode() {
    }

    public DataNode(int _id, String data, int id_node) {
        this._id = _id;
        this.data = data;
        this.id_node = id_node;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getId_node() {
        return id_node;
    }

    public void setId_node(int id_node) {
        this.id_node = id_node;
    }

    @Override
    public String toString() {
        return "DataNode{" +
                "_id=" + _id +
                ", data='" + data + '\'' +
                ", id_node=" + id_node +
                '}';
    }
}
