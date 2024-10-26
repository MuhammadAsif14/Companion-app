package com.example.companionek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class landing_1_activity extends AppCompatActivity {

	private TextView how_s_you_feeling_today;
	private ImageView l1_closevector, currentimage, previousimage, nextimage;
	private TextView emotion;
	private View l1_space;
	private TextView l1_swipe_right_for_more;
	private ImageView next_vector;
	private ImageView cancel_vector;

	String[] emojiString = {"happy", "sad", "angry", "neutral", "surprised", "tired", "fearful"};
	int[] rsids = {R.drawable.happy_emoji, R.drawable.sad_emoji, R.drawable.angry_emoji, R.drawable.neutral_emoji, R.drawable.surprised_emoji, R.drawable.tired_emoji, R.drawable.fearful_emoji};
	private int currentImageIndex = 0; // Start with the first image

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landing_1);

		cancel_vector=(ImageView) findViewById(R.id.cancel_vector);
		emotion = (TextView) findViewById(R.id.emotion);
		l1_space = (View) findViewById(R.id.l1_space);

		how_s_you_feeling_today = (TextView) findViewById(R.id.how_s_you_feeling_today);
		l1_closevector = (ImageView) findViewById(R.id.cancel_vector);

		currentimage = (ImageView) findViewById(R.id.current_emoji);
		currentimage.setImageResource(rsids[currentImageIndex]); // Set initial image
		emotion.setText(emojiString[currentImageIndex]);

		previousimage = (ImageView) findViewById(R.id.previousImage);
		previousimage.setImageResource(rsids[(currentImageIndex + rsids.length - 1) % rsids.length]); // Set previous image (last element)



		nextimage = (ImageView) findViewById(R.id.nextImage);
		nextimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]); // Set next image

		l1_swipe_right_for_more = (TextView) findViewById(R.id.l1_swipe_right_for_more);
		next_vector = (ImageView) findViewById(R.id.next_vector);

		// Implement swipe detection using OnSwipeTouchListener
		ConstraintLayout landingLayout = findViewById(R.id.landing_1);
		landingLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
			@Override
			public void onSwipeRight() {
//				Toast.makeText(landing_1_activity.this, "Swiped to previous emoji", Toast.LENGTH_SHORT).show();

				// Swipe left - move to previous image
				currentImageIndex--;
				if (currentImageIndex < 0) {
					currentImageIndex = rsids.length - 1; // Wrap around to the last image
				}

				// Update ImageViews based on the current index and modulo (ensure order)
				previousimage.setImageResource(rsids[(currentImageIndex + rsids.length - 1) % rsids.length]);
				currentimage.setImageResource(rsids[currentImageIndex % rsids.length]);
				emotion.setText(emojiString[currentImageIndex % rsids.length]);
				nextimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
			}

			@Override
			public void onSwipeLeft() {
//				Toast.makeText(landing_1_activity.this, "Swiped to next emoji", Toast.LENGTH_SHORT).show();

				// Swipe right - move to next image
				if (currentImageIndex >= rsids.length) {
					currentImageIndex = 0; // Wrap around to the first image
				}

				// Update ImageViews based on the current index and modulo (ensure order)
				previousimage.setImageResource(rsids[currentImageIndex % rsids.length]);
				currentimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
				emotion.setText(emojiString[(currentImageIndex + 1) % rsids.length]);
				nextimage.setImageResource(rsids[(currentImageIndex + 2) % rsids.length]);
				currentImageIndex++;

			}
		});

		nextimage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (currentImageIndex >= rsids.length) {
					currentImageIndex = 0; // Wrap around to the first image
				}

				// Update ImageViews based on the current index and modulo (ensure order)
				previousimage.setImageResource(rsids[currentImageIndex % rsids.length]);
				currentimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
				emotion.setText(emojiString[(currentImageIndex + 1) % rsids.length]);
				nextimage.setImageResource(rsids[(currentImageIndex + 2) % rsids.length]);
				currentImageIndex++;
			}
		});
		previousimage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentImageIndex--;
				if (currentImageIndex < 0) {
					currentImageIndex = rsids.length - 1; // Wrap around to the last image
				}

				// Update ImageViews based on the current index and modulo (ensure order)
				previousimage.setImageResource(rsids[(currentImageIndex + rsids.length - 1) % rsids.length]);
				currentimage.setImageResource(rsids[currentImageIndex % rsids.length]);
				emotion.setText(emojiString[currentImageIndex % rsids.length]);
				nextimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
			}
		});
		next_vector.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (currentImageIndex >= rsids.length) {
					currentImageIndex = 0; // Wrap around to the first image
				}

				// Update ImageViews based on the current index and modulo (ensure order)
				previousimage.setImageResource(rsids[currentImageIndex % rsids.length]);
				currentimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
				emotion.setText(emojiString[(currentImageIndex + 1) % rsids.length]);
				nextimage.setImageResource(rsids[(currentImageIndex + 2) % rsids.length]);
				currentImageIndex++;
			}
		});

		cancel_vector.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(landing_1_activity.this, NavBarActivity.class);
				startActivity(intent);

			}
		});


	}
}