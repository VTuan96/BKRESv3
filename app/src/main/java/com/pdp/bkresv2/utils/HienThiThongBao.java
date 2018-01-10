package com.pdp.bkresv2.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by PhucBKHN on 26/08/2016.
 */
public class HienThiThongBao {
    public static void HienThiAlertDialogLoiMang(Context ctx){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
        builder1.setMessage("Lỗi xảy ra khi kết nối đến Server. Thử lại");
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

    public static void HienThiThongBaoKhongCoDuLieu(Context ctx){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
        builder1.setMessage("Không có dữ liệu từ thiết bị gửi lên!");
        builder1.setTitle("Chú ý");
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

    //Hien thi thong bao thong tin ung dung
    public static void HienThiThongTinNhomPhanMem(Context ctx){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);

        builder1.setTitle("Thông tin");
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
