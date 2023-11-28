package com.example.calcal.request

import com.example.calcal.modelDTO.ChannelDTO
import com.example.calcal.modelDTO.NaverGeocodingResponseDTO
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.modelDTO.TestDTO
import com.example.calcal.signlogin.MemberDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/tests")
    fun saveData(
        @Body test: TestDTO,
    ): Call<String>

    @POST("api/members")
    fun memberData(
        @Body member: MemberDTO,
    ): Call<String>


    @GET("v1/search/local.json")
    fun searchLocation(
        @Query("query") query: String,
        @Query("display") display: Int,
        @Query("start") start: Int,
        @Query("sort") sort:String,
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientKey: String
    ):Call<ChannelDTO>

    @GET("map-geocode/v2/geocode")
    fun geocode(
        @Query("query") query: String,
        @Query("coordinate") coordinate: String,
        @Header("X-NCP-APIGW-API-KEY-ID") apiKeyId: String,
        @Header("X-NCP-APIGW-API-KEY") apiKey: String
    ): Call<NaverGeocodingResponseDTO>


    @GET("map-reversegeocode/v2/gc")
    fun reverseGeocode(
        @Query("request") request : String,
        @Query("coords") coords: String,
        @Query("sourcecrs") sourcecrs:String,
        @Query("output") output : String,
        @Query("orders") orders: String,
        @Header("X-NCP-APIGW-API-KEY-ID") apiKeyId: String,
        @Header("X-NCP-APIGW-API-KEY") apiKey: String
    ): Call<ReverseGeocodingResponseDTO>


    @POST("api/login")
    fun login(
        @Body member: MemberDTO,
    ): Call<String>

    @POST("api/withdraw")
    fun withdraw(
        @Body member: MemberDTO
    ): Call<String>

}