package com.example.vehicam


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vehicam.databinding.LandingPageBinding

class LandingPage : AppCompatActivity(){

    private lateinit var bindingPage: LandingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        bindingPage= LandingPageBinding.inflate(layoutInflater)
        setContentView(bindingPage.root)

        bindingPage.btnGetStarted.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}