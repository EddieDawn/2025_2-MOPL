package com.example.gostopmobileappprogramminglab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immediately goes to GoStopActivity and closes MainActivity
        startActivity(Intent(this, GoStopActivity::class.java))
        finish()
    }
}

