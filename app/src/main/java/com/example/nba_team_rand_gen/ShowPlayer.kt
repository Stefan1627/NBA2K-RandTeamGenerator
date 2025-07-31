package com.example.nba_team_rand_gen


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.serialization.json.Json

class ShowPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_player)

        val json = intent.getStringExtra("teamsJson") ?: "[]"
        val teams: List<PlayerWithTeam> = Json.decodeFromString(json)

        val half = teams.size / 2
        val firstTeam  = teams.subList(0, half)
        val secondTeam = teams.subList(half, teams.size)

        val tvFirst  = findViewById<TextView>(R.id.textView3)
        val tvSecond = findViewById<TextView>(R.id.textView4)

        tvFirst.text = buildString {
            append(getString(R.string.first_team))
            appendLine()
            append(
                firstTeam.joinToString("\n") {
                    "${it.player.player_name}(${it.player.ovr}) - ${it.teamName}"
                }
            )
        }

        tvSecond.text = buildString {
            append(getString(R.string.second_team))
            appendLine()
            append(
                secondTeam.joinToString("\n") {
                    "${it.player.player_name}(${it.player.ovr}) - ${it.teamName}"
                }
            )
        }

        val acceptBtn = findViewById<Button>(R.id.accept_btn)
        acceptBtn.setOnClickListener {
            MatchHistory.uploadMatch(json)
                .addOnSuccessListener { code ->
                    if (code == 0) {
                        Toast.makeText(this, "Match saved to your history!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Save failed.", Toast.LENGTH_LONG).show()
                    }
                }
        }

        val backbtn = findViewById<Button>(R.id.back_btn)
        backbtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        if (supportActionBar != null) {
            supportActionBar?.elevation = 10F
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.action_bar_gradient, theme)
            supportActionBar?.setBackgroundDrawable(drawable)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}