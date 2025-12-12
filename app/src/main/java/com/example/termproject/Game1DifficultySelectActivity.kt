package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Game1DifficultySelectActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game1_difficulty_select)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Number Baseball"

        findViewById<Button>(R.id.btnHard).setOnClickListener {
            startGameWithDifficulty(9)
        }

        findViewById<Button>(R.id.btnMedium).setOnClickListener {
            startGameWithDifficulty(6)
        }

        findViewById<Button>(R.id.btnEasy).setOnClickListener {
            startGameWithDifficulty(3)
        }
    }

    private fun startGameWithDifficulty(maxDigit: Int) {
        val intent = Intent(this, PlayGame1Activity::class.java).apply {
            putExtra("MAX_DIGIT", maxDigit)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
