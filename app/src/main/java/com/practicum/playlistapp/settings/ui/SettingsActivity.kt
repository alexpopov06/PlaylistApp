package com.practicum.playlistapp.settings.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistapp.R
import com.practicum.playlistapp.creator.App
import com.practicum.playlistapp.creator.Creator
import com.practicum.playlistapp.settings.domain.repository.ThemeRepository
import com.practicum.playlistapp.sharing.domain.api.SharingInteractor

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeRepository: ThemeRepository
    private lateinit var sharingInteractor: SharingInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        themeRepository = (application as App).themeRepository
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharingInteractor = Creator.provideSharingInteractor()
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
            startActivity(sharingInteractor.shareLink())
        }
        val writeButton = findViewById<Button>(R.id.writeSup)
        writeButton.setOnClickListener {
            startActivity(sharingInteractor.writeSupport())
        }
        val agreeButton = findViewById<Button>(R.id.agreementButton)
        agreeButton.setOnClickListener {
            startActivity(sharingInteractor.agreement())
        }
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.my_switch)

        themeSwitcher.isChecked = themeRepository.getCurrentTheme()
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            themeRepository.setTheme(checked)


        }



    }
}