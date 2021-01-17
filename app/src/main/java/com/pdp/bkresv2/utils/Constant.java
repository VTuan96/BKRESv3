package com.pdp.bkresv2.utils;

/**
 * Created by Phung Dinh Phuc on 28/07/2017.
 */

public class Constant {
    //public static final String URL = "http://192.168.1.144:6688/";
    public static final String URL = "http://202.191.56.104:5522/";
    public static final String TAG_LOGIN = "LOGIN ACTIVITY";
    public static final String TAG_MAIN = "MAIN ACTIVITY";
    public static final String TAG_URL_SERVICE = "URL";
    public static final String TAG_DATA_RESPONSE= "Data Response";

    public static final String API_CUSTOMER_LOGIN = "authentication/login";
    public static final String API_USER_INFO = "users/info";
    public static final String API_GET_LAKE_AND_DEVICE = "Lake/GetLakeAndDeviceByHomeId?";
    public static final String API_GET_DATA_PACKAGE = "Datapackage/GetDatapackageByDeviceId?";
    public static final String API_GET_DATA_THONGKE = "ThongKe/GetValues?";

    public static final String API_PROJECT_LIST = "project/list?page=0";

    public static final String API_NODE_LIST = "node/list?page=0";

    public static final float DEFAULT_TEMP_MAX = 33f;
    public static final float DEFAULT_TEMP_MIN = 18f;
    public static final float DEFAULT_SALT_MAX = 35f;
    public static final float DEFAULT_SALT_MIN = 5f;
    public static final float DEFAULT_OXY_MAX = 100f;
    public static final float DEFAULT_OXY_MIN = 3.5f;
    public static final float DEFAULT_PH_MAX = 9f;
    public static final float DEFAULT_PH_MIN = 7f;
    public static final float DEFAULT_NH4_MAX = 0.3f;
    public static final float DEFAULT_NH4_MIN = 0.0f;
    public static final float DEFAULT_H2S_MAX = 0.05f;
    public static final float DEFAULT_H2S_MIN = 0.0f;
    public static final float DEFAULT_NO2_MAX = 1.0f;
    public static final float DEFAULT_NO2_MIN = 0.0f;

    public static final String PROJECT_NAME = "BKRES-PY";
}
