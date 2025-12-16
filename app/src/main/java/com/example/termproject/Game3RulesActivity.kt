package com.example.termproject

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Game3RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game3_rules)

        findViewById<Button>(R.id.backButtonFromRules).setOnClickListener {
            finish()
        }
    }
}
