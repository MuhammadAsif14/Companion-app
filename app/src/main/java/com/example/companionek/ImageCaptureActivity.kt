package com.example.companionek

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.companionek.ml.Model
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
class ImageCaptureActivity : AppCompatActivity() {
    private lateinit var camera: Button
    private lateinit var gallery: Button
    private lateinit var imageView: ImageView
    private lateinit var result: TextView
    private val imageSize = 224

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_capture)

        camera = findViewById(R.id.button_camera)
        gallery = findViewById(R.id.button_gallery)
        result = findViewById(R.id.result)
        imageView = findViewById(R.id.imageView)
        camera.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }
        gallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 1)
        }
    }
    private fun classifyImage(image: Bitmap) {
        try {
            val model = Model.newInstance(applicationContext)
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
            byteBuffer.order(ByteOrder.nativeOrder())
            val intValues = IntArray(imageSize * imageSize)
            image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
            var pixel = 0
            for (i in 0 until imageSize) {
                for (j in 0 until imageSize) {
                    val value = intValues[pixel++]
                    byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255))
                    byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255))
                    byteBuffer.putFloat((value and 0xFF) * (1f / 255))
                }
            }
            inputFeature0.loadBuffer(byteBuffer)
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray
            val maxPos = confidences.indices.maxByOrNull { confidences[it] } ?: 0
            val classes = arrayOf("Angry", "Disgust", "Happy", "Sad", "Neutral", "Fearful", "Surprised")

            result.text = classes[maxPos]
            // Show alert if emotion is detected as "Sad"
            if (classes[maxPos] != "Neutral"&&classes[maxPos] != "Happy") {
                showChatAlert(classes[maxPos])
            }
            model.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
private fun showChatAlert(emotion: String) {
    val dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
    val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
    dialogBuilder.setView(dialogView)

    val alertDialog = dialogBuilder.create()
    alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

    // Define message content based on emotion
    val (title, message) = when (emotion) {
        "Sad" -> "We're Here for You 💙" to "It seems like things might be a bit heavy right now. Remember, you’re not alone. Our caring chatbot is here to listen and help you feel a bit lighter."
        "Fearful" -> "Feeling Anxious? 🤗" to "It’s okay to feel nervous sometimes. If you'd like, our chatbot can help you feel supported and offer some calming words."
        "Disgust" -> "Let’s Talk It Out 🤝" to "Sometimes things just don’t sit right, and that’s okay. Talking with our chatbot might help you feel better."
        "Angry" -> "Here to Listen 🔥" to "It sounds like something’s got you heated. If you need a safe space to vent, our chatbot is here to lend an ear and help cool things down."
        "Surprised" -> "Need a Moment? 😲" to "Life can throw unexpected things our way. If you'd like to chat and process it, our chatbot is here for you."
        else -> "We're Here for You 💙" to "If you’d like, our chatbot is here to listen and help you feel a bit lighter."
    }

    // Set title and message based on the emotion
    dialogView.findViewById<TextView>(R.id.dialog_title).text = title
    dialogView.findViewById<TextView>(R.id.dialog_message).text = message

    // Positive button action
    dialogView.findViewById<Button>(R.id.positive_button).setOnClickListener {
        // Open the chat screen
        val intent = Intent(this, chat_screen::class.java)
        intent.putExtra("Emotion", emotion)
        startActivity(intent)
        alertDialog.dismiss()
    }

    // Negative button action
    dialogView.findViewById<Button>(R.id.negative_button).setOnClickListener {
        alertDialog.dismiss()
    }

    alertDialog.show()
}
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val image = when (requestCode) {
                3 -> (data?.extras?.get("data") as? Bitmap)?.let {
                    val dimension = minOf(it.width, it.height)
                    ThumbnailUtils.extractThumbnail(it, dimension, dimension)
                }
                1 -> data?.data?.let { uri ->
                    MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                }
                else -> null
            }
            image?.let {
                // Detect face and crop
                detectAndCropFace(it)
          }
        }
    }
    private fun detectAndCropFace(bitmap: Bitmap) {
        // Configure high-accuracy mode for ML Kit face detection
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()
        val detector = FaceDetection.getClient(options)
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        // Detect faces in the image
        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]  // Use the first detected face
                    val croppedFaceBitmap = cropFaceFromBitmap(bitmap, face.boundingBox)

                    // Resize and classify if face is detected
                    croppedFaceBitmap?.let {
                        val scaledImage = Bitmap.createScaledBitmap(it, imageSize, imageSize, false)
                        imageView.setImageBitmap(scaledImage)
                        classifyImage(scaledImage)
                    }
                } else {
                    Log.i("FaceDetection", "No faces detected")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FaceDetection", "Face detection failed", e)
            }
    }
    private fun cropFaceFromBitmap(bitmap: Bitmap, boundingBox: Rect): Bitmap? {
        return try {
            Bitmap.createBitmap(
                bitmap,
                boundingBox.left.coerceAtLeast(0),
                boundingBox.top.coerceAtLeast(0),
                boundingBox.width().coerceAtMost(bitmap.width - boundingBox.left),
                boundingBox.height().coerceAtMost(bitmap.height - boundingBox.top)
            )
        } catch (e: Exception) {
            Log.e("FaceDetection", "Error cropping face", e)
            null
        }
    }
}

