package com.example.nba_team_rand_gen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : View.OnClickListener, AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main)

        if (supportActionBar != null) {
            supportActionBar?.elevation = 10F
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.action_bar_gradient, theme)
            supportActionBar?.setBackgroundDrawable(drawable)
        }

        val randbtn = findViewById<Button>(R.id.random_button)


        val type = findViewById<Spinner>(R.id.choose_type)
        val options = arrayOf("All", "Current", "Classic", "All-time")
        var finalType = ""
        if (type != null) {
            val adapter = ArrayAdapter(this,
                R.layout.spinner_list, options)
            adapter.setDropDownViewResource(R.layout.spinner_list)
            type.adapter = adapter

            type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                    finalType = options[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>) {
                    Toast.makeText(this@MainActivity, "nothing selected", Toast.LENGTH_LONG).show()
                }
            }
        }

        val gametype = findViewById<Spinner>(R.id.choose_game_type)
        val gameoptions = arrayOf("1vs1", "2vs2", "3vs3", "4vs4", "5vs5")
        var finalGame = ""
        if(gametype != null) {
            val adapter = ArrayAdapter(this, R.layout.spinner_list, gameoptions)
            adapter.setDropDownViewResource(R.layout.spinner_list)
            gametype.adapter = adapter

            gametype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    finalGame = gameoptions[p2]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Toast.makeText(this@MainActivity, "nothing selected", Toast.LENGTH_LONG).show()
                }
            }
        }

        randbtn.setOnClickListener{
            val intent = Intent(this, ShowPlayer::class.java)
            startActivity(intent)
            val randomizeGame = RandomizeGame(this)
            randomizeGame.Randomize(finalType, finalGame)
        }

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}