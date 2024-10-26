package com.example.mergepdf

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)

        if (isTablet()) {
            // Launch MainActivity2 for tablets only if the current activity is not MainActivity2
            if (this.javaClass != MainActivity2::class.java) {
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            // Launch MainActivity for mobile devices only if the current activity is not MainActivity
            if (this.javaClass != MainActivity::class.java) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


        Log.d("MainActivity3", "Checking if tablet: " + isTablet())

    }


    private fun isTablet(): Boolean {
        val displayMetrics = resources.displayMetrics
        val widthInDp = displayMetrics.widthPixels / displayMetrics.density
        Log.d("MainActivity3", "Screen width in dp: $widthInDp")
        return widthInDp >= 678
    }




}

