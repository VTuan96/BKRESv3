package com.pdp.bkresv2.service;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pdp.bkresv2.activity.LoginActivity;
import com.pdp.bkresv2.model.Project;
import com.pdp.bkresv2.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectService {
    private Context mContext;
    private String PROJECT_LIST_URL = Constant.URL + Constant.API_PROJECT_LIST;

    public ProjectService(Context mContext) {
        this.mContext = mContext;
    }

    public ArrayList<Project> getProjectList(){
        ArrayList<Project> listProject = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.GET, PROJECT_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(Constant.TAG_MAIN, "Get JSON respone: " + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error => " + error.toString());
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
                Log.e("ERROR", "FUNCTION: getHeaders, Token: " + LoginActivity.AUTHEN_TOKEN);

                Map<String,String> params = new HashMap<String, String>();
                params.put("accept","application/json");
                params.put("Authorization","Token " + LoginActivity.AUTHEN_TOKEN);
                return params;
            }
        };
        queue.add(postRequest);

        return listProject;
    }

    public void getProjectInfo(final String projectName, final ProjectServiceCallBack callBack){
        final Project project = new Project();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.GET, PROJECT_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(Constant.TAG_MAIN, "FUNCTION: getProjectInfo, response: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray data = jsonObject.getJSONArray("data");
                            int length = data.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject objItem = data.getJSONObject(i);
                                String id = objItem.getString("id");
                                int noOfNumber = objItem.getInt("Stt");
                                String name = objItem.getString("name");
                                String description = objItem.getString("description");
                                String id_user = objItem.getString("id_user");

                                if (name.toUpperCase().equals(projectName) == true){
                                    project.setId_user(id_user);
                                    project.set_id(id);
                                    project.setName(name);
                                    project.setDescription(description);

                                    callBack.onProjectInfoReceived(project);
                                    break;
                                }
                                else
                                {
                                    callBack.onProjectInfoReceived(project);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "error => " + error.toString());

                        Project errorProject = new Project();
                        callBack.onProjectInfoFailed(errorProject);
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
                Log.e("ERROR", "FUNCTION: getHeaders, Token: " + LoginActivity.AUTHEN_TOKEN);

                Map<String,String> params = new HashMap<String, String>();
                params.put("accept","application/json");
                params.put("Authorization","Token " + LoginActivity.AUTHEN_TOKEN);
                return params;
            }
        };
        queue.add(postRequest);
    }

    public interface ProjectServiceCallBack {
        void onProjectInfoReceived(Project projectSuccess);
        void onProjectInfoFailed(Project projectFailed);
    }
}
