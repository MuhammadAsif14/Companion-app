//package com.example.companionek.facedetection
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.Rect
//import android.os.Bundle
//import android.util.Log
//import android.widget.TextView
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import androidx.camera.core.Preview
//import androidx.camera.lifecycle.ProcessCameraProvider
//import com.example.companionek.CameraXViewModel
//import com.example.companionek.databinding.ActivityFaceDetectionBinding
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.face.FaceDetection
//import com.google.mlkit.vision.face.FaceDetector
//import com.google.mlkit.vision.face.FaceDetectorOptions
//import org.tensorflow.lite.Interpreter
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.util.concurrent.Executors
//
//class FaceDetectionActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityFaceDetectionBinding
//    private lateinit var cameraSelector: CameraSelector
//    private lateinit var processCameraProvider: ProcessCameraProvider
//    private lateinit var cameraPreview: Preview
//    private lateinit var imageAnalysis: ImageAnalysis
//    private val cameraXViewModel = viewModels<CameraXViewModel>()
//    // MobileNetV2 TFLite Interpreter
//    private lateinit var interpreter: Interpreter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        // Initialize the TFLite model
//        interpreter = loadModel()
//        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
//        cameraXViewModel.value.processCameraProvider.observe(this) { provider ->
//            processCameraProvider = provider
//            bindCameraPreview()
//            bindInputAnalyser()
//        }
//    }
//    // Load MobileNetV2 model from assets
//    private fun loadModel(): Interpreter {
//        val assetFileDescriptor = assets.openFd("model.tflite")
//        val inputStream = assetFileDescriptor.createInputStream()
//        val modelBytes = inputStream.readBytes()
//        val buffer = ByteBuffer.allocateDirect(modelBytes.size).order(ByteOrder.nativeOrder())
//        buffer.put(modelBytes)
//        return Interpreter(buffer)
//    }
//    private fun bindCameraPreview() {
//        cameraPreview = Preview.Builder()
//            .setTargetRotation(binding.previewView.display.rotation)
//            .build()
//        cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
//        try {
//            processCameraProvider.bindToLifecycle(this, cameraSelector, cameraPreview)
//        } catch (illegalStateException: IllegalStateException) {
//            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
//        } catch (illegalArgumentException: IllegalArgumentException) {
//            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
//        }
//    }
//    private fun bindInputAnalyser() {
//        val detector = FaceDetection.getClient(
//            FaceDetectorOptions.Builder()
//                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
//                .build()
//        )
//        imageAnalysis = ImageAnalysis.Builder()
//            .setTargetRotation(binding.previewView.display.rotation)
//            .build()
//
//        val cameraExecutor = Executors.newSingleThreadExecutor()
//
//        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
//
//            val emotionTextView = binding.emotionTextView
//            processImageProxy(detector, imageProxy,emotionTextView)
//        }
//        try {
//            processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
//        } catch (illegalStateException: IllegalStateException) {
//            Log.e(TAG, illegalStateException.message ?: "IllegalStateException")
//        } catch (illegalArgumentException: IllegalArgumentException) {
//            Log.e(TAG, illegalArgumentException.message ?: "IllegalArgumentException")
//        }
//    }
//    @SuppressLint("UnsafeOptInUsageError")
//    private fun processImageProxy(detector: FaceDetector, imageProxy: ImageProxy,emotionTextView: TextView) {
//        try {
//            val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
//            detector.process(inputImage)
//                .addOnSuccessListener { faces ->
//                    binding.graphicOverlay.clear()
//                    faces.forEach { face ->
//                        val faceBox = FaceBox(binding.graphicOverlay, face, imageProxy.image!!.cropRect)
//                        binding.graphicOverlay.add(faceBox)
//                        // Safely crop and process the face
//                        try {
//                            val bitmap = imageProxy.toBitmap()
//                            if (bitmap != null) {
//                                val croppedFace = cropFace(face.boundingBox, bitmap)
//                                val resizedFace = Bitmap.createScaledBitmap(croppedFace, 224, 224, true)
//                                val preprocessedFace = preprocessImage(resizedFace)
//
//                                // Run the preprocessed face through the model
//                                val emotionProbabilities = runModel(preprocessedFace)
//                                // Get the predicted emotion label
//                                val emotionLabel = getEmotionLabel(emotionProbabilities)
//                                emotionTextView.text = emotionLabel
//                                Log.d(TAG, "Emotion Prob: $emotionProbabilities")
//
//                            }
//                        } catch (e: Exception) {
//                            Log.e(TAG, "Error processing face image: ${e.message}")
//                        }
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e(TAG, "Face detection failed: ${e.message}")
//                }
//                .addOnCompleteListener {
//                    imageProxy.close()
//                }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error processing imageProxy: ${e.message}")
//            imageProxy.close()
//        }
//    }
//
//    fun cropFace(faceRect: Rect, bitmap: Bitmap): Bitmap {
//        return try {
//            // Ensure the faceRect fits within the bitmap boundaries
//            val clampedRect = Rect(
//                faceRect.left.coerceAtLeast(0),
//                faceRect.top.coerceAtLeast(0),
//                faceRect.right.coerceAtMost(bitmap.width),
//                faceRect.bottom.coerceAtMost(bitmap.height)
//            )
//            Bitmap.createBitmap(bitmap, clampedRect.left, clampedRect.top, clampedRect.width(), clampedRect.height())
//        } catch (e: Exception) {
//            Log.e(TAG, "Error cropping face: ${e.message}")
//            bitmap // Return the original bitmap in case of an error
//        }
//    }
//    // Convert the face image to a ByteBuffer required by MobileNetV2
//    private fun preprocessImage(image: Bitmap): ByteBuffer {
//        val inputBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)
//        inputBuffer.order(ByteOrder.nativeOrder())
//        for (y in 0 until 224) {
//            for (x in 0 until 224) {
//                val pixel = image.getPixel(x, y)
//                // Convert RGB to BGR
//                inputBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f)
//                inputBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)
//                inputBuffer.putFloat((pixel and 0xFF) / 255.0f)
//            }
//        }
//        return inputBuffer
//    }
//    // Run MobileNetV2 model to get emotion prediction
//    private fun runModel(input: ByteBuffer): FloatArray {
//        val output = Array(1) { FloatArray(7) } // Model returns 1x7 array (2D array)
//        interpreter.run(input, output) // Run the interpreter with the input buffer and get output
//        return output[0] // Return the first (and only) row of the output
//    }
//    private fun getEmotionLabel(probabilities: FloatArray): String {
//        val labels = arrayOf("Angry", "Disgust", "Happy", "Sad", "Neutral", "Fear", "Surprise")
//        if (probabilities.isEmpty() || labels.size != probabilities.size) return "Unknown"
//
//        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
//        return if (maxIndex in labels.indices) labels[maxIndex] else "Unknown"
//    }
//
//
//    companion object {
//        private val TAG = FaceDetectionActivity::class.simpleName
//        fun startActivity(context: Context) {
//            Intent(context, FaceDetectionActivity::class.java).also {
//                context.startActivity(it)
//            }
//        }
//    }
//
//
//}

package com.example.companionek.facedetection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.example.companionek.CameraXViewModel
import com.example.companionek.databinding.ActivityFaceDetectionBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Executors

class FaceDetectionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaceDetectionBinding
    private lateinit var cameraSelector: CameraSelector
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private val cameraXViewModel = viewModels<CameraXViewModel>()
    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        interpreter = loadModel()
        cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        cameraXViewModel.value.processCameraProvider.observe(this) { provider ->
            processCameraProvider = provider
            bindCameraPreview()
            bindInputAnalyser()
        }
    }

    private fun loadModel(): Interpreter {
        val assetFileDescriptor = assets.openFd("model.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val modelBytes = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(modelBytes.size).order(ByteOrder.nativeOrder())
        buffer.put(modelBytes)
        return Interpreter(buffer)
    }

    private fun bindCameraPreview() {
        cameraPreview = Preview.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        cameraPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
        try {
            processCameraProvider.bindToLifecycle(this, cameraSelector, cameraPreview)
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Error binding camera preview")
        }
    }

    private fun bindInputAnalyser() {
        val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .build()
        )
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .build()
        val cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            val emotionTextView = binding.emotionTextView
            processImageProxy(detector, imageProxy, emotionTextView)
        }
        try {
            processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
        } catch (e: Exception) {
            Log.e(TAG, e.message ?: "Error binding input analyzer")
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(detector: FaceDetector, imageProxy: ImageProxy, emotionTextView: TextView) {
        try {
            val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    binding.graphicOverlay.clear()
                    faces.forEach { face ->
                        val faceBox = FaceBox(binding.graphicOverlay, face, imageProxy.image!!.cropRect)
                        binding.graphicOverlay.add(faceBox)
                        try {
                            val bitmap = imageProxy.toBitmap()
                            if (bitmap != null) {
                                val croppedFace = cropFace(face.boundingBox, bitmap)
                                val resizedFace = Bitmap.createScaledBitmap(
                                    croppedFace.rotate(imageProxy.imageInfo.rotationDegrees), 224, 224, true
                                )
                                val preprocessedFace = preprocessImage(resizedFace)

                                val emotionProbabilities = runModel(preprocessedFace)
                                val emotionLabel = getEmotionLabel(emotionProbabilities)
                                emotionTextView.text = emotionLabel
                                Log.d(TAG, "Emotion Probabilities: ${emotionProbabilities.joinToString()}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error processing face image: ${e.message}")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Face detection failed: ${e.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing imageProxy: ${e.message}")
            imageProxy.close()
        }
    }

    private fun cropFace(faceRect: Rect, bitmap: Bitmap): Bitmap {
        return try {
            val clampedRect = Rect(
                faceRect.left.coerceAtLeast(0),
                faceRect.top.coerceAtLeast(0),
                faceRect.right.coerceAtMost(bitmap.width),
                faceRect.bottom.coerceAtMost(bitmap.height)
            )
            Bitmap.createBitmap(bitmap, clampedRect.left, clampedRect.top, clampedRect.width(), clampedRect.height())
        } catch (e: Exception) {
            Log.e(TAG, "Error cropping face: ${e.message}")
            bitmap
        }
    }

    private fun preprocessImage(image: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = image.getPixel(x, y)
                inputBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
                inputBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
                inputBuffer.putFloat((pixel and 0xFF) / 255.0f)        // Blue
            }
        }
        return inputBuffer
    }

    private fun runModel(input: ByteBuffer): FloatArray {
        val output = Array(1) { FloatArray(7) }
        interpreter.run(input, output)
        return applySoftmax(output[0])
    }

    private fun applySoftmax(logits: FloatArray): FloatArray {
        val expValues = logits.map { Math.exp(it.toDouble()) }
        val sumExp = expValues.sum()
        return expValues.map { (it / sumExp).toFloat() }.toFloatArray()
    }

    private fun getEmotionLabel(probabilities: FloatArray): String {
        val labels = arrayOf("Angry", "Disgust", "Happy", "Sad", "Neutral", "Fear", "Surprise")
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        return if (maxIndex in labels.indices) labels[maxIndex] else "Unknown"
    }

    private fun Bitmap.rotate(rotation: Int): Bitmap {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    companion object {
        private val TAG = FaceDetectionActivity::class.simpleName
        fun startActivity(context: Context) {
            Intent(context, FaceDetectionActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
}
