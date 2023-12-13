package com.example.calcal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calcal.util.Resource

class TargetCalViewModel : ViewModel() {


    private val _saveSuccess: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val saveSuccess: LiveData<Resource<Boolean>> get() = _saveSuccess


    // 운동 목표 칼로리 저장용
    private val _getTargetCalorie = MutableLiveData<Double?>()
    val getTargetCalorie: LiveData<Double?> get() = _getTargetCalorie
    val userInput = MutableLiveData<Double>()


    val calculatedValue = MutableLiveData<String>()

    fun calculate(userInput: Double, excalValue: Int) {
        val result = userInput / excalValue
        calculatedValue.value = "$result"
    }

    fun getTargetCalorie(item: Double){
        _getTargetCalorie.value = item
    }
}