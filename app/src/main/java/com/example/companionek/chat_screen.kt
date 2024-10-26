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
import android.widget.ImageView
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
import com.example.companionek.data.ChatMessage
import com.example.companionek.data.Message
import com.example.companionek.utils.Constants.OPEN_GOOGLE
import com.example.companionek.utils.Constants.OPEN_SEARCH
import com.example.companionek.utils.Constants.RECEIVE_ID
import com.example.companionek.utils.Constants.SEND_ID
import com.example.companionek.utils.Time
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
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
    private lateinit var cameraVector: ImageView

    private lateinit var chatSessionRef: DatabaseReference
    private lateinit var database:FirebaseDatabase
    private var userId: String? = null
    private var chatSessionId: String? = null



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
        database = FirebaseDatabase.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid

        // Initialize session ID and session reference
        if (userId != null) {
            // Check if the chatSessionId already exists before generating a new one
            if (chatSessionId == null) {
                chatSessionId = database.getReference("user/$userId/chatSessions").push().key
                chatSessionRef = database.getReference("user/$userId/chatSessions/$chatSessionId")

                Toast.makeText(this, "chatSessionID: $chatSessionId", Toast.LENGTH_LONG).show()
            }



            Toast.makeText(this, "chatSessionID:${chatSessionId}", Toast.LENGTH_LONG).show()

        }else{
            Toast.makeText(this, "user id from database is null", Toast.LENGTH_LONG).show()

        }

        requestCameraPermission()
        // Request camera permission

        recyclerView()

        clickEvents()

        val random = (0..3).random()
        customBotMessage("Hello! Today you're speaking with ${botList[random]}, how may I help?")
        cameraVector=findViewById(R.id.cameraVector)
        cameraVector.setOnClickListener {
            // Handle the click event here
            openTestFaceDetectionActivity() // Replace this with your desired action
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
            saveChatMessage(message, "user") // Save user's message

            try {
                fetchAnswerFromHuggingFace(message)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        }
    }

    // Function to save chat messages under the current session
    fun saveChatMessage(messageText: String, sender: String) {
        if (userId != null && chatSessionId != null) {
            val chatId = chatSessionRef.push().key // Generate a unique chat ID
            if (chatId != null) {
                val chatMessage = ChatMessage(
                    message = messageText,
                    timestamp = System.currentTimeMillis(),
                    sender = sender
                )

                // Save message under the specific session ID and chat ID
                chatSessionRef.child("$chatId").setValue(chatMessage)
                    .addOnSuccessListener {
                        Log.d("ChatMessage", "Message saved to Firebase: $messageText")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ChatMessage", "Failed to save message: ", exception)
                    }
            }
        }
    }

    //to update the emotion of the user for each seesion
    fun addEmotionToSession(emotion: String) {
        if (userId != null && chatSessionId != null) {
            // Reference the emotion list in the session node
            val emotionListRef = chatSessionRef.child("$chatSessionId/emotion")

            // Use a transaction to safely add the new emotion to the list to update use following code
//            val detectedEmotion = "happy" // Example emotion detected
//            addEmotionToSession(detectedEmotion)
            emotionListRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentList = currentData.getValue(object : GenericTypeIndicator<MutableList<String>>() {}) ?: mutableListOf()
                    currentList.add(emotion)
                    currentData.value = currentList
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                    if (error != null) {
                        Log.e("ChatSession", "Failed to update emotion list: ", error.toException())
                    } else {
                        Log.d("ChatSession", "Emotion added to session: $emotion")
                    }
                }
            })
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


                //save chatbot's response to database
                saveChatMessage(response, "bot") // Save bot's response

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

    private fun openTestFaceDetectionActivity() {
        // Start your new chat activity or perform your action
        val intent = Intent(this, TestFaceDetection::class.java)
        startActivity(intent)
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

    fun startNewSession() {
        // Create a new session if sessionId is null
        if (userId != null && chatSessionId == null) {
            chatSessionId = chatSessionRef.push().key  // Generate a single sessionId
            chatSessionRef.child(chatSessionId!!).child("emotion").setValue(arrayListOf<String>())
                .addOnSuccessListener {
                    Log.d("ChatSession", "New session started with sessionId: $chatSessionId")
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatSession", "Failed to start session: ", exception)
                }
        }
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