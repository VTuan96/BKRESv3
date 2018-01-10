package com.pdp.bkresv2.task;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pdp.bkresv2.utils.Constant;
import com.pdp.bkresv2.utils.HienThiThongBao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Phung Dinh Phuc on 29/07/2017.
 */

public class DownloadJSON {
    Context ctx;

    public DownloadJSON(Context ctx){
        this.ctx = ctx;
    }

    public interface  DownloadJSONCallBack{
        void onSuccess (String msgData);

        void onFail(String msgError);
    }


    public void GetJSON(Uri builder, final DownloadJSONCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = builder.toString();
        Log.i(Constant.TAG_URL_SERVICE, url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(Constant.TAG_DATA_RESPONSE,response.toString());
                DeleteCache.deleteCache(ctx);
                String msgData = "";
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    boolean success = jsonObj.getBoolean("success");
                    if(success){
                        JSONArray jsonArray = jsonObj.getJSONArray("data");
                        msgData = jsonArray.toString();

                    }else{

                    }

                    callBack.onSuccess(msgData);

                } catch(JSONException e){
                    e.printStackTrace();
                    callBack.onFail(e.toString());
                    HienThiThongBao.HienThiAlertDialogLoiMang(ctx);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constant.TAG_DATA_RESPONSE, error.getMessage());
            }
        });

        // Adding request to request queue
        queue.add(strReq);
    }

    public void GetJSON2(Uri builder, final DownloadJSONCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = builder.toString();
        Log.i(Constant.TAG_URL_SERVICE, url);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i(Constant.TAG_DATA_RESPONSE,response.toString());
                DeleteCache.deleteCache(ctx);
                String msgData = "";
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    boolean success = jsonObj.getBoolean("success");
                    if(success){
                        JSONObject jsonArray = jsonObj.getJSONObject("data");
                        msgData = jsonArray.toString();

                    }else{

                    }

                    callBack.onSuccess(msgData);

                } catch(JSONException e){
                    e.printStackTrace();
                    callBack.onFail(e.toString());
                    HienThiThongBao.HienThiAlertDialogLoiMang(ctx);
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constant.TAG_DATA_RESPONSE, error.getMessage());
            }
        });

        // Adding request to request queue
        queue.add(strReq);
    }
}
