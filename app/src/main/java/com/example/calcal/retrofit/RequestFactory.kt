package com.example.calcal.retrofit

import com.example.calcal.request.ApiService
import com.example.calcal.signlogin.MemberDTO
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object RequestFactory {


//    private const val baseUrl = "http://10.100.203.32:8080" //해명 주소
//    private const val baseUrl = "http://10.100.203.52:8080" //연주 주소
    private const val baseUrl = "http://10.100.203.53:8080" //용성 주소

    private const val naverMapUrl = "https://naveropenapi.apigw.ntruss.com/"

    private const val localSearch = "https://openapi.naver.com/"

    // 서버 연결
    fun create():ApiService{
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())) // <<-- JSON으로 바꿔주는 부분, 유심히 관찰 할것. 오류 발생 가능성 있음
                .build()
        return retrofit.create(ApiService::class.java)
    }

    // 주소 검색, 지도 연결
    fun create2():ApiService{
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(naverMapUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create())) // <<-- JSON으로 바꿔주는 부분, 유심히 관찰 할것. 오류 발생 가능성 있음
            .build()
        return retrofit.create(ApiService::class.java)
    }


    // 지역 검색
    fun create3():ApiService{
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(localSearch)
            .addConverterFactory(GsonConverterFactory.create()) // <<-- JSON으로 바꿔주는 부분, 유심히 관찰 할것. 오류 발생 가능성 있음
            .build()
        return retrofit.create(ApiService::class.java)
    }



}