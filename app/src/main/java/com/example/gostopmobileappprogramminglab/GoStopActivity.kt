package com.example.gostopmobileappprogramminglab

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gostopmobileappprogramminglab.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.example.gostopmobileappprogramminglab.logic.GameEngine
import com.example.gostopmobileappprogramminglab.model.*

class GoStopActivity : AppCompatActivity() {

    private lateinit var state: GameState

    private lateinit var fieldAdapter: CardAdapter
    private lateinit var handAdapter: CardAdapter


    // Card history (last 5 played)
    private val historyCards = mutableListOf<GoStopCard>()
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter


    private lateinit var cardImage: ImageView
    private lateinit var captureCount: TextView
    private lateinit var turnIndicator: TextView

    private lateinit var opponentName: TextView
    private lateinit var opponentImage: ImageView

    private lateinit var cpuBubble: View
    private lateinit var cpuThinkingText: TextView
    private lateinit var cpuSpinner: ProgressBar

    private lateinit var fieldRecycler: RecyclerView
    private lateinit var goStopBar: View
    private lateinit var btnGo: TextView
    private lateinit var btnStop: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_stop)

        cardImage = findViewById(R.id.cardImage)
        captureCount = findViewById(R.id.captureCount)
        turnIndicator = findViewById(R.id.turnIndicator)

        opponentName = findViewById(R.id.opponentName)
        opponentImage = findViewById(R.id.opponentImage)

        cpuBubble = findViewById(R.id.cpuBubble)
        cpuThinkingText = findViewById(R.id.cpuThinkingText)
        cpuSpinner = findViewById(R.id.cpuSpinner)
        cpuBubble.visibility = View.GONE

        fieldRecycler = findViewById(R.id.fieldRecycler)

        goStopBar = findViewById(R.id.goStopBar)
        btnGo = findViewById(R.id.btnGo)
        btnStop = findViewById(R.id.btnStop)
        goStopBar.visibility = View.GONE

        btnGo.setOnClickListener { chooseGo() }
        btnStop.setOnClickListener { chooseStop() }

        state = GameEngine.startNewGame()

        setupOpponent()
        setupFieldRecycler()
        setupHandRecycler()
        setupHistoryRecycler()
        updateUI()
    }

    // Field
    private fun setupFieldRecycler() {
        fieldRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        fieldAdapter = CardAdapter(state.field, selectable = false)
        fieldAdapter.fieldCards = state.field
        fieldRecycler.adapter = fieldAdapter
    }

    // Hand
    private fun setupHandRecycler() {
        val recycler = findViewById<RecyclerView>(R.id.cardRecycler)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        handAdapter = CardAdapter(
            cards = state.playerHand,
            selectable = true,
            onCardSelected = { card -> onPlayerCardSelected(card) }
        )

        handAdapter.fieldCards = state.field
        recycler.adapter = handAdapter
    }

    // HISTORY (last 5 played)

    private fun setupHistoryRecycler() {
        historyRecycler = findViewById(R.id.historyRecycler)

        historyRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        historyAdapter = HistoryAdapter(historyCards)
        historyRecycler.adapter = historyAdapter
    }

    // UI update
    private fun updateUI() {
        handAdapter.fieldCards = state.field
        fieldAdapter.fieldCards = state.field

        handAdapter.notifyDataSetChanged()
        fieldAdapter.notifyDataSetChanged()

        captureCount.text =
            "You: ${state.playerCaptured.size}   CPU: ${state.cpuCaptured.size}"

        turnIndicator.text =
            if (state.currentTurn == Player.HUMAN) "Your Turn"
            else "CPU Turn"

        fieldRecycler.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.slide_in_capture)
        )
    }

    // PLAYER MOVE
    private fun onPlayerCardSelected(card: GoStopCard) {
        if (state.currentTurn != Player.HUMAN) return

        cardImage.setImageResource(card.drawableRes)

        state.playerHand.remove(card)
        GameEngine.playCard(state, card)

        // Add to history
        historyAdapter.addCard(card)

        updateUI()

        if (GameEngine.isRoundOver(state)) {
            endRound()
            return
        }

        if (GameEngine.shouldOfferGoStop(state)) {
            goStopBar.visibility = View.VISIBLE
            return
        }

        state.currentTurn = Player.CPU
        showCpuThinkingUI(true)

        Handler(Looper.getMainLooper()).postDelayed({
            launchCpuMove()
        }, 900)
    }

    // CPU move
    private fun launchCpuMove() {
        lifecycleScope.launch(Dispatchers.Default) {

            val card = GameEngine.cpuPlay(state)

            withContext(Dispatchers.Main) {
                showCpuThinkingUI(false)

                if (card != null) {
                    cardImage.setImageResource(card.drawableRes)
                    historyAdapter.addCard(card)
                }

                updateUI()

                if (GameEngine.isRoundOver(state)) {
                    endRound()
                    return@withContext
                }

                state.currentTurn = Player.HUMAN
                turnIndicator.text = "Your Turn"
            }
        }
    }

    private fun showCpuThinkingUI(show: Boolean) {
        if (show) {
            cpuBubble.visibility = View.VISIBLE
            val pulse = AnimationUtils.loadAnimation(this, R.anim.cpu_pulse)
            opponentImage.startAnimation(pulse)
        } else {
            opponentImage.clearAnimation()
            cpuBubble.visibility = View.GONE
        }
    }

    private fun chooseGo() {
        state.multiplier += 1
        goStopBar.visibility = View.GONE

        state.currentTurn = Player.CPU
        showCpuThinkingUI(true)

        Handler(Looper.getMainLooper()).postDelayed({
            launchCpuMove()
        }, 900)
    }

    private fun chooseStop() {
        val (p, c) = GameEngine.calculateFinalScore(state)

        AlertDialog.Builder(this)
            .setTitle("Final Score")
            .setMessage("You: $p × ${state.multiplier}\nCPU: $c")
            .setPositiveButton("OK") { _, _ -> finish() }
            .show()
    }

    private fun endRound() {
        val (playerScore, cpuScore) = GameEngine.calculateFinalScore(state)

        lifecycleScope.launch {
            val quote = fetchQuote()

            AlertDialog.Builder(this@GoStopActivity)
                .setTitle("Round Over")
                .setMessage("You: $playerScore\nCPU: $cpuScore\n\n$quote")
                .setPositiveButton("OK") { _, _ -> finish() }
                .show()
        }
    }

    private fun setupOpponent() {
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.randomUserApi.getRandomUser()
                val user = resp.results.first()

                opponentName.text =
                    "${user.name.first} ${user.name.last} (${user.dob.age}, ${user.location.country})"

                opponentImage.load(user.picture.large)
            } catch (_: Exception) {
                opponentName.text = "CPU Opponent"
            }
        }
    }

    private suspend fun fetchQuote(): String {
        return try {
            val list = RetrofitClient.quoteApi.getRandomQuote()
            val q = list.first()
            "\"${q.q}\" — ${q.a}"
        } catch (e: Exception) {
            "(Unable to load quote)"
        }
    }
}
