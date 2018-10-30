package com.example.lockphone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements OnClickListener {

	Button button1;
	Button button2;
	Button button3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();

		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);

		if (isServiceLive()) {
			button2.setText(R.string.button22);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		// After lock the screen, close the app interface
		if(!powerManager.isScreenOn()) {
			this.finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			activeManager(); // ask activate lock/unlock permission
			break;
		case R.id.button2:
			if (isServiceLive()) {
				stopService(); //click button to stop/start service
			} else {
				startService();
			}
			break;
		case R.id.button3:
			startSetActivity(); //set the wave actions
			break;
		default:
			break;
		}
	}
	
	/** start lock service */
	public void startSetActivity() {
		startActivity(new Intent(this, SettingActivity.class));
	}

	DevicePolicyManager policyManager;
	ComponentName componentName;
	ActivityManager activityManager;
	PowerManager powerManager;

	/** initialize device administrator  */
	public void init() {
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, LockReceiver.class);
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
	}

	/** activate device administrator for lock/unlock security permission */
	public void activeManager() {
		if (policyManager.isAdminActive(componentName)) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.tip4), Toast.LENGTH_SHORT)
					.show();
		} else {
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					componentName);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
					getResources().getString(R.string.app_name));
			startActivity(intent);
		}
	}

	/** deactivate permission */
	public void unActiveManager() {
		if (policyManager.isAdminActive(componentName)) {
			policyManager.removeActiveAdmin(componentName);
		}
	}

	/** start wave lock/unlock */
	public void startService() {
		if (!policyManager.isAdminActive(componentName)) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.tip1), Toast.LENGTH_SHORT)
					.show();
		} else {
			startService(new Intent(this, ScreenService.class));
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.tip2), Toast.LENGTH_SHORT)
					.show();
			button2.setText(R.string.button22);
		}
	}

	/** Stop wave lock/unlock */
	public void stopService() {
		stopService(new Intent(this, ScreenService.class));
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.tip3), Toast.LENGTH_SHORT)
				.show();
		button2.setText(R.string.button2);
	}


	/** judge whether the server is running or not */
	public boolean isServiceLive() {
		List<RunningServiceInfo> runningService = activityManager
				.getRunningServices(30);
		for (RunningServiceInfo srv : runningService) {
			if ("com.test.lock.ScreenService".equals(srv.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
