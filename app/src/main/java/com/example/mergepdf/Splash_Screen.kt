package com.example.mergepdf

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import android.util.DisplayMetrics
import android.util.Log

class Splash_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)




        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@Splash_Screen, MainActivity3::class.java)
            startActivity(intent)
            finish()
        },2000)

        Log.d("Splash_Screen", "Starting MainActivity3")


    }



}