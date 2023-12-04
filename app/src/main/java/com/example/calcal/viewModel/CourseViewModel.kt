package com.example.calcal.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.R
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.repository.CourseRepository
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch
import java.util.Collections.addAll

class CourseViewModel(private val repository: CourseRepository): ViewModel() {


    private val _getCourse : MutableLiveData<Resource<List<CourseListDTO>?>> = MutableLiveData()

    val getCourse : LiveData<Resource<List<CourseListDTO>?>> get() = _getCourse

    private val _getPlaceList : MutableLiveData<CourseListDTO> = MutableLiveData()

    val getPlaceList : LiveData<CourseListDTO> get() =_getPlaceList




    fun saveCourse(courseName:String, placeList:List<CoordinateDTO>){
        _getPlaceList.value = CourseListDTO(cid = 0,courseName,placeList)
        Log.d("$$","saveCourse 에 접근")
        viewModelScope.launch {

            try{
                repository.saveCourse(courseName, placeList) {

                   /* val currentResource: Resource<List<CourseListDTO>?> = _getCourse.value ?: Resource.Success(emptyList())
                    val currentList: List<CourseListDTO>? =
                        when (currentResource) {
                        is Resource.Success -> currentResource.data
                        else -> emptyList()
                    }

                    val transformedResource: Resource<List<CourseListDTO>> = when (it) {
                        is Resource.Success -> {
                            // Success인 경우, 데이터를 리스트에 감싸서 새로운 Success를 생성

                            Resource.Success( (currentList ?: emptyList()) + it.data)
                        }
                        is Resource.Error -> {
                            // Error인 경우, 그대로 전달
                            Resource.Error(it.string)
                        }
                        is Resource.Loading -> {
                            Resource.Loading
                        }
                    }

                    _getCourse.value = transformedResource*/
                }
            }catch (e:Exception){
                _getCourse.value = Resource.Error(e.message.toString())
            }


        }
    }

    fun getCourse() {
        viewModelScope.launch {
            _getCourse.value = Resource.Loading // 로딩 상태 설정

            repository.getCourses() { result ->
                when (result) {
                    is Resource.Success -> {
                        _getCourse.value = result // 코스 데이터를 LiveData에 설정
                    }
                    is Resource.Error -> {
                        _getCourse.value = result // 에러 처리
                    }

                    else -> {}
                }
            }
        }
    }

    fun delete(cid:Long){
        viewModelScope.launch {
            _getCourse.value = Resource.Loading
            try{
                repository.getCourses(){
                    _getCourse.value = it
                }
            }catch (e:Exception){
                _getCourse.value = Resource.Error(e.message.toString())
            }
        }
    }

    fun getPlaceList(selectedPlaceList:CourseListDTO){
        _getPlaceList.value = selectedPlaceList
    }

}