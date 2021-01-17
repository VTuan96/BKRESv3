package com.pdp.bkresv2.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.github.mikephil.charting.data.Entry;
import com.pdp.bkresv2.R;
import com.pdp.bkresv2.activity.HomeActivity;
import com.pdp.bkresv2.activity.SettingsActivity;
import com.pdp.bkresv2.adapter.GraphAdapter;
import com.pdp.bkresv2.model.Datapackage;
import com.pdp.bkresv2.model.Device;
import com.pdp.bkresv2.model.Graph;
import com.pdp.bkresv2.model.Lake;
import com.pdp.bkresv2.model.Node;
import com.pdp.bkresv2.model.Project;
import com.pdp.bkresv2.model.User;
import com.pdp.bkresv2.service.NodeService;
import com.pdp.bkresv2.service.ProjectService;
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
public class BieuDoRealTimeFragment extends Fragment {

    User customer;
    DownloadJSON downloadJSON;
    ProgressDialog pDialog;

    public double PH_Max, PH_Min, Temp_Max, Temp_Min, Salt_Max, Salt_Min, Oxy_Max, Oxy_Min, H2S_Max, H2S_Min,NO2_Max, NO2_Min,NH4_Max,NH4_Min;


    private final int REQUEST_SETTING_CONFIG = 111;

    //Widget
    TextView txt_Nav_UserName, txt_Nav_Email, txt_Tittle, txt_Time_Update;

    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();
    String selectedDevice = "";
    String selectedLake = " ";
    String selectedImeiDevice = "";

    private RecyclerView rvBieuDoThongKe;
    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter=new GraphAdapter(listGraph);
    private int count=0;
    private String time="";

    //All components of all graphs
    private ArrayList<Entry> entriesPH=new ArrayList<>();
    private ArrayList labelsPH = new ArrayList<String>();

    private ArrayList<Entry> entriesSalt=new ArrayList<>();
    private ArrayList labelsSalt = new ArrayList<String>();

    private ArrayList<Entry> entriesDO =new ArrayList<>();
    private ArrayList labelsDO = new ArrayList<String>();

    private ArrayList<Entry> entriesTemp=new ArrayList<>();
    private ArrayList labelsTemp = new ArrayList<String>();


    private ArrayList<Entry> entriesH2S=new ArrayList<>();
    private ArrayList labelsH2S = new ArrayList<String>();

    private ArrayList<Entry> entriesNH4 =new ArrayList<>();
    private ArrayList labelsNH4 = new ArrayList<String>();

    private ArrayList<Entry> entriesNH4Min=new ArrayList<>();
    private ArrayList labelsNH4Min = new ArrayList<String>();

    private ArrayList<Entry> entriesNH4Max=new ArrayList<>();
    private ArrayList labelsNH4Max = new ArrayList<String>();

    private ArrayList<Entry> entriesNO2Min=new ArrayList<>();
    private ArrayList labelsNO2Min = new ArrayList<String>();

    private ArrayList<Entry> entriesTUR =new ArrayList<>();
    private ArrayList labelsTUR = new ArrayList<String>();

    private ArrayList<Entry> entriesH2SMax=new ArrayList<>();
    private ArrayList labelsH2SMax = new ArrayList<String>();

    private ArrayList<Entry> entriesH2SMin=new ArrayList<>();
    private ArrayList labelsH2SMin = new ArrayList<String>();

    //All label of graph
    private String [] arrLabels=new String[]{"PH","Salt","Oxy", "Temp", "H2S","NH3","NH4 Max","NH4 Min",
            "NO2 Max","NO2 Min","H2S Max","H2S Min" };

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

    public BieuDoRealTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        customer = HomeActivity.customer;
//        getLakeAndDevice();
        getProjectInformation();
        getNodesInformation();

        View v=inflater.inflate(R.layout.fragment_bieu_do_real_time,container,false);

        initWidget(v);
        pDialog = new ProgressDialog(getContext());

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeviceDialog("Xin lựa chọn Node xem dữ liệu");
            }
        });


        // Socket IO
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

    public void initWidget(View v){
        txt_Time_Update = (TextView) v.findViewById(R.id.txt_time_update);
        txt_Tittle = (TextView) v.findViewById(R.id.txt_title);

        //create graph
        listGraph=new ArrayList<>();
        rvBieuDoThongKe= (RecyclerView) v.findViewById(R.id.rvBieuDoThongKe);
        rvBieuDoThongKe.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        rvBieuDoThongKe.setLayoutManager(manager);
        Graph gPH=new Graph(arrLabels[0],entriesPH,labelsPH);
        Graph gSalt=new Graph(arrLabels[1],entriesSalt,labelsSalt);
        Graph gOxy=new Graph(arrLabels[2], entriesDO, labelsDO);
        Graph gTemp=new Graph(arrLabels[3],entriesTemp,labelsTemp);
        Graph gH2S=new Graph(arrLabels[4],entriesH2S,labelsH2S);
        Graph gNH3=new Graph(arrLabels[5], entriesNH4, labelsNH4);
        Graph gNH4Max=new Graph(arrLabels[6],entriesNH4Max,labelsNH4Max);
        Graph gNH4Min=new Graph(arrLabels[7],entriesNH4Min,labelsNH4Min);
        Graph gNO2Max=new Graph(arrLabels[8],entriesNO2Min, labelsTUR);
        Graph gNO2Min=new Graph(arrLabels[9],entriesNO2Min,labelsNO2Min);
        Graph gH2SMax=new Graph(arrLabels[10],entriesH2SMax,labelsH2SMax);
        Graph gH2SMin=new Graph(arrLabels[11],entriesH2SMin,labelsH2SMin);
        Graph [] arrGraph=new Graph[]{gPH,gSalt,gTemp, gH2S,gH2SMax,gH2SMin,gNH3,gNH4Max,gNH4Min,gNO2Max,gNO2Min,gOxy};
        for (Graph g:arrGraph){
            listGraph.add(g);
        }
        adapter=new GraphAdapter(listGraph);
        rvBieuDoThongKe.setAdapter(adapter);
    }

    String tempSelectedDevice;

    public void selectDeviceDialog(String title) {
        tempSelectedDevice = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.layout_select_device,null);
        builder.setView(view);
        builder.setTitle(title);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(800, 600); //Controlling width and height.
        alertDialog.show();

        final Spinner spinner_Node = (Spinner)alertDialog.findViewById(R.id.spinner_lake);

        if(listNodes.size() == 0){
            Toast.makeText(getContext(), "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listNodes.size()];

        for(int i=0; i<listNodes.size(); i++)
            arr_lake[i] = listNodes.get(i).getName();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (getContext(), android.R.layout.simple_spinner_item,arr_lake);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Node.setAdapter(adapter);

        spinner_Node.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                int LakeId = listLake.get(i).getLakeId();
//                selectedLake = spinner_Lake.getSelectedItem().toString();
//                int count = 0;
//                for(int k=0 ; k<listDevice.size(); k++){
//                    if(listDevice.get(k).getLakeId() == LakeId){
//                        count++;
//                    }
//                }
//
//                String arr[] = new String [count];
//                count = -1;
//                for(int k=0 ; k<listDevice.size(); k++){
//                    if(listDevice.get(k).getLakeId() == LakeId){
//                        count++;
//                        arr[count] = listDevice.get(k).getName();
//                    }
//                }
//
//                ArrayAdapter<String> adapter2 =new ArrayAdapter<String>
//                        (getContext(), android.R.layout.simple_spinner_item, arr);
//                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner_Device.setAdapter(adapter2);
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

    private void getNodesInformation() {
        NodeService service = new NodeService(getContext());
        service.getNodeInfo(new NodeService.NodeServiceCallBack() {
            @Override
            public void onNodeInfoReceived(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    int length = data.length();

                    for (int i = 0; i < length; i++){
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNodeInfoFailed(String failed) {

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
        txt_Time_Update.setText("Cập nhật: " + datapackage.getTime_Package());
        adapter.notifyDataSetChanged();
    }

    public void handleExit(){
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onDataReceive);

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
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(),
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

                    Toast.makeText(getContext(),
                            "Disconnect", Toast.LENGTH_SHORT).show();
                    mSocket.connect();

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
                    Toast.makeText(getContext(),
                            "Lỗi cập nhật dữ liệu", Toast.LENGTH_SHORT).show();
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
//                    Log.i("Socket IO data", data);

                    try {
                        Node node = new Node();
                        if (listNodes.size() > 0) {
                            node = listNodes.get(13);
                        }

                        JSONObject jsonObj = new JSONObject(data);
                        String node_id_server_gen = jsonObj.getString("id_node_server_gen");
                        if(node.getId_server_gen().equals(node_id_server_gen) == true){ //NodeId_Server_gen decide to which node is receive data through SocketIO
                            String dataJSON = jsonObj.getString("data");

                            int firstIndexOfBracket = dataJSON.indexOf("{");
                            String timePackage = dataJSON.substring(0, firstIndexOfBracket - 1);
                            dataJSON = dataJSON.substring(firstIndexOfBracket);

                            JSONObject dataObj = new JSONObject(dataJSON);
                            String latitude = dataObj.getString("LAT");
                            String longitude = dataObj.getString("LONG");
                            String name = dataObj.getString("ID");
                            double temperature = Double.parseDouble(dataObj.getString("T"));
                            double pH = Double.parseDouble(dataObj.getString("pH"));
                            double dO = Double.parseDouble(dataObj.getString("DO"));
                            double salt = Double.parseDouble(dataObj.getString("Salt"));
                            double h2s = Double.parseDouble(dataObj.getString("H2S"));
                            double nh4 = Double.parseDouble(dataObj.getString("NH4"));
                            double tur = Double.parseDouble(dataObj.getString("TUR"));


                            Log.d("BieuDoRealTimeFragment", "FUNCTION: onDataReceive, data: " + dataJSON);


//                            String Time_Package = jsonObj.getString("Datetime_Packet");
//                            double PH = jsonObj.getDouble("PH");
//                            double Salt = jsonObj.getDouble("Salt");
//                            double Oxy = jsonObj.getDouble("Oxy");
//                            double Temp = jsonObj.getDouble("NhietDo");
//                            double H2S = jsonObj.getDouble("H2S");
//                            double NH3 = jsonObj.getDouble("NH3");
//                            double NH4Min = jsonObj.getDouble("NH4Min");
//                            double NH4Max = jsonObj.getDouble("NH4Max");
//                            double NO2Min = jsonObj.getDouble("NO2Min");
//                            double NO2Max = jsonObj.getDouble("NO2Max");
//                            double SulfideMin = jsonObj.getDouble("SulfideMin");
//                            double SulfideMax = jsonObj.getDouble("SulfideMax");
//                            double Alkalinity= jsonObj.getDouble("Alkalinity");
//                            String NgayTao = jsonObj.getString("Datetime_Packet");

                            //them gia tri thong so, va thoi gian vao bang bieu do
                            addEntryAndLabel(entriesPH,labelsPH,pH,count,time);
                            addEntryAndLabel(entriesSalt,labelsSalt,salt,count,time);
                            addEntryAndLabel(entriesH2S,labelsH2S,h2s,count,time);
                            addEntryAndLabel(entriesNH4, labelsNH4,nh4,count,time);
                            addEntryAndLabel(entriesTUR, labelsTUR,tur,count,time);
                            addEntryAndLabel(entriesDO, labelsDO,dO,count,time);
                            addEntryAndLabel(entriesTemp,labelsTemp,temperature,count,time);


//                            System.out.println("size graph:"+listGraph.size());
                            System.out.println("cout:"+count);

                            adapter=new GraphAdapter(listGraph);
                            adapter.notifyDataSetChanged();
                            rvBieuDoThongKe.setAdapter(adapter);

                            Datapackage datapackage = new Datapackage();
                            datapackage.setDO(dO);
                            datapackage.setH2S(h2s);
                            datapackage.setNH4(nh4);
                            datapackage.setPH(pH);
                            datapackage.setSalt(salt);
                            datapackage.setTemp(temperature);
                            datapackage.setTUR(tur);
                            datapackage.setTime_Package(timePackage);

                            updateView(datapackage);
                            count++;

                            //get settings of data
                            getPreferences();
                            //check current data and data on settings
                            checkParameter(pH,PH_Min,PH_Max);
                            checkParameter(temperature,Temp_Min,Temp_Max);
                            checkParameter(dO,Oxy_Min,Oxy_Max);
                            checkParameter(salt,Salt_Min,Salt_Max);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    //add Entry and label into a graph
    private void addEntryAndLabel(ArrayList<Entry> entries,ArrayList<String> labels, double value, int index, String time){
        entries.add(new Entry(index,(float) value));
        labels.add(time);
    }

    //show warining about out of range data
    private void showWarning(){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Cảnh báo")
                .setContentText("Thông số bị vượt ngưỡng lúc "+time+"!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setSound(alarmSound);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(100,mBuilder.build());

    }

    //get data on settings
    private void getPreferences(){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
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

    //check limitted of parameter. if it out of range => show warning
    private void checkParameter(double parameter, double min, double max){
        if (parameter < min || parameter > max){
            showWarning();
        }
    }


}
