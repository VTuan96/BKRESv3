package com.pdp.bkresv2.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.pdp.bkresv2.adapter.CustomPagerAdapter;
import com.pdp.bkresv2.adapter.GraphAdapter;
import com.pdp.bkresv2.adapter.LakeAdapter;
import com.pdp.bkresv2.fragment.BieuDoRealTimeFragment;
import com.pdp.bkresv2.fragment.DatePickerFragment;
import com.pdp.bkresv2.fragment.DeviceFragment;
import com.pdp.bkresv2.model.Customer;
import com.pdp.bkresv2.model.Device;
import com.pdp.bkresv2.model.Graph;
import com.pdp.bkresv2.model.Lake;
import com.pdp.bkresv2.model.Node;
import com.pdp.bkresv2.model.Project;
import com.pdp.bkresv2.service.NodeService;
import com.pdp.bkresv2.service.ProjectService;
import com.pdp.bkresv2.task.DownloadJSON;
import com.pdp.bkresv2.utils.Constant;
import com.pdp.bkresv2.utils.XuLyThoiGian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


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


    TextView txtSelectNode, txtSelectTime;
    RecyclerView rvGraphHistory;
    ImageView ivSelectNode, ivSelectTime;
    ProgressBar pbGraphHistory;

    //My code
    private ViewPager pagerDevice;
    private CustomPagerAdapter adapterDevice;
    private List<DeviceFragment> listFragments = new ArrayList<>();
    public static int positionFragment=0;

    private ArrayList<Node> listNodes = new ArrayList<>();
    private Project project = new Project();
    private Node selectedNode;
    private int selectedIndex = 0;
    private  boolean isNodeChanged = false;

    private boolean isNodeSelected = false;
    private boolean isTimeSelected = false;

    private ArrayList<Graph> listGraph=new ArrayList<>();
    private GraphAdapter adapter = new GraphAdapter(listGraph);
    private int count = 0;

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

    private ArrayList<Entry> entriesCOD=new ArrayList<>();
    private ArrayList labelsCOD = new ArrayList<String>();

    private ArrayList<Entry> entriesTSS =new ArrayList<>();
    private ArrayList labelsTSS = new ArrayList<String>();

    //All label of graph
    private String [] arrLabels = new String[]{"PH","Salt","DO", "Temp", "H2S","NH3", "TSS", "COD" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bieu_do);

        getProjectInformation();
        getNodesInformation();

        initWidget();
        showBackArrow();

//        calendar = Calendar.getInstance();
//        year = calendar.get(Calendar.YEAR);
//
//        month = calendar.get(Calendar.MONTH);
//        day = calendar.get(Calendar.DAY_OF_MONTH);

        getHistoryTime();
        getHistoryNode();

    }

    private void getProjectInformation() {
        ProjectService service = new ProjectService(this);
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
        selectedNode = HomeActivity.listNodes.size() > 0 ? HomeActivity.listNodes.get(0) : null; // Default is first node

        /*
        NodeService service = new NodeService(this);
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
         */
    }

    private void selectDeviceDialog(String title, final Context context) {
        String tempSelectedDevice = "";
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_select_device,null);
//        builder.setView(R.layout.layout_select_device);
        builder.setView(view);
        builder.setTitle(title);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(800, 600); //Controlling width and height.
        alertDialog.show();

        final Spinner spinner_Lake = (Spinner)alertDialog.findViewById(R.id.spinner_lake);
//        final Spinner spinner_Device = (Spinner)alertDialog.findViewById(R.id.spinner_device);

        int listSize = HomeActivity.listNodes.size();
        if(listSize == 0){
            Toast.makeText(context, "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listSize];

        for(int i = 0; i < listSize; i++)
        {
            arr_lake[i] = HomeActivity.listNodes.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,arr_lake);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Lake.setAdapter(adapter);

        spinner_Lake.setSelection(selectedIndex);

        spinner_Lake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedIndex != i) {
                    isNodeChanged = true;
                }
                selectedIndex = i;
                selectedNode = HomeActivity.listNodes.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button btn_Ok = (Button) alertDialog.findViewById(R.id.btn_Ok);
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isNodeChanged == true) {
                    txtSelectNode.setText(selectedNode.getName());
                    isNodeSelected = true;

                    resetGraphHistoryData();
                    processData();
                }
                alertDialog.dismiss();
                isNodeChanged = false;
            }
        });

        Button btn_Huy = (Button) alertDialog.findViewById(R.id.btn_Huy);
        btn_Huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                isNodeChanged = false;
            }
        });
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private double setDataValue(String valStr) {
        if (isNumeric(valStr)) {
            return Double.parseDouble(valStr);
        }
        return Double.parseDouble("0");
    }

    private void showProgressBar() {
        final int[] progress = {0};
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress[0] = progress[0] + 5;
                if (progress[0] == 100) {
                    progress[0] = 0;
                }
                pbGraphHistory.setProgress(progress[0]);
            }
        });
        thread.start();

    }

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }

        pbGraphHistory.setProgress(0);
        pbGraphHistory.setVisibility(View.GONE);
    }

    private void startTimer(){
        pbGraphHistory.setVisibility(View.VISIBLE);
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
                        pbGraphHistory.setProgress(progress[0]);
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 200);
    }

    private void processData() {
        if (isNodeSelected && isTimeSelected) {
            startTimer();

            String nodeId = selectedNode.getId_server_gen();
            String startDate = tmpNgayThangNam + " " + "00:00:00";
            String endDate = tmpNgayThangNam + " " + "23:59:59";

            NodeService service = new NodeService(this);
            service.getNodeHistoryInfo(new NodeService.NodeHistoryCallBack() {
                @Override
                public void onNodeHistoryCallBack(String data) {
                    System.out.println("processData - onNodeHistoryCallBack: " + data);

                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        JSONArray dataArr = jsonObject.getJSONArray("data");
                        int length = dataArr.length();
                        for (int i = 0; i < length; i++) {
                            JSONObject objItem = dataArr.getJSONObject(i);

                            String time = objItem.getString("time");
                            int indexOfColon = time.indexOf(":");
                            time = time.substring(indexOfColon - 2);
                            double temperature = 0, pH = 0, dO = 0, salt = 0, h2s = 0, nh4 = 0, tss = 0, cod = 0;
                            String tempStr = objItem.getString("T");
                            String pHStr = objItem.getString("PH");
                            String dOStr = objItem.getString("DO");
                            String saltStr = objItem.getString("Salt");
                            String h2sStr = objItem.getString("H2S");
                            String nh4Str = objItem.getString("NH3");
                            String tssStr = objItem.getString("TSS");
                            String codStr = objItem.getString("COD");

                            try {
                                temperature = setDataValue(tempStr);
                                pH = setDataValue(pHStr);
                                dO = setDataValue(dOStr);
                                salt = setDataValue(saltStr);
                                h2s = setDataValue(h2sStr);
                                nh4 = setDataValue(nh4Str);
                                tss = setDataValue(tssStr);
                                cod = setDataValue(codStr);
                            } catch (NumberFormatException ex) {

                            }

                            BieuDoRealTimeFragment.addEntryAndLabel(entriesPH, labelsPH, pH, count, time);
                            BieuDoRealTimeFragment.addEntryAndLabel(entriesSalt, labelsSalt, salt, count, time);
                            BieuDoRealTimeFragment.addEntryAndLabel(entriesH2S, labelsH2S, h2s, count, time);
                            BieuDoRealTimeFragment.addEntryAndLabel(entriesNH4, labelsNH4, nh4, count, time);
                            BieuDoRealTimeFragment.addEntryAndLabel(entriesTSS, labelsTSS, tss, count, time);
                            BieuDoRealTimeFragment.addEntryAndLabel(entriesDO, labelsDO, dO, count, time);
                            BieuDoRealTimeFragment.addEntryAndLabel(entriesTemp, labelsTemp, temperature, count, time);
                            BieuDoRealTimeFragment.addEntryAndLabel(entriesCOD, labelsCOD, cod, count, time);

                            count++;
                        }

                        //get settings of data
                        adapter.notifyDataSetChanged();

                        stopTimer();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stopTimer();
                    }
                }

                @Override
                public void onNodeHistoryFailed(String data) {
                    stopTimer();
                }
            }, nodeId, startDate, endDate);
        }
    }

    private void getHistoryNode() {
        ivSelectNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeviceDialog("Chọn Trạm dữ liệu", BieuDoActivity.this);
            }
        });
    }

    private void getHistoryTime() {
        ivSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "Date Picker");
            }
        });
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
        txtSelectNode = (TextView) findViewById(R.id.txtSelectNode);
        txtSelectTime = (TextView) findViewById(R.id.txtSelectTime);
        ivSelectNode = (ImageView) findViewById(R.id.ivSelectNode);
        ivSelectTime = (ImageView) findViewById(R.id.ivSelectTime);
        pbGraphHistory = (ProgressBar) findViewById(R.id.pbGraphHistory);

        rvGraphHistory = (RecyclerView) findViewById(R.id.rvGraphHistory);
        rvGraphHistory.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(BieuDoActivity.this);
        rvGraphHistory.setLayoutManager(manager);

        count = 0;
        listGraph = new ArrayList<>();
        Graph gPH=new Graph(arrLabels[0],entriesPH,labelsPH);
        Graph gSalt=new Graph(arrLabels[1],entriesSalt,labelsSalt);
        Graph gDO=new Graph(arrLabels[2], entriesDO, labelsDO);
        Graph gTemp=new Graph(arrLabels[3],entriesTemp,labelsTemp);
        Graph gH2S=new Graph(arrLabels[4],entriesH2S,labelsH2S);
        Graph gNH4=new Graph(arrLabels[5], entriesNH4, labelsNH4);
        Graph gTSS=new Graph(arrLabels[6], entriesTSS, labelsTSS);
        Graph gCOD=new Graph(arrLabels[7], entriesCOD, labelsCOD);
        Graph [] arrGraph = new Graph[] {gPH, gSalt, gDO, gTemp, gH2S, gNH4, gTSS, gCOD};
        for (Graph g : arrGraph){
            listGraph.add(g);
        }
        adapter = new GraphAdapter(listGraph);
        rvGraphHistory.setAdapter(adapter);
    }

    private void resetGraphHistoryData() {
        entriesPH = new ArrayList<Entry>();
        entriesSalt = new ArrayList<Entry>();
        entriesDO = new ArrayList<Entry>();
        entriesTemp = new ArrayList<Entry>();
        entriesH2S = new ArrayList<Entry>();
        entriesNH4 = new ArrayList<Entry>();
        entriesTSS = new ArrayList<Entry>();
        entriesCOD = new ArrayList<Entry>();
        listGraph = new ArrayList<Graph>();

        count = 0;
        listGraph = new ArrayList<>();
        Graph gPH=new Graph(arrLabels[0],entriesPH,labelsPH);
        Graph gSalt=new Graph(arrLabels[1],entriesSalt,labelsSalt);
        Graph gDO=new Graph(arrLabels[2], entriesDO, labelsDO);
        Graph gTemp=new Graph(arrLabels[3],entriesTemp,labelsTemp);
        Graph gH2S=new Graph(arrLabels[4],entriesH2S,labelsH2S);
        Graph gNH4=new Graph(arrLabels[5], entriesNH4, labelsNH4);
        Graph gTSS=new Graph(arrLabels[6], entriesTSS, labelsTSS);
        Graph gCOD=new Graph(arrLabels[7], entriesCOD, labelsCOD);
        Graph [] arrGraph = new Graph[] {gPH, gSalt, gDO, gTemp, gH2S, gNH4, gTSS, gCOD};
        for (Graph g : arrGraph){
            listGraph.add(g);
        }
        adapter = new GraphAdapter(listGraph);
        rvGraphHistory.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu_graph,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }
     */

    public void onDateSet(int year, int month, int day){
        String dayStr = "", monthStr = "", yearStr = "" + year;
        if (day < 10) {
            dayStr = "0" + day;
        }
        else {
            dayStr = day + "";
        }

        if (month < 9) {
            monthStr = "0" + (month + 1);
        }
        else {
            monthStr = (month + 1) + "";
        }
        tmpNgayThangNam = dayStr + "/" + monthStr + "/" + yearStr;

        txtSelectTime.setText(tmpNgayThangNam);

        isTimeSelected = true;
        resetGraphHistoryData();
        processData();
    }

}

