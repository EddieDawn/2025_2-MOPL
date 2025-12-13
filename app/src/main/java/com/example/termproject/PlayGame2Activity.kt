package com.example.termproject

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlin.random.Random

class PlayGame2Activity : AppCompatActivity() {

    // Enum for game states
    private enum class GameState {
        READY, WAITING, FAKE_SIGNAL, REAL_SIGNAL, GAME_OVER
    }

    // View variables
    private lateinit var gameContainer: ConstraintLayout
    private lateinit var btnTopPlayer: Button
    private lateinit var btnBottomPlayer: Button
    private lateinit var tvCenterDraw: TextView
    private lateinit var tvReadyCountdown: TextView

    // Coroutine Job for managing the game loop
    private var gameJob: Job? = null
    // Current state of the game
    private var currentState: GameState = GameState.READY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game2)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        gameContainer = findViewById(R.id.gameContainer)
        btnTopPlayer = findViewById(R.id.btnTopPlayer)
        btnBottomPlayer = findViewById(R.id.btnBottomPlayer)
        tvCenterDraw = findViewById(R.id.tvCenterDraw)
        tvReadyCountdown = findViewById(R.id.tvReadyCountdown)

        // Set button click listeners
        btnTopPlayer.setOnClickListener { handleButtonClick("Top Player") }
        btnBottomPlayer.setOnClickListener { handleButtonClick("Bottom Player") }

        // Start the game
        startGame()
    }

    private fun startGame() {
        // Cancel any existing game loop
        gameJob?.cancel()
        // Start a new game loop in the Coroutine scope of this activity
        gameJob = CoroutineScope(Dispatchers.Main).launch {
            resetGame()
            runCountdown()
            runGameLoop()
        }
    }

    private fun resetGame() {
        currentState = GameState.READY
        tvCenterDraw.visibility = TextView.INVISIBLE
        tvReadyCountdown.visibility = TextView.VISIBLE
        btnTopPlayer.isEnabled = false
        btnBottomPlayer.isEnabled = false
        gameContainer.setBackgroundColor(ContextCompat.getColor(this@PlayGame2Activity, R.color.game_background_waiting))
    }

    private suspend fun runCountdown() {
        for (i in 3 downTo 1) {
            tvReadyCountdown.text = "READY\n$i"
            delay(1000)
        }
        tvReadyCountdown.visibility = TextView.INVISIBLE
        btnTopPlayer.isEnabled = true
        btnBottomPlayer.isEnabled = true
    }

    private suspend fun runGameLoop() {
        currentState = GameState.WAITING
        gameContainer.setBackgroundColor(ContextCompat.getColor(this@PlayGame2Activity, R.color.game_background_waiting))

        while (currentState != GameState.GAME_OVER) {
            // Wait for a random time before showing a signal
            val randomWait = Random.nextLong(2000, 5000)
            delay(randomWait)

            // Decide whether to show a fake or real signal (60% chance for a fake signal)
            if (Random.nextInt(100) < 60) {
                // Show FAKE signal (green)
                if (currentState == GameState.WAITING) {
                    currentState = GameState.FAKE_SIGNAL
                    gameContainer.setBackgroundColor(ContextCompat.getColor(this@PlayGame2Activity, R.color.game_background_fake))
                    // Wait for a short duration. If a button is pressed during this time, it's a loss.
                    delay(Random.nextLong(1000, 2000))
                    // If game is not over, go back to waiting
                    if (currentState == GameState.FAKE_SIGNAL) {
                        currentState = GameState.WAITING
                        gameContainer.setBackgroundColor(ContextCompat.getColor(this@PlayGame2Activity, R.color.game_background_waiting))
                    }
                }
            } else {
                // Show REAL signal (blue)
                if (currentState == GameState.WAITING) {
                    currentState = GameState.REAL_SIGNAL
                    gameContainer.setBackgroundColor(ContextCompat.getColor(this@PlayGame2Activity, R.color.game_background_real))
                    tvCenterDraw.visibility = TextView.VISIBLE
                    // The loop will now wait for a player to click.
                    // A click will set the state to GAME_OVER, exiting the loop.
                    break
                }
            }
        }
    }

    private fun handleButtonClick(player: String) {
        if (currentState == GameState.GAME_OVER) return

        val opponent = if (player == "Top Player") "Bottom Player" else "Top Player"

        when (currentState) {
            GameState.REAL_SIGNAL -> {
                // Correctly pressed on the real signal
                showResult("$player WINS!", "You drew at the right time!")
            }
            GameState.FAKE_SIGNAL -> {
                // Pressed on the fake signal
                showResult("$opponent WINS!", "$player reacted to a fake signal!")
            }
            GameState.WAITING, GameState.READY -> {
                // Pressed too early
                showResult("$opponent WINS!", "$player made a false start!")
            }
            else -> {}
        }
        currentState = GameState.GAME_OVER
        gameJob?.cancel()
    }

    private fun showResult(title: String, message: String) {
        btnTopPlayer.isEnabled = false
        btnBottomPlayer.isEnabled = false

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Play Again") { _, _ -> startGame() }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the coroutine job when the activity is destroyed
        gameJob?.cancel()
    }
}