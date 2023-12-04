package com.example.calcal.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calcal.repository.MemberRepository
import com.example.calcal.viewModel.MemberViewModel

class MemberViewModelFactory(private val repository: MemberRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            return MemberViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}