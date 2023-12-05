package com.example.calcal.repository

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.GenderActivity
import com.example.calcal.signlogin.MemberDTO
import com.example.calcal.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberRepositoryImpl: MemberRepository {

    private val apiService = RequestFactory.create()
    override suspend fun saveMember(memberDTO: MemberDTO, result: (Resource<Boolean>) -> Unit) {
        val call: Call<String> = apiService.memberData(memberDTO)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("$$","onResponse 응답 response : $response")
                if (response.isSuccessful) {
                    // 서버 응답이 성공적으로 받아졌을 때
                    val responseBody: String? = response.body()

                    result.invoke(Resource.Success(true))



                } else {
                    // 서버 응답이 실패했을 때
                    result.invoke(Resource.Success(false))
                    Log.d("$$", "onResponse 실패 response : ${response.code()}")


                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                result.invoke(Resource.Error("서버 전송 실패"))
                Log.d("$$","onFailure 발생")
            }
        })
    }

    override suspend fun getMember(result: (MemberDTO) -> Unit) {

        TODO("Not yet implemented")
    }
}