package com.pdp.bkresv2.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pdp.bkresv2.R;
import com.pdp.bkresv2.utils.Constant;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    EditText edt_PH_Max, edt_PH_Min, edt_Temp_Max, edt_Temp_Min, edt_Salt_Max, edt_Salt_Min, edt_Oxy_Max, edt_Oxy_Min, edt_H2S, edt_NO2, edt_NH3;
    SharedPreferences SP;

    double PH_Max, PH_Min, Temp_Max, Temp_Min, Salt_Max, Salt_Min, Oxy_Max, Oxy_Min, H2S, NO2, NH3;

    public static final String SETTING_PREFERENCES = "SettingPrefer";
    public static final String KEY_TEMP_MAX = "TempMax";
    public static final String KEY_TEMP_MIN = "TempMin";
    public static final String KEY_PH_MAX = "PHMax";
    public static final String KEY_PH_MIN = "PHMin";
    public static final String KEY_SALT_MAX = "SaltMax";
    public static final String KEY_SALT_MIN = "SaltMin";
    public static final String KEY_OXY_MAX = "OxyMax";
    public static final String KEY_OXY_MIN = "OxyMin";
    public static final String KEY_H2S = "H2S";
    public static final String KEY_NO2 = "NO2";
    public static final String KEY_NH3 = "NH3";

    public static String[][] ThongSo = {{"Nhiệt độ", "20", "30"},   //0
                                        {"Độ Muối", "10", "25"},    //1
                                        {"Độ Trong", "30", "35"},   //2
                                        {"pH", "7.5", "8.5"},       //3
                                        {"Độ kiềm", "100", "150"},  //4
                                        {"Oxy hòa tan", "0", "0.05"},//5
                                        {"H2S", "0", "0.03"},       //6
                                        {"NH3", "0", "0.1"},        //7
                                        {"NO2", "0", "0.2"}};       //8
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        showBackArrow();
        getSupportActionBar().setDisplayShowTitleEnabled(true); //optional

        initWidget();

        SP = getSharedPreferences(SETTING_PREFERENCES, Context.MODE_PRIVATE);
        loadDataFromSharePreferences();
    }

    public void showBackArrow(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
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

    public void initWidget(){
        edt_PH_Max = (EditText) findViewById(R.id.edtxt_PH_Max);
        edt_PH_Min = (EditText) findViewById(R.id.edtxt_PH_Min);
        edt_Temp_Max = (EditText) findViewById(R.id.edtxt_Temperature_Max);
        edt_Temp_Min = (EditText) findViewById(R.id.edtxt_Temperature_Min);
        edt_Salt_Max = (EditText) findViewById(R.id.edtxt_Salt_Max);
        edt_Salt_Min = (EditText) findViewById(R.id.edtxt_Salt_Min);
        edt_Oxy_Max = (EditText) findViewById(R.id.edtxt_Oxy_Max);
        edt_Oxy_Min = (EditText) findViewById(R.id.edtxt_Oxy_Min);

        edt_NH3 = (EditText) findViewById(R.id.edt_NH3);
        edt_NO2 = (EditText) findViewById(R.id.edt_NO2);
        edt_H2S = (EditText) findViewById(R.id.edt_H2S);
    }

    public void loadDataFromSharePreferences(){
        SP = PreferenceManager.getDefaultSharedPreferences(this);
        PH_Max =  SP.getFloat(KEY_PH_MAX, Constant.DEFAULT_PH_MAX);
        PH_Min =  SP.getFloat(KEY_PH_MIN, Constant.DEFAULT_PH_MIN);

        Temp_Max =  SP.getFloat(KEY_TEMP_MAX, Constant.DEFAULT_TEMP_MAX);
        Temp_Min =  SP.getFloat(KEY_TEMP_MIN, Constant.DEFAULT_TEMP_MIN);

        Salt_Max =  SP.getFloat(KEY_SALT_MAX, Constant.DEFAULT_SALT_MAX);
        Salt_Min =  SP.getFloat(KEY_SALT_MIN, Constant.DEFAULT_SALT_MIN);

        Oxy_Max =  SP.getFloat(KEY_OXY_MAX, Constant.DEFAULT_OXY_MAX);
        Oxy_Min =  SP.getFloat(KEY_OXY_MIN, Constant.DEFAULT_OXY_MIN);

        H2S =  SP.getFloat(KEY_H2S, Constant.DEFAULT_H2S);
        NH3 =  SP.getFloat(KEY_NH3, Constant.DEFAULT_NH3);
        NO2 =  SP.getFloat(KEY_NO2, Constant.DEFAULT_NO2);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.####", otherSymbols);

        edt_Temp_Max.setText(df.format(Temp_Max));
        edt_Temp_Min.setText(df.format(Temp_Min));

        edt_Salt_Max.setText(df.format(Salt_Max));
        edt_Salt_Min.setText(df.format(Salt_Min));

        edt_Oxy_Max.setText(df.format(Oxy_Max));
        edt_Oxy_Min.setText(df.format(Oxy_Min));

        edt_PH_Max.setText(df.format(PH_Max));
        edt_PH_Min.setText(df.format(PH_Min));

        edt_H2S.setText(df.format(H2S));
        edt_NH3.setText(df.format(NH3));
        edt_NO2.setText(df.format(NO2));
    }

    public void updateDataPreference(){
        float _PH_Max = Float.parseFloat(edt_PH_Max.getText().toString());
        float _PH_Min = Float.parseFloat(edt_PH_Min.getText().toString());

        float _Sal_Max = Float.parseFloat(edt_Salt_Max.getText().toString());
        float _Sal_Min = Float.parseFloat(edt_Salt_Min.getText().toString());

        float _Oxy_Max = Float.parseFloat(edt_Oxy_Max.getText().toString());
        float _Oxy_Min = Float.parseFloat(edt_Oxy_Min.getText().toString());

        float _Temp_Max = Float.parseFloat(edt_Temp_Max.getText().toString());
        float _Temp_Min = Float.parseFloat(edt_Temp_Min.getText().toString());

        float _H2S = Float.parseFloat(edt_H2S.getText().toString());
        float _NH3 = Float.parseFloat(edt_NH3.getText().toString());
        float _N02 = Float.parseFloat(edt_NO2.getText().toString());





        SharedPreferences.Editor editor = SP.edit();
        editor.putFloat(KEY_TEMP_MAX, _Temp_Max);
        editor.putFloat(KEY_TEMP_MIN, _Temp_Min);

        editor.putFloat(KEY_SALT_MAX, _Sal_Max);
        editor.putFloat(KEY_SALT_MIN, _Sal_Min);

        editor.putFloat(KEY_OXY_MAX, _Oxy_Max);
        editor.putFloat(KEY_OXY_MIN, _Oxy_Min);

        editor.putFloat(KEY_PH_MAX, _PH_Max);
        editor.putFloat(KEY_PH_MIN, _PH_Min);

        editor.putFloat(KEY_NH3, _NH3);
        editor.putFloat(KEY_H2S, _H2S);
        editor.putFloat(KEY_NO2, _N02);

        editor.commit();
        Toast.makeText(this,"Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingsActivity.this);
            builder1.setMessage("Bạn muốn cập nhật cấu hình thông số mặc định?");
            builder1.setTitle("Cấu hình thông báo");
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            getDefaultDataAndSetToEdt();
                        }
                    });
            builder1.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alert11 = builder1.create();
            alert11.show();

            return true;
        }
        if(id == R.id.action_save){
            updateDataPreference();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDefaultDataAndSetToEdt(){
        edt_Temp_Max.setText(ThongSo[0][1]);
        edt_Temp_Min.setText(ThongSo[0][2]);

        edt_Salt_Max.setText(ThongSo[1][1]);
        edt_Salt_Min.setText(ThongSo[1][2]);

        edt_Oxy_Max.setText(ThongSo[5][1]);
        edt_Oxy_Min.setText(ThongSo[5][2]);

        edt_PH_Max.setText(ThongSo[3][1]);
        edt_PH_Min.setText(ThongSo[3][2]);

        edt_H2S.setText(ThongSo[6][2]);
        edt_NH3.setText(ThongSo[7][2]);
        edt_NO2.setText(ThongSo[8][2]);
    }
}
