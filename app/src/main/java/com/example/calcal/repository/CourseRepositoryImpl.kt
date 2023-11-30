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
        courseList: List<CoordinateDTO>,
        result: (Resource<CourseListDTO>) -> Unit
    ) {
        // 코스를 데이터베이스에 저장
        val call : Call<String> = apiService.saveCourseList(courseName, courseList)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    val responseBody: String? = response.body()

                    result.invoke(Resource.Success(CourseListDTO(courseName, courseList)))


                }else{
                    result.invoke(Resource.Error("course 저장 응답 실패"))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                result.invoke(Resource.Error("course 저장 요청 실패"))
            }

        })
    }
}