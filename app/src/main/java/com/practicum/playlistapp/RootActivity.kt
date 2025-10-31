package com.practicum.playlistapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistapp.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment -> hideBottomNavigation()
                else -> showBottomNavigation()
            }
        }
    }

    fun hideBottomNavigation() {
        binding.bottomNavigationView.visibility = View.GONE
        binding.divider.visibility = View.GONE


        val params = binding.rootFragmentContainerView.layoutParams as ConstraintLayout.LayoutParams
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        binding.rootFragmentContainerView.layoutParams = params
    }

    fun showBottomNavigation() {
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.divider.visibility = View.VISIBLE

        // Восстанавливаем оригинальные констрейнты
        val params = binding.rootFragmentContainerView.layoutParams as ConstraintLayout.LayoutParams
        params.bottomToTop = R.id.divider
        binding.rootFragmentContainerView.layoutParams = params
    }
}