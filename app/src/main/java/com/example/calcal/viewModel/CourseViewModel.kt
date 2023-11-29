package com.example.calcal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.repository.CourseRepository
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository): ViewModel() {


    private val _getCourse : MutableLiveData<Resource<List<CoordinateDTO>>> = MutableLiveData()

    val getCourse : LiveData<Resource<List<CoordinateDTO>>> get() = _getCourse



    fun saveCourse(courseList:List<CoordinateDTO>){
        viewModelScope.launch {
            _getCourse.value = Resource.Loading
            try{
                repository.saveCourse(courseList) {
                    _getCourse.value = it
                }
            }catch (e:Exception){
                _getCourse.value = Resource.Error(e.message.toString())
            }


        }
    }

}