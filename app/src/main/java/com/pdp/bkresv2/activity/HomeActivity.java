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

    public static Customer customer;
    private final int REQUEST_SETTING_CONFIG = 111;

    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();

    //Widget
    TextView txt_Nav_UserName, txt_Nav_Email;

    //View pager
    private ViewPager pagerGiamSatHeThong;
    private CustomPagerPagerGiamSat adapterPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        customer = (Customer) i.getSerializableExtra("customerObject");
        getLakeAndDevice();

        String userName = customer.getUsername();
        String email = customer.getEmail();
        txt_Nav_UserName.setText(userName);
        txt_Nav_Email.setText(email);

        pagerGiamSatHeThong= (ViewPager) findViewById(R.id.pagerGiamSatHeThong);
        adapterPager=new CustomPagerPagerGiamSat(getSupportFragmentManager(),getListFragments());
        pagerGiamSatHeThong.setAdapter(adapterPager);

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
        BieuDoRealTimeFragment bieuDoRealTimeFragment=new BieuDoRealTimeFragment();
        list.add(bieuDoRealTimeFragment);
        ThongSoRealTimeFragment thongSoRealTimeFragment=new ThongSoRealTimeFragment();
        list.add(thongSoRealTimeFragment);

        return list;
    }

    public void getLakeAndDevice(){
        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_LAKE_AND_DEVICE)
                .buildUpon()
                .appendQueryParameter("HomeId", customer.getHomeId() + "").build();
        DownloadJSON downloadJSON = new DownloadJSON(this);

        System.out.println(builder.toString());

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



                }
            }

            @Override
            public void onFail(String msgError) {
                Log.i("Error", msgError);
            }
        });
    }


}
