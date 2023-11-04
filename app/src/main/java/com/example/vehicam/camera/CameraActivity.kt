package com.example.vehicam.camera

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vehicam.databinding.MainActivityBinding


class MainActivity : AppCompatActivity() {

    private lateinit var bindingCamera: MainActivityBinding

    private val Camera_Request_Code=100
    private val Storage_Request_Code=101
    private val Video_Pick_Camera_Code=102
    private val Video_Pick_Gallery_Code=103

    private lateinit var cameraPermission: Array<String>
    private lateinit var storagePermission: Array<String>
    private lateinit var pVideoView: VideoView
    private var videoUri:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        bindingCamera = MainActivityBinding.inflate(layoutInflater)
        setContentView(bindingCamera.root)

        cameraPermission = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        storagePermission  = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        bindingCamera.btnTakeVidio.setOnClickListener{
            videoPickDialog()
        }
    }

    private fun videoPickDialog() {
        val options= arrayOf("Camera", "Gallery")
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Pick Video From")
        builder.setItems(options){dialog, which->
            if(which==0) {
                if(!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
            } else {
                if(!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }
        builder.create().show()
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type="video/*" // Hanya mengambi vidio
        startActivityForResult(
            galleryIntent,
            Video_Pick_Gallery_Code
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,Storage_Request_Code)
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )==PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        val values=ContentValues()
        values.put(MediaStore.Video.Media.TITLE, "Judul Vidio")
        values.put(MediaStore.Video.Media.DESCRIPTION, "Deskripsi Vidio")
        videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
        startActivityForResult(
            cameraIntent,
            Video_Pick_Camera_Code
        )
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,Camera_Request_Code)
    }

    private fun checkCameraPermission(): Boolean {
        val result=ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        val result1= ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Video_Pick_Gallery_Code) {
                videoUri = data?.data
                bindingCamera.videoView.setVideoURI(videoUri)
                bindingCamera.videoView.start()
                Log.d("VideoUri", "Video URI: $videoUri")
            } else if (requestCode == Video_Pick_Camera_Code) {
                videoUri = data?.data
                bindingCamera.videoView.setVideoURI(videoUri)
                bindingCamera.videoView.start()
                Log.d("VideoUri", "Video URI: $videoUri")
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Camera_Request_Code->{
                if(grantResults.isNotEmpty()) {
                    val cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED
                    val storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED
                    if(cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(this, "Kamera tidak diizinkan",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Storage_Request_Code->{
                if(grantResults.isNotEmpty()) {
                    val storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED
                    if(storageAccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Penyimpanan tidak diizinkan",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}