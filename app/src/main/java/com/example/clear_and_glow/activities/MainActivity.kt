package com.example.clear_and_glow.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.clear_and_glow.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the navigation controller
        val navController = findNavController(R.id.main_fragment_container)

        val destination = intent.getStringExtra("navigateTo")
        if (destination == "myProductsFragment") {
            navController.navigate(R.id.myProductsFragment)
        }

        // Setup bottom navigation with navigation controller
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setupWithNavController(navController)
    }
}
