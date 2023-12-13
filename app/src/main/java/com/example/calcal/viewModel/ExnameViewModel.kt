package com.example.calcal.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calcal.util.Resource

class ExnameViewModel: ViewModel() {

    private val _saveSuccess: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    val saveSuccess: LiveData<Resource<Boolean>> get() = _saveSuccess

    // 저장 중인 상태를 나타내는 LiveData를 추가
    private val _isSaving: MutableLiveData<Boolean> = MutableLiveData()
    val isSaving: LiveData<Boolean> get() = _isSaving

    private val _selectedItem = MutableLiveData<String?>()
    val selectedItem: LiveData<String?> get() = _selectedItem

    fun setSelectedItem(item: String) {
        _isSaving.value = true // 저장 시작 시점에 _isSaving을 true로 설정
        _selectedItem.value = item
        _isSaving.value = false // 저장 완료 시점에 _isSaving을 false로 설정
    }
}