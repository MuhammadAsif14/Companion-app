package com.example.companionek

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.companionek.ml.Model
import com.example.companionek.utils.CameraUtil
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RealTimeFaceDetectionActivity : AppCompatActivity() {
    private lateinit var surfaceView: SurfaceView
    private lateinit var emotionTextView: TextView
    private val imageSize = 224
    private val classes = arrayOf("Angry", "Disgust", "Happy", "Sad", "Neutral", "Fearful", "Surprised")

    private var model: Model? = null
    private var frameCounter = 0
    private val detectionInterval = 3

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_face_detection)

        surfaceView = findViewById(R.id.surfaceView)
        emotionTextView = findViewById(R.id.emotionTextView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            startCameraPreview()
        }

        try {
            model = Model.newInstance(applicationContext)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        model?.close()
    }

    private fun startCameraPreview() {
        val holder = surfaceView.holder
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.d("RealTimeFaceDetection", "Surface created, starting camera preview")
                CameraUtil.startCameraPreview(holder, this@RealTimeFaceDetectionActivity, ::processCameraFrame)
            }
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                CameraUtil.stopCamera()
            }
        })
    }

    private fun processCameraFrame(bitmap: Bitmap) {
        frameCounter++
        if (frameCounter % detectionInterval == 0) {
            detectAndCropFace(bitmap)
        }
    }

    private fun detectAndCropFace(bitmap: Bitmap) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
        val detector = FaceDetection.getClient(options)
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]
                    val croppedFaceBitmap = cropFaceFromBitmap(bitmap, face.boundingBox)
                    croppedFaceBitmap?.let {
                        val scaledImage = Bitmap.createScaledBitmap(it, imageSize, imageSize, false)
                        classifyImage(scaledImage)
                    }
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

    private fun classifyImage(image: Bitmap) {
        model?.let { model ->
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

            runOnUiThread {
                emotionTextView.text = classes[maxPos]
            }
        }
    }
}

