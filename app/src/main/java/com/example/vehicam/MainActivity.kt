package com.example.vehicam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.vehicam.databinding.MainActivityBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException




class MainActivity : AppCompatActivity() {

    private lateinit var bindingCamera: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        bindingCamera = MainActivityBinding.inflate(layoutInflater)
        setContentView(bindingCamera.root)

        bindingCamera.btnTakePicture.isEnabled = false
        bindingCamera.btnTakeVidio.isEnabled = false
        bindingCamera.btnSaveImage.isEnabled = false

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100
            )
        } else {
            bindingCamera.btnTakePicture.isEnabled = true
            bindingCamera.btnTakeVidio.isEnabled = true
            bindingCamera.btnSaveImage.isEnabled = true
        }

        bindingCamera.btnTakePicture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 101)
        }

        bindingCamera.btnTakeVidio.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, 102)
        }
    }

    private fun getMimeType(uri: Uri?): MediaType? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
        val mimeType = mimeTypeMap.getMimeTypeFromExtension(extension)
        return mimeType?.toMediaTypeOrNull()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                101 -> {
                    val pictureUri: Bitmap? = data?.getParcelableExtra("data")
                    bindingCamera.imgView.setImageBitmap(pictureUri)
                    bindingCamera.imgView.visibility = View.VISIBLE
                    bindingCamera.videoView.visibility = View.GONE

                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(
                            "image",
                            "image.jpg",
                            createRequestBodyFromBitmap(pictureUri)
                        )
                        .build()

                    val request = Request.Builder()
                        .url("https://qb5tvr8s-5000.asse.devtunnels.ms/predict")
                        .post(requestBody)
                        .build()

                    val client = OkHttpClient()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            runOnUiThread {
                                Toast.makeText(
                                    applicationContext,
                                    "Request failed: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseBody = response.body?.string()

                            runOnUiThread {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Response: $responseBody",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Request failed: ${response.code}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
                }
                102 -> {
                    val videoUri: Uri? = data?.data
                    val videoPath = getRealPathFromUri(videoUri)

                    if (videoPath != null) {
                        val videoFile = File(videoPath)

                        bindingCamera.videoView.setVideoURI(videoUri)
                        bindingCamera.videoView.start()
                        bindingCamera.videoView.visibility = View.VISIBLE
                        bindingCamera.imgView.visibility = View.GONE

                        val request = Request.Builder()
                            .url("https://qb5tvr8s-5000.asse.devtunnels.ms/predict")
                            .post(
                                MultipartBody.Builder()
                                    .setType(MultipartBody.FORM)
                                    .addFormDataPart(
                                        "video",
                                        "video.mp4",
                                        RequestBody.create("video/mp4".toMediaTypeOrNull(), videoFile)
                                    )
                                    .build()
                            )
                            .build()

                        val client = OkHttpClient()
                        client.newCall(request).enqueue(object : okhttp3.Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                runOnUiThread {
                                    Toast.makeText(
                                        applicationContext,
                                        "Request failed: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val responseBody = response.body?.string()

                                runOnUiThread {
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            applicationContext,
                                            "Response: $responseBody",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "Request failed: ${response.code}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        })
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Failed to get video file path.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // ...
            }
        }
    }
    private fun getRealPathFromUri(uri: Uri?): String? {
        var realPath: String? = null
        uri?.let {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (it.moveToFirst()) {
                    realPath = it.getString(columnIndex)
                }
            }
        }
        return realPath
    }

    private fun createRequestBodyFromBitmap(bitmap: Bitmap?): RequestBody {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        return RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindingCamera.btnTakePicture.isEnabled = true
            bindingCamera.btnTakeVidio.isEnabled = true
            bindingCamera.btnSaveImage.isEnabled = true
        }
    }
}