package com.example.calcal.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.LoginActivity.Companion.KEY_EMAIL
import com.example.calcal.signlogin.LoginActivity.Companion.PREF_NAME
import com.example.calcal.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordRepositoryImpl:RecordRepository {

    private val apiService = RequestFactory.create()
    override suspend fun saveRecord(
        myRouteRecords: List<RouteAndTimeDTO>,
        courseName:String,
        email:String,
        calorie:Double,
        result: (Resource<String>) -> Unit
    ) {

        Log.d("$$","saveRecord 저장 : myRouteRecords = $myRouteRecords // courseName = $courseName // storedEmail = $email")
        val call : Call<String> = apiService.saveRouteRecord(myRouteRecords,email,courseName,calorie)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    result.invoke(Resource.Success("성공적으로 기록을 저장했습니다."))
                    Log.d("$$","내 기록 저장 성공")
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "내 기록 저장 실패!! 응답코드: ${response.code()}, 오류 내용: $errorBody"
                    Log.d("$$", errorMessage)
                    result.invoke(Resource.Error("기록 저장 관련 응답 실패"))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("$$","내 기록 응답 실패~~~~")
                result.invoke(Resource.Error("기록 저장 실패!!"))
            }
        })



    }


    override suspend fun getRecord(
        email: String,
        result: (Resource<List<RouteRecordDTO>?>) -> Unit
    ) {
        val getCall:Call<List<RouteRecordDTO>> = apiService.getRouteRecord(email)

        getCall.enqueue(object :Callback<List<RouteRecordDTO>>{
            override fun onResponse(
                call: Call<List<RouteRecordDTO>>,
                response: Response<List<RouteRecordDTO>>
            ) {
                if(response.isSuccessful){
                    result.invoke(Resource.Success(response.body()))
                    Log.d("$$","내 기록 저장 성공")
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "내 기록 불러오기 실패!! 응답코드: ${response.code()}, 오류 내용: $errorBody"
                    Log.d("$$", errorMessage)
                    result.invoke(Resource.Error("기록 불러오기 관련 응답 실패"))
                }

            }

            override fun onFailure(call: Call<List<RouteRecordDTO>>, t: Throwable) {
                result.invoke(Resource.Error("기록 불러오기 관련 요청 실패"))
            }

        })
    }
}