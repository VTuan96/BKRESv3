package com.pdp.bkresv2.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class XuLyThoiGian {
	
	public static String layNgayHienTai () {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String date = sdf.format(cal.getTime());
		return date;
	}
	
	public static String layGioPhutHienTai () {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
		String time = sdf.format(cal.getTime());
		return time;
	}

	//Chuyen string Json nhan duoc tu API thanh String
	public static String StringToDatetimeString(String strDateTime){
		String numberOnly= strDateTime.replaceAll("[^0-9]", "");
		long milliSeconds = Long.parseLong(numberOnly);

		// Create a DateFormatter object for displaying date in specified format.
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		// Create a calendar object that will convert the date and time value in milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	//Chuyen string Json nhan duoc tu API thanh kieu Date
	public static Date StringToDatetime(String strDateTime){
		String numberOnly= strDateTime.replaceAll("[^0-9]", "");
		long milliSeconds = Long.parseLong(numberOnly);

		Date date = new Date(milliSeconds);
		return date;
	}

	//Chuyen tu kieu Date thanh String
	public static String DateToString(Date dateTime){

		// Create a DateFormatter object for displaying date in specified format.
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		return formatter.format(dateTime.getTime());
	}

	//Lay thoi gian hien tai he thong (mili giay)
	public static long LayThoiGianHienTai(){
		long time= System.currentTimeMillis();
		return  time;
	}


	//Kiem tra Thiet bi Online - Offline
	public static boolean isOnline(Date date){
		long datePacket = date.getTime();
		long currentTime = XuLyThoiGian.LayThoiGianHienTai();

		final long timeLimit = 5*60*1000;

		if((currentTime - datePacket) >= timeLimit){
			return false;
		} else
			return true;

	}
}
