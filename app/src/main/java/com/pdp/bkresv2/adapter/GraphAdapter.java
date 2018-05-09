package com.pdp.bkresv2.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pdp.bkresv2.R;
import com.pdp.bkresv2.activity.SettingsActivity;
import com.pdp.bkresv2.model.Graph;
import com.pdp.bkresv2.utils.Constant;

import java.util.ArrayList;

/**
 * Created by vutuan on 12/12/2017.
 */

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder> {
    ArrayList<Graph> listGraph;
    Context context;

    public GraphAdapter(ArrayList<Graph> listGraph) {
        this.listGraph = listGraph;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bieu_do_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        context=parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Graph graph=listGraph.get(position);

        if (graph!=null){
            //set ten bieu do
            String nameGraph=graph.getName_graph();
            holder.txtTenBieuDo.setText(nameGraph);

            //set du lieu cho bieu do
            if (graph.getEntries().size()>0){
                int lineChart=1; // line chart in a chart ( default =1 line)
                double min=0; //min of line chart data
                double max=0; //max of line chart data

                SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);

                if (nameGraph.equals("PH")){
                    min=preferences.getFloat(SettingsActivity.KEY_PH_MIN, Constant.DEFAULT_PH_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_PH_MAX,Constant.DEFAULT_PH_MAX);
                    lineChart=3; // 3 line chart
                } else if(nameGraph.equals("Salt")||nameGraph.equals("Muối")){
                    min=preferences.getFloat(SettingsActivity.KEY_SALT_MIN,Constant.DEFAULT_SALT_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_SALT_MAX,Constant.DEFAULT_SALT_MAX);
                    lineChart=3;
                } else if (nameGraph.equals("Temp")||nameGraph.equals("Nhiệt độ")){
                    min=preferences.getFloat(SettingsActivity.KEY_TEMP_MIN,Constant.DEFAULT_TEMP_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_TEMP_MAX,Constant.DEFAULT_TEMP_MAX);
                    lineChart=3;
                } else if (nameGraph.equals("Oxy")){
                    min=preferences.getFloat(SettingsActivity.KEY_OXY_MIN,Constant.DEFAULT_TEMP_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_OXY_MAX,Constant.DEFAULT_TEMP_MAX);
                    lineChart=3;
                } else if (nameGraph.equals("H2S")){
                    min=preferences.getFloat(SettingsActivity.KEY_H2S_MIN,Constant.DEFAULT_H2S_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_H2S_MAX,Constant.DEFAULT_H2S_MAX);
                    lineChart=3;
                } else if (nameGraph.equals("H2S Max") || nameGraph.equals("Sulfide Max")){
                    min=preferences.getFloat(SettingsActivity.KEY_H2S_MIN,Constant.DEFAULT_H2S_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_H2S_MAX,Constant.DEFAULT_H2S_MAX);
                    lineChart=3;
                } else if (nameGraph.equals("H2S Min") || nameGraph.equals("Sulfide Min")){
                    min=preferences.getFloat(SettingsActivity.KEY_H2S_MIN,Constant.DEFAULT_H2S_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_H2S_MAX,Constant.DEFAULT_H2S_MAX);
                    lineChart=3;
                }else if (nameGraph.equals("NH3")){
                    min=preferences.getFloat(SettingsActivity.KEY_NH4_MIN,Constant.DEFAULT_NH4_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_NH4_MAX,Constant.DEFAULT_NH4_MAX);
                    lineChart=3;
                }else if (nameGraph.equals("NH4 Max")){
                    min=preferences.getFloat(SettingsActivity.KEY_NH4_MIN,Constant.DEFAULT_NH4_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_NH4_MAX,Constant.DEFAULT_NH4_MAX);
                    lineChart=3;
                }else if (nameGraph.equals("NH4 Min")){
                    min=preferences.getFloat(SettingsActivity.KEY_NH4_MIN,Constant.DEFAULT_NH4_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_NH4_MAX,Constant.DEFAULT_NH4_MAX);
                    lineChart=3;
                } else if (nameGraph.equals("NO2 Min")){
                    min=preferences.getFloat(SettingsActivity.KEY_NO2_MIN,Constant.DEFAULT_NO2_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_NO2_MAX,Constant.DEFAULT_NO2_MAX);
                    lineChart=3;
                }else if (nameGraph.equals("NO2 Max")){
                    min=preferences.getFloat(SettingsActivity.KEY_NO2_MIN,Constant.DEFAULT_NO2_MIN);
                    max=preferences.getFloat(SettingsActivity.KEY_NO2_MAX,Constant.DEFAULT_NO2_MAX);
                    lineChart=3;
                }

                ArrayList<ILineDataSet> lineDataSets=new ArrayList<>();

                LineDataSet dataset = new LineDataSet(graph.getEntries(), "Current");
                dataset.setColors(Color.BLUE); //
                dataset.setCircleColor(Color.BLUE);
                dataset.setDrawFilled(false);

                if (lineChart==3){
                    ArrayList<Entry> entry1=new ArrayList<>(); //max entries
                    ArrayList<Entry> entry2=new ArrayList<>(); //min entries
                    ArrayList<Entry> tmp=graph.getEntries();

                    for (int i=0;i<tmp.size();i++){
                        entry1.add(new Entry(i, (float) max));
                        entry2.add(new Entry(i, (float) min));
                    }

                    LineDataSet dataset1 = new LineDataSet(entry1, "High Warning");
                    dataset1.setColors(Color.RED); //
                    dataset1.setCircleColor(Color.RED);
                    dataset1.setDrawFilled(false);
                    lineDataSets.add(dataset1);

                    LineDataSet dataset2 = new LineDataSet(entry2, "Low Warning");
                    dataset2.setColors(Color.YELLOW); //
                    dataset2.setCircleColor(Color.YELLOW);
                    dataset2.setDrawFilled(false);
                    lineDataSets.add(dataset2);

                    lineDataSets.add(dataset);

                } else
                    lineDataSets.add(dataset);

                IAxisValueFormatter formatter = new IAxisValueFormatter() {

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if (value<graph.getLabels().size() && value>=0) return graph.getLabels().get((int) value);
                        else return String.valueOf(value);
                    }

                };

                XAxis xAxis = holder.graph.getXAxis();
                xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                xAxis.setValueFormatter(formatter);

                holder.graph.setData(new LineData(lineDataSets));
                holder.graph.animateY(3000);
                holder.graph.invalidate();
            }

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
