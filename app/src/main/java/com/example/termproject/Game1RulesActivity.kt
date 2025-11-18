package com.example.termproject

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Game1RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        // Back button click listener
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish() // Close current activity and return to previous screen
        }
    }
}
