package com.pdp.bkresv2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Created by vutuan on 12/12/2017.
 */

public class Graph implements Parcelable{
    private String name_graph;
    private ArrayList<Entry> entries;
    private ArrayList<String> labels;

    public Graph(String name_graph, ArrayList<Entry> entries, ArrayList<String> labels) {
        this.name_graph = name_graph;
        this.entries = entries;
        this.labels = labels;
    }

    protected Graph(Parcel in) {
        name_graph = in.readString();
        entries = in.createTypedArrayList(Entry.CREATOR);
        labels = in.createStringArrayList();
    }

    public static final Creator<Graph> CREATOR = new Creator<Graph>() {
        @Override
        public Graph createFromParcel(Parcel in) {
            return new Graph(in);
        }

        @Override
        public Graph[] newArray(int size) {
            return new Graph[size];
        }
    };

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

    public void resetEntry() {
        this.entries = new ArrayList<Entry>();
    }

    public Graph() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name_graph);
        parcel.writeTypedList(entries);
        parcel.writeStringList(labels);
    }
}
