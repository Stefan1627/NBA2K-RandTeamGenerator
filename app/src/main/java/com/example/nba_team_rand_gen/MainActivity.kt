package com.example.nba_team_rand_gen

import TheChosens
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity


class MainActivity : View.OnClickListener, AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setElevation(10F);
            getSupportActionBar()?.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_gradient))
        }

        val randbtn = findViewById<Button>(R.id.random_button)


        val type = findViewById<Spinner>(R.id.choose_type)
        val options = arrayOf("All", "Current", "Classic", "All-time")
        var final_type = ""
        if (type != null) {
            val adapter = ArrayAdapter(this,
                R.layout.spinner_list, options)
            adapter.setDropDownViewResource(R.layout.spinner_list)
            type.adapter = adapter

            type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                    final_type = options[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>) {
                    Toast.makeText(this@MainActivity, "nothing selected", Toast.LENGTH_LONG).show()
                }
            }
        }

        val gametype = findViewById<Spinner>(R.id.choose_game_type)
        val gameoptions = arrayOf("1vs1", "2vs2", "3vs3", "4vs4", "5vs5")
        var final_game = ""
        if(gametype != null) {
            val adapter = ArrayAdapter(this, R.layout.spinner_list, gameoptions)
            adapter.setDropDownViewResource(R.layout.spinner_list)
            gametype.adapter = adapter

            gametype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    final_game = gameoptions[p2]
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
            randomizeGame.Randomize(final_type, final_game)
        }

    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}