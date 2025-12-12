package com.example.gostopmobileappprogramminglab

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load

import com.example.gostopmobileappprogramminglab.ai.SimpleOpponent
import com.example.gostopmobileappprogramminglab.api.RetrofitClient
import com.example.gostopmobileappprogramminglab.logic.GameEngine
import com.example.gostopmobileappprogramminglab.model.GameState
import com.example.gostopmobileappprogramminglab.model.Player
import kotlinx.coroutines.launch


class GoStopActivity : AppCompatActivity() {

    private lateinit var selectedCardImage: ImageView
    private lateinit var cardRecycler: RecyclerView
    private lateinit var fieldRecycler: RecyclerView
    private lateinit var turnIndicator: TextView
    private lateinit var captureCount: TextView
    private lateinit var opponentName: TextView
    private lateinit var opponentImage: ImageView

    private lateinit var state: GameState


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_stop)

        //Link layout views
        selectedCardImage = findViewById(R.id.cardImage)
        cardRecycler = findViewById(R.id.cardRecycler)
        fieldRecycler = findViewById(R.id.fieldRecycler)
        turnIndicator = findViewById(R.id.turnIndicator)
        captureCount = findViewById(R.id.captureCount)
        opponentName = findViewById(R.id.opponentName)
        opponentImage = findViewById(R.id.opponentImage)

        //Initialize game
        state = GameEngine.startNewGame()

        //RecyclerViews
        cardRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fieldRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Show first card
        if (state.playerHand.isNotEmpty()) {
            selectedCardImage.setImageResource(state.playerHand[0].drawableRes)
        }

        // Initial field
        fieldRecycler.adapter = CardAdapter(state.field.map { it.drawableRes }) {}

        //Load random opponent via API
        loadRandomOpponent()

        // Final UI update
        refreshUI()
    }



    // API: Load opponent with RandomUser API

    private fun loadRandomOpponent() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.randomUserApi.getRandomUser()
                val user = response.results[0]

                val fullName = "${user.name.first} ${user.name.last}"
                val country = user.location.country
                val age = user.dob.age

                opponentName.text = "Opponent: $fullName ($country, $age)"
                opponentImage.load(user.picture.large)

            } catch (e: Exception) {
                opponentName.text = "Opponent: Failed to load"
            }
        }
    }




    // Main UI refresh & game loop

    private fun refreshUI() {

        // Player hand adapter
        cardRecycler.adapter = CardAdapter(state.playerHand.map { it.drawableRes }) { clickedResId ->

            val playedCard = state.playerHand.find { it.drawableRes == clickedResId }

            if (playedCard != null && state.currentTurn == Player.HUMAN) {

                // Player plays card
                GameEngine.playCard(state, playedCard)
                state.playerHand.remove(playedCard)
                selectedCardImage.setImageResource(clickedResId)

                // Update field view
                fieldRecycler.adapter = CardAdapter(state.field.map { it.drawableRes }) { }

                // Switch to CPU
                GameEngine.switchTurn(state)
                turnIndicator.text = "CPU’s Turn"

                // CPU takes turn with slight delay
                Handler(Looper.getMainLooper()).postDelayed({

                    val aiCard = SimpleOpponent.chooseMove(state)
                    if (aiCard != null) {
                        GameEngine.playCard(state, aiCard)
                        state.opponentHand.remove(aiCard)

                        fieldRecycler.adapter = CardAdapter(state.field.map { it.drawableRes }) {}
                    }

                    // Back to player
                    GameEngine.switchTurn(state)
                    turnIndicator.text = "Your Turn"

                    refreshUI()

                }, 800)
            }
        }

        // Sync selected card
        if (state.playerHand.isNotEmpty()) {
            selectedCardImage.setImageResource(state.playerHand[0].drawableRes)
        }

        // Capture count UI
        captureCount.text =
            "Captured: ${state.playerCaptured.size} / ${state.cpuCaptured.size}"

        // Offer to Go or Stop
        val playerScore = GameEngine.calculateScore(state.playerCaptured)
        if (playerScore >= 3 && state.currentTurn == Player.HUMAN) {
            showGoStopDialog(playerScore)
        }

        // End of round
        if (GameEngine.isRoundOver(state)) {
            val pScore = GameEngine.calculateScore(state.playerCaptured)
            val cScore = GameEngine.calculateScore(state.cpuCaptured)
            showRoundEndDialog(pScore, cScore)
        }
    }


    // Go Stop Dialog

    private fun showGoStopDialog(playerScore: Int) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Go or Stop?")
            .setMessage("Your score is $playerScore.\nDo you want to Go or Stop?")
            .setPositiveButton("Go") { _, _ ->
                state.goCount += 1
                state.multiplier = 1 + state.goCount
            }
            .setNegativeButton("Stop") { _, _ ->
                val finalScore = playerScore * state.multiplier
                val cpuScore = GameEngine.calculateScore(state.cpuCaptured)
                showRoundEndDialog(finalScore, cpuScore)
            }
            .create()

        dialog.show()
    }




    // Round-End Dialog & Quotable API

    private fun showRoundEndDialog(playerScore: Int, cpuScore: Int) {

        lifecycleScope.launch {

            // Try fetching a quote
            val quoteText = try {
                val quoteResponse = RetrofitClient.quoteApi.getRandomQuote()
                quoteResponse.content
            } catch (e: Exception) {
                e.printStackTrace()
                "(Quote unavailable)"
            }


            val resultText = when {
                playerScore > cpuScore -> "You win!"
                cpuScore > playerScore -> "CPU wins!"
                else -> "It's a tie!"
            }

            val message = """
                $resultText
                Score: $playerScore vs $cpuScore

                Quote:
                "$quoteText"
            """.trimIndent()

            val dialog = android.app.AlertDialog.Builder(this@GoStopActivity)
                .setTitle("Round Over")
                .setMessage(message)
                .setPositiveButton("Play Again") { _, _ ->
                    state = GameEngine.startNewGame()
                    refreshUI()
                }
                .setNegativeButton("Quit", null)
                .create()

            dialog.show()
        }
    }
}






