package com.example.calcal.repository


import com.example.calcal.signlogin.MemberDTO
import com.example.calcal.util.Resource


interface MemberRepository{
    suspend fun saveMember(memberDTO: MemberDTO, result: (Resource<Boolean>)->Unit)

    suspend fun getMember(result: (MemberDTO) -> Unit)
}