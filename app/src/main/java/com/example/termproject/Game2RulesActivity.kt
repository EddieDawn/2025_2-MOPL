package com.example.termproject

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Game2RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game2_rules)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Back button click listener
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    // Handle back button click
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
