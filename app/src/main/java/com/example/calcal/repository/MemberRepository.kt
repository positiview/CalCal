package com.example.calcal.repository


import com.example.calcal.signlogin.MemberDTO
import com.example.calcal.util.Resource


interface MemberRepository{
    suspend fun saveMember(memberDTO: MemberDTO, result: (Resource<Boolean>)->Unit)

    suspend fun getMember(email: String, result: (MemberDTO) -> Unit)
    suspend fun updateMember(memberDTO: MemberDTO): MemberDTO
    suspend fun deleteMember(email: String, result: (Resource<Boolean>) -> Unit)
    suspend fun updateGoalCal(email:String, goalcal: Int, result: (String) -> Unit)



}