package com.example.calcal.repository

import android.util.Log
import com.example.calcal.modelDTO.CalDTO
import com.example.calcal.modelDTO.ExRecordDTO
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.retrofit.RequestFactory
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
        goalCalorie:Double,
        calorie:Double,
        distance:String,
        result: (Resource<String>) -> Unit
    ) {

        Log.d("$$","saveRecord 저장 : myRouteRecords = $myRouteRecords // courseName = $courseName // storedEmail = $email")
        val call : Call<String> = apiService.saveRouteRecord(myRouteRecords,email,courseName,goalCalorie, calorie, distance)

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
        val getCall: Call<List<RouteRecordDTO>> = apiService.getRouteRecord(email)

        Log.d("$$", "getRecord: Making network request...")

        getCall.enqueue(object : Callback<List<RouteRecordDTO>> {
            override fun onResponse(
                call: Call<List<RouteRecordDTO>>,
                response: Response<List<RouteRecordDTO>>
            ) {
                Log.d("$$", "getRecord: onResponse called")

                if (response.isSuccessful) {
                    Log.d("$$", "getRecord: Successful response received")
                    result.invoke(Resource.Success(response.body()))
                    Log.d("$$", "내 기록 불러오기 성공")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage =
                        "내 기록 불러오기 실패!! 응답코드: ${response.code()}, 오류 내용: $errorBody"
                    Log.d("$$", errorMessage)
                    result.invoke(Resource.Error("기록 불러오기 관련 응답 실패"))
                }
            }

            override fun onFailure(call: Call<List<RouteRecordDTO>>, t: Throwable) {
                Log.e("$$", "getRouteRecord: Network request failed", t)
                result.invoke(Resource.Error("기록 불러오기 관련 요청 실패"))
            }

        })
    }

    override suspend fun saveExRecord(
        exRecords: List<ExRecordDTO>,
        email: String,
        exname: String,
        goalCalorie: Double,
        calorie: Double,
        result: (Resource<String>) -> Unit
    ) {
        Log.d("$$","saveExRecord 함수가 호출되었습니다.")
        val call : Call<String> = apiService.saveExRecord(exRecords,email,exname,goalCalorie, calorie)

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

    override suspend fun getExRecord(
        email: String,
        result: (Resource<List<ExRecordDTO>?>) -> Unit
    ) {
        val getCall: Call<List<ExRecordDTO>> = apiService.getExRecord(email)

        Log.d("$$", "getRecord: Making network request...")

        getCall.enqueue(object : Callback<List<ExRecordDTO>> {
            override fun onResponse(
                call: Call<List<ExRecordDTO>>,
                response: Response<List<ExRecordDTO>>
            ) {
                Log.d("$$", "getRecord: onResponse called")

                if (response.isSuccessful) {
                    Log.d("$$", "getRecord: Successful response received")
                    result.invoke(Resource.Success(response.body()))
                    Log.d("$$", "내 기록 불러오기 성공")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage =
                        "내 기록 불러오기 실패!! 응답코드: ${response.code()}, 오류 내용: $errorBody"
                    Log.d("$$", errorMessage)
                    result.invoke(Resource.Error("기록 불러오기 관련 응답 실패"))
                }
            }


            override fun onFailure(call: Call<List<ExRecordDTO>>, t: Throwable) {
                Log.e("$$", "getExRecord: Network request failed", t)
                result.invoke(Resource.Error("기록 불러오기 관련 요청 실패"))
            }

        })
    }


    override suspend fun getTodayRecord(email: String, result: (Resource<List<CalDTO>?>) -> Unit) {

        val getTodayCall: Call<List<CalDTO>> = apiService.getTodayRecord(email)

        getTodayCall.enqueue(object : Callback<List<CalDTO>>{
            override fun onResponse(call: Call<List<CalDTO>>, response: Response<List<CalDTO>>) {
                if(response.isSuccessful){
                    Log.d("$$", "오늘의 기록 불러오기 성공")
                    Log.d("$$","${response.body()}")
                    result.invoke(Resource.Success(response.body()))
                }else{
                    val errorBody = response.errorBody()?.string()
                    val errorMessage =
                        "오늘의 기록 불러오기 실패!! 응답코드: ${response.code()}, 오류 내용: $errorBody"
                    Log.d("$$", errorMessage)
                    result.invoke(Resource.Error("오늘의 기록 불러오기 관련 응답 실패"))
                }
            }

            override fun onFailure(call: Call<List<CalDTO>>, t: Throwable) {
                result.invoke(Resource.Error("오늘의 기록 불러오기 관련 요청 실패"))
            }
        })

    }
}