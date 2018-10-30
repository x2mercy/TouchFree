package com.example.lockphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreManager {

	private static final String PRE_NAME = "save_info";
	
	SharedPreferences preferences;
	Editor editor;
	
	public PreManager(Context context) {
		preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	
	public void saveTimes(int times) {
		editor.putInt("times", times);
		editor.commit();
	}
	
	public void saveDelay(int delay) {
		editor.putInt("delay", delay);
		editor.commit();
	}
	
	public int getTimes() {
		return preferences.getInt("times", 3);
	}
	
	public int getDelay() {
		return preferences.getInt("delay", 20);
	}
	
}
