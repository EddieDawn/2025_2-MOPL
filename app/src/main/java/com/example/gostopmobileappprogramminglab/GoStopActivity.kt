package com.example.gostopmobileappprogramminglab

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import android.os.Handler
import android.os.Looper

import com.example.gostopmobileappprogramminglab.ai.SimpleOpponent
import com.example.gostopmobileappprogramminglab.logic.GameEngine
import com.example.gostopmobileappprogramminglab.model.GameState
import com.example.gostopmobileappprogramminglab.model.Player

class GoStopActivity : AppCompatActivity() {

    private lateinit var selectedCardImage: ImageView
    private lateinit var cardRecycler: RecyclerView
    private lateinit var fieldRecycler: RecyclerView
    private lateinit var turnIndicator: TextView
    private lateinit var captureCount: TextView
    private lateinit var state: GameState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_stop)

        // Links layout elements
        selectedCardImage = findViewById(R.id.cardImage)
        cardRecycler = findViewById(R.id.cardRecycler)
        fieldRecycler = findViewById(R.id.fieldRecycler)
        turnIndicator = findViewById(R.id.turnIndicator)
        captureCount = findViewById(R.id.captureCount)

        // Initialise the game state
        state = GameEngine.startNewGame()

        // RecyclerView setup
        cardRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        fieldRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Show the first card
        if (state.playerHand.isNotEmpty()) {
            selectedCardImage.setImageResource(state.playerHand[0].drawableRes)
        }

        // Initial field
        fieldRecycler.adapter =
            CardAdapter(state.field.map { it.drawableRes }) { }

        refreshUI()
    }

    private fun refreshUI() {
        // Update player's hand
        cardRecycler.adapter = CardAdapter(state.playerHand.map { it.drawableRes }) { clickedResId ->
            val playedCard = state.playerHand.find { it.drawableRes == clickedResId }

            if (playedCard != null && state.currentTurn == Player.HUMAN) {

                // Play card
                GameEngine.playCard(state, playedCard)
                state.playerHand.remove(playedCard)
                selectedCardImage.setImageResource(clickedResId)

                // Update field
                fieldRecycler.adapter =
                    CardAdapter(state.field.map { it.drawableRes }) { }

                // CPU Turn
                GameEngine.switchTurn(state)
                turnIndicator.text = "CPU’s Turn"

                Handler(Looper.getMainLooper()).postDelayed({

                    val aiCard = SimpleOpponent.chooseMove(state)
                    if (aiCard != null) {
                        GameEngine.playCard(state, aiCard)
                        state.opponentHand.remove(aiCard)

                        fieldRecycler.adapter =
                            CardAdapter(state.field.map { it.drawableRes }) { }
                    }

                    // Switch back to player
                    GameEngine.switchTurn(state)
                    turnIndicator.text = "Your Turn"

                    refreshUI()

                }, 800)
            }
        }

        // Keep the top image synced with hand
        if (state.playerHand.isNotEmpty()) {
            selectedCardImage.setImageResource(state.playerHand[0].drawableRes)
        }

        // Update capture count
        captureCount.text =
            "Captured: ${state.playerCaptured.size} / ${state.cpuCaptured.size}"

        // Offer Go or Stop if player has 3+ points
        val currentPlayerScore = GameEngine.calculateScore(state.playerCaptured)
        if (currentPlayerScore >= 3 && state.currentTurn == Player.HUMAN) {
            showGoStopDialog(currentPlayerScore)
        }

        // Round End check
        if (GameEngine.isRoundOver(state)) {
            val finalPlayerScore = GameEngine.calculateScore(state.playerCaptured)
            val finalCpuScore = GameEngine.calculateScore(state.cpuCaptured)

            showRoundEndDialog(finalPlayerScore, finalCpuScore)
            return
        }
    }

   //Round end dialog
    private fun showRoundEndDialog(playerScore: Int, cpuScore: Int) {
        val message = when {
            playerScore > cpuScore -> "You win!\nScore: $playerScore vs $cpuScore"
            cpuScore > playerScore -> "CPU wins!\nScore: $playerScore vs $cpuScore"
            else -> "It's a tie!\nScore: $playerScore vs $cpuScore"
        }

        val dialog = android.app.AlertDialog.Builder(this)
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


    // Go stop dialog

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
}





