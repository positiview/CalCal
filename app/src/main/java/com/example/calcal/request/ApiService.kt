package com.example.calcal.request

import com.example.calcal.modelDTO.ChannelDTO
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.modelDTO.DataDTO
import com.example.calcal.modelDTO.DirectionResponseDTO
import com.example.calcal.modelDTO.FeatureCollection
import com.example.calcal.modelDTO.NaverGeocodingResponseDTO
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.modelDTO.TMapRouteRequest
import com.example.calcal.modelDTO.TestDTO
import com.example.calcal.signlogin.MemberDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
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


    @POST("course/save")
    fun saveCourseList(
        @Query("courseName") courseName: String,
        @Body courseList: List<CoordinateDTO>
    ): Call<String>

    @GET("course/getList")
    fun getCourseList():Call<List<CourseListDTO>>

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

    @GET("map-direction/v1/driving")
    fun directions5(
        @Query("start") start:String,
        @Query("end") end:String,
        @Query("waypoints") waypoints:String
    ):Call<DirectionResponseDTO>

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

    @POST("routes/pedestrian")
    fun pedestrianRoute(
        @Query("version") version: String,
//        @Query("format") format:String,
        @Body data: DataDTO,
        @Header("appKey") appKey:String
    ): Call<FeatureCollection>

    @GET("routes/pedestrian")
    fun getPedestrianPath(
        @Query("version") version: String,
        @Query("format") format: String,
        @Query("appKey") appKey: String,
        @Query("startX") startX: Double,
        @Query("startY") startY: Double,
        @Query("endX") endX: Double,
        @Query("endY") endY: Double
    ): Call<FeatureCollection>


    @POST("routes/pedestrian")
    fun getPedestrianRoute(
        @Header("appKey") appKey: String,
        @Body request: TMapRouteRequest,
        @Query("version") version: String
    ): Call<FeatureCollection>
}