package com.example.nba_team_rand_gen

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
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
            val input = EditText(this).apply {
                hint = "Enter match name"
                layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
            }
            val container = FrameLayout(this).apply {
                setPadding(50, 20, 50, 0)
                addView(input)
            }

            val dialog = AlertDialog.Builder(this, R.style.Theme_NBA_Team_Rand_Gen_AlertDialog)
                .setTitle("Name your match")
                .setNegativeButton("Return") { d, _ -> d.dismiss() }
                .setPositiveButton("Save", null)
                .setView(container)
                .show()

            val saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveBtn.isEnabled = false

            input.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    saveBtn.isEnabled = !s.isNullOrBlank()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            })

            saveBtn.setOnClickListener {
                val matchName = input.text.toString().trim()

                MatchHistory.uploadMatch(json, matchName)
                    .addOnSuccessListener { code ->
                        if (code == 0) {
                            Toast.makeText(this, "Match \"$matchName\" saved!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Save failed.", Toast.LENGTH_LONG).show()
                        }
                    }
                dialog.dismiss()
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