package com.practicum.playlistapp.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistapp.R
import com.practicum.playlistapp.domain.repository.ThemeRepository

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        themeRepository = (application as App).themeRepository
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }
        val back = findViewById<Button>(R.id.button_back)
        back.setOnClickListener {
            finish()
        }
        val shareButton = findViewById<Button>(R.id.share)
        shareButton.setOnClickListener {
            val share = Intent(Intent.ACTION_SENDTO)
            val sms = R.string.LinkAndroid
            share.data = Uri.parse("smsto:")
            share.putExtra(Intent.EXTRA_TEXT, getString(sms))
            startActivity(share)
        }
        val writeButton = findViewById<Button>(R.id.writeSup)
        writeButton.setOnClickListener {
            val write = Intent(Intent.ACTION_SENDTO)
            write.data = Uri.parse("mailto:")
            write.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.myEmail)))
            write.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.MessageToDev))
            write.putExtra(Intent.EXTRA_TEXT, getString(R.string.ThanksToDev))
            startActivity(write)
        }
        val agreeButton = findViewById<Button>(R.id.agreementButton)
        agreeButton.setOnClickListener {
            val browse = Intent(Intent.ACTION_VIEW)
            browse.data = Uri.parse(getString(R.string.AndoidOffer))
            startActivity(browse)
        }
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.my_switch)

        themeSwitcher.isChecked = themeRepository.getCurrentTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            themeRepository.setTheme(checked)


        }



    }
}