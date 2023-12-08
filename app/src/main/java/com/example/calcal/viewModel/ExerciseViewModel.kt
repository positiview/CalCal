package com.example.calcal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.signlogin.MemberDTO
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {
    private val _exerciseList: MutableLiveData<Resource<ExerciseDTO>> = MutableLiveData()
    val exerciseList: LiveData<Resource<ExerciseDTO>> get() = _exerciseList

    private val _saveSuccess: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val saveSuccess: LiveData<Resource<Boolean>> get() = _saveSuccess


//    init {
//        _exerciseList.value = repository.getExercise()
//    }

    fun saveExercise(exercise: ExerciseDTO) {
        _saveSuccess.value = Resource.Loading
        viewModelScope.launch {
            try {
                repository.saveExercise(exercise) {
                    _saveSuccess.value = it // repository안에서 Resource에 담아 가져옴
                }
            } catch (e: Exception) {
                _saveSuccess.value = Resource.Error(e.message.toString())
            }
        }


//        fun setExerciseList(exercises: Resource<ExerciseDTO>) {
//            _exerciseList.value = exercises
//        }

    }
}