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

    // DB에서 사용자별 루트 검색 기록
    private val _getCourse : MutableLiveData<Resource<List<CourseListDTO>?>> = MutableLiveData()

    val getCourse : LiveData<Resource<List<CourseListDTO>?>> get() = _getCourse


    // 경로 루트들 + 이름
    private val _getPlaceList : MutableLiveData<CourseListDTO> = MutableLiveData()

    val getPlaceList : LiveData<CourseListDTO> get() =_getPlaceList




    fun saveCourse(course_no: Long, email: String, courseName:String, placeList:List<CoordinateDTO>){
        _getPlaceList.value = CourseListDTO(course_no, email,courseName,placeList)
        Log.d("$$","saveCourse 에 접근")
        viewModelScope.launch {

            try{
                repository.saveCourse(email, courseName, placeList) {
                }
            }catch (e:Exception){
                _getCourse.value = Resource.Error(e.message.toString())
            }


        }
    }

    fun getCourse(course_no: Long, userEmail: String) {
        viewModelScope.launch {
            _getCourse.value = Resource.Loading // 로딩 상태 설정

            repository.getCourses(course_no, userEmail) { result ->

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

    fun deleteCourse(course_no: Long, param: (Any) -> Unit){


        viewModelScope.launch {
            _getCourse.value = Resource.Loading
            try{
                repository.deleteCourse(course_no){

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