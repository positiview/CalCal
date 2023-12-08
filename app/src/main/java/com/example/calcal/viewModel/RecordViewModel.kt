package com.example.calcal.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.repository.RecordRepository
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch

class RecordViewModel(private val repository: RecordRepository):ViewModel() {

    private val _getRecord : MutableLiveData<Resource<List<RouteRecordDTO>?>> = MutableLiveData()

    val getRecord : LiveData<Resource<List<RouteRecordDTO>?>> get() = _getRecord

    private val _getRecentRecord : MutableLiveData<List<RouteAndTimeDTO>> = MutableLiveData()

    val getRecentRecord : LiveData<List<RouteAndTimeDTO>> get() = _getRecentRecord

    private val _successfulSave: MutableLiveData<Resource<String>> = MutableLiveData()

    val successfulSave :LiveData<Resource<String>> get() = _successfulSave

    fun saveRecord(listRecord : List<RouteAndTimeDTO>, courseName: String, email: String, calorie:Double){

        viewModelScope.launch {
            Log.d("$$","saveRecord ViewModel")
            _getRecentRecord.value = listRecord

            _successfulSave.value = Resource.Loading
            try{

                repository.saveRecord(listRecord,courseName, email, calorie){
                    _successfulSave.value = it
                }
            }catch (e:Exception){
                _successfulSave.value = Resource.Error(e.message.toString())
            }
        }

    }

    fun getRecord(email: String){
        Log.d("$$","getRecord 실행")
        viewModelScope.launch {
            _getRecord.value = Resource.Loading
            try{
                repository.getRecord(email){
                    _getRecord.value = it
                }
            }catch (e:Exception){
                _getRecord.value = Resource.Error(e.message.toString())
            }
        }
    }



}