package com.example.calcal.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.repository.CourseRepository
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository): ViewModel() {


    private val _getCourse : MutableLiveData<Resource<CourseListDTO>> = MutableLiveData()

    val getCourse : LiveData<Resource<CourseListDTO>> get() = _getCourse




    fun saveCourse(courseName:String, courseList:List<CoordinateDTO>){
        viewModelScope.launch {
            _getCourse.value = Resource.Loading
            try{
                repository.saveCourse(courseName, courseList) {
                    Log.d("$$","saveCourse 부분 $it")
                    _getCourse.value = it
                }
            }catch (e:Exception){
                _getCourse.value = Resource.Error(e.message.toString())
            }


        }
    }

}