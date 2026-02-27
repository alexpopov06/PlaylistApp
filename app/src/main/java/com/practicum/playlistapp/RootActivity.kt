package com.practicum.playlistapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistapp.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private var isKeyboardVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupKeyboardListener()
        setupNavController()
    }

    private fun setupNavController() {
        val navHost = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView)
                as NavHostFragment

        val navController = navHost.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.playerFragment || destination.id == R.id.createPlaylistFragment) hideBottomNav()
            else if (!isKeyboardVisible) showBottomNav()
        }
    }

    private fun setupKeyboardListener() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->

            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            val keyboardNowVisible = imeHeight > 0

            if (keyboardNowVisible != isKeyboardVisible) {
                isKeyboardVisible = keyboardNowVisible
                if (keyboardNowVisible) hideBottomNav()
                else showBottomNav()
            }

            insets
        }
    }

    private fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
        binding.divider.visibility = View.GONE
    }

    private fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.divider.visibility = View.VISIBLE
    }
}
