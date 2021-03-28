package com.pdp.bkresv2.service;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pdp.bkresv2.activity.LoginActivity;
import com.pdp.bkresv2.utils.Constant;

import java.util.HashMap;
import java.util.Map;

public class NodeService {
    private static final String TAG_NODE_SERVICE = "NodeService";
    private Context mContext;
    private String NODE_LIST_URL = Constant.URL + Constant.API_NODE_LIST;
    private String NODE_HISTORY_URL = Constant.URL + Constant.API_NODE_HISTORY;

    public NodeService(Context mContext) {
        this.mContext = mContext;
    }

    public void getNodeHistoryInfo(final NodeHistoryCallBack callBack, String nodeId, String startDate, String endDate){
        Uri builder = Uri.parse(NODE_HISTORY_URL)
                .buildUpon()
                .appendQueryParameter("page", "1")
                .appendQueryParameter("id", nodeId)
                .appendQueryParameter("start", startDate)
                .appendQueryParameter("end", endDate).build();

        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.GET, builder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG_NODE_SERVICE, "FUNCTION: onNodeHistoryCallBack, response: " + response);
                        callBack.onNodeHistoryCallBack(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG_NODE_SERVICE, "error => onNodeHistoryCallBack: " + error.toString());
                        callBack.onNodeHistoryFailed(error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(TAG_NODE_SERVICE, "FUNCTION: getHeaders, Token: " + LoginActivity.AUTHEN_TOKEN);

                Map<String,String> params = new HashMap<String, String>();
                params.put("accept","application/json");
                params.put("Authorization","Token " + LoginActivity.AUTHEN_TOKEN);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public void getNodeInfo(final NodeServiceCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.GET, NODE_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG_NODE_SERVICE, "FUNCTION: onNodeInfoReceived, response: " + response);
                        callBack.onNodeInfoReceived(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG_NODE_SERVICE, "error => onNodeInfoReceived: " + error.toString());
                        callBack.onNodeInfoFailed(error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(TAG_NODE_SERVICE, "FUNCTION: getHeaders, Token: " + LoginActivity.AUTHEN_TOKEN);

                Map<String,String> params = new HashMap<String, String>();
                params.put("accept","application/json");
                params.put("Authorization","Token " + LoginActivity.AUTHEN_TOKEN);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public interface NodeServiceCallBack {
        void onNodeInfoReceived(String response);
        void onNodeInfoFailed(String failed);
    }

    public interface NodeHistoryCallBack {
        void onNodeHistoryCallBack(String data);
        void onNodeHistoryFailed(String data);
    }
}
