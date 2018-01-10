package com.pdp.bkresv2.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pdp.bkresv2.R;
import com.pdp.bkresv2.model.Customer;
import com.pdp.bkresv2.model.Datapackage;
import com.pdp.bkresv2.model.Device;
import com.pdp.bkresv2.model.Lake;
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

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Customer customer;
    DownloadJSON downloadJSON;
    ProgressDialog pDialog;

    private final int REQUEST_SETTING_CONFIG = 111;

    //Widget
    TextView txt_Nav_UserName, txt_Nav_Email, txt_Tittle, txt_Time_Update;
    TextView txt_Temp, txt_PH, txt_Oxy, txt_Salt, txt_NH4, txt_H2S, txt_NO2_Min, txt_NO2_Max, txt_NH4_Min, txt_NH4_Max, txt_H2S_Min, txt_H2S_Max;

    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();
    String selectedDevice = "";
    String selectedLake = " ";
    String selectedImeiDevice = "";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initWidget();
        pDialog = new ProgressDialog(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeviceDialog("Chọn thiết bị");
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customerObject");

        View hView = navigationView.getHeaderView(0);
        txt_Nav_UserName = (TextView) hView.findViewById(R.id.txt_nav_username);
        txt_Nav_Email= (TextView) hView.findViewById(R.id.txt_nav_email);
        String userName = customer.getUsername();
        String email = customer.getEmail();
        txt_Nav_UserName.setText(userName);
        txt_Nav_Email.setText(email);

        getLakeAndDevice();

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

    }

    //Ham xu li su kien khi giu phim Back de thoat ung dung
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                handleExit(); // finish activity
            } else {
                Toast.makeText(this, "Giữ phím Back để thoát chương trình",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chart) {
            Intent t = new Intent(HomeActivity.this, BieuDoThongKeActivity.class);
            t.putExtra("customerObj", customer);
            t.putExtra("listLake", listLake);
            t.putExtra("listDevice", listDevice);
            startActivity(t);

        }  else if (id == R.id.nav_setting) {
            Intent t = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivityForResult(t, REQUEST_SETTING_CONFIG);

        } else if (id == R.id.nav_share) {
            Intent t = new Intent(HomeActivity.this, GopYActivity.class);
            startActivity(t);

        } else if (id == R.id.nav_send) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Thông tin ứng dụng");
            View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.thongtin_ungdung_layout,null);
//            alert.setView(R.layout.thongtin_ungdung_layout);
            alert.setView(view);
            alert.setCancelable(false);

            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();

        } else if (id==R.id.nav_info){
            Intent intentInfo=new Intent(HomeActivity.this,InfoActivity.class);
            startActivity(intentInfo);
        }
        else if (id == R.id.nav_exit) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Chú ý!");
            builder.setMessage("Bạn có muốn thoát chương trình!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handleExit();
                }
            });
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            android.support.v7.app.AlertDialog dialog = builder.create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initWidget(){
        txt_Temp = (TextView) findViewById(R.id.txt_temperature);
        txt_Time_Update = (TextView) findViewById(R.id.txt_time_update);
        txt_PH = (TextView) findViewById(R.id.txt_PH);
        txt_Salt = (TextView) findViewById(R.id.txt_Salt);
        txt_Oxy = (TextView) findViewById(R.id.txt_Oxi);
        txt_NH4 = (TextView) findViewById(R.id.txt_Nh4);
        txt_H2S = (TextView) findViewById(R.id.txt_Sulfide);
        txt_NO2_Min = (TextView) findViewById(R.id.txt_NO2_Min);
        txt_NO2_Max = (TextView) findViewById(R.id.txt_NO2_Max);
        txt_NH4_Min = (TextView) findViewById(R.id.txt_NH4_Min);
        txt_NH4_Max = (TextView) findViewById(R.id.txt_NH4_Max);
        txt_H2S_Min = (TextView) findViewById(R.id.txt_Sulfide_Min);
        txt_H2S_Max = (TextView) findViewById(R.id.txt_Sulfide_Max);
        txt_Tittle = (TextView) findViewById(R.id.txt_title);
    }


    String tempSelectedDevice;
    public void selectDeviceDialog(String title) {
        tempSelectedDevice = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_select_device,null);
//        builder.setView(R.layout.layout_select_device);
        builder.setView(view);
        builder.setTitle(title);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(800, 600); //Controlling width and height.
        alertDialog.show();

        final Spinner spinner_Lake = (Spinner)alertDialog.findViewById(R.id.spinner_lake);
        final Spinner spinner_Device = (Spinner)alertDialog.findViewById(R.id.spinner_device);

        if(listLake.size() == 0){
            Toast.makeText(this, "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listLake.size()];
        String arr_device[] = new String[listDevice.size()];

        for(int i=0; i<listLake.size(); i++)
            arr_lake[i] = listLake.get(i).getName();
        for(int j=0; j<listDevice.size(); j++)
            arr_device[j] = listDevice.get(j).getName();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,arr_lake);
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
                       (HomeActivity.this, android.R.layout.simple_spinner_item, arr);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_Device.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_Device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempSelectedDevice = spinner_Device.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

    public void getLakeAndDevice(){
        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_LAKE_AND_DEVICE)
                .buildUpon()
                .appendQueryParameter("HomeId", customer.getHomeId() + "").build();
        downloadJSON = new DownloadJSON(this);

        downloadJSON.GetJSON(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Data", msgData);
                if(msgData.length()>1){
                    try {
                        JSONArray jsonArray = new JSONArray(msgData);
                        for(int i=0 ; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            int LakeId = objTmp.getInt("LakeId");
                            String Name = objTmp.getString("Name");
                            int HomeId = objTmp.getInt("HomeId");
                            String MapUrl = objTmp.getString("MapUrl");
                            String CreateTime = objTmp.getString("CreateTime");
                            Lake lakeObj = new Lake(LakeId, Name, HomeId,MapUrl, CreateTime);
                            listLake.add(lakeObj);

                            JSONArray jsonDeviceArray = objTmp.getJSONArray("listDevice");
                            for(int j=0; j<jsonDeviceArray.length(); j++){
                                JSONObject jsonDeviceObj = jsonDeviceArray.getJSONObject(j);
                                int IdDevice = jsonDeviceObj.getInt("Id");
                                String NameDevice = jsonDeviceObj.getString("Name");
                                String ImeiDevice = jsonDeviceObj.getString("Imei");
                                String CreateTimeDevice = jsonDeviceObj.getString("CreateTime");
                                String WarningNumberPhone = jsonDeviceObj.getString("WarningNumberPhone");
                                String WarningMail = jsonDeviceObj.getString("WarningMail");
                                int LakeIdDevice = jsonDeviceObj.getInt("LakeId");
                                Device deviceObj = new Device(IdDevice, NameDevice, ImeiDevice, CreateTimeDevice, WarningNumberPhone, WarningMail, LakeIdDevice);
                                listDevice.add(deviceObj);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i("Number of Lake", "Lake: " + listLake.size() + " - Device:" + listDevice.size());

                    if(listDevice.size()>0 && listLake.size()>0){
                        selectedLake = listLake.get(0).getName();
                        selectedDevice = listDevice.get(0).getName();
                        selectedImeiDevice = listDevice.get(0).getImei();
                        mSocket.emit("authentication", selectedImeiDevice);
                        mSocket.emit("join", selectedImeiDevice);
                        mSocket.on("new message", onDataReceive);
                        Log.i("IMEI DEVICE SELECT", selectedImeiDevice);
                        getDatapackageByDeviceName();
                    } else {
                        Toast.makeText(HomeActivity.this, "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
                    }


                } else{
                    Toast.makeText(HomeActivity.this, "Tài khoản " + customer.getUsername() + " không có thiết bị giám sát", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(String msgError) {
                Log.i("Error", msgError);
            }
        });
    }

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
        downloadJSON = new DownloadJSON(this);

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
                    String NgayTao = jsonObj.getString("NgayTao");

                    Datapackage datapackage = new Datapackage(Id, DeviceId, Time_Package, PH, Salt, Temp, Oxy, H2S, NH3, NH4Max, NH4Min, NO2Min, SulfideMin, NO2Max, SulfideMax, NgayTao);
                    updateView(datapackage);

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

    public void updateView(Datapackage datapackage){
        txt_Tittle.setText("Ao " + selectedLake + " - Thiết bị " + selectedDevice);
        txt_Time_Update.setText("Cập nhật: " + XuLyThoiGian.StringToDatetimeString(datapackage.getTime_Package()));
        txt_Temp.setText(datapackage.getTemp()+"");
        txt_PH.setText(datapackage.getPH()+"");
        txt_Salt.setText(datapackage.getSalt()+"");
        txt_Oxy.setText(datapackage.getOxy()+"");
        txt_NH4 .setText(datapackage.getNH3()+"");
        txt_H2S.setText(datapackage.getH2S()+"");
        txt_NO2_Min .setText(datapackage.getNO2Min()+"");
        txt_NO2_Max .setText(datapackage.getNO2Max()+"");
        txt_NH4_Min.setText(datapackage.getNH4Min()+"");
        txt_NH4_Max .setText(datapackage.getNH4Max()+"");
        txt_H2S_Min.setText(datapackage.getSulfideMin()+"");
        txt_H2S_Max.setText(datapackage.getSulfideMax()+"");
    }

    public void handleExit(){
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onDataReceive);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Connected", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "disconnected");

                    //Toast.makeText(getApplicationContext(),
                     //       "Disconnect", Toast.LENGTH_SHORT).show();

                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
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
                            String NgayTao = jsonObj.getString("Datetime_Packet");

                            Datapackage datapackage = new Datapackage(-1, -1, Time_Package, PH, Salt, Temp, Oxy, H2S, NH3, NH4Max, NH4Min, NO2Min, SulfideMin, NO2Max, SulfideMax, NgayTao);
                            updateView(datapackage);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
}
