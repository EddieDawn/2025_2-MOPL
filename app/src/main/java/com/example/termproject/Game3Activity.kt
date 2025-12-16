package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Game3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game3)

        val playButton = findViewById<Button>(R.id.btnPlay)
        val rulesButton = findViewById<Button>(R.id.btnRules)
        val backButton = findViewById<Button>(R.id.btnBack)

        playButton.setOnClickListener {
            val intent = Intent(this, GoStopActivity::class.java)
            startActivity(intent)
        }

        rulesButton.setOnClickListener {
            val intent = Intent(this, Game3RulesActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener {
            finish()
        }
    }
}
