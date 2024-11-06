package com.example.companionek

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TestFaceDetection : AppCompatActivity() {

    private val CAMERA_PERMISSION_REQUEST_CODE = 100

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_face_detection)

        val faceDetectButton: Button = findViewById(R.id.button_face_detect)

        faceDetectButton.setOnClickListener {
            // Check if the camera permission is granted
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, start the FaceDetectionActivity
                startFaceDetectionActivity()
            } else {
                // If permission is not granted, request the camera permission
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST_CODE)
            }
        }
        val buttonImage:Button=findViewById(R.id.button_image_emotion_detection)
        buttonImage.setOnClickListener {
               val intent =Intent(this,ImageCaptureActivity::class.java)
                startActivity(intent)
        }

    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, start FaceDetectionActivity
                startFaceDetectionActivity()
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    // Function to start FaceDetectionActivity
    private fun startFaceDetectionActivity() {
        val intent = Intent(this, RealTimeFaceDetectionActivity::class.java)
        startActivity(intent)
    }
}
