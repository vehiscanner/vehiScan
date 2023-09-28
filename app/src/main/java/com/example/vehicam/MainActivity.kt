package com.example.vehicam

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.vehicam.databinding.ActivityMainBinding

class  MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTakePicture.isEnabled = false

        // memunculkan dialog permisson
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            binding.btnTakePicture.isEnabled = true
        }

        // ketika button take di klik ia memanggil camera
        binding.btnTakePicture.setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 101)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (requestCode == 101){
            var picture: Bitmap? = data?.getParcelableExtra("data")
            binding.imgView.setImageBitmap(picture)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            binding.btnTakePicture.isEnabled = false
        }
    }
}