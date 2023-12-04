package com.example.calcal.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.LoginActivity.Companion.KEY_EMAIL
import com.example.calcal.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecordRepositoryImpl:RecordRepository {

    private val apiService = RequestFactory.create()
    private lateinit var sharedPreferences: SharedPreferences
    override suspend fun saveRecord(
        myRouteRecords: List<RouteAndTimeDTO>,
        result: (Resource<List<RouteAndTimeDTO>>) -> Unit
    ) {
        val storedEmail = sharedPreferences.getString(KEY_EMAIL, "")
        val call : Call<String> = apiService.saveRouteRecord(myRouteRecords,storedEmail?:"")

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    result.invoke(Resource.Success(myRouteRecords))
                    Log.d("$$","내 기록 저장 성공")
                }else{
                    result.invoke(Resource.Error("기록 저장 관련 응답 실패"))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                result.invoke(Resource.Error("기록 저장 실패!!"))
            }
        })



    }

    override suspend fun getRecord(result: (Resource<List<RouteAndTimeDTO>>) -> Unit) {


    }
}