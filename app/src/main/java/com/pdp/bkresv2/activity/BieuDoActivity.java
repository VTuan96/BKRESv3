package com.pdp.bkresv2.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.pdp.bkresv2.R;
import com.pdp.bkresv2.model.Customer;
import com.pdp.bkresv2.model.Device;
import com.pdp.bkresv2.model.Lake;
import com.pdp.bkresv2.task.DownloadJSON;
import com.pdp.bkresv2.utils.Constant;
import com.pdp.bkresv2.utils.XuLyThoiGian;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


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
    //GraphView graph;
    //GraphViewDa[] data;
    //LineGraphSeries<DataPoint> series;
    LineChart graph;
    LineDataSet dataset;
    LineData data;
    ArrayList<Entry> entries;
    ArrayList<String> labels;

    Spinner spinner_Lake, spinner_Device, spinner_Thongso;
    Button btn_DoiNgayThang, btn_Xem;
    TextView txt_NgayThangNam, txt_BieuDo;

    DownloadJSON downloadJSON;
    Customer customer;
    ArrayList<Lake> listLake = new ArrayList<>();
    ArrayList<Device> listDevice = new ArrayList<>();

    String arr_thongso[] = {"PH", "Muối", "Oxy", "Nhiệt độ", "H2S", "NH3", "NH4 Min", "NH4 Max", "N02 Min", "NO2 Max", "Sulfide Min", "Sulfide Max"};
    String selectedLake = "";
    String tempSelectedDevice = "";
    String tmpSelectedDeviceId = "";
    String tempSelectThongSo = "";
    String tmpNgayThangNam = "";

    private DatePicker datePicker;
    private Calendar calendar;
    SimpleDateFormat formatDate = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a", Locale.US);
    Date dateMin, dateMax;
    private int year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bieu_do);
        initWidget();
        showBackArrow();
        tmpNgayThangNam = XuLyThoiGian.layNgayHienTai();
        txt_NgayThangNam.setText(tmpNgayThangNam);
        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customerObj");
        listLake = (ArrayList<Lake>) i.getSerializableExtra("listLake");
        listDevice = (ArrayList<Device>) i.getSerializableExtra("listDevice");
        putDataToSpinner();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        btn_DoiNgayThang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        btn_Xem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataThongKe();
            }
        });


        /*series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);*/
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
        spinner_Lake = (Spinner) findViewById(R.id.spinner_lake);
        spinner_Device = (Spinner) findViewById(R.id.spinner_device);
//        spinner_Thongso = (Spinner) findViewById(R.id.spinner_thongso);
        //graph = (GraphView) findViewById(R.id.graph);
        graph = (LineChart) findViewById(R.id.graph);
//        btn_DoiNgayThang = (Button) findViewById(R.id.btn_ChonNgay);
        btn_Xem = (Button) findViewById(R.id.btn_Ok);
//        txt_NgayThangNam = (TextView) findViewById(R.id.txt_NgayThangNam);
        txt_BieuDo = (TextView) findViewById(R.id.txt_TenBieuDo);
    }

    public void putDataToSpinner() {
        if (listLake.size() == 0) {
            Toast.makeText(this, "Tài khoản này không quản lý thiết bị nào!", Toast.LENGTH_SHORT).show();
        } else {
            String arr_lake[] = new String[listLake.size()];
            String arr_device[] = new String[listDevice.size()];

            for (int i = 0; i < listLake.size(); i++)
                arr_lake[i] = listLake.get(i).getName();
            for (int j = 0; j < listDevice.size(); j++)
                arr_device[j] = listDevice.get(j).getName();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item, arr_lake);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_Lake.setAdapter(adapter);

            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>
                    (this, android.R.layout.simple_spinner_item, arr_thongso);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_Thongso.setAdapter(adapter3);

            spinner_Lake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    int LakeId = listLake.get(i).getLakeId();
                    selectedLake = spinner_Lake.getSelectedItem().toString();
                    int count = 0;
                    for (int k = 0; k < listDevice.size(); k++) {
                        if (listDevice.get(k).getLakeId() == LakeId) {
                            count++;
                        }
                    }

                    String arr[] = new String[count];
                    count = -1;
                    for (int k = 0; k < listDevice.size(); k++) {
                        if (listDevice.get(k).getLakeId() == LakeId) {
                            count++;
                            arr[count] = listDevice.get(k).getName();
                        }
                    }

                    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                            (BieuDoActivity.this, android.R.layout.simple_spinner_item, arr);
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
                    for (int j = 0; j < listDevice.size(); j++) {
                        if (listDevice.get(j).getName().compareTo(tempSelectedDevice) == 0)
                            tmpSelectedDeviceId = listDevice.get(j).getId() + "";
                    }
                    //Toast.makeText(BieuDoActivity.this, tmpSelectedDeviceId, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner_Thongso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tempSelectThongSo = (position + 1) + "";
                    //Toast.makeText(BieuDoActivity.this, tempSelectThongSo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void getDataThongKe() {
        final ProgressDialog progress = ProgressDialog.show(this, null,
                "Đang tải dữ liệu", true);
        progress.show();

        Uri builder = Uri.parse(Constant.URL + Constant.API_GET_DATA_THONGKE)
                .buildUpon()
                .appendQueryParameter("strCode", "QN290394")
                .appendQueryParameter("time", tmpNgayThangNam)
                .appendQueryParameter("paramId", tempSelectThongSo)
                .appendQueryParameter("deviceId", tmpSelectedDeviceId).build();
        Log.d("bieu do: ",builder.toString());
        downloadJSON = new DownloadJSON(this);



        downloadJSON.GetJSON(builder, new DownloadJSON.DownloadJSONCallBack() {
            @Override
            public void onSuccess(String msgData) {
                Log.i("Data", msgData);
                try{
                    //JSONObject jsonObj = new JSONObject(msgData);
                    JSONArray jsonArray = new JSONArray(msgData);
                    if(jsonArray.length()==0){
                        progress.dismiss();
                        Toast.makeText(BieuDoActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                    }

                    else{
                        txt_BieuDo.setText("Biểu đồ " + arr_thongso[Integer.parseInt(tempSelectThongSo)-1]);
                        entries = new ArrayList<>();
                        labels = new ArrayList<String>();

                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject objTmp = jsonArray.getJSONObject(i);
                            double value = objTmp.getDouble("value");
                            String time = objTmp.getString("time");
                            String[] words = time.split("\\s");
                            Log.d("time",words[0]+words[1]);
                            entries.add(new Entry((float)value, i));
                            labels.add(words[1]);
                        }

                        dataset = new LineDataSet(entries, arr_thongso[Integer.parseInt(tempSelectThongSo)-1]);
                        data = new LineData(labels, dataset);
                        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
                        dataset.setDrawCubic(true);
                        dataset.setDrawFilled(true);

                        graph.setData(data);
                        graph.animateY(3000);
                        progress.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String msgError) {
                progress.dismiss();
                Log.i("Error", msgError);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
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
                    //showDate(arg1, arg2 + 1, arg3);
                    txt_NgayThangNam.setText(tmpNgayThangNam);
                }
            };

    private void showDate(int year, int month, int day) {
        txt_NgayThangNam.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

}
