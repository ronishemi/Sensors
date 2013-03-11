package com.example.sensors;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Chose extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Choose button
		setContentView(R.layout.chose);
		// Find all the sensors
		Bundle extras = getIntent().getExtras();
		String[] values = extras.getStringArray("sensors");
		// List view of sensors
		final ListView listView = (ListView) findViewById(R.id.mylist);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		// Assign adapter to ListView
		listView.setAdapter(adapter);
		// On click method
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				// identification of item on list
				Object o = listView.getItemAtPosition(position);
				// Run main activity on choose
				Intent intent = new Intent(Chose.this, Sensors.class);
				intent.putExtra("choice", o.toString());
				intent.putExtra("position", position);
				// Check the selection
				if (o.toString() == null)
					setResult(RESULT_CANCELED, intent);
				else
					setResult(RESULT_OK, intent);
				finish();
			}
		});

	}

}
