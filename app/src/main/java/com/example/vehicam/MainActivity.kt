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

    private lateinit var bindingMain: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)

        bindingMain.btnTakePicture.isEnabled = false

        // memunculkan dialog permisson
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        } else {
            bindingMain.btnTakePicture.isEnabled = true
        }

        // ketika button take di klik ia memanggil camera
        bindingMain.btnTakePicture.setOnClickListener {
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 101)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101){
            var picture: Bitmap? = data?.getParcelableExtra("data")
            bindingMain.imgView.setImageBitmap(picture)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            bindingMain.btnTakePicture.isEnabled = false
        }
    }
}