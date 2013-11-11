package com.example.bluetoothdemo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int REQUEST_ENABLE = 100;
	private static final int REQUEST_DISCOVERABLE = 200;
	private BluetoothAdapter mBluetoothAdapter;
	private MyBlueToothReceiver receiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		receiver = new MyBlueToothReceiver();
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null){
			Log.e(getClass().getSimpleName(), "Bluetooth not supported");
		}
	}
	
	private UUID getUUID() {
		return UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	}
	
	public void MyOnClick(View view) {
		switch (view.getId()) {
		case R.id.btn_enable_bt:
			isBluetoothEnabled();
			break;
		case R.id.btn_discoverable_bt:
			makeDeviceDiscoverable();
			break;
		case R.id.btn_disable_bt:
			disableBluetooth();
			break;
		case R.id.btn_paired_device:
			getListOfConnectedDevices();
			break;
		case R.id.btn_search_devices:
			discoverDevices();
			break;
		case R.id.btn_bluetooth_server:
			if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
				new TaskServerSocket().execute();
			}
			break;
		}
	}
	
	class TaskServerSocket extends AsyncTask<Void, Void, Void>{

		BluetoothServerSocket mServerSocket;
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(getClass().getSimpleName(), getUUID());
				while (true) {
					BluetoothSocket mBluetoothSocket = mServerSocket.accept(10000);
					if(mBluetoothSocket != null){
						Log.e("socket", "connection made sucessfully!");
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			finally{
				try {
					if(mServerSocket != null) mServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}
	
	private void disableBluetooth() {
		if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.disable();
			showToast("Bluetooth is disabled");
		}
		else if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
			showToast("Bluetooth is already disabled");
		}
	}
	
	private void makeDeviceDiscoverable() {
		if(mBluetoothAdapter != null && !mBluetoothAdapter.isDiscovering()){
			Intent discoverable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			startActivityForResult(discoverable, REQUEST_DISCOVERABLE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_ENABLE){
			showToast("Bluetooth enabled");
		}
		else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_DISCOVERABLE) {
			showToast("Bluetooth made discoverable");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerDiscoveryBroadcastReceiver();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterDiscovery();
	}
	
	private void isBluetoothEnabled() {
		if(mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()){
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth, REQUEST_ENABLE);
		}
		else{
			showToast("Bluetooth is already enabled");
		}
	}
	
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	private void getListOfConnectedDevices() {
		if(mBluetoothAdapter != null){
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			
			List<String> list = new ArrayList<String>();
			for(BluetoothDevice bt : pairedDevices){
				list.add(bt.getName());
			}
			
			ListView myList = (ListView) findViewById(R.id.myList);
			myList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
		}
	}
	
	private void registerDiscoveryBroadcastReceiver() {
		registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
	    registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
	    registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
	}
	
	private void unregisterDiscovery() {
		unregisterReceiver(receiver);
	}
	
	private void discoverDevices() {
		if(mBluetoothAdapter != null && !mBluetoothAdapter.isDiscovering()){
			mBluetoothAdapter.startDiscovery();
		}
		else{
			showToast("Discovery already running...");
		}
	}
}
