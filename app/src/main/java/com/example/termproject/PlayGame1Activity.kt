package com.example.termproject

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class PlayGame1Activity : AppCompatActivity() {

    private lateinit var etDigit1: EditText
    private lateinit var etDigit2: EditText
    private lateinit var etDigit3: EditText
    private lateinit var etDigit4: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvAttemptCounter: TextView
    private lateinit var historyContainer: LinearLayout

    private var secretNumber = ""
    private var attemptCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_game1)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        etDigit1 = findViewById(R.id.etDigit1)
        etDigit2 = findViewById(R.id.etDigit2)
        etDigit3 = findViewById(R.id.etDigit3)
        etDigit4 = findViewById(R.id.etDigit4)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvAttemptCounter = findViewById(R.id.tvAttemptCounter)
        historyContainer = findViewById(R.id.historyContainer)

        // Generate random 4-digit number with unique digits
        secretNumber = generateSecretNumber()

        // Setup auto-focus between EditTexts
        setupAutoFocus()

        // Submit button click listener
        btnSubmit.setOnClickListener {
            submitGuess()
        }
    }

    // Generate a random 4-digit number with unique digits
    private fun generateSecretNumber(): String {
        val digits = (0..9).toMutableList()
        digits.shuffle(Random)
        return digits.take(4).joinToString("")
    }

    // Setup auto-focus: move to next EditText when current is filled
    private fun setupAutoFocus() {
        etDigit1.addTextChangedListener(createTextWatcher(etDigit2))
        etDigit2.addTextChangedListener(createTextWatcher(etDigit3))
        etDigit3.addTextChangedListener(createTextWatcher(etDigit4))
        etDigit4.addTextChangedListener(createTextWatcher(null))
    }

    private fun createTextWatcher(nextField: EditText?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1 && nextField != null) {
                    nextField.requestFocus()
                }
            }
        }
    }

    // Submit the guess and calculate strikes/balls
    private fun submitGuess() {
        val digit1 = etDigit1.text.toString()
        val digit2 = etDigit2.text.toString()
        val digit3 = etDigit3.text.toString()
        val digit4 = etDigit4.text.toString()

        // Validate input
        if (digit1.isEmpty() || digit2.isEmpty() || digit3.isEmpty() || digit4.isEmpty()) {
            Toast.makeText(this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show()
            return
        }

        val guess = digit1 + digit2 + digit3 + digit4

        // Check for duplicate digits
        if (guess.toSet().size != 4) {
            Toast.makeText(this, "All digits must be unique!", Toast.LENGTH_SHORT).show()
            return
        }

        // Increment attempt counter
        attemptCount++
        tvAttemptCounter.text = "Attempt: $attemptCount"

        // Calculate strikes and balls
        val (strikes, balls) = calculateStrikesAndBalls(guess)

        // Add to history
        addToHistory(guess, strikes, balls)

        // Check for win
        if (strikes == 4) {
            showCongratulations()
        } else {
            // Clear input fields for next attempt
            clearInputFields()
        }
    }

    // Calculate strikes and balls
    private fun calculateStrikesAndBalls(guess: String): Pair<Int, Int> {
        var strikes = 0
        var balls = 0

        for (i in guess.indices) {
            if (guess[i] == secretNumber[i]) {
                strikes++
            } else if (secretNumber.contains(guess[i])) {
                balls++
            }
        }

        return Pair(strikes, balls)
    }

    // Add guess and result to history
    private fun addToHistory(guess: String, strikes: Int, balls: Int) {
        val historyItem = TextView(this).apply {
            text = "#$attemptCount: $guess → ${strikes}S ${balls}B"
            textSize = 16f
            setPadding(8, 12, 8, 12)

            // Color code for better readability
            setTextColor(when (strikes) {
                4 -> Color.GREEN
                3 -> Color.rgb(0, 150, 0)
                2 -> Color.rgb(200, 100, 0)
                1 -> Color.rgb(150, 150, 0)
                else -> Color.BLACK
            })
        }

        // Add to top of history (most recent first)
        historyContainer.addView(historyItem, 0)
    }

    // Clear input fields
    private fun clearInputFields() {
        etDigit1.text.clear()
        etDigit2.text.clear()
        etDigit3.text.clear()
        etDigit4.text.clear()
        etDigit1.requestFocus()
    }

    // Show congratulations dialog
    private fun showCongratulations() {
        AlertDialog.Builder(this)
            .setTitle("🎉 Congratulations! 🎉")
            .setMessage("You guessed the number $secretNumber in $attemptCount attempts!")
            .setPositiveButton("Play Again") { _, _ ->
                // Reset game
                resetGame()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // Reset game
    private fun resetGame() {
        secretNumber = generateSecretNumber()
        attemptCount = 0
        tvAttemptCounter.text = "Attempt: 1"
        historyContainer.removeAllViews()
        clearInputFields()
    }

    // Handle back button
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
