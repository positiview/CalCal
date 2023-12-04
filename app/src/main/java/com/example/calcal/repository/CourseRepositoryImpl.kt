package com.example.calcal.repository

import android.util.Log
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseRepositoryImpl: CourseRepository {

    private val apiService = RequestFactory.create()
    override suspend fun saveCourse(
        courseName: String,
        placeList: List<CoordinateDTO>,
        result: (Resource<CourseListDTO>) -> Unit
    ) {
        // 코스를 데이터베이스에 저장
        val call : Call<String> = apiService.saveCourseList(courseName, placeList)
        Log.d("$$", "repository에 접근 placeList = $placeList")
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    val responseBody: String? = response.body()
                    var cid:Long = 0
                    val coordinateCount = placeList.size // CoordinateDTO의 개수를 계산
                    result.invoke(Resource.Success(CourseListDTO(cid,courseName, placeList, coordinateCount))) // coordinateCount를 설정
                    Log.d("$$","코스 저장 성공!!")

                }else{
                    result.invoke(Resource.Error("course 저장 응답 실패"))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                result.invoke(Resource.Error("course 저장 요청 실패"))
            }

        })

    }

    override suspend fun getCourses(result: (Resource<List<CourseListDTO>?>) -> Unit) {
        val call : Call<List<CourseListDTO>> = apiService.getCourseList()

        call.enqueue(object : Callback<List<CourseListDTO>>{
            override fun onResponse(
                call: Call<List<CourseListDTO>>,
                response: Response<List<CourseListDTO>>
            ) {
                if(response.isSuccessful){
                    val responseBody: List<CourseListDTO>? = response.body()
                    Log.d("$$","ResponseBody => $responseBody")
                    result.invoke(Resource.Success(responseBody))
                }else{
                    result.invoke(Resource.Error("courseList 응답 실패"))
                }
            }

            override fun onFailure(call: Call<List<CourseListDTO>>, t: Throwable) {
                result.invoke(Resource.Error("courseList 요청 실패"))
            }
        })
    }

    override suspend fun deleteCourse(num: Int) {
        TODO("Not yet implemented")
    }
}