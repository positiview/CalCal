package com.example.calcal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.repository.MemberRepository

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exerciseList = MutableLiveData<List<ExerciseDTO>>()
    val exerciseList: LiveData<List<ExerciseDTO>> get() = _exerciseList

//    init {
//        _exerciseList.value = repository.getExercise()
//    }

    fun setExerciseList(exercises: List<ExerciseDTO>) {
        _exerciseList.value = exercises
    }
}