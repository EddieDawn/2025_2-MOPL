package com.example.termproject

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.random.Random

class PlayGame2Activity : AppCompatActivity() {

    private lateinit var gameContainer: ConstraintLayout
    private lateinit var btnTopPlayer: Button
    private lateinit var btnBottomPlayer: Button
    private lateinit var tvCenterDraw: TextView
    private lateinit var tvReadyCountdown: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var isBlue = false
    private var gameEnded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game2)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        gameContainer = findViewById(R.id.gameContainer)
        btnTopPlayer = findViewById(R.id.btnTopPlayer)
        btnBottomPlayer = findViewById(R.id.btnBottomPlayer)
        tvCenterDraw = findViewById(R.id.tvCenterDraw)
        tvReadyCountdown = findViewById(R.id.tvReadyCountdown)

        // Start ready countdown
        startReadyCountdown()

        // Set button click listeners
        btnTopPlayer.setOnClickListener {
            if (!gameEnded) {
                handleButtonClick("Top Player")
            }
        }

        btnBottomPlayer.setOnClickListener {
            if (!gameEnded) {
                handleButtonClick("Bottom Player")
            }
        }
    }

    // Start 5-second ready countdown
    private fun startReadyCountdown() {
        var countdown = 5

        val countdownRunnable = object : Runnable {
            override fun run() {
                if (countdown > 0) {
                    tvReadyCountdown.text = "READY\n$countdown"
                    countdown--
                    handler.postDelayed(this, 1000)
                } else {
                    // Hide ready countdown and start game
                    tvReadyCountdown.visibility = TextView.INVISIBLE
                    startGame()
                }
            }
        }

        handler.post(countdownRunnable)
    }

    // Start the actual game
    private fun startGame() {
        // Enable buttons
        btnTopPlayer.isEnabled = true
        btnBottomPlayer.isEnabled = true

        // Set background to pastel red
        gameContainer.setBackgroundColor(Color.parseColor("#FFB3BA"))

        // Random delay between 0-15 seconds (in milliseconds)
        val randomDelay = Random.nextLong(0, 15001)

        // After random delay, change to blue and show DRAW!
        handler.postDelayed({
            if (!gameEnded) {
                changeToBlue()
            }
        }, randomDelay)
    }

    // Change background to pastel blue and show DRAW! text
    private fun changeToBlue() {
        isBlue = true
        gameContainer.setBackgroundColor(Color.parseColor("#AED6F1"))
        tvCenterDraw.visibility = TextView.VISIBLE
    }

    // Handle button click
    private fun handleButtonClick(player: String) {
        gameEnded = true

        if (isBlue) {
            // Clicked after blue - Player wins!
            showResult("$player WINS!", "You drew at the right time!")
        } else {
            // Clicked before blue - Player loses!
            val opponent = if (player == "Top Player") "Bottom Player" else "Top Player"
            showResult("$opponent WINS!", "$player made a false start!")
        }
    }

    // Show result dialog
    private fun showResult(title: String, message: String) {
        // Disable buttons
        btnTopPlayer.isEnabled = false
        btnBottomPlayer.isEnabled = false

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Play Again") { _, _ ->
                // Restart game
                restartGame()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // Restart the game
    private fun restartGame() {
        gameEnded = false
        isBlue = false
        tvCenterDraw.visibility = TextView.INVISIBLE
        tvReadyCountdown.visibility = TextView.VISIBLE
        gameContainer.setBackgroundColor(Color.parseColor("#FFB3BA"))
        btnTopPlayer.isEnabled = false
        btnBottomPlayer.isEnabled = false

        // Remove all pending callbacks
        handler.removeCallbacksAndMessages(null)

        // Start countdown again
        startReadyCountdown()
    }

    // Handle back button
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up handler
        handler.removeCallbacksAndMessages(null)
    }
}
