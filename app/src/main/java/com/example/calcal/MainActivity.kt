package com.example.calcal

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.calcal.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "onCreate 호출됨")

        binding.bottomNavigation.post {
            navController = findNavController(R.id.my_nav)
            binding.bottomNavigation.setupWithNavController(navController)
            binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, navController)
            }
        }


    }

    override fun onStart() {
        super.onStart()

//        navController = findNavController(R.id.my_nav)
//        binding.bottomNavigation.setupWithNavController(navController)

        val callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // If it's not possible to navigate up in the navigation hierarchy, exit the app
                if (!navController.navigateUp()) {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }


}