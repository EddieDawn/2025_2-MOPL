package com.example.termproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // game 1 button click listener
        findViewById<Button>(R.id.btnGame1).setOnClickListener {
            val intent = Intent(this, Game1Activity::class.java)
            startActivity(intent)
        }

        // game 2 button click listener
        findViewById<Button>(R.id.btnGame2).setOnClickListener {
            val intent = Intent(this, Game2Activity::class.java)
            startActivity(intent)
        }

        // game 3 button click listener
        findViewById<Button>(R.id.btnGame3).setOnClickListener {
            val intent = Intent(this, Game3Activity::class.java)
            startActivity(intent)
        }
    }
}