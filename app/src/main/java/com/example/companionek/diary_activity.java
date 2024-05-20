package com.example.companionek;

import android.app.Activity;
import android.os.Bundle;


import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;

public class diary_activity extends Activity {

	
	private View _bg__diary;
	private View rectangle_6;
	private ScrollView diary;
	private View _bg__x_ek1;
	private ImageView vector;
	private View _bg__frame_1_ek1;
	private TextView wednesday__march_27___2024;
	private TextView feeling_stuck;
	private TextView what_s_making_you_feel_stuck__;
	private TextView feeling_stuck_is_a_common_experience_we_all_encounter_at_times__it_s_that_frustrating_sensation_when_we_find_ourselves_unable_to_make_progress_or_find_a_solution_to_a_problem__it_can_manifest_in_various_aspects_of_life__such_as_work__relationships__or_personal_goals__being_stuck_often_arises_from_a_lack_of_clarity__motivation_;
	private View _bg__frame_3_ek1;
	private View _bg__frame_2_ek1;
	private View _bg__microphone_ek1;
	private ImageView vector_ek1;
	private View _bg__spotifylogo_ek1;
	private ImageView vector_ek2;
	private View _bg__camera_ek1;
	private ImageView vector_ek3;
	private View _bg__trash_ek1;
	private ImageView vector_ek4;
	private View _bg__frame_28_ek1;
	private View _bg__chevron_left_ek1;
	private ImageView back_vector;
	private View _bg__chevron_left_ek3;
	private ImageView vector_ek6;
	private View _bg__addnew_ek1;
	private View ellipse_6;
	private View _bg__plus_ek1;
	private ImageView vector_ek7;
	private ImageView vector_ek8;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary);


		wednesday__march_27___2024 = (TextView) findViewById(R.id.wednesday__march_27___2024);
		back_vector = (ImageView) findViewById(R.id.back_vector);
	
		
		//custom code goes here
		back_vector.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	
	}
}
	
	