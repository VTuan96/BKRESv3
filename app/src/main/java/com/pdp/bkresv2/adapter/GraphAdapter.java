package com.pdp.bkresv2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pdp.bkresv2.R;
import com.pdp.bkresv2.model.Graph;

import java.util.ArrayList;

/**
 * Created by vutuan on 12/12/2017.
 */

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {
    ArrayList<Graph> listGraph;

    public GraphAdapter(ArrayList<Graph> listGraph) {
        this.listGraph = listGraph;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bieu_do_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Graph graph=listGraph.get(position);

        if (graph!=null){
            //set ten bieu do
            holder.txtTenBieuDo.setText(graph.getName_graph());

            //set du lieu cho bieu do
            LineDataSet dataset = new LineDataSet(graph.getEntries(), graph.getName_graph());
            LineData data = new LineData(graph.getLabels(), dataset);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
            dataset.setDrawCubic(true);
            dataset.setDrawFilled(true);
            holder.graph.setData(data);
            holder.graph.animateY(3000);
        }

    }

    @Override
    public int getItemCount() {
        return listGraph.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTenBieuDo;
        public LineChart graph;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTenBieuDo= (TextView) itemView.findViewById(R.id.txt_TenBieuDo);
            graph= (LineChart) itemView.findViewById(R.id.graph);
        }
    }
}
