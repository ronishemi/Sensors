package com.example.sensors;

import java.util.Iterator;
import java.util.List;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Sensors extends Activity {
	// define variable
	static final int NEW_ACTIVITY = 100;
	// text
	private EditText editText;
	private TextView viewText, Xview, Yview, Zview;
	// buttons
	private Button scanBtn;
	private Button showBtn;
	private Button choseBtn;
	private IntentIntegrator integrator;
	// other
	private String result;
	private SensorManager sensorManager;
	private Integer position;
	private String choice;
	private Sensor defSensor;
	private List<Sensor> sensors;
	private SensorEventListener listener;
	private SharedPreferences sharedPref;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// initialization, onCreate()
		sharedPref = getSharedPreferences("hasRunBefore", MODE_PRIVATE);
		position = sharedPref.getInt("position", 0);
		integrator = new IntentIntegrator(this);
		// get sensor manager
		sensorManager = (SensorManager) getSystemService(this.SENSOR_SERVICE);
		// List sensors
		sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
		Sensor pressSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_PRESSURE);
		if (pressSensor == null)
			Toast.makeText(getApplicationContext(), "barometer sensor missing",
					Toast.LENGTH_LONG).show();
		final String[] values = new String[sensors.size() + 1];
		int i = 0;
		Iterator<Sensor> it = sensors.iterator();
		while (it.hasNext()) {
			Sensor sen = (Sensor) it.next();
			values[i++] = sen.getName();
		}
		values[i++] = "QR";
		// variables configuration
		viewText = (TextView) findViewById(R.id.editText1);
		editText = (EditText) findViewById(R.id.editText2);
		choseBtn = (Button) findViewById(R.id.choseBtn);
		scanBtn = (Button) findViewById(R.id.button1);
		showBtn = (Button) findViewById(R.id.button2);
		Xview = (TextView) findViewById(R.id.x);
		Yview = (TextView) findViewById(R.id.y);
		Zview = (TextView) findViewById(R.id.z);
		// on click method
		choseBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				scanBtn.setVisibility(View.INVISIBLE);
				showBtn.setVisibility(View.INVISIBLE);
				Xview.setVisibility(View.INVISIBLE);
				Yview.setVisibility(View.INVISIBLE);
				Zview.setVisibility(View.INVISIBLE);
				viewText.setVisibility(View.INVISIBLE);
				editText.setVisibility(View.INVISIBLE);
				Intent intent = new Intent(Sensors.this, Chose.class);
				intent.putExtra("sensors", values);
				startActivityForResult(intent, Sensors.NEW_ACTIVITY);

			}
		});

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// Unregister on onPause()
		sharedPref.edit().putInt("position", position);
		if (position < 8)
			sensorManager.unregisterListener(listener);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Register on onResume()
		position = sharedPref.getInt("position", 0);
		if (position < 8) {
			getSensor();
			// Get default sensor. return null if none
			defSensor = sensorManager.getDefaultSensor(sensors.get(position)
					.getType());
			if(defSensor != null){
			sensorManager.registerListener(listener, defSensor,
					sensorManager.SENSOR_DELAY_NORMAL);
			viewText.setText(defSensor.getName() + "\n\n");
			}
		} else
			setScane();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// handle result
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);

		if (scanResult != null) {
			result = scanResult.getContents();

			viewText.setText(result);
			// do something with result
		}
		if (requestCode == NEW_ACTIVITY && resultCode == RESULT_OK) {

			choice = intent.getStringExtra("choice");
			position = intent.getIntExtra("position", 0);
			sharedPref.edit().putInt("position", position).commit();
			if (choice != null)
				viewText.setText(choice + "\n\n");
			if ((choice != null) && (choice.matches("QR"))) {
				setScane();
			} else
				getSensor();

		}
	}

	// getting sensor values
	public SensorEventListener getSensor() {
		viewText.setVisibility(View.VISIBLE);
		Xview.setVisibility(View.VISIBLE);
		Xview.setText("0");
		Yview.setText("0");
		Zview.setText("0");
		listener = new SensorEventListener() {
			@Override
			public void onSensorChanged(SensorEvent event) {
				// ///
				scanBtn.setVisibility(View.INVISIBLE);
				showBtn.setVisibility(View.INVISIBLE);
				editText.setVisibility(View.INVISIBLE);
				// /

				float[] valus = event.values;

				Xview.setVisibility(View.VISIBLE);
				Xview.setText(Float.toString(valus[0]));
				if (valus[1] != 0) {
					Yview.setVisibility(View.VISIBLE);
					Yview.setText(Float.toString(valus[1]));
				}
				if (valus[2] != 0) {
					Zview.setVisibility(View.VISIBLE);
					Zview.setText(Float.toString(valus[2]));
				}

			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// SensorManager.SENSOR_STATUS_ACCURACY_LOW/MEDIUM/HIGH
			}
		};
		return listener;
	}

	// scan
	public void setScane() {

		Xview.setVisibility(View.INVISIBLE);
		Yview.setVisibility(View.INVISIBLE);
		Zview.setVisibility(View.INVISIBLE);
		scanBtn.setVisibility(View.VISIBLE);
		showBtn.setVisibility(View.VISIBLE);
		editText.setVisibility(View.VISIBLE);
		viewText.setVisibility(View.VISIBLE);
		scanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// activate, onClick()
				integrator.initiateScan();
			}

		});

		showBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Generate QR
				integrator.shareText(editText.getText().toString());
			}

		});
	}
}
