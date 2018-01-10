package com.pdp.bkresv2.model;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Created by vutuan on 12/12/2017.
 */

public class Graph{
    private String name_graph;
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;

    public Graph(String name_graph, ArrayList<Entry> entries, ArrayList<String> labels) {
        this.name_graph = name_graph;
        this.entries = entries;
        this.labels = labels;
    }

    public String getName_graph() {
        return name_graph;
    }

    public void setName_graph(String name_graph) {
        this.name_graph = name_graph;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public Graph() {

    }
}
