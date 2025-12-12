package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Game2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game2)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Play button click listener
        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            val intent = Intent(this, PlayGame2Activity::class.java)
            startActivity(intent)
        }

        // Rules button click listener
        findViewById<Button>(R.id.btnRules).setOnClickListener {
            val intent = Intent(this, Game2RulesActivity::class.java)
            startActivity(intent)
        }

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
