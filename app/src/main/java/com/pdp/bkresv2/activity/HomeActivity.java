package com.pdp.bkresv2.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.github.mikephil.charting.data.Entry;
import com.pdp.bkresv2.R;
import com.pdp.bkresv2.adapter.CustomPagerAdapter;
import com.pdp.bkresv2.adapter.CustomPagerPagerGiamSat;
import com.pdp.bkresv2.adapter.GraphAdapter;
import com.pdp.bkresv2.fragment.BieuDoRealTimeFragment;
import com.pdp.bkresv2.fragment.ThongSoRealTimeFragment;
import com.pdp.bkresv2.model.Customer;
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
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static User customer;
    private final int REQUEST_SETTING_CONFIG = 111;

    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();

    //Widget
    TextView txt_Nav_UserName, txt_Nav_Email;

    //View pager
    private ViewPager pagerGiamSatHeThong;
    private CustomPagerPagerGiamSat adapterPager;

    public static Project projectInfo = null;
    public static Node selectedNode;
    public static int selectedIndex = 0;
    public ArrayList<Node> listNodes = new ArrayList<>();
    public Project project = new Project();
    public static boolean isNodeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getProjectInformation();
        getNodesInformation();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        txt_Nav_UserName = (TextView) hView.findViewById(R.id.txt_nav_username);
        txt_Nav_Email= (TextView) hView.findViewById(R.id.txt_nav_email);

        Intent i = getIntent();
        customer = (User) i.getSerializableExtra("customerObject");
//        getLakeAndDevice();

        String userName = customer.getUsername();
        String email = customer.getEmail();
        txt_Nav_UserName.setText(userName);
        txt_Nav_Email.setText(email);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabThongSo);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeviceDialog("Chọn Trạm dữ liệu");
            }
        });

        pagerGiamSatHeThong= (ViewPager) findViewById(R.id.pagerGiamSatHeThong);
        adapterPager= new CustomPagerPagerGiamSat(getSupportFragmentManager(),getListFragments());
        pagerGiamSatHeThong.setAdapter(adapterPager);

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
    }

    private void selectDeviceDialog(String title) {
        String tempSelectedDevice = "";
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.layout_select_device,null);
//        builder.setView(R.layout.layout_select_device);
        builder.setView(view);
        builder.setTitle(title);
        final android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(800, 600); //Controlling width and height.
        alertDialog.show();

        final Spinner spinner_Lake = (Spinner)alertDialog.findViewById(R.id.spinner_lake);
//        final Spinner spinner_Device = (Spinner)alertDialog.findViewById(R.id.spinner_device);

        if(listNodes.size() == 0){
            Toast.makeText(this, "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        }

        String arr_lake[] = new String[listNodes.size()];

        for(int i = 0; i < listNodes.size(); i++)
        {
            arr_lake[i] = listNodes.get(i).getName();
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
                selectedNode = listNodes.get(i);

                System.out.println("HomeActivity: onItemSelected!!!");

//                spinner_Device.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        Button btn_Ok = (Button) alertDialog.findViewById(R.id.btn_Ok);
        btn_Ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((listener != null) && (isNodeChanged == true)) {
                    Toast.makeText(HomeActivity.this, "Vui lòng đợi trong giây lát!", Toast.LENGTH_LONG).show();
                    listener.onNodeChanged();
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


    //Ham xu li su kien khi giu phim Back de thoat ung dung
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                finish(); // finish activity
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
            Intent t = new Intent(HomeActivity.this, BieuDoActivity.class);
            startActivity(t);

            Toast.makeText(this, "Chức năng đang phát triển!", Toast.LENGTH_LONG).show();

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
                    finish();
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

    public List<Fragment> getListFragments(){
        List<Fragment> list=new ArrayList<>();

        ThongSoRealTimeFragment thongSoRealTimeFragment=new ThongSoRealTimeFragment();
        list.add(thongSoRealTimeFragment);

        BieuDoRealTimeFragment bieuDoRealTimeFragment=new BieuDoRealTimeFragment();
        list.add(bieuDoRealTimeFragment);

        return list;
    }

    public interface NodeChangeListener {
        public void onNodeChanged();
    }

    public static NodeChangeListener listener;
    public void setListener(NodeChangeListener listener) {
        this.listener = listener;
    }
}
