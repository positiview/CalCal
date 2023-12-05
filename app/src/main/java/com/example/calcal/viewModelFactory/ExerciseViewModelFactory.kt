package com.example.calcal.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.repository.MemberRepository
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModel.MemberViewModel

class ExerciseViewModelFactory(private val repository: ExerciseRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            return ExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}