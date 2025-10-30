package com.practicum.playlistapp.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistapp.R
import com.practicum.playlistapp.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivitySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWindowInsets()
        setupListeners()
        setupThemeSwitcher()
    }

    private fun setupWindowInsets() {
        val rootView = binding.root
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }
    }

    private fun setupListeners() {

        val shareButton = binding.share
        shareButton.setOnClickListener {
            startActivity(viewModel.shareLink())
        }

        val writeButton = binding.writeSup
        writeButton.setOnClickListener {
            startActivity(viewModel.writeSupport())
        }

        val agreeButton = binding.agreementButton
        agreeButton.setOnClickListener {
            startActivity(viewModel.agreement())
        }
    }

    private fun setupThemeSwitcher() {
        val themeSwitcher = binding.mySwitch
        themeSwitcher.isChecked = viewModel.isDarkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.setTheme(checked)
        }
    }
}