package com.pdp.bkresv2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pdp.bkresv2.R;
import com.pdp.bkresv2.model.Customer;
import com.pdp.bkresv2.utils.CheckInternet;
import com.pdp.bkresv2.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etID, etPass;
    CheckBox chkShowPass, chkRemember;

    String tmpID, tmpPass;
    ProgressDialog pDialog;
    Button btnLogin;

    //Dat ten cho tap tin luu trang thai
    String prefname = "login_data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        pDialog = new ProgressDialog(this);
        etID = (EditText) this.findViewById(R.id.login_tf_id);
        etPass = (EditText) this.findViewById(R.id.login_tf_pass);

        chkShowPass = (CheckBox) this.findViewById(R.id.chk_hien_pass);
        chkRemember = (CheckBox) this.findViewById(R.id.chk_nho_tk);

        btnLogin = (Button) this.findViewById(R.id.btnLogin);

        XuLyDangNhap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savingPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoringPreferences();
    }

    private void XuLyDangNhap() {
        chkShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked)
                    etPass.setTransformationMethod(null);
                else
                    etPass.setTransformationMethod(new PasswordTransformationMethod());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    LinearLayout mainLayout;

                    // Get your layout set up, this is just an example
                    mainLayout = (LinearLayout) findViewById(R.id.layout_login);

                    // Then just use the following:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                tmpID = etID.getText().toString();
                tmpPass = etPass.getText().toString();
                boolean isFullInputData = false, isConnecting = false;

                if (tmpID.length() == 0) {
                    Toast.makeText(LoginActivity.this, "Cần nhập Username", Toast.LENGTH_SHORT).show();
                } else if (tmpPass.length() == 0) {
                    Toast.makeText(LoginActivity.this, "Cần nhập Password", Toast.LENGTH_SHORT).show();
                } else isFullInputData = true;

                if (!CheckInternet.isNetworkAvailable(getApplicationContext())) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                    builder1.setMessage("Không có kết nối Internet");
                    builder1.setTitle("Lỗi mạng");
                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else isConnecting = true;

                if (isFullInputData && isConnecting) {


                    Uri builder = Uri.parse(Constant.URL + Constant.API_CUSTOMER_LOGIN)
                            .buildUpon()
                            .appendQueryParameter("Username", tmpID)
                            .appendQueryParameter("Pass", tmpPass).build();
                    String url = builder.toString();

                    Log.i(Constant.TAG_LOGIN, url);
                    requestWithSomeHttpHeaders(url);
                }
            }
        });
    }

    public void requestWithSomeHttpHeaders(String url) {
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        pDialog.setMessage("Đang tải...");
        pDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Display the first 500 characters of the response string.
                        Log.e(Constant.TAG_LOGIN, "Get JSON respone: " + response.toString());
                        pDialog.dismiss();

                        try {
                            boolean success = false;
                            JSONObject jsonObj = new JSONObject(response);
                            success = jsonObj.getBoolean("success");
                            if(success){

                                JSONObject customerObj = jsonObj.getJSONObject("data");
                                int Id = customerObj.getInt("Id");
                                String CustomerGuid = customerObj.getString("CustomerGuid");
                                String Username = customerObj.getString("Username");
                                String Email = customerObj.getString("Email");
                                int PasswordFormatId = customerObj.getInt("PasswordFormatId");
                                String PasswordSalt = customerObj.getString("PasswordSalt");
                                String Password = customerObj.getString("Password");
                                int HomeId = customerObj.getInt("HomeId");
                                int TinhId = customerObj.getInt("TinhId");

                                Customer customer = new Customer(Id, CustomerGuid, Username, Email, PasswordFormatId, PasswordSalt, Password, HomeId, TinhId);



                                Toast.makeText(LoginActivity.this, Username + " đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                //Chuyen Activity
                                Intent t = new Intent(LoginActivity.this, HomeActivity.class);
                                t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                t.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                t.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                t.putExtra("customerObject", customer);

                                startActivity(t);
                            } else
                            {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                builder1.setMessage("Tài khoản hoặc mật khẩu không đúng");
                                builder1.setTitle("Lỗi đăng nhập");
                                builder1.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
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
                        pDialog.hide();

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                        builder1.setMessage("Lỗi khi đăng nhập. Vui Lòng Thử lại");
                        builder1.setTitle("Lỗi mạng");
                        builder1.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                }
        );
        queue.add(postRequest);
    }

    //Ham luu thong tin dang nhap
    public void savingPreferences(){
        SharedPreferences pre = getSharedPreferences(prefname, MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        boolean bchk = chkRemember.isChecked();
        if(!bchk){
            editor.clear();
        }else {
            editor.putString("username", etID.getText().toString());
            editor.putString("password", etPass.getText().toString());
            editor.putBoolean("checked", bchk);
        }

        editor.commit();
    }

    //Ham doc lai thong tin dang nhap da luu truoc do
    public void restoringPreferences()
    {
        SharedPreferences pre=getSharedPreferences
                (prefname, MODE_PRIVATE);
        //lấy giá trị checked ra, nếu không thấy thì giá trị mặc định là false
        boolean bchk=pre.getBoolean("checked", false);
        if(bchk)
        {
            //lấy user, pwd, nếu không thấy giá trị mặc định là rỗng
            String user=pre.getString("username", "");
            String pwd=pre.getString("password", "");
            etID.setText(user);
            etPass.setText(pwd);
        }
        chkRemember.setChecked(bchk);
    }
}
