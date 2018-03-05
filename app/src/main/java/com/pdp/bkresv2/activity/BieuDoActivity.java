package com.pdp.bkresv2.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pdp.bkresv2.R;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.pdp.bkresv2.adapter.CustomPagerAdapter;
import com.pdp.bkresv2.adapter.GraphAdapter;
import com.pdp.bkresv2.adapter.LakeAdapter;
import com.pdp.bkresv2.fragment.DeviceFragment;
import com.pdp.bkresv2.model.Customer;
import com.pdp.bkresv2.model.Device;
import com.pdp.bkresv2.model.Graph;
import com.pdp.bkresv2.model.Lake;
import com.pdp.bkresv2.task.DownloadJSON;
import com.pdp.bkresv2.utils.Constant;
import com.pdp.bkresv2.utils.XuLyThoiGian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/*
Param to get data from server:
paramId: 1 = PH
        2 = Salt
        3 = Oxy
        4 = Temp
        5 = H2S
        6 = NH3
        7 = NH4Min
        8 = NH4Max
        9 = NO2Min
        10 = NO2Max
        11 = SulfideMin
        12 = SulfideMax
 */

public class BieuDoActivity extends AppCompatActivity {

    TextView txt_NgayThangNam, txt_BieuDo;

    DownloadJSON downloadJSON;
    Customer customer;
    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();

    String arr_thongso[] = {"PH", "Muối", "Oxy", "Nhiệt độ", "H2S", "NH3", "NH4 Min", "NH4 Max", "N02 Min", "NO2 Max", "Sulfide Min", "Sulfide Max"};
    public static String tmpSelectedDeviceId = "";
    public static String tmpNgayThangNam = "";

    private Calendar calendar;
    private int year, month, day;


    //My code
    ArrayList<Graph> listGraph=new ArrayList<>();

    private ViewPager pagerDevice;
    private CustomPagerAdapter adapterDevice;
    private List<DeviceFragment> listFragments=new ArrayList<>();
    public static int positionFragment=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bieu_do);
        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();
        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customerObj");
        listLake = (ArrayList<Lake>) i.getSerializableExtra("listLake");
        listDevice = (ArrayList<Device>) i.getSerializableExtra("listDevice");
//        tmpSelectedDeviceId=String.valueOf(listDevice.get(0).getId());

        initWidget();
        showBackArrow();

        initViewPager();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }

    public void showBackArrow(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_bieudo);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initWidget() {
//        txtContent= (TextView) findViewById(R.id.txtContent);
        txt_BieuDo = (TextView) findViewById(R.id.txt_TenBieuDo);
        pagerDevice= (ViewPager) findViewById(R.id.pagerDevice);
    }

    private void initViewPager(){
        listFragments=new ArrayList<>();
        listFragments=getListFragments();
        adapterDevice=new CustomPagerAdapter(getSupportFragmentManager(),listFragments);
        pagerDevice.setAdapter(adapterDevice);

    }

    private List<DeviceFragment> getListFragments(){
        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customerObj");
        listLake = (ArrayList<Lake>) i.getSerializableExtra("listLake");
        listDevice = (ArrayList<Device>) i.getSerializableExtra("listDevice");
        List<DeviceFragment> mList=new ArrayList<>();
        for(Device d:listDevice){
            mList.add(DeviceFragment.newInstance(d.getName(),d.getId()));
//            System.out.println("device id in getListFragments: "+d.getId());
        }

        return mList;
    }


    public void getDataThongKe(final int tempSelectThongSo, final ArrayList<Graph> listGraph) {

        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_THONGKE)
                .buildUpon()
                .appendQueryParameter("strCode", "QN290394")
                .appendQueryParameter("time", tmpNgayThangNam)
                .appendQueryParameter("paramId", String.valueOf(tempSelectThongSo))
                .appendQueryParameter("deviceId", tmpSelectedDeviceId).build();

        downloadJSON = new DownloadJSON(this);


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
//                        txtContent.setVisibility(View.GONE);
//
//                        graphAdapter=new GraphAdapter(listGraph);
//                        graphAdapter.notifyDataSetChanged();
//                        rvBieuDo.setAdapter(graphAdapter);

                        Log.d("size graph",listGraph.size()+"");
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

    public void getDataThongKe(final int tempSelectThongSo) {

        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_THONGKE)
                .buildUpon()
                .appendQueryParameter("strCode", "QN290394")
                .appendQueryParameter("time", tmpNgayThangNam)
                .appendQueryParameter("paramId", String.valueOf(tempSelectThongSo))
                .appendQueryParameter("deviceId", tmpSelectedDeviceId).build();

        downloadJSON = new DownloadJSON(this);


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

                        Log.i("size graph",listGraph.size()+"");
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

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getBaseContext(),"Resume",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            DatePickerDialog datePickerDialog=new DatePickerDialog(this,myDateListener,year,month,day);

            return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    tmpNgayThangNam = arg1 +"-" + (arg2+1) + "-" + arg3;

                    initViewPager();
                    adapterDevice.notifyDataSetChanged();

                }
            };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu subMenu= menu.addSubMenu(0,9999,0,"Chọn ao").setIcon(R.drawable.ic_lake_menu);

        for (int i=0;i<listLake.size();i++){
            SubMenu subMenu1= subMenu.addSubMenu(0,888,i,listLake.get(i).getName());
            for (int j=0;j<listDevice.size();j++){
                if (listLake.get(i).getLakeId()==listDevice.get(j).getLakeId())
                    subMenu1.add(j,j,j,listDevice.get(j).getName());
            }
        }

        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_graph,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        for (int i=0;i<listDevice.size();i++) {
            if (id==i) {
                Log.d("menu ",listDevice.get(i).getName());

                int deviceID = listDevice.get(i).getId();
                tmpSelectedDeviceId=String.valueOf(deviceID);

                return true;
            }
        }


        if (id== R.id.mnuTime) {
            showDialog(999);

            return true;
        }

        return super.onOptionsItemSelected(item);

    }


}

