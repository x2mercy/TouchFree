package com.example.lockphone;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class ScreenService extends Service {

	public static int delay = 2000; // the time between two waves in ms
	public static int times = 3;
	private AudioManager audioManager;
	private KeyguardManager keyguardManager;
	private Sensor proximitySensor;

	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		init();
		registSensor();

	}
	
	@Override
	public void onDestroy() {
		unregistSensor();
		super.onDestroy();

	}
	
	DevicePolicyManager policyManager;
	PowerManager powerManager;
	SensorManager sensorManager;
	PreManager preManager;


	/** initialize managers */
	public void init() {
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		preManager = new PreManager(getApplicationContext());
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
	//	screenStateReceiver = setScreenStateReceiver(this);

		proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		delay = preManager.getDelay() * 100;
		times = preManager.getTimes();
	}
	
	/** lock the screen */
	public void lockScreen() {
		policyManager.lockNow();
	}

	/** unlock the screen */
	public void unlockAction()
	{
			Intent intent = new Intent(getApplicationContext(), LockScreenActivity.class);
			intent.addFlags(Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
	}

	/** Judge the screen is on or not */
	public boolean isScreenOn() {
		return powerManager.isScreenOn();
	}
	//public boolean isInteractive() {return powerManager.isInteractive();}
	
	// use for detecting waves
	int count = 0;
	long time = 0;
	
	/** proximity listener to catch waves */
	SensorEventListener listener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {

			if(count == 0 || System.currentTimeMillis()-time > delay){
				count = 0;
				time = System.currentTimeMillis();
			}
			count ++;
			if(count == times*2){
				long t = System.currentTimeMillis() - time;
				if(t < delay && t > 300){
					if(keyguardManager.inKeyguardRestrictedInputMode()|| !isScreenOn()){
						unlockAction();
						Log.v("lock", "unlock phone");
					} else {
						lockScreen();
						Log.v("lock", "lock phone");
					}
				}
				count = 0;
			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	/** register proximity sensor */
	public void registSensor() {
		Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_UI);
	}
	
	/** unregister proximity sensor */
	public void unregistSensor() {
		sensorManager.unregisterListener(listener);

	}
	
}
