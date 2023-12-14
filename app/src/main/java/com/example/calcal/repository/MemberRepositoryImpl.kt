package com.example.calcal.repository

import android.util.Log
import com.example.calcal.retrofit.RequestFactory
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


    override suspend fun getMember(email: String, result: (MemberDTO) -> Unit) {
        val call: Call<MemberDTO> = apiService.getMemberData(email) // 사용자 정보를 가져오는 API 호출

        call.enqueue(object : Callback<MemberDTO> {
            override fun onResponse(call: Call<MemberDTO>, response: Response<MemberDTO>) {
                if (response.isSuccessful) {
                    val responseBody: MemberDTO? = response.body()
                    if (responseBody != null) {
                        result(responseBody)
                    } else {
                        Log.d("$$","No user data")
                    }
                } else {
                    Log.d("$$","Failed to get user data")
                }
            }

            override fun onFailure(call: Call<MemberDTO>, t: Throwable) {
                Log.d("$$","Failed to get user data: ${t.message}")
            }
        })
    }

    override suspend fun updateMember(memberDTO: MemberDTO): MemberDTO {
        val call: Call<String> = apiService.updateMemberData(memberDTO)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody: String? = response.body()
                    // responseBody에서 "Success" 등의 값을 확인하거나 원하는 처리를 수행
                } else {
                    // 서버 응답이 실패했을 때의 처리
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // 서버 전송 실패 시의 처리
            }
        })

        return memberDTO
    }

    override suspend fun deleteMember(email: String, result: (Resource<Boolean>) -> Unit) {
        try {
            val response = apiService.deleteMemberData(email)
            if (response.isSuccessful) {
                result(Resource.Success(true))
            } else {
                result(Resource.Error("회원 탈퇴에 실패하였습니다."))
            }
        } catch (e: Exception) {
            result(Resource.Error(e.message ?: "오류가 발생하였습니다."))
        }
    }

    override suspend fun updateGoalCal(email:String, goalcal: Int, result: (String) -> Unit) {

        val goalcalCall = apiService.updateGoalCal(email,goalcal)

        goalcalCall.enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val txt = response.body()
                    Log.d("$$", "목표 칼로리 업데이트 $txt")
                    if (txt != null) {
                        result.invoke(txt)
                    }
                } else {
                    Log.d("$$", "목표 칼로리 업데이트 서버에서 failure")
                }

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("$$", "목표 칼로리 업데이트 전송 오류 발생")
            }
        })


    }

}