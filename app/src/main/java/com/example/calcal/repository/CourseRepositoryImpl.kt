package com.example.calcal.repository

import android.content.Context
import android.util.Log
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.util.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CourseRepositoryImpl(private val context: Context): CourseRepository {

    private val apiService = RequestFactory.create()
    val sharedPreferences =
        context.getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
    val email = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "") ?: ""

    override suspend fun saveCourse(
        email: String,
        courseName: String,
        placeList: List<CoordinateDTO>,
        result: (Resource<CourseListDTO>) -> Unit
    ) {
        // 코스를 데이터베이스에 저장
        val call: Call<String> = apiService.saveCourseList(email, courseName, placeList)
        Log.d("$$", "repository에 접근 placeList = $placeList")
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    var course_no: Long = 0
                    val responseBody: String? = response.body()
                    val sharedPreferences =
                        context.getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
                    val email = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "") ?: ""
                    val coordinateCount = placeList.size // CoordinateDTO의 개수를 계산
                    result.invoke(
                        Resource.Success(
                            CourseListDTO(
                                course_no,
                                email,
                                courseName,
                                placeList
                            )
                        )
                    ) // coordinateCount를 설정
                    Log.d("$$", "코스 저장 성공!!")
                } else {
                    result.invoke(Resource.Error("course 저장 응답 실패"))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.printStackTrace()
                result.invoke(Resource.Error("course 저장 요청 실패: ${t.message}"))
            }
        })
    }

    override suspend fun getCourses(
        course_no: Long,
        email: String,
        result: (Resource<List<CourseListDTO>?>) -> Unit
    ) {
        val call: Call<List<CourseListDTO>> = apiService.getCourseList(email)

        call.enqueue(object : Callback<List<CourseListDTO>> {
            override fun onResponse(
                call: Call<List<CourseListDTO>>,
                response: Response<List<CourseListDTO>>
            ) {
                if (response.isSuccessful) {
                    val responseBody: List<CourseListDTO>? = response.body()
                    Log.d("CourseViewModel", "API 응답 성공: $responseBody")
                    result.invoke(Resource.Success(responseBody))
                } else {
                    Log.e("CourseViewModel", "API 응답 실패: ${response.code()}")
                    result.invoke(Resource.Error("courseList 응답 실패"))
                }
            }

            override fun onFailure(call: Call<List<CourseListDTO>>, t: Throwable) {
                Log.e("CourseViewModel", "API 요청 실패: ${t.message}")
                result.invoke(Resource.Error("courseList 요청 실패"))
            }
        })
    }

    override suspend fun deleteCourse(course_no: Long, result: (String) -> Unit) {
        Log.d("DeleteCourseLog", "서버로의 요청 시작: courseNo = $course_no")
        val call: Call<String> = apiService.deleteCourse(course_no)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("DeleteCourseLog", "서버에서 응답 받음: 삭제 성공")
                    result.invoke(Resource.Success(Unit).toString())

                } else {
                    Log.e("DeleteCourseLog", "서버에서 응답 받음: 삭제 실패, status code = ${response.code()}, message = ${response.message()}")
                    result.invoke(Resource.Error("삭제 실패").toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("DeleteCourseLog", "서버 요청 실패: ${t.message}")
                result.invoke(Resource.Error("요청 실패: ${t.message}").toString())
            }
        })
    }

}