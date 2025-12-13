package com.example.gostopmobileappprogramminglab

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import android.graphics.Color

import com.example.gostopmobileappprogramminglab.api.QuoteResponse
import com.example.gostopmobileappprogramminglab.api.RandomUserResponse
import com.example.gostopmobileappprogramminglab.api.RetrofitClient

import com.example.gostopmobileappprogramminglab.logic.GameEngine
import com.example.gostopmobileappprogramminglab.model.GameState
import com.example.gostopmobileappprogramminglab.model.GoStopCard
import com.example.gostopmobileappprogramminglab.model.Player
import com.example.gostopmobileappprogramminglab.ai.SimpleOpponent

class GoStopActivity : AppCompatActivity() {

    private lateinit var state: GameState

    private lateinit var fieldAdapter: CardAdapter
    private lateinit var handAdapter: CardAdapter

    private lateinit var cardImage: ImageView
    private lateinit var captureCount: TextView
    private lateinit var turnIndicator: TextView
    private lateinit var opponentName: TextView
    private lateinit var opponentImage: ImageView
    private lateinit var cpuThinkingText: TextView
    private lateinit var cpuSpinner: ProgressBar
    private lateinit var fieldRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_stop)

        cardImage = findViewById(R.id.cardImage)
        captureCount = findViewById(R.id.captureCount)
        turnIndicator = findViewById(R.id.turnIndicator)
        opponentName = findViewById(R.id.opponentName)
        opponentImage = findViewById(R.id.opponentImage)
        cpuThinkingText = findViewById(R.id.cpuThinkingText)
        cpuSpinner = findViewById(R.id.cpuSpinner)
        fieldRecycler = findViewById(R.id.fieldRecycler)

        cpuThinkingText.visibility = View.GONE
        cpuSpinner.visibility = View.GONE

        state = GameEngine.startNewGame()

        setupOpponent()
        setupFieldRecycler()
        setupHandRecycler()
        updateUI()
    }

    // Load random user as cpu

    private fun setupOpponent() {
        lifecycleScope.launch {
            try {
                val response: RandomUserResponse = RetrofitClient.randomUserApi.getRandomUser()
                val user = response.results.first()

                opponentName.text =
                    "${user.name.first} ${user.name.last} (${user.dob.age}, ${user.location.country})"
                opponentImage.load(user.picture.large)

            } catch (e: Exception) {
                opponentName.text = "Opponent: CPU Bot"
            }
        }
    }

    private fun setupFieldRecycler() {
        fieldRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fieldAdapter = CardAdapter(state.field, selectable = false)
        fieldRecycler.adapter = fieldAdapter
    }

    private fun setupHandRecycler() {
        val recycler = findViewById<RecyclerView>(R.id.cardRecycler)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        handAdapter = CardAdapter(state.playerHand, selectable = true) { card ->
            onPlayerCardSelected(card)
        }
        recycler.adapter = handAdapter
    }

    // User turn
    private fun onPlayerCardSelected(card: GoStopCard) {
        if (state.currentTurn != Player.HUMAN) return

        cardImage.setImageResource(card.drawableRes)

        state.playerHand.remove(card)
        GameEngine.playCard(state, card)

        updateUI()

        if (GameEngine.isRoundOver(state)) {
            endRound()
            return
        }

        // CPU turn start
        GameEngine.switchTurn(state)
        showCpuThinkingUI(true)

        Handler(Looper.getMainLooper()).postDelayed({
            launchCpuMove()
        }, 800)
    }

    // CPU Turn
    private fun launchCpuMove() {
        lifecycleScope.launch(Dispatchers.Default) {

            val aiCard = SimpleOpponent.chooseMove(state)

            if (aiCard != null) {
                state.opponentHand.remove(aiCard)
                GameEngine.playCard(state, aiCard)
            }

            // Switches back to ui thread
            withContext(Dispatchers.Main) {
                showCpuThinkingUI(false)
                updateUI()

                if (GameEngine.isRoundOver(state)) {
                    endRound()
                    return@withContext
                }

                GameEngine.switchTurn(state)
                turnIndicator.text = "Your Turn"
                turnIndicator.setTextColor(getColor(R.color.blue))
            }
        }
    }
    // CPU thinking animation

    private fun showCpuThinkingUI(show: Boolean) {
        if (show) {
            turnIndicator.text = "CPU Thinking…"
            turnIndicator.setTextColor(getColor(R.color.red))
            cpuSpinner.visibility = View.VISIBLE
            cpuThinkingText.visibility = View.VISIBLE
        } else {
            cpuSpinner.visibility = View.GONE
            cpuThinkingText.visibility = View.GONE
        }
    }

    // -------------------------
    // UPDATE UI
    // -------------------------
    private fun updateUI() {
        handAdapter.notifyDataSetChanged()
        fieldAdapter.notifyDataSetChanged()

        val pCap = state.playerCaptured.size
        val cCap = state.cpuCaptured.size
        captureCount.text = "Captured: $pCap / $cCap"

        if (state.currentTurn == Player.HUMAN) {
            turnIndicator.text = "Your Turn"
            turnIndicator.setTextColor(getColor(R.color.blue))
        } else {
            turnIndicator.text = "CPU Turn"
            turnIndicator.setTextColor(getColor(R.color.red))
        }
    }


    // round end

    private fun endRound() {
        val (playerScore, cpuScore) = GameEngine.calculateFinalScore(state)

        lifecycleScope.launch {
            val quoteText = fetchQuote()

            AlertDialog.Builder(this@GoStopActivity)
                .setTitle("Round Over")
                .setMessage(
                    """
                    You: $playerScore  
                    CPU: $cpuScore

                    $quoteText
                    """.trimIndent()
                )
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }

    private suspend fun fetchQuote(): String {
        return try {
            val q: QuoteResponse = RetrofitClient.quoteApi.getRandomQuote()
            "\"${q.content}\" - ${q.author}"
        } catch (e: Exception) {
            "(Quote unavailable)"
        }
    }
}

