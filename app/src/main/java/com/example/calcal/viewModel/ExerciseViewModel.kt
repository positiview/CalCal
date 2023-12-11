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
    private val _exerciseList: MutableLiveData<Resource<List<ExerciseDTO>>> = MutableLiveData()
    val exerciseList: LiveData<Resource<List<ExerciseDTO>>> get() = _exerciseList

    private val _saveSuccess: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val saveSuccess: LiveData<Resource<Boolean>> get() = _saveSuccess
    private val _selectedItem = MutableLiveData<String?>()
    val selectedItem: LiveData<String?> get() = _selectedItem
    val userInput = MutableLiveData<Double>()
    val calculatedValue = MutableLiveData<String>()

    fun calculate(userInput: Double, excalValue: Int) {
        val result = userInput / excalValue
        calculatedValue.value = "$result"
    }

    fun selectItem(item: String) {
        _selectedItem.value = item
    }


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

    }
    fun getExerciseInfo(exercise: String){
        _exerciseList.value = Resource.Loading
        viewModelScope.launch {
            try{
                repository.getExercise(exercise){ exercises ->
                    _exerciseList.value = Resource.Success(exercises)
                }
            }catch (e:Exception){
                _exerciseList.value = Resource.Error(e.message.toString())
            }
        }
    }
    fun getAllExercises() {
        _exerciseList.value = Resource.Loading

        viewModelScope.launch {
            try {
                _exerciseList.value = repository.getAllExercises()
            } catch (e: Exception) {
                _exerciseList.value = Resource.Error("Error occurred while getting all exercises")
            }
        }
    }
    fun updateExerciseInfo(exerciseDTO: ExerciseDTO) {
        _exerciseList.value = Resource.Loading
        viewModelScope.launch {
            try {
                val updatedMember = repository.updateExercise(exerciseDTO)
                _exerciseList.value = Resource.Success(listOf(updatedMember))
            } catch (e: Exception) {
                _exerciseList.value = Resource.Error(e.message.toString())
            }
        }
    }
}