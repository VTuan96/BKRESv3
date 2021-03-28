package com.pdp.bkresv2.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.pdp.bkresv2.R;
import com.pdp.bkresv2.utils.Constant;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


/*
    Document of SeekBar custom:
    https://github.com/warkiz/IndicatorSeekBar/blob/master/app/src/main/res/layout/continuous.xml
 */

public class SettingsActivity extends AppCompatActivity {
    public static SharedPreferences SP;
    public RangeSeekBar sbPH, sbTemp, sbOxy, sbSalt, sbNO2, sbH2S, sbNH4;

    public static double PH_Max, PH_Min, Temp_Max, Temp_Min, Salt_Max, Salt_Min, Oxy_Max, Oxy_Min, H2S_Max, H2S_Min,NO2_Max, NO2_Min,NH4_Max,NH4_Min;

    public static final String SETTING_PREFERENCES = "SettingPrefer";
    public static final String KEY_TEMP_MAX = "TempMax";
    public static final String KEY_TEMP_MIN = "TempMin";
    public static final String KEY_PH_MAX = "PHMax";
    public static final String KEY_PH_MIN = "PHMin";
    public static final String KEY_SALT_MAX = "SaltMax";
    public static final String KEY_SALT_MIN = "SaltMin";
    public static final String KEY_OXY_MAX = "OxyMax";
    public static final String KEY_OXY_MIN = "OxyMin";
    public static final String KEY_H2S_MAX = "H2SMax";
    public static final String KEY_H2S_MIN = "H2SMin";
    public static final String KEY_NO2_MAX= "NO2Max";
    public static final String KEY_NO2_MIN= "NO2Min";
    public static final String KEY_NH4_MIN = "NH4Min";
    public static final String KEY_NH4_MAX = "NH4Max";

    public static float[][] ThongSo = {{20, 30},   //0, nhiet do
                                        {10, 25},    //1, muoi
                                        {30, 35},   //2, Do trong
                                        {7.5f, 8.5f},       //3, pH
                                        {100, 150},  //4, Do kiem
                                        {0, 0.05f},//5, Oxy
                                        {0, 0.03f},       //6, H2S
                                        {0, 0.1f},        //7, NH3
                                        {0, 0.2f}};       //8, NO2
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
        sbPH= (RangeSeekBar) findViewById(R.id.sbPH);
        sbTemp= (RangeSeekBar) findViewById(R.id.sbTemp);
        sbH2S= (RangeSeekBar) findViewById(R.id.sbH2S);
//        sbOxy= (RangeSeekBar) findViewById(R.id.sbOxy);
//        sbNO2= (RangeSeekBar) findViewById(R.id.sbNO2);
        sbNH4= (RangeSeekBar) findViewById(R.id.sbNH4);
        sbSalt= (RangeSeekBar) findViewById(R.id.sbSalt);
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

        H2S_Max =  SP.getFloat(KEY_H2S_MAX, Constant.DEFAULT_H2S_MAX);
        H2S_Min =  SP.getFloat(KEY_H2S_MIN, Constant.DEFAULT_H2S_MIN);
        NH4_Max =  SP.getFloat(KEY_NH4_MAX, Constant.DEFAULT_NH4_MAX);
        NH4_Min =  SP.getFloat(KEY_NH4_MIN, Constant.DEFAULT_NH4_MIN);
        NO2_Max=  SP.getFloat(KEY_NO2_MAX, Constant.DEFAULT_NO2_MAX);
        NO2_Min=  SP.getFloat(KEY_NO2_MIN, Constant.DEFAULT_NO2_MIN);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#.####", otherSymbols);

//        sbPHMax.setProgress((float) PH_Max);
//        sbPHMax.setRangeValues(PH_Min, PH_Max);
        sbPH.setSelectedMaxValue(PH_Max);
        sbPH.setSelectedMinValue(PH_Min);

        sbTemp.setSelectedMaxValue(Temp_Max);
        sbTemp.setSelectedMinValue(Temp_Min);

        sbNH4.setSelectedMaxValue(NH4_Max);
        sbNH4.setSelectedMinValue(NH4_Min);

//        sbNO2.setSelectedMaxValue(NO2_Max);
//        sbNO2.setSelectedMinValue(NO2_Min);

        sbSalt.setSelectedMaxValue(Salt_Max);
        sbSalt.setSelectedMinValue(Salt_Min);

        sbH2S.setSelectedMaxValue(H2S_Max);
        sbH2S.setSelectedMinValue(H2S_Min);

//        sbOxy.setSelectedMaxValue(Oxy_Max);
//        sbOxy.setSelectedMinValue(Oxy_Min);

    }

    public void updateDataPreference(){
//        float _PH_Max = sbPHMax.getProgress();
        float _PH_Max= sbPH.getSelectedMaxValue().floatValue();
        float _PH_Min = sbPH.getSelectedMinValue().floatValue();

        float _Sal_Max = sbSalt.getSelectedMaxValue().floatValue();
        float _Sal_Min = sbSalt.getSelectedMinValue().floatValue();

//        float _Oxy_Max = sbOxy.getSelectedMaxValue().floatValue();
//        float _Oxy_Min = sbOxy.getSelectedMinValue().floatValue();

        float _Temp_Max = sbTemp.getSelectedMaxValue().floatValue();
        float _Temp_Min = sbTemp.getSelectedMinValue().floatValue();

        float _H2S_Max = sbH2S.getSelectedMaxValue().floatValue();
        float _H2S_Min = sbH2S.getSelectedMinValue().floatValue();

        float _NH4_Max = sbNH4.getSelectedMaxValue().floatValue();
        float _NH4_Min = sbNH4.getSelectedMinValue().floatValue();

//        float _N02_Max = sbNO2.getSelectedMaxValue().floatValue();
//        float _N02_Min = sbNO2.getSelectedMinValue().floatValue();


        SharedPreferences.Editor editor = SP.edit();
        editor.putFloat(KEY_TEMP_MAX, _Temp_Max);
        editor.putFloat(KEY_TEMP_MIN, _Temp_Min);

        editor.putFloat(KEY_SALT_MAX, _Sal_Max);
        editor.putFloat(KEY_SALT_MIN, _Sal_Min);

//        editor.putFloat(KEY_OXY_MAX, _Oxy_Max);
//        editor.putFloat(KEY_OXY_MIN, _Oxy_Min);

        editor.putFloat(KEY_PH_MAX, _PH_Max);
        editor.putFloat(KEY_PH_MIN, _PH_Min);

        editor.putFloat(KEY_NH4_MAX, _NH4_Max);
        editor.putFloat(KEY_NH4_MIN, _NH4_Min);
        editor.putFloat(KEY_H2S_MAX, _H2S_Max);
        editor.putFloat(KEY_H2S_MIN, _H2S_Min);
//        editor.putFloat(KEY_NO2_MIN, _N02_Min);
//        editor.putFloat(KEY_NO2_MAX, _N02_Max);

        editor.commit();
        Toast.makeText(this,"Saved", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,"Saved "+_H2S, Toast.LENGTH_SHORT).show();
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
        } else if(id == R.id.action_save){
            updateDataPreference();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDefaultDataAndSetToEdt(){
//        sbPHMax.setProgress(ThongSo[3][1]);
        sbPH.setSelectedMaxValue(ThongSo[3][1]);
        sbPH.setSelectedMinValue(ThongSo[3][0]);

        sbSalt.setSelectedMaxValue(ThongSo[1][1]);
        sbSalt.setSelectedMinValue(ThongSo[1][0]);

//        sbOxy.setSelectedMaxValue(ThongSo[5][1]);
//        sbOxy.setSelectedMinValue(ThongSo[5][0]);

        sbTemp.setSelectedMaxValue(ThongSo[0][1]);
        sbTemp.setSelectedMinValue(ThongSo[0][0]);

        sbH2S.setSelectedMaxValue(ThongSo[6][1]);
        sbH2S.setSelectedMinValue(ThongSo[6][0]);

        sbNH4.setSelectedMaxValue(ThongSo[7][1]);
        sbNH4.setSelectedMinValue(ThongSo[7][0]);

//        sbNO2.setSelectedMaxValue(ThongSo[8][1]);
//        sbNO2.setSelectedMinValue(ThongSo[8][0]);
    }

}
