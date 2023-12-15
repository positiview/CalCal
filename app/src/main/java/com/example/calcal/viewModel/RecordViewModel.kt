package com.example.calcal.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.modelDTO.CalDTO
import com.example.calcal.modelDTO.ExRecordDTO
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.repository.RecordRepository
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch

class RecordViewModel(private val repository: RecordRepository):ViewModel() {

    private val _getRecord : MutableLiveData<Resource<List<RouteRecordDTO>?>> = MutableLiveData()

    val getRecord : LiveData<Resource<List<RouteRecordDTO>?>> get() = _getRecord


    private val _getSelectedRecord : MutableLiveData<List<RouteAndTimeDTO>> = MutableLiveData()

    val getSelectedRecord : LiveData<List<RouteAndTimeDTO>> get() = _getSelectedRecord

    //exRecord

    private val _getExRecord : MutableLiveData<Resource<List<ExRecordDTO>?>> = MutableLiveData()

    val getExRecord : LiveData<Resource<List<ExRecordDTO>?>> get() = _getExRecord

    private val _getSelectedExRecord : MutableLiveData<List<ExRecordDTO>> = MutableLiveData()
    val getSelectedExRecord : LiveData<List<ExRecordDTO>> get() = _getSelectedExRecord

    private val _successfulSave: MutableLiveData<Resource<String>> = MutableLiveData()

    val successfulSave :LiveData<Resource<String>> get() = _successfulSave

    private val _getTodayRecord: MutableLiveData<Resource<Map<String, List<CalDTO>>?>> = MutableLiveData()

    val getTodayRecord: LiveData<Resource<Map<String, List<CalDTO>>?>> get() = _getTodayRecord

    fun saveRecord(listRecord : List<RouteAndTimeDTO>, courseName: String, email: String, goalCalorie:Double, calorie:Double, distance:String){

        viewModelScope.launch {
            Log.d("$$","saveRecord ViewModel")
            _getSelectedRecord.value = listRecord

            _successfulSave.value = Resource.Loading
            try{

                repository.saveRecord(listRecord,courseName, email, goalCalorie,calorie, distance){
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

    fun getSelectedRecord(ratList : List<RouteAndTimeDTO>){
        _getSelectedRecord.value = ratList
    }


    fun getTodayRecord(email:String){
        viewModelScope.launch {
            _getTodayRecord.value = Resource.Loading
            try{
                repository.getTodayRecord(email){
                    _getTodayRecord.value = it
                }
            }catch (e:Exception){
                _getRecord.value = Resource.Error(e.message.toString())
            }
        }
    }
    fun saveExRecord(exRecord: List<ExRecordDTO>, email: String, exname: String, goalCalorie: Double, calorie: Double) {

        viewModelScope.launch {
            Log.d("$$", "saveRecord ViewModel")

            _successfulSave.value = Resource.Loading

            try {
                repository.saveExRecord(exRecord, email, exname, goalCalorie, calorie) { response ->
                    _successfulSave.value = response
                }
            } catch (e: Exception) {
                _successfulSave.value = Resource.Error(e.message.toString())
                // 오류 상황에서 추가 작업 수행
            }
        }
    }

    fun getExRecord(email: String){
        Log.d("$$","getExRecord 실행")
        viewModelScope.launch {
            _getExRecord.value = Resource.Loading
            try{
                repository.getExRecord(email){
                    _getExRecord.value = it
                }
            }catch (e:Exception){
                _getExRecord.value = Resource.Error(e.message.toString())
            }
        }
    }

    fun getSelectedExRecord(ratList : List<ExRecordDTO>){
        _getSelectedExRecord.value = ratList
    }


}