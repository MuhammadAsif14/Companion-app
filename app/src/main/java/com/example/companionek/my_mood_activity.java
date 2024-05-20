package com.example.companionek;

	


import android.app.Activity;
import android.os.Bundle;


import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class my_mood_activity extends AppCompatActivity {

	private ConstraintLayout container;
	private View my_mood_bg_frame;
	private ImageView back_vector;
	private Button history;
	private Button charts;
	private ImageView emotion_emoji;
	private TextView emotion_detail;
	private TextView date;
	private ImageView edit_vector;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_mood);


		my_mood_bg_frame= (View) findViewById(R.id.my_mood_bg_frame);
		back_vector = (ImageView) findViewById(R.id.back_vector);
		history = (Button) findViewById(R.id.history);
		charts = (Button) findViewById(R.id.charts);

//		emotion_emoji = (ImageView) findViewById(R.id.emotion_emoji);
//		emotion_detail = (TextView) findViewById(R.id.emotion_detail);
//		date = (TextView) findViewById(R.id.date);
//		edit_vector=(ImageView) findViewById(R.id.edit_vector);

		history.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Replace fragment code here
				replaceFragment(new HistoryFragment());
			}
		});
		charts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Replace fragment code here
				replaceFragment(new ChartsFragment());
			}
		});
		back_vector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		history.performClick();

	}

	private void replaceFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.fragment_container, fragment); // Replace with your fragment container ID
		transaction.commit();

	}
}
	
	