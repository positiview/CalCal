package com.example.calcal.request

import com.example.calcal.modelDTO.CalDTO
import com.example.calcal.modelDTO.ChannelDTO
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.modelDTO.DataDTO
import com.example.calcal.modelDTO.DirectionResponseDTO
import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.modelDTO.ExRecordDTO
import com.example.calcal.modelDTO.FeatureCollection
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.NaverGeocodingResponseDTO
import com.example.calcal.modelDTO.ReverseGeocodingResponseDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.modelDTO.TestDTO
import com.example.calcal.signlogin.MemberDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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


    @POST("api/exercises")
    fun exerciseData(
        @Body exercise: ExerciseDTO,
    ): Call<String>


    @POST("course/save")
    fun saveCourseList(
        @Query("email") email: String,
        @Query("courseName") courseName: String,
        @Body courseList: List<CoordinateDTO>
    ): Call<String>

    @GET("course/getList")
    fun getCourseList(
        @Query("email") email: String
    ):Call<List<CourseListDTO>>

    @DELETE("course/delete/{course_no}")
    fun deleteCourse(
        @Path("course_no") course_no: Long
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



    @GET("map-direction/v1/driving")
    fun directions5(
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Query("waypoints") waypoints: String,
        @Query("option") option: String,
        @Header("X-NCP-APIGW-API-KEY-ID") apiKeyId: String,
        @Header("X-NCP-APIGW-API-KEY") apiKey: String
    ): Call<DirectionResponseDTO>

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
        @Query("format") format:String,
        @Body data: DataDTO,
        @Header("appKey") appKey:String,
        @Header("Accept") accept: String,
        @Header("Content-Type") contentType: String,
    ): Call<FeatureCollection>



    @POST("record/save")
    fun saveRouteRecord(
        @Body routeRecord: List<RouteAndTimeDTO>,
        @Query("userEmail") userEmail: String,
        @Query("courseName") courseName: String,
        @Query("goalCalorie") goalCalorie: Double,
        @Query("calorie") calorie:Double,
        @Query("distance") distance:String
    ): Call<String>

    @GET("record/history")
    fun getRouteRecord(
        @Query("userEmail") userEmail: String
    ): Call<List<RouteRecordDTO>>


    @GET("record/today")
    fun getTodayRecord(
        @Query("userEmail") userEmail: String
    ): Call<Map<String, List<CalDTO>>>

    @PUT("api/updateMemberData")
    fun updateMemberData(@Body memberDTO: MemberDTO): Call<String>

    @GET("api/getMemberData")
    fun getMemberData(@Query("email") email: String): Call<MemberDTO>

    @GET("api/updateGoalCal")
    fun updateGoalCal(@Query("email") email: String,
                      @Query("goalcal") goalcal: Int): Call<String>

    @DELETE("api/deleteMember/{email}")
    suspend fun deleteMemberData(@Path("email") email: String): Response<Unit>

    @PUT("api/updateExerciseData")
    fun updateExerciseData(@Body exerciseDTO: ExerciseDTO): Call<String>

    @GET("api/getExerciseData")
    fun getExerciseData(@Query("exname") exname: String): Call<ExerciseDTO>

    @GET("api/getAllExercises")
    suspend fun getAllExercises(): List<ExerciseDTO>
    @POST("exrecord/exSave")
    fun saveExRecord(
        @Body exRecord: List<ExRecordDTO>,
        @Query("userEmail") userEmail: String,
        @Query("exName") exName: String,
        @Query("goalCalorie") goalCalorie: Double,
        @Query("calorie") calorie: Double
    ): Call<String>

    @DELETE("api/deleteExercise/{exname}")
    suspend fun deleteExerciseData(@Path("exname") exname: String): Response<Unit>

    @GET("exrecord/get")
    fun getExRecord(email: String): Call<List<ExRecordDTO>>


}