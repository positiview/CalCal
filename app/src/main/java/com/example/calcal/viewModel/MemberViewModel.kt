package com.example.calcal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calcal.repository.MemberRepository

class MemberViewModel(private val repository: MemberRepository) : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    init {
        _email.value = repository.getEmail()
    }

    fun updateEmail(email: String) {
        _email.value = email
    }
}