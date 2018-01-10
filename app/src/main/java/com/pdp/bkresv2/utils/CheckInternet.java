package com.pdp.bkresv2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {
	
	public static boolean isNetworkAvailable  (Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info!=null);
	}
}
