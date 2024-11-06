package com.example.companionek.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.Camera
import android.view.SurfaceHolder
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.IOException

object CameraUtil {
    private var camera: Camera? = null

    fun startCameraPreview(holder: SurfaceHolder, context: Context, onFrameCaptured: (Bitmap) -> Unit) {
        // Release any previous instance of the camera
        stopCamera()

        try {
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
            camera?.apply {
                setPreviewDisplay(holder)
                setDisplayOrientation(90)  // Rotate to portrait mode

                // Set camera preview callback to get each frame as a byte array
                setPreviewCallback { data, cam ->
                    val parameters = cam.parameters
                    val width = parameters.previewSize.width
                    val height = parameters.previewSize.height

                    // Convert YUV to Bitmap
                    val yuvImage = android.graphics.YuvImage(data, parameters.previewFormat, width, height, null)
                    val out = ByteArrayOutputStream()
                    yuvImage.compressToJpeg(android.graphics.Rect(0, 0, width, height), 100, out)
                    val imageBytes = out.toByteArray()
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    // Rotate bitmap for correct orientation
                    val rotatedBitmap = rotateBitmap(bitmap, 270f)
                    onFrameCaptured(rotatedBitmap)
                }
                startPreview()
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Error setting camera preview", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun stopCamera() {
        camera?.apply {
            stopPreview()
            setPreviewCallback(null)
            release()
        }
        camera = null
    }
}
