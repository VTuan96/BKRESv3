package com.pdp.bkresv2.service;

import android.content.Context;
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

    public NodeService(Context mContext) {
        this.mContext = mContext;
    }

    public void getNodeInfo(final NodeServiceCallBack callBack){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.GET, NODE_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(TAG_NODE_SERVICE, "FUNCTION: getProjectInfo, response: " + response);
                        callBack.onNodeInfoReceived(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG_NODE_SERVICE, "error => " + error.toString());
                        callBack.onNodeInfoFailed(error.toString());
//                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
//                        builder1.setMessage("Lỗi khi đăng nhập. Vui Lòng Thử lại");
//                        builder1.setTitle("Lỗi mạng");
//                        builder1.setPositiveButton(
//                                "OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//
//                        AlertDialog alert11 = builder1.create();
//                        alert11.show();
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
}
