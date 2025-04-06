package com.practicum.playlistapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val back = findViewById<Button>(R.id.button_back)
        back.setOnClickListener {
            finish()
        }
        val shareButton = findViewById<Button>(R.id.share)
        shareButton.setOnClickListener {
            val share = Intent(Intent.ACTION_SENDTO)
            val sms = R.string.LinkAndroid
            share.data = Uri.parse("smsto:")
            share.putExtra(Intent.EXTRA_TEXT, "sms")
            startActivity(share)
        }
        val writeButton = findViewById<Button>(R.id.writeSup)
        writeButton.setOnClickListener {
            val write = Intent(Intent.ACTION_SENDTO)
            write.data = Uri.parse("mailto:")
            write.putExtra(Intent.EXTRA_EMAIL, arrayOf("Alexpopov06@mail.ru"))
            write.putExtra(Intent.EXTRA_SUBJECT, R.string.MessageToDev)
            write.putExtra(Intent.EXTRA_TEXT, R.string.ThanksToDev)
            startActivity(write)
        }
        val agreeButton = findViewById<Button>(R.id.agreementButton)
        agreeButton.setOnClickListener {
            val browse = Intent(Intent.ACTION_VIEW)
            browse.data = Uri.parse(getString(R.string.AndoidOffer))
            startActivity(browse)
        }




    }
}