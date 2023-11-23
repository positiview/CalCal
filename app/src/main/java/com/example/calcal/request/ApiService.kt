package com.example.calcal.request

import com.example.calcal.modelDTO.NaverAddressResponseDTO
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

    @GET("map-geocode/v2/geocode")
    fun geocode(
        @Query("query") query: String,
        @Header("X-NCP-APIGW-API-KEY-ID") apiKeyId: String,
        @Header("X-NCP-APIGW-API-KEY") apiKey: String
    ): Call<NaverAddressResponseDTO>

}