package com.example.lockphone;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends DeviceAdminReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		//System.out.println("onreceiver");
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		//System.out.println("Activate Permission");
		super.onEnabled(context, intent);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		//System.out.println("Deactivate Permission");
		super.onDisabled(context, intent);
	}
}
