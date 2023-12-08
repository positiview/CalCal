package com.example.calcal.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.repository.RecordRepository
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch

class RecordViewModel(private val repository: RecordRepository):ViewModel() {

    private val _getRecord : MutableLiveData<Resource<List<RouteAndTimeDTO>>> = MutableLiveData()

    val getRecord : LiveData<Resource<List<RouteAndTimeDTO>>> get() = _getRecord

    private val _getRecentRecord : MutableLiveData<List<RouteAndTimeDTO>> = MutableLiveData()

    val getRecentRecord : LiveData<List<RouteAndTimeDTO>> get() = _getRecentRecord

    fun saveRecord(listRecord : List<RouteAndTimeDTO>, courseName: String, email: String){
        viewModelScope.launch {
            Log.d("$$","saveRecord ViewModel")
            _getRecord.value = Resource.Loading
            try{
                _getRecentRecord.value = listRecord
                repository.saveRecord(listRecord,courseName, email){
                    _getRecord.value = it
                }
            }catch (e:Exception){
                _getRecord.value = Resource.Error(e.message.toString())
            }
        }

    }

    fun getRecord(){
        Log.d("$$","getRecord 실행")
        viewModelScope.launch {
            _getRecord.value = Resource.Loading
            try{
                repository.getRecord(){
                    _getRecord.value = it
                }
            }catch (e:Exception){
                _getRecord.value = Resource.Error(e.message.toString())
            }
        }
    }


}