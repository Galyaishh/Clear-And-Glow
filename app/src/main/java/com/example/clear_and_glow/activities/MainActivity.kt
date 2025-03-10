package com.example.clear_and_glow.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.clear_and_glow.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container)
        val navController = navHostFragment?.findNavController()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (navController != null) {
            bottomNav.setupWithNavController(navController)
        }
    }
}
