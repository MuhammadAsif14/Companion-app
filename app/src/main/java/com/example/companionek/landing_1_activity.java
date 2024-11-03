//package com.example.companionek;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//public class landing_1_activity extends AppCompatActivity {
//
//	private TextView how_s_you_feeling_today;
//	private ImageView l1_closevector, currentimage, previousimage, nextimage;
//	private TextView emotion;
//	private View l1_space;
//	private TextView l1_swipe_right_for_more;
//	private ImageView next_vector;
//	private ImageView cancel_vector;
//
//	String[] emojiString = {"happy", "sad", "angry", "neutral", "surprised", "tired", "fearful"};
//	int[] rsids = {R.drawable.happy_emoji, R.drawable.sad_emoji, R.drawable.angry_emoji, R.drawable.neutral_emoji, R.drawable.surprised_emoji, R.drawable.tired_emoji, R.drawable.fearful_emoji};
//	private int currentImageIndex = 0; // Start with the first image
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.landing_1);
//
//		cancel_vector=(ImageView) findViewById(R.id.cancel_vector);
//		emotion = (TextView) findViewById(R.id.emotion);
//		l1_space = (View) findViewById(R.id.l1_space);
//
//		how_s_you_feeling_today = (TextView) findViewById(R.id.how_s_you_feeling_today);
//		l1_closevector = (ImageView) findViewById(R.id.cancel_vector);
//
//		currentimage = (ImageView) findViewById(R.id.current_emoji);
//		currentimage.setImageResource(rsids[currentImageIndex]); // Set initial image
//		emotion.setText(emojiString[currentImageIndex]);
//
//		previousimage = (ImageView) findViewById(R.id.previousImage);
//		previousimage.setImageResource(rsids[(currentImageIndex + rsids.length - 1) % rsids.length]); // Set previous image (last element)
//
//
//
//		nextimage = (ImageView) findViewById(R.id.nextImage);
//		nextimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]); // Set next image
//
//		l1_swipe_right_for_more = (TextView) findViewById(R.id.l1_swipe_right_for_more);
//		next_vector = (ImageView) findViewById(R.id.next_vector);
//
//		// Implement swipe detection using OnSwipeTouchListener
//		ConstraintLayout landingLayout = findViewById(R.id.landing_1);
//		landingLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
//			@Override
//			public void onSwipeRight() {
////				Toast.makeText(landing_1_activity.this, "Swiped to previous emoji", Toast.LENGTH_SHORT).show();
//
//				// Swipe left - move to previous image
//				currentImageIndex--;
//				if (currentImageIndex < 0) {
//					currentImageIndex = rsids.length - 1; // Wrap around to the last image
//				}
//
//				// Update ImageViews based on the current index and modulo (ensure order)
//				previousimage.setImageResource(rsids[(currentImageIndex + rsids.length - 1) % rsids.length]);
//				currentimage.setImageResource(rsids[currentImageIndex % rsids.length]);
//				emotion.setText(emojiString[currentImageIndex % rsids.length]);
//				nextimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
//			}
//
//			@Override
//			public void onSwipeLeft() {
////				Toast.makeText(landing_1_activity.this, "Swiped to next emoji", Toast.LENGTH_SHORT).show();
//
//				// Swipe right - move to next image
//				if (currentImageIndex >= rsids.length) {
//					currentImageIndex = 0; // Wrap around to the first image
//				}
//
//				// Update ImageViews based on the current index and modulo (ensure order)
//				previousimage.setImageResource(rsids[currentImageIndex % rsids.length]);
//				currentimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
//				emotion.setText(emojiString[(currentImageIndex + 1) % rsids.length]);
//				nextimage.setImageResource(rsids[(currentImageIndex + 2) % rsids.length]);
//				currentImageIndex++;
//
//			}
//		});
//
//
//		nextimage.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				if (currentImageIndex >= rsids.length) {
//					currentImageIndex = 0; // Wrap around to the first image
//				}
//
//				// Update ImageViews based on the current index and modulo (ensure order)
//				previousimage.setImageResource(rsids[currentImageIndex % rsids.length]);
//				currentimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
//				emotion.setText(emojiString[(currentImageIndex + 1) % rsids.length]);
//				nextimage.setImageResource(rsids[(currentImageIndex + 2) % rsids.length]);
//				currentImageIndex++;
//			}
//		});
//		currentimage.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(emojiString[currentImageIndex]!="happy"){
//					showChatAlert(emojiString[currentImageIndex]);
//
//				}
//
//			}
//		});
//		previousimage.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				currentImageIndex--;
//				if (currentImageIndex < 0) {
//					currentImageIndex = rsids.length - 1; // Wrap around to the last image
//				}
//
//				// Update ImageViews based on the current index and modulo (ensure order)
//				previousimage.setImageResource(rsids[(currentImageIndex + rsids.length - 1) % rsids.length]);
//				currentimage.setImageResource(rsids[currentImageIndex % rsids.length]);
//				emotion.setText(emojiString[currentImageIndex % rsids.length]);
//				nextimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
//			}
//		});
//		next_vector.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				if (currentImageIndex >= rsids.length) {
//					currentImageIndex = 0; // Wrap around to the first image
//				}
//
//				// Update ImageViews based on the current index and modulo (ensure order)
//				previousimage.setImageResource(rsids[currentImageIndex % rsids.length]);
//				currentimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
//				emotion.setText(emojiString[(currentImageIndex + 1) % rsids.length]);
//				nextimage.setImageResource(rsids[(currentImageIndex + 2) % rsids.length]);
//				currentImageIndex++;
//			}
//		});
//
//		cancel_vector.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//
//				Intent intent = new Intent(landing_1_activity.this, NavBarActivity.class);
//				startActivity(intent);
//
//			}
//		});
//
//
//	}
//	private void showChatAlert(String emotion) {
//		// Inflate the custom layout for the dialog
//		View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
//		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
//		dialogBuilder.setView(dialogView);
//
//		AlertDialog alertDialog = dialogBuilder.create();
//		// Set the background for rounded corners
//		if (alertDialog.getWindow() != null) {
//			alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//		}
//
//		// Define title and message based on emotion
//		String title;
//		String message;
//		switch (emotion) {
//			case "sad":
//				title = "We're Here for You 💙";
//				message = "It seems like things might be a bit heavy right now. Remember, you’re not alone. Our caring chatbot is here to listen and help you feel a bit lighter.";
//				break;
//			case "fearful":
//				title = "Feeling Anxious? 🤗";
//				message = "It’s okay to feel nervous sometimes. If you'd like, our chatbot can help you feel supported and offer some calming words.";
//				break;
//			case "tired":
//				title = "Let’s Talk It Out 🤝";
//				message = "Sometimes things just don’t sit right, and that’s okay. Talking with our chatbot might help you feel better.";
//				break;
//			case "angry":
//				title = "Here to Listen 🔥";
//				message = "It sounds like something’s got you heated. If you need a safe space to vent, our chatbot is here to lend an ear and help cool things down.";
//				break;
//			case "surprised":
//				title = "Need a Moment? 😲";
//				message = "Life can throw unexpected things our way. If you'd like to chat and process it, our chatbot is here for you.";
//				break;
//			default:
//				title = "We're Here for You 💙";
//				message = "If you’d like, our chatbot is here to listen and help you feel a bit lighter.";
//				break;
//		}
//
//		// Set title and message based on emotion
//		TextView titleView = dialogView.findViewById(R.id.dialog_title);
//		TextView messageView = dialogView.findViewById(R.id.dialog_message);
//		titleView.setText(title);
//		messageView.setText(message);
//
//		// Positive button action
//		Button positiveButton = dialogView.findViewById(R.id.positive_button);
//		positiveButton.setOnClickListener(v -> {
//			// Open the chat screen
//			Intent intent = new Intent(this, chat_screen.class);
//			intent.putExtra("Emotion", emotion);
//			startActivity(intent);
//			alertDialog.dismiss();
//		});
//
//		// Negative button action
//		Button negativeButton = dialogView.findViewById(R.id.negative_button);
//		negativeButton.setOnClickListener(v -> alertDialog.dismiss());
//
//		alertDialog.show();
//	}
//
//}

package com.example.companionek;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
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

	String[] emojiString = {"Happy", "Sad", "Angry", "Neutral", "Surprised", "Disgust", "Fearful"};
	int[] rsids = {R.drawable.happy_emoji, R.drawable.sad_emoji, R.drawable.angry_emoji, R.drawable.neutral_emoji, R.drawable.surprised_emoji, R.drawable.tired_emoji, R.drawable.fearful_emoji};
	private int currentImageIndex = 0; // Start with the first image

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.landing_1);

		cancel_vector = findViewById(R.id.cancel_vector);
		emotion = findViewById(R.id.emotion);
		l1_space = findViewById(R.id.l1_space);
		how_s_you_feeling_today = findViewById(R.id.how_s_you_feeling_today);
		l1_closevector = findViewById(R.id.cancel_vector);
		currentimage = findViewById(R.id.current_emoji);
		previousimage = findViewById(R.id.previousImage);
		nextimage = findViewById(R.id.nextImage);
		l1_swipe_right_for_more = findViewById(R.id.l1_swipe_right_for_more);
		next_vector = findViewById(R.id.next_vector);

		// Set initial images
		updateEmotionImages();

		// Implement swipe detection using OnSwipeTouchListener
		ConstraintLayout landingLayout = findViewById(R.id.landing_1);
		landingLayout.setOnTouchListener(new OnSwipeTouchListener(this) {
			@Override
			public void onSwipeRight() {
				// Move to previous image
				currentImageIndex = (currentImageIndex + rsids.length - 1) % rsids.length;
				updateEmotionImages();
			}

			@Override
			public void onSwipeLeft() {
				// Move to next image
				currentImageIndex = (currentImageIndex + 1) % rsids.length;
				updateEmotionImages();
			}
		});

		nextimage.setOnClickListener(v -> {
			currentImageIndex = (currentImageIndex + 1) % rsids.length;
			updateEmotionImages();
		});

		previousimage.setOnClickListener(v -> {
			currentImageIndex = (currentImageIndex + rsids.length - 1) % rsids.length;
			updateEmotionImages();
		});

		currentimage.setOnClickListener(v -> {
			if (!emojiString[currentImageIndex].equals("happy")) {
				showChatAlert(emojiString[currentImageIndex]);
			}
		});

		cancel_vector.setOnClickListener(v -> {
			Intent intent = new Intent(landing_1_activity.this, NavBarActivity.class);
			startActivity(intent);
		});
	}

	private void updateEmotionImages() {
		previousimage.setImageResource(rsids[(currentImageIndex + rsids.length - 1) % rsids.length]);
		currentimage.setImageResource(rsids[currentImageIndex]);
		emotion.setText(emojiString[currentImageIndex]);
		nextimage.setImageResource(rsids[(currentImageIndex + 1) % rsids.length]);
	}

	private void showChatAlert(String emotion) {
		// Inflate the custom layout for the dialog
		View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
		dialogBuilder.setView(dialogView);

		AlertDialog alertDialog = dialogBuilder.create();
		// Set the background for rounded corners
		if (alertDialog.getWindow() != null) {
			alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		}

		// Define title and message based on emotion
		String title;
		String message;
		switch (emotion) {
			case "Sad":
				title = "We're Here for You 💙";
				message = "It seems like things might be a bit heavy right now. Remember, you’re not alone. Our caring chatbot is here to listen and help you feel a bit lighter.";
				break;
			case "Fearful":
				title = "Feeling Anxious? 🤗";
				message = "It’s okay to feel nervous sometimes. If you'd like, our chatbot can help you feel supported and offer some calming words.";
				break;
			case "Disgust":
				title = "Let’s Talk It Out 🤝";
				message = "Sometimes things just don’t sit right, and that’s okay. Talking with our chatbot might help you feel better.";
				break;
			case "Angry":
				title = "Here to Listen 🔥";
				message = "It sounds like something’s got you heated. If you need a safe space to vent, our chatbot is here to lend an ear and help cool things down.";
				break;
			case "Surprised":
				title = "Need a Moment? 😲";
				message = "Life can throw unexpected things our way. If you'd like to chat and process it, our chatbot is here for you.";
				break;
			default:
				title = "We're Here for You 💙";
				message = "If you’d like, our chatbot is here to listen and help you feel a bit lighter.";
				break;
		}

		// Set title and message based on emotion
		TextView titleView = dialogView.findViewById(R.id.dialog_title);
		TextView messageView = dialogView.findViewById(R.id.dialog_message);
		titleView.setText(title);
		messageView.setText(message);

		// Positive button action
		Button positiveButton = dialogView.findViewById(R.id.positive_button);
		positiveButton.setOnClickListener(v -> {
			// Open the chat screen
			Intent intent = new Intent(this, chat_screen.class);
			intent.putExtra("Emotion", emotion);
			startActivity(intent);
			alertDialog.dismiss();
		});

		// Negative button action
		Button negativeButton = dialogView.findViewById(R.id.negative_button);
		negativeButton.setOnClickListener(v -> alertDialog.dismiss());

		alertDialog.show();
	}
}
