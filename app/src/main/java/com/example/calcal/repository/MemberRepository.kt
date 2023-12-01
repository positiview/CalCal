package com.example.calcal.repository

import android.content.SharedPreferences
import android.util.Log

class MemberRepository(private val sharedPreferences: SharedPreferences) {
    fun getEmail(): String {
        val email = sharedPreferences.getString("email", "") ?: ""
        Log.d("MemberRepository", "getEmail: $email")
        return email
    }


    fun saveEmail(email: String) {
        with(sharedPreferences.edit()) {
            putString("email", email)
            apply()
        }
    }
}