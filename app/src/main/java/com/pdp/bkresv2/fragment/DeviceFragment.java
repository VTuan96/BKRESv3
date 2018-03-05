package com.pdp.bkresv2.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.pdp.bkresv2.R;
import com.pdp.bkresv2.activity.BieuDoActivity;
import com.pdp.bkresv2.adapter.GraphAdapter;
import com.pdp.bkresv2.model.Graph;
import com.pdp.bkresv2.task.DownloadJSON;
import com.pdp.bkresv2.utils.Constant;
import com.pdp.bkresv2.utils.XuLyThoiGian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceFragment extends Fragment {
    private static final String TITLE="TITLE";
    private static final String GRAPH="GRAPH";
    private static final String TIME="TIME";
    private static final String DEVICEID="DEVICEID";
    private static final String DATE_TIME="DATE_TIME";

    private TextView txtItemContent;
    private TextView txtTitle;
    private RecyclerView rvItemBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter;

    private DownloadJSON downloadJSON;
    String arr_thongso[] = {"PH", "Muối", "Oxy", "Nhiệt độ", "H2S", "NH3", "NH4 Min", "NH4 Max", "N02 Min", "NO2 Max", "Sulfide Min", "Sulfide Max"};

    public  String tmpNgayThangNam="";
    public  String tmpSelectedDeviceId="";

    private Calendar calendar;
    private int year, month, day;

    private View v;

    public static DeviceFragment newInstance(String title,int selectDeviceID){
        DeviceFragment df=new DeviceFragment();
        Bundle bundle=new Bundle();
        bundle.putString(TITLE,title);
        bundle.putInt(DEVICEID,selectDeviceID);
        df.setArguments(bundle);
        return df;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        v=LayoutInflater.from(getContext()).inflate(R.layout.fragment_device,container,false);
        initWidgets(v);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Bundle bundle=getArguments();
        String title=bundle.getString(TITLE);
        tmpSelectedDeviceId=String.valueOf(bundle.getInt(DEVICEID));

        txtTitle.setText("Thiết bị: "+title);
        rvItemBieuDoThongKe.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        rvItemBieuDoThongKe.setLayoutManager(manager);

        adapter=new GraphAdapter(listGraph);
        rvItemBieuDoThongKe.setAdapter(adapter);
        for (int i=1;i<=12;i++){
            getDataThongKe(i);
        }

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private void initWidgets(View v){
        txtItemContent= (TextView) v.findViewById(R.id.txtItemContent);
        txtTitle= (TextView) v.findViewById(R.id.txtTitle);
        rvItemBieuDoThongKe= (RecyclerView) v.findViewById(R.id.rvItemBieuDoThongKe);
    }

    public void getDataThongKe(final int tempSelectThongSo) {
        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_THONGKE)
                .buildUpon()
                .appendQueryParameter("strCode", "QN290394")
                .appendQueryParameter("time", BieuDoActivity.tmpNgayThangNam)
                .appendQueryParameter("paramId", String.valueOf(tempSelectThongSo))
                .appendQueryParameter("deviceId", tmpSelectedDeviceId).build();

        downloadJSON = new DownloadJSON(getContext());


        downloadJSON.GetJSON(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                try{
                    //JSONObject jsonObj = new JSONObject(msgData);
                    JSONArray jsonArray = new JSONArray(msgData);
                    if(jsonArray.length()==0){
//                        txtContent.setVisibility(View.VISIBLE);
                    }
                    else{
                        String nameGraph=arr_thongso[tempSelectThongSo-1];
                        ArrayList<Entry> entries = new ArrayList<>();
                        ArrayList labels = new ArrayList<String>();

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            double value = objTmp.getDouble("value");
                            String time = objTmp.getString("time");
                            String[] words = time.split("\\s");
                            Log.d("time",words[0]+words[1]);
                            entries.add(new Entry((float)value, i));
                            labels.add(words[1]);
                        }
                        Graph graph=new Graph(nameGraph,entries,labels);
                        listGraph.add(graph);
                        if (listGraph.size()>0){
                            txtItemContent.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            Log.i("size graph",listGraph.size()+"");
                        } else {
                            txtItemContent.setVisibility(View.VISIBLE);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
//                progress.dismiss();
                Log.i("Error", msgError);
            }
        });
    }

}
