package com.example.calcal.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calcal.repository.MemberRepository
import com.example.calcal.signlogin.MemberDTO
import com.example.calcal.util.Resource
import kotlinx.coroutines.launch

class MemberViewModel(private val repository: MemberRepository) : ViewModel() {
    /*private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    init {
        _email.value = repository.getEmail()
    }

    fun updateEmail(email: String) {
        _email.value = email
    }*/

    // 유저 정보(MemberDTO)에 관한 모든정보의 업데이트및 로딩은 여기 liveData를 사용할것을 권장
    private val _getMemberInfo : MutableLiveData<Resource<MemberDTO>> = MutableLiveData()

    val getMemberInfo : LiveData<Resource<MemberDTO>> get() = _getMemberInfo

    // 회원 가입 성공 여부를 Boolean으로 알려줌
    private val _saveSuccess : MutableLiveData<Resource<Boolean>> = MutableLiveData()

    val saveSuccess : LiveData<Resource<Boolean>> get() = _saveSuccess


    fun saveMemberInfo(memberDTO: MemberDTO){
        // 회원가입은 viewModel 필요 없음. 직접 repository에서 사용가능.. 하지만 사용함 ㅋ
        _saveSuccess.value = Resource.Loading // viewModelScope에서 데이터가 처리되는 동안 로딩 표시
        viewModelScope.launch {
            try{
                repository.saveMember(memberDTO){
                    _saveSuccess.value = it // repository안에서 Resource에 담아 가져옴
                }
            }catch (e:Exception){
                Resource.Error(e.message.toString())

            }
        }
    }

    fun getMemberInfo(email: String){
        _getMemberInfo.value = Resource.Loading
        viewModelScope.launch {
            try{
                repository.getMember(email){
                    _getMemberInfo.value = it
                }
            }catch (e:Exception){
                _getMemberInfo.value = Resource.Error(e.message.toString())
            }
        }
    }

    fun updateMemberInfo(memberDTO: MemberDTO) {
        _getMemberInfo.value = Resource.Loading
        viewModelScope.launch {
            try {
                val updatedMember = repository.updateMember(memberDTO)
                _getMemberInfo.value = Resource.Success(updatedMember)
            } catch (e: Exception) {
                _getMemberInfo.value = Resource.Error(e.message.toString())
            }
        }
    }




}