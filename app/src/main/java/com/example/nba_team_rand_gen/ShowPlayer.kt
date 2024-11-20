package com.example.nba_team_rand_gen

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ShowPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_player)

        val backbtn = findViewById<Button>(R.id.back_btn)
        backbtn.setOnClickListener{
            finish()
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setElevation(10F);
            getSupportActionBar()?.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_gradient))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}