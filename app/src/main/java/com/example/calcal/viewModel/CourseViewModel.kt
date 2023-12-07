package com.example.calcal.viewModel

import android.provider.ContactsContract.CommonDataKinds.Email
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

    // DB에서 사용자별 루트 검색 기록
    private val _getCourse : MutableLiveData<Resource<List<CourseListDTO>?>> = MutableLiveData()

    val getCourse : LiveData<Resource<List<CourseListDTO>?>> get() = _getCourse


    // 경로 루트들 + 이름
    private val _getPlaceList : MutableLiveData<CourseListDTO> = MutableLiveData()

    val getPlaceList : LiveData<CourseListDTO> get() =_getPlaceList




    fun saveCourse(email: String,courseName:String, placeList:List<CoordinateDTO>){
        _getPlaceList.value = CourseListDTO(email,courseName,placeList)

        viewModelScope.launch {

            try{
                repository.saveCourse(email, courseName, placeList) {
                }
            }catch (e:Exception){
                _getCourse.value = Resource.Error(e.message.toString())
            }


        }
    }

    fun getCourse(userEmail : String) {
        viewModelScope.launch {
            _getCourse.value = Resource.Loading // 로딩 상태 설정
            Log.d("$$","getCourse 에 접근")
            repository.getCourses(userEmail) { result ->
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

    fun delete(cid:Double){
        viewModelScope.launch {
            _getCourse.value = Resource.Loading
            try{
                repository.deleteCourse(cid){

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