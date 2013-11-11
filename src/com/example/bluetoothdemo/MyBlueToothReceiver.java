package com.example.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyBlueToothReceiver extends BroadcastReceiver {

	boolean isDeviceDiscovered = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		if(intent.getAction().equalsIgnoreCase(BluetoothDevice.ACTION_FOUND)){
			isDeviceDiscovered = true;
			BluetoothDevice remoteDevice;
			remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
			showToast(context, "Discovered: " + remoteDevice.getName() + "  RSSI: " + rssi);
			Log.e(getClass().getSimpleName(), "Name: " + remoteDevice.getName());
			Log.e(getClass().getSimpleName(), "Address: " + remoteDevice.getAddress());
			Log.e(getClass().getSimpleName(), "RSSI: " + rssi);
		}
		else if (intent.getAction().equalsIgnoreCase(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
			isDeviceDiscovered = false;
            Log.e(getClass().getSimpleName(), "Discovery Started...");
            showToast(context, "Discovery Started...");
        }
		else if (intent.getAction().equalsIgnoreCase(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
			if(!isDeviceDiscovered){
				showToast(context, "No device found");
			}
			showToast(context, "Discovery Finished...");
			Log.e(getClass().getSimpleName(), "Discovery Finished...");
        }
	}
	
	private void showToast(Context mContext, String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	}
}
