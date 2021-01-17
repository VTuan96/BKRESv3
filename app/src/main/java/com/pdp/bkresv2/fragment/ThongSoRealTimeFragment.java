package com.pdp.bkresv2.fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pdp.bkresv2.R;
import com.pdp.bkresv2.activity.HomeActivity;
import com.pdp.bkresv2.activity.SettingsActivity;
import com.pdp.bkresv2.model.Customer;
import com.pdp.bkresv2.model.Datapackage;
import com.pdp.bkresv2.model.Device;
import com.pdp.bkresv2.model.Lake;
import com.pdp.bkresv2.model.User;
import com.pdp.bkresv2.task.DownloadJSON;
import com.pdp.bkresv2.utils.Constant;
import com.pdp.bkresv2.utils.XuLyThoiGian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThongSoRealTimeFragment extends Fragment {
    TextView txt_Temp, txt_PH, txt_Oxy, txt_Salt, txt_NH4, txt_H2S, txt_NO2_Min, txt_NO2_Max, txt_NH4_Min, txt_NH4_Max, txt_H2S_Min, txt_H2S_Max, txt_Alkalinity;
    TextView txt_Time_Update, txt_Tittle;
    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();
    String selectedDevice = "";
    String selectedLake = " ";
    String selectedImeiDevice = "";

    User customer;
    DownloadJSON downloadJSON;
    ProgressDialog pDialog;

    public double PH_Max, PH_Min, Temp_Max, Temp_Min, Salt_Max, Salt_Min, Oxy_Max, Oxy_Min, H2S_Max, H2S_Min,NO2_Max, NO2_Min,NH4_Max,NH4_Min;


    private Socket mSocket;
    final String TAG = "Socket IO";

    {
        try {
            IO.Options opts = new IO.Options();
            opts.path = "/socket.io 2.03 version new";
            mSocket = IO.socket("http://202.191.56.103:5505/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public ThongSoRealTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_thong_so_real_time,container,false);
        customer = HomeActivity.customer;
        initWidget(v);
        pDialog = new ProgressDialog(getContext());


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabThongSo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeviceDialog("Chọn thiết bị");
            }
        });

//        getLakeAndDevice();

        // Socket IO
        //mSocket.emit("authentication", "354725065508131");

        mSocket.emit("authentication", selectedImeiDevice);
        mSocket.emit("join", selectedImeiDevice);
        mSocket.on("new message", onDataReceive);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.connect();



        return v;
    }

    public void initWidget(View v){
        txt_Temp = (TextView) v.findViewById(R.id.txt_temperature);
        txt_Time_Update = (TextView) v.findViewById(R.id.txt_time_update_thong_so);
        txt_PH = (TextView) v.findViewById(R.id.txt_PH);
        txt_Salt = (TextView) v.findViewById(R.id.txt_Salt);
        txt_Oxy = (TextView) v.findViewById(R.id.txt_Oxi);
        txt_NH4 = (TextView) v.findViewById(R.id.txt_Nh4);
        txt_H2S = (TextView) v.findViewById(R.id.txt_Sulfide);
        txt_NO2_Min = (TextView) v.findViewById(R.id.txt_NO2_Min);
        txt_NO2_Max = (TextView) v.findViewById(R.id.txt_NO2_Max);
        txt_NH4_Min = (TextView) v.findViewById(R.id.txt_NH4_Min);
        txt_NH4_Max = (TextView) v.findViewById(R.id.txt_NH4_Max);
        txt_H2S_Min = (TextView) v.findViewById(R.id.txt_Sulfide_Min);
        txt_H2S_Max = (TextView) v.findViewById(R.id.txt_Sulfide_Max);
        txt_Tittle = (TextView) v.findViewById(R.id.txt_title_thong_so);
        txt_Alkalinity= (TextView) v.findViewById(R.id.txt_Alkalinity);
    }


    String tempSelectedDevice;

    public void selectDeviceDialog(String title) {
        tempSelectedDevice = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.layout_select_device,null);
//        builder.setView(R.layout.layout_select_device);
        builder.setView(view);
        builder.setTitle(title);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(800, 600); //Controlling width and height.
        alertDialog.show();

        final Spinner spinner_Lake = (Spinner)alertDialog.findViewById(R.id.spinner_lake);
//        final Spinner spinner_Device = (Spinner)alertDialog.findViewById(R.id.spinner_device);

        if(listLake.size() == 0){
            Toast.makeText(getContext(), "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listLake.size()];
        String arr_device[] = new String[listDevice.size()];

        for(int i=0; i<listLake.size(); i++)
            arr_lake[i] = listLake.get(i).getName();
        for(int j=0; j<listDevice.size(); j++)
            arr_device[j] = listDevice.get(j).getName();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,arr_lake);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Lake.setAdapter(adapter);
        //spinner_Lake.setOnItemSelectedListener(new MyOnItemSelectedListener());





        spinner_Lake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int LakeId = listLake.get(i).getLakeId();
                selectedLake = spinner_Lake.getSelectedItem().toString();
                int count = 0;
                for(int k=0 ; k<listDevice.size(); k++){
                    if(listDevice.get(k).getLakeId() == LakeId){
                        count++;
                    }
                }

                String arr[] = new String [count];
                count = -1;
                for(int k=0 ; k<listDevice.size(); k++){
                    if(listDevice.get(k).getLakeId() == LakeId){
                        count++;
                        arr[count] = listDevice.get(k).getName();
                    }
                }

                ArrayAdapter<String> adapter2 =new ArrayAdapter<String>
                        (getContext(), android.R.layout.simple_spinner_item, arr);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner_Device.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        spinner_Device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                tempSelectedDevice = spinner_Device.getSelectedItem().toString();
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        Button btn_Ok = (Button) alertDialog.findViewById(R.id.btn_Ok);
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
                //Toast.makeText(HomeActivity.this, selectedDevice, Toast.LENGTH_SHORT).show();
                selectedDevice = tempSelectedDevice;
                for(int k=0; k<listDevice.size(); k++){
                    if(listDevice.get(k).getName().compareTo(selectedDevice) == 0){
                        selectedImeiDevice = listDevice.get(k).getImei();
                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                        mSocket.emit("authentication", selectedImeiDevice);
                        mSocket.emit("join", selectedImeiDevice);
                        mSocket.on("new message", onDataReceive);
                        mSocket.connect();
                    }

                }
                getDatapackageByDeviceName();
            }
        });

        Button btn_Huy = (Button) alertDialog.findViewById(R.id.btn_Huy);
        btn_Huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

//    public void getLakeAndDevice(){
//        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_LAKE_AND_DEVICE)
//                .buildUpon()
//                .appendQueryParameter("HomeId", customer.getHomeId() + "").build();
//        downloadJSON = new DownloadJSON(getContext());
//
//        downloadJSON.GetJSON(builder, new DownloadJSON.DownloadJSONCallBack() {
//            @Override
//            public void onSuccess(String msgData) {
//                Log.i("Data", msgData);
//                if(msgData.length()>1){
//                    try {
//                        JSONArray jsonArray = new JSONArray(msgData);
//                        for(int i=0 ; i<jsonArray.length(); i++){
//                            JSONObject objTmp = jsonArray.getJSONObject(i);
//                            int LakeId = objTmp.getInt("LakeId");
//                            String Name = objTmp.getString("Name");
//                            int HomeId = objTmp.getInt("HomeId");
//                            String MapUrl = objTmp.getString("MapUrl");
//                            String CreateTime = objTmp.getString("CreateTime");
//                            Lake lakeObj = new Lake(LakeId, Name, HomeId,MapUrl, CreateTime);
//                            listLake.add(lakeObj);
//
//                            JSONArray jsonDeviceArray = objTmp.getJSONArray("listDevice");
//                            for(int j=0; j<jsonDeviceArray.length(); j++){
//                                JSONObject jsonDeviceObj = jsonDeviceArray.getJSONObject(j);
//                                int IdDevice = jsonDeviceObj.getInt("Id");
//                                String NameDevice = jsonDeviceObj.getString("Name");
//                                String ImeiDevice = jsonDeviceObj.getString("Imei");
//                                String CreateTimeDevice = jsonDeviceObj.getString("CreateTime");
//                                String WarningNumberPhone = jsonDeviceObj.getString("WarningNumberPhone");
//                                String WarningMail = jsonDeviceObj.getString("WarningMail");
//                                int LakeIdDevice = jsonDeviceObj.getInt("LakeId");
//                                Device deviceObj = new Device(IdDevice, NameDevice, ImeiDevice, CreateTimeDevice, WarningNumberPhone, WarningMail, LakeIdDevice);
//                                listDevice.add(deviceObj);
//                            }
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    Log.i("Number of Lake", "Lake: " + listLake.size() + " - Device:" + listDevice.size());
//
//                    if(listDevice.size()>0 && listLake.size()>0){
//                        selectedLake = listLake.get(0).getName();
//                        selectedDevice = listDevice.get(0).getName();
//                        selectedImeiDevice = listDevice.get(0).getImei();
//                        mSocket.emit("authentication", selectedImeiDevice);
//                        mSocket.emit("join", selectedImeiDevice);
//                        mSocket.on("new message", onDataReceive);
//                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
//                        getDatapackageByDeviceName();
//                    } else {
//                        Toast.makeText(getContext(), "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                } else{
//                    Toast.makeText(getContext(), "Tài khoản " + customer.getUsername() + " không có thiết bị giám sát", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFail(String msgError) {
//                Log.i("Error", msgError);
//            }
//        });
//    }

    public void getDatapackageByDeviceName(){
        pDialog.setMessage("Đang tải...");
        pDialog.show();

        int deviceId = -1;
        for(int i=0; i<listDevice.size(); i++){
            if(listDevice.get(i).getName().compareTo(selectedDevice) == 0){
                deviceId = listDevice.get(i).getId();
                break;
            }
        }


        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_PACKAGE)
                .buildUpon()
                .appendQueryParameter("DeviceId", deviceId + "").build();
        downloadJSON = new DownloadJSON(getContext());

        downloadJSON.GetJSON2(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Data", msgData);

                try {
                    JSONObject jsonObj = new JSONObject(msgData);
                    int Id = jsonObj.getInt("Id");
                    int DeviceId = jsonObj.getInt("DeviceId");
                    String Time_Package = jsonObj.getString("Time_Package");
                    double PH = jsonObj.getDouble("PH");
                    double Salt = jsonObj.getDouble("Salt");
                    double Oxy = jsonObj.getDouble("Oxy");
                    double Temp = jsonObj.getDouble("Temp");
                    double H2S = jsonObj.getDouble("H2S");
                    double NH3 = jsonObj.getDouble("NH3");
                    double NH4Min = jsonObj.getDouble("NH4Min");
                    double NH4Max = jsonObj.getDouble("NH4Max");
                    double NO2Min = jsonObj.getDouble("NO2Min");
                    double NO2Max = jsonObj.getDouble("NO2Max");
                    double SulfideMin = jsonObj.getDouble("SulfideMin");
                    double SulfideMax = jsonObj.getDouble("SulfideMax");
                    double Alkalinity = jsonObj.getDouble("Alkalinity");
                    String NgayTao = jsonObj.getString("NgayTao");

                    Datapackage datapackage = new Datapackage();
//                    updateView(datapackage);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
                Log.i("Error", msgError);
            }
        });

        pDialog.dismiss();
    }

//    public void updateView(Datapackage datapackage){
//        txt_Tittle.setText("Ao " + selectedLake + " - Thiết bị " + selectedDevice);
//        txt_Time_Update.setText("Cập nhật: " + XuLyThoiGian.StringToDatetimeString(datapackage.getTime_Package()));
//        txt_Temp.setText(datapackage.getTemp()+"");
//        txt_PH.setText(datapackage.getPH()+"");
//        txt_Salt.setText(datapackage.getSalt()+"");
//        txt_Oxy.setText(datapackage.getOxy()+"");
//        txt_NH4 .setText(datapackage.getNH3()+"");
//        txt_H2S.setText(datapackage.getH2S()+"");
//        txt_NO2_Min .setText(datapackage.getNO2Min()+"");
//        txt_NO2_Max .setText(datapackage.getNO2Max()+"");
//        txt_NH4_Min.setText(datapackage.getNH4Min()+"");
//        txt_NH4_Max .setText(datapackage.getNH4Max()+"");
//        txt_H2S_Min.setText(datapackage.getSulfideMin()+"");
//        txt_H2S_Max.setText(datapackage.getSulfideMax()+"");
//
//        String alkal=String.valueOf(datapackage.getAlkalinity());
//        alkal=alkal.substring(0,6)+"...";
//        txt_Alkalinity.setText(alkal);
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onDataReceive);
    }



    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),
                            "Connected", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "disconnected");
                    mSocket.connect();
                    //Toast.makeText(getApplicationContext(),
                    //       "Disconnect", Toast.LENGTH_SHORT).show();

                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, args[0].toString());
//                    Toast.makeText(getApplicationContext(),
//                            "Lỗi cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onDataReceive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = args[0].toString();
                    Log.i("Socket IO data", data);

                    try {
                        JSONObject jsonObj = new JSONObject(data);

                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                        String deviceImei = jsonObj.getString("Device_IMEI");
                        Log.i("IMEI DEVICE SERVER", deviceImei);

                        if(deviceImei.compareTo(selectedImeiDevice) == 0){
                            String Time_Package = jsonObj.getString("Datetime_Packet");
                            double PH = jsonObj.getDouble("PH");
                            double Salt = jsonObj.getDouble("Salt");
                            double Oxy = jsonObj.getDouble("Oxy");
                            double Temp = jsonObj.getDouble("NhietDo");
                            double H2S = jsonObj.getDouble("H2S");
                            double NH3 = jsonObj.getDouble("NH3");
                            double NH4Min = jsonObj.getDouble("NH4Min");
                            double NH4Max = jsonObj.getDouble("NH4Max");
                            double NO2Min = jsonObj.getDouble("NO2Min");
                            double NO2Max = jsonObj.getDouble("NO2Max");
                            double SulfideMin = jsonObj.getDouble("SulfideMin");
                            double SulfideMax = jsonObj.getDouble("SulfideMax");
                            double Alkalinity = jsonObj.getDouble("Alkalinity");
                            String NgayTao = jsonObj.getString("Datetime_Packet");

                            Datapackage datapackage = new Datapackage();
//                            updateView(datapackage);

                            //get settings of data
                            getPreferences();
                            //check current data and data on settings
                            setColorTextWarning(PH,PH_Min,PH_Max,txt_PH);
                            setColorTextWarning(Oxy,Oxy_Min,Oxy_Max,txt_Oxy);
                            setColorTextWarning(Salt,Salt_Min,Salt_Max,txt_Salt);
                            setColorTextWarning(Temp,Temp_Min,Temp_Max,txt_Temp);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //change color of text parameter if it's out of range
    private void setColorTextWarning(double param, double min, double max, TextView txtParam){
        if (param>max || param<min){
            txtParam.setTextColor(Color.RED);
        } else {
            txtParam.setTextColor(getResources().getColor(R.color.colorParameter));
        }
    }


    //get data on settings
    private void getPreferences(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        PH_Max =  preferences.getFloat(SettingsActivity.KEY_PH_MAX, Constant.DEFAULT_PH_MAX);
        PH_Min =  preferences.getFloat(SettingsActivity.KEY_PH_MIN, Constant.DEFAULT_PH_MIN);

        Temp_Max =  preferences.getFloat(SettingsActivity.KEY_TEMP_MAX, Constant.DEFAULT_TEMP_MAX);
        Temp_Min =  preferences.getFloat(SettingsActivity.KEY_TEMP_MIN, Constant.DEFAULT_TEMP_MIN);

        Salt_Max =  preferences.getFloat(SettingsActivity.KEY_SALT_MAX, Constant.DEFAULT_SALT_MAX);
        Salt_Min =  preferences.getFloat(SettingsActivity.KEY_SALT_MIN, Constant.DEFAULT_SALT_MIN);

        Oxy_Max =  preferences.getFloat(SettingsActivity.KEY_OXY_MAX, Constant.DEFAULT_OXY_MAX);
        Oxy_Min =  preferences.getFloat(SettingsActivity.KEY_OXY_MIN, Constant.DEFAULT_OXY_MIN);

        H2S_Max =  preferences.getFloat(SettingsActivity.KEY_H2S_MAX, Constant.DEFAULT_H2S_MAX);
        H2S_Min =  preferences.getFloat(SettingsActivity.KEY_H2S_MIN, Constant.DEFAULT_H2S_MIN);
        NH4_Max =  preferences.getFloat(SettingsActivity.KEY_NH4_MAX, Constant.DEFAULT_NH4_MAX);
        NH4_Min =  preferences.getFloat(SettingsActivity.KEY_NH4_MIN, Constant.DEFAULT_NH4_MIN);
        NO2_Max =  preferences.getFloat(SettingsActivity.KEY_NO2_MAX, Constant.DEFAULT_NO2_MAX);
        NO2_Min =  preferences.getFloat(SettingsActivity.KEY_NO2_MIN, Constant.DEFAULT_NO2_MIN);
    }

    //check limit of parameter
    private boolean checkParameter(double parameter, double min, double max){
        if (parameter < min || parameter > max){
            return true; //if out of range
        }
        return false; //if in range
    }
}
