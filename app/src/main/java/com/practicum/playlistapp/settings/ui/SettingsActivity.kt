package com.practicum.playlistapp.settings.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistapp.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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
            startActivity(viewModel.shareLink())
        }

        val writeButton = findViewById<Button>(R.id.writeSup)
        writeButton.setOnClickListener {
            startActivity(viewModel.writeSupport())
        }

        val agreeButton = findViewById<Button>(R.id.agreementButton)
        agreeButton.setOnClickListener {
            startActivity(viewModel.agreement())
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.my_switch)
        themeSwitcher.isChecked = viewModel.isDarkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setTheme(checked)
        }
    }
}