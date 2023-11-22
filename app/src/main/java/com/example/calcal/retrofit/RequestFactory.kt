package com.example.calcal.retrofit

import com.example.calcal.request.ApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RequestFactory {

//    private const val baseUrl = "http://10.100.203.32:8080" //해명 주소
    private const val baseUrl = "http://10.100.203.52:8080" //연주 주소


    fun create():ApiService{
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())) // <<-- JSON으로 바꿔주는 부분, 유심히 관찰 할것. 오류 발생 가능성 있음
                .build()
        return retrofit.create(ApiService::class.java)
    }

}