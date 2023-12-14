package com.example.calcal.retrofit

import com.example.calcal.request.ApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RequestFactory {


    private const val baseUrl = "http://54.180.106.100:8080" //해명 주소
//    private const val baseUrl = "http://10.100.203.52:8080" //연주 주소
//   private const val baseUrl = "http://10.100.203.53:8080" //용성 주소
//    private const val baseUrl ="http://43.200.5.151:8080" // EC2주소

    private const val naverMapUrl = "https://naveropenapi.apigw.ntruss.com/"

    private const val localSearch = "https://openapi.naver.com/"

    private const val tmapUrl = "https://apis.openapi.sk.com/tmap/"

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

    // Tmap 연결 테스트
    fun create4():ApiService{
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(tmapUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

}