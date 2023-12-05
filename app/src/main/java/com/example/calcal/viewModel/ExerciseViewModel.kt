package com.example.calcal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.repository.MemberRepository

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exname = MutableLiveData<String>()
    val exname: LiveData<String> get() = _exname

    init {
        _exname.value = repository.getExercise()
    }

    fun updateExname(exname: String) {
        _exname.value = exname
    }
}