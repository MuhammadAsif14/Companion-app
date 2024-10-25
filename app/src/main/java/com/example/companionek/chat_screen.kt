package com.example.companionek

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.companionek.data.Message
import com.example.companionek.utils.Constants.OPEN_GOOGLE
import com.example.companionek.utils.Constants.OPEN_SEARCH
import com.example.companionek.utils.Constants.RECEIVE_ID
import com.example.companionek.utils.Constants.SEND_ID
import com.example.companionek.utils.Time
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.tensorflow.lite.Interpreter
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class chat_screen: AppCompatActivity() {
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var processCameraProvider: ProcessCameraProvider
    private lateinit var cameraPreview: Preview
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var interpreter: Interpreter
    private val TAG = "chat_screen"

    // it was used only for my personal debugging
    var messagesList = mutableListOf<Message>()

    private lateinit var rv_messages: RecyclerView
    private lateinit var et_message: EditText
    private lateinit var adapter: MessagingAdapter
    private lateinit var btn_send: Button
    private val botList = listOf("M.A.M", "Ayesha", "Nizam", "Asif")

    private val client = OkHttpClient()


    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_screen)
        rv_messages = findViewById(R.id.rv_messages) // Get the RecyclerView
        et_message = findViewById(R.id.et_message) // Get the EditText
        btn_send = findViewById(R.id.btn_send)
        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        // Check for camera permission
        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera() // Start the camera if permission is granted
            } else {
                Toast.makeText(this, "Camera permission is required to use FER feature.", Toast.LENGTH_SHORT).show()

            }
        }
        requestCameraPermission()
        // Request camera permission

        recyclerView()

        clickEvents()

        val random = (0..3).random()
        customBotMessage("Hello! Today you're speaking with ${botList[random]}, how may I help?")
    }
    private fun requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera() // Start the camera if permission is already granted
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA) // Prompt user for permission
        }
    }
    private fun startCamera() {
        // Initialize the TFLite model
        interpreter = loadModel()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        ProcessCameraProvider.getInstance(this).addListener({
            processCameraProvider = ProcessCameraProvider.getInstance(this).get()
            bindImageAnalysis(cameraSelector)
        }, ContextCompat.getMainExecutor(this))
    }
    private fun loadModel(): Interpreter {
        val assetFileDescriptor = assets.openFd("BestModel66.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val modelBytes = inputStream.readBytes()
        val buffer = ByteBuffer.allocateDirect(modelBytes.size).order(ByteOrder.nativeOrder())
        buffer.put(modelBytes)
        return Interpreter(buffer)
    }
    private fun bindImageAnalysis(cameraSelector: CameraSelector) {
        val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .build()
        )

        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280, 720)) // Set the resolution you need
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val cameraExecutor = Executors.newSingleThreadExecutor()

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(detector, imageProxy)
        }

        processCameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis)
    }
    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(detector: FaceDetector, imageProxy: ImageProxy) {
        try {
            val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val face = faces.first() // Process only the first detected face
                        val bitmap = imageProxy.toBitmap()
                        if (bitmap != null) {
                            val croppedFace = cropFace(face.boundingBox, bitmap)
                            val resizedFace = Bitmap.createScaledBitmap(croppedFace, 224, 224, true)
                            val preprocessedFace = preprocessImage(resizedFace)
                            val emotionProbabilities = runModel(preprocessedFace)

                            val emotionLabel = getEmotionLabel(emotionProbabilities)
//                            textView.text = emotionLabel  // Update UI with the detected emotion
                            Log.d(TAG, "Detected Emotion: $emotionLabel")
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
        val clampedRect = Rect(
            faceRect.left.coerceAtLeast(0),
            faceRect.top.coerceAtLeast(0),
            faceRect.right.coerceAtMost(bitmap.width),
            faceRect.bottom.coerceAtMost(bitmap.height)
        )
        return Bitmap.createBitmap(bitmap, clampedRect.left, clampedRect.top, clampedRect.width(), clampedRect.height())
    }

    private fun preprocessImage(image: Bitmap): ByteBuffer {
        val inputBuffer = ByteBuffer.allocateDirect(1 * 224 * 224 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = image.getPixel(x, y)
                inputBuffer.putFloat((pixel shr 16 and 0xFF) / 255.0f) // Red
                inputBuffer.putFloat((pixel shr 8 and 0xFF) / 255.0f)  // Green
                inputBuffer.putFloat((pixel and 0xFF) / 255.0f)         // Blue
            }
        }
        return inputBuffer
    }
    private fun runModel(input: ByteBuffer): FloatArray {
        val output = Array(1) { FloatArray(7) }
        interpreter.run(input, output)
        return output[0]
    }

    private fun getEmotionLabel(probabilities: FloatArray): String {
        val labels = arrayOf("Angry", "Disgust", "Happy", "Sad", "Neutral", "Fear", "Surprise")
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        return if (maxIndex != -1) labels[maxIndex] else "Unknown"
    }



    private fun clickEvents() {

        //Send a message
        btn_send.setOnClickListener {
            sendMessage()
        }

        //Scroll back to correct position when user clicks on text view
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)

                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)

                }
            }
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            //Adds it to our local list
            messagesList.add(Message(message, SEND_ID, timeStamp))
            et_message.setText("")

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            try {
                fetchAnswerFromHuggingFace(message)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            //Fake response delay
            delay(1000)

            withContext(Dispatchers.Main) {
                //Gets the response
                val response = message

                //Adds it to our local list
                messagesList.add(Message(response, RECEIVE_ID, timeStamp))

                //Inserts our message into the adapter
                adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))

                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

                //Starts Google
                when (response) {
                    OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfterLast("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }

                }
            }
        }
    }
    private fun fetchAnswerFromHuggingFace(userInput: String) {
        val apiKey = "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"  // Replace with your API key

        // JSON payload for the request
        val jsonObject = JSONObject().apply {
            put("model", "meta-llama/Llama-3.2-1B-Instruct")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userInput)
                })
            })
            put("max_tokens", 500)
            put("stream", false)
        }

        // Build the request
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            jsonObject.toString()
        )
        val request = Request.Builder()
            .url("https://api-inference.huggingface.co/models/meta-llama/Llama-3.2-1B-Instruct/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        // Make the API call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()  // Handle the error
                runOnUiThread {
                    botResponse(e.message.toString())
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        val errorBody = response.body?.string()
                        runOnUiThread {
                            val errorRes= "Error: ${it.code} - ${errorBody ?: "No additional error information"}"
                            botResponse(errorRes)
                        }
                        throw IOException("Unexpected code $response")
                    }

                    // Parse the JSON response
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)

                    // Extract the response content from the JSON
                    val choices = jsonResponse.getJSONArray("choices")
                    val messageContent = choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    // Update the TextView with the model's response
                    runOnUiThread {
                        botResponse(messageContent)
                    }
                }
            }


        })
    }

    private fun customBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagesList.add(Message(message, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}