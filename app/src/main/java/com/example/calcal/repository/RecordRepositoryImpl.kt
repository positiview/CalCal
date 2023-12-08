package com.example.calcal.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.calcal.modelDTO.RouteAndTimeDTO
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
        result: (Resource<List<RouteAndTimeDTO>>) -> Unit
    ) {

        Log.d("$$","saveRecord 저장 : myRouteRecords = $myRouteRecords // courseName = $courseName // storedEmail = $email")
        val call : Call<String> = apiService.saveRouteRecord(myRouteRecords,email,courseName)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    result.invoke(Resource.Success(myRouteRecords))
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

    override suspend fun getRecord(result: (Resource<List<RouteAndTimeDTO>>) -> Unit) {


    }
}