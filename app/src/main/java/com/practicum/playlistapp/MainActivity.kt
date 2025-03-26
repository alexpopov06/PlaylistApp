package com.practicum.playlistapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)


        val but1 = findViewById<Button>(R.id.pos1)

        but1.setOnClickListener {
            val displayIntent = Intent(this, Search::class.java)
            startActivity(displayIntent)
        }
        val but2 = findViewById<Button>(R.id.pos2)
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val displayIntent = Intent(this@MainActivity, MediatekActivity::class.java)
                startActivity(displayIntent)
            }
        }

        but2.setOnClickListener(imageClickListener)
        val but3 = findViewById<Button>(R.id.pos3)

        but3.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }


    }
}