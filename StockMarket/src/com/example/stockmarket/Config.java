package com.example.stockmarket;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

public class Config {

	public static void putSharedPreferences(Context context, String preference,
			String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void putSharedPreferences(Context context, String preference,
			String key, int value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static String getSharedPreferences(Context context,
			String preference, String key, String defValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, context.MODE_PRIVATE);
		String value = sharedPreferences.getString(key, defValue);
		return value;
	}

	public static int getSharedPreferences(Context context, String preference,
			String key, int defValue) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				preference, context.MODE_PRIVATE);
		int value = sharedPreferences.getInt(key, defValue);
		return value;
	}

	public static void alertBox(String s, Context c) {
		AlertDialog.Builder altDialog;
		altDialog = new AlertDialog.Builder(c);
		altDialog.setMessage(s); // here add your message
		altDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
		altDialog.show();
	}

	public static void toastShow(String s, Context c) {
		Toast toast = Toast.makeText(c, s, 15);
		TextView v = (TextView) toast.getView().findViewById(
				android.R.id.message);
		v.setTextSize(20);
		toast.show();
	}

}
