package com.example.calcal

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.calcal.databinding.ActivityMainBinding
import com.example.calcal.mainFrag.CalendarFragment
import com.example.calcal.mainFrag.GraphFragment
import com.example.calcal.mainFrag.MainFragment
import com.example.calcal.mainFrag.MypageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.my_nav)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        bottomNavigationView.setupWithNavController(navController)

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
//    override fun onBackPressed() {
//        if (!findNavController(R.id.my_nav).navigateUp()) {
//            super.onBackPressed()
//        }
//    }


}