package com.example.calcal.viewModel

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

    fun saveRecord(listRecord : List<RouteAndTimeDTO>, courseName: String){

        viewModelScope.launch {
            _getRecord.value = Resource.Loading
            try{
                repository.saveRecord(listRecord,courseName){
                    _getRecord.value = it
                }
            }catch (e:Exception){
                _getRecord.value = Resource.Error(e.message.toString())
            }
        }

    }

    fun getRecord(){
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