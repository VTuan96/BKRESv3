package com.pdp.bkresv2.fragment;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pdp.bkresv2.R;
import com.pdp.bkresv2.activity.HomeActivity;
import com.pdp.bkresv2.activity.SettingsActivity;
import com.pdp.bkresv2.model.Datapackage;
import com.pdp.bkresv2.model.Device;
import com.pdp.bkresv2.model.Lake;
import com.pdp.bkresv2.model.Node;
import com.pdp.bkresv2.model.Project;
import com.pdp.bkresv2.model.User;
import com.pdp.bkresv2.service.NodeService;
import com.pdp.bkresv2.service.ProjectService;
import com.pdp.bkresv2.task.DownloadJSON;
import com.pdp.bkresv2.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThongSoRealTimeFragment extends Fragment {
    TextView txt_Temp, txt_PH, txt_DO, txt_Salt, txt_NH4, txt_H2S, txt_TSS, txt_COD, txt_NH4_Min, txt_NH4_Max, txt_H2S_Min, txt_H2S_Max, txt_Alkalinity;
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

    ProgressBar pbParamRealtime;

    private Socket mSocket;
    final String TAG = "Socket IO";

    {
        try {
            IO.Options opts = new IO.Options();
            opts.path = "/socket.io 2.03 version new";
            mSocket = IO.socket("http://202.191.56.104:5522/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    //New verson with new database design
    public static Project project = new Project();
    public static ArrayList<Node> listNodes = new ArrayList<>();

    private boolean isFirstTimeLoadingData = true;
    private String selectedNodeName = "";
    private Node selectedNode = null;
    private int selectedIndex = 0;

    public ThongSoRealTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        getProjectInformation();
//        getNodesInformation();

        View v= inflater.inflate(R.layout.fragment_thong_so_real_time,container,false);
        customer = HomeActivity.customer;
        initWidget(v);
//        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabThongSo);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectDeviceDialog("Chọn Trạm dữ liệu");
//            }
//        });

//        getLakeAndDevice();

        // Socket IO
        //mSocket.emit("authentication", "354725065508131");

        mSocket.connect();
        mSocket.on("monitor_data_from_node", onDataReceive);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);

        return v;
    }

    private void getProjectInformation() {
        ProjectService service = new ProjectService(getContext());
        service.getProjectInfo(Constant.PROJECT_NAME, new ProjectService.ProjectServiceCallBack() {
            @Override
            public void onProjectInfoReceived(Project projectSuccess) {
                if (projectSuccess != null)
                    project = projectSuccess;

                Log.e("BieuDoRealTime", "FUNCTION: getProjectInformation, Project: " + project.toString());
            }

            @Override
            public void onProjectInfoFailed(Project projectFailed) {
                if (projectFailed != null)
                    project = projectFailed;

                Log.e("BieuDoRealTime", "FUNCTION: getProjectInformation, Project: " + project.toString());
            }
        });

    }

    private void getNodesInformation() {
        NodeService service = new NodeService(getContext());
        service.getNodeInfo(new NodeService.NodeServiceCallBack() {
            @Override
            public void onNodeInfoReceived(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    int length = data.length();

                    for (int i = length - 1; i > 0; i--){
                        JSONObject nodeItem = data.getJSONObject(i);
                        Node node = new Node();
                        node.set_id(nodeItem.getString("id"));
                        node.setName(nodeItem.getString("name"));
                        String description = "";
                        try {
                            description = nodeItem.getString("description");
                        }
                        catch (JSONException e)
                        {
                            description = "";
                        }
                        node.setDescription(description);
                        node.setId_server_gen(nodeItem.getString("id_server_gen"));
//                        node.setId_gateway_server_gen(nodeItem.getString("id_gateway_server_gen"));
//                        node.setId_gateway(nodeItem.getString("id_gateway"));
                        node.setId_project(nodeItem.getString("id_project"));
//                        node.setId_communication(nodeItem.getString("id_communication"));

                        if (node.getId_project().equals(project.get_id()) == true)
                            listNodes.add(node);
                    }

                    //init selected node
                    if (listNodes.size() > 0) {
                        selectedNode = listNodes.get(0); // Default is first node
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNodeInfoFailed(String failed) {

            }
        });
    }

    public void initWidget(View v){
        txt_Tittle = (TextView) v.findViewById(R.id.txt_title_thong_so);
        txt_Temp = (TextView) v.findViewById(R.id.txt_temperature);
        txt_Time_Update = (TextView) v.findViewById(R.id.txt_time_update_thong_so);
        txt_PH = (TextView) v.findViewById(R.id.txt_PH);
        txt_Salt = (TextView) v.findViewById(R.id.txt_Salt);
        txt_DO = (TextView) v.findViewById(R.id.txt_DO);
        txt_NH4 = (TextView) v.findViewById(R.id.txt_Nh4);
        txt_H2S = (TextView) v.findViewById(R.id.txt_Sulfide);
        txt_TSS = (TextView) v.findViewById(R.id.txt_TSS);
        txt_COD = (TextView) v.findViewById(R.id.txt_COD);
//        txt_NH4_Min = (TextView) v.findViewById(R.id.txt_NH4_Min);
//        txt_NH4_Max = (TextView) v.findViewById(R.id.txt_NH4_Max);
//        txt_H2S_Min = (TextView) v.findViewById(R.id.txt_Sulfide_Min);
//        txt_H2S_Max = (TextView) v.findViewById(R.id.txt_Sulfide_Max);
//        txt_Alkalinity= (TextView) v.findViewById(R.id.txt_Alkalinity);

        pbParamRealtime = (ProgressBar) v.findViewById(R.id.pbParamRealtime);
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

        if(listNodes.size() == 0){
            Toast.makeText(getContext(), "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listNodes.size()];

        for(int i = 0; i < listNodes.size(); i++)
        {
            arr_lake[i] = listNodes.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,arr_lake);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Lake.setAdapter(adapter);

        spinner_Lake.setSelection(selectedIndex);

        spinner_Lake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedIndex = i;
                selectedNode = listNodes.get(i);
//                spinner_Device.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button btn_Ok = (Button) alertDialog.findViewById(R.id.btn_Ok);
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getContext(), "Vui lòng đợi trong giây lát!", Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
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

    public void updateView(Datapackage datapackage){
        txt_Tittle.setText("Trạm dữ liệu: " + selectedNodeName) ;
        txt_Time_Update.setText("Cập nhật: " + datapackage.getTime_Package());
        txt_Temp.setText(datapackage.getTemp() + "");
        txt_PH.setText(datapackage.getPH() + "");
        txt_Salt.setText(datapackage.getSalt() + "");
        txt_DO.setText(datapackage.getDO() + "");
        txt_NH4.setText(datapackage.getNH3() + "");
        txt_H2S.setText(datapackage.getH2S() + "");
        txt_TSS.setText(datapackage.getTSS() + "");
        txt_COD.setText(datapackage.getCOD() + "");
    }


    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }

        pbParamRealtime.setProgress(0);
        pbParamRealtime.setVisibility(View.GONE);
    }

    private void startTimer(){
        pbParamRealtime.setVisibility(View.VISIBLE);
        mTimer1 = new Timer();
        final int[] progress = {0};
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        //TODO
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progress[0] = progress[0] + 5;
                        if (progress[0] > 100) {
                            progress[0] = 0;
                        }
                        pbParamRealtime.setProgress(progress[0]);
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 200);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onDataReceive);
        stopTimer();
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),
                            "Connected", Toast.LENGTH_SHORT).show();

                    startTimer();
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

                    stopTimer();
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
                    stopTimer();
                }
            });
        }
    };

    private Emitter.Listener onDataReceive = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = args[0].toString();
                        System.out.println("onDataReceive ---> Data: " + data);

                        try {
                            JSONObject jsonObj = new JSONObject(data);
                            String node_id_server_gen = jsonObj.getString("id_node_server_gen");

                        /*
                        * 2021.03.13 Tuan.VuAnh
                        * This code is temporary for testing
                        /*
                        Node node = new Node();
                        if ((listNodes.size() > 0)) {
                            int length = listNodes.size();
                            for (int i = 0; i < length ; i++){
                                Node item = listNodes.get(i);
                                if (item.getId_server_gen().equals(node_id_server_gen)){
                                    node = item;
                                    break;
                                }
                            }
                        }
                        */

                            if ((HomeActivity.selectedNode != null) && (HomeActivity.selectedNode.getId_server_gen().equals(node_id_server_gen) == true)) { //NodeId_Server_gen decide to which node is receive data through SocketIO
                                String dataJSON = jsonObj.getString("data");

                                int firstIndexOfBracket = dataJSON.indexOf("{");
                                int secondIndexOfBracket = dataJSON.substring(firstIndexOfBracket + 1).indexOf("{");

                                String timePackage = dataJSON.substring(firstIndexOfBracket + 2, secondIndexOfBracket - 2);
                                ;
                                dataJSON = dataJSON.substring(secondIndexOfBracket, dataJSON.length() - 1);

                                JSONObject dataObj = new JSONObject(dataJSON);
//                            String latitude = dataObj.getString("LAT");
//                            String longitude = dataObj.getString("LONG");
                                String name = dataObj.getString("ID");
                                double temperature = Double.parseDouble(dataObj.getString("T"));
                                double pH = Double.parseDouble(dataObj.getString("PH"));
                                double dO = Double.parseDouble(dataObj.getString("DO"));
                                double salt = Double.parseDouble(dataObj.getString("Salt"));
                                double h2s = Double.parseDouble(dataObj.getString("H2S"));
                                double nh4 = Double.parseDouble(dataObj.getString("NH3"));
                                double tss = Double.parseDouble(dataObj.getString("TSS"));
                                double cod = Double.parseDouble(dataObj.getString("COD"));

//                            Log.d("BieuDoRealTimeFragment", "FUNCTION: onDataReceive, data: " + dataJSON);

                                Datapackage datapackage = new Datapackage();
                                datapackage.setDO(dO);
                                datapackage.setH2S(h2s);
                                datapackage.setNH3(nh4);
                                datapackage.setPH(pH);
                                datapackage.setSalt(salt);
                                datapackage.setTemp(temperature);
                                datapackage.setTSS(tss);
                                datapackage.setCOD(cod);
                                datapackage.setTime_Package(timePackage);

                                selectedNodeName = HomeActivity.selectedNode.getName();
                                updateView(datapackage);

                                //get settings of data
                                getPreferences();
                                //check current data and data on settings

                                setColorTextWarning(temperature, Temp_Min, Temp_Max, txt_Temp);
                                setColorTextWarning(h2s, H2S_Min, H2S_Max, txt_H2S);
                                setColorTextWarning(salt, Salt_Min, Salt_Max, txt_Salt);
                                setColorTextWarning(nh4, NH4_Min, NH4_Max, txt_NH4);

                                System.out.println("onDataReceive ---> id_node_server_gen: " + HomeActivity.selectedNode.getId_server_gen());

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }
    };

    //change color of text parameter if it's out of range
    private void setColorTextWarning(double param, double min, double max, TextView txtParam){
        if (param > max || param < min){
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

//        Oxy_Max =  preferences.getFloat(SettingsActivity.KEY_OXY_MAX, Constant.DEFAULT_OXY_MAX);
//        Oxy_Min =  preferences.getFloat(SettingsActivity.KEY_OXY_MIN, Constant.DEFAULT_OXY_MIN);

        H2S_Max =  preferences.getFloat(SettingsActivity.KEY_H2S_MAX, Constant.DEFAULT_H2S_MAX);
        H2S_Min =  preferences.getFloat(SettingsActivity.KEY_H2S_MIN, Constant.DEFAULT_H2S_MIN);
        NH4_Max =  preferences.getFloat(SettingsActivity.KEY_NH4_MAX, Constant.DEFAULT_NH4_MAX);
        NH4_Min =  preferences.getFloat(SettingsActivity.KEY_NH4_MIN, Constant.DEFAULT_NH4_MIN);
//        NO2_Max =  preferences.getFloat(SettingsActivity.KEY_NO2_MAX, Constant.DEFAULT_NO2_MAX);
//        NO2_Min =  preferences.getFloat(SettingsActivity.KEY_NO2_MIN, Constant.DEFAULT_NO2_MIN);
    }

    //check limit of parameter
    private boolean checkOutOfRangeParameter(double parameter, double min, double max){
        if (parameter < min || parameter > max) {
            return true; //if out of range
        }
        return false; //if in range
    }
}
