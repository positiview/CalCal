package com.example.calcal.repository

import android.util.Log
import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ExerciseRepositoryImpl : ExerciseRepository {
    private val apiService = RequestFactory.create()

    override suspend fun saveExercise(
        exerciseDTO: ExerciseDTO,
        result: (Resource<Boolean>) -> Unit
    ) {
        val call: Call<String> = apiService.exerciseData(exerciseDTO)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("$$", "onResponse 응답 response : $response")
                if (response.isSuccessful) {
                    // 서버 응답이 성공적으로 받아졌을 때
                    val responseBody: String? = response.body()
                    result(Resource.Success(true))
                } else {
                    // 서버 응답이 실패했을 때
                    result(Resource.Success(false))
                    Log.d("$$", "onResponse 실패 response : ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                result(Resource.Error("서버 전송 실패"))
                Log.d("$$", "onFailure 발생")
            }
        })
    }

    override suspend fun getExercise(exname: String, result: (Resource<ExerciseDTO>) -> Unit) {
        val call: Call<ExerciseDTO> = apiService.getExerciseData(exname)

        call.enqueue(object : Callback<ExerciseDTO> {
            override fun onResponse(call: Call<ExerciseDTO>, response: Response<ExerciseDTO>) {
                if (response.isSuccessful) {
                    val responseBody: ExerciseDTO? = response.body()
                    if (responseBody != null) {
                        result(Resource.Success(responseBody))
                    } else {
                        result(Resource.Error("No user data"))
                    }
                } else {
                    result(Resource.Error("Failed to get user data"))
                }
            }

            override fun onFailure(call: Call<ExerciseDTO>, t: Throwable) {
                result(Resource.Error("Failed to get user data: ${t.message}"))
            }
        })
    }

    override suspend fun updateExercise(exerciseDTO: ExerciseDTO): ExerciseDTO = withContext(
        Dispatchers.IO) {
        val call: Call<String> = apiService.updateExerciseData(exerciseDTO)

        try {
            val response = call.execute()

            if (response.isSuccessful) {
                val responseBody: String? = response.body()
                // responseBody에서 "Success" 등의 값을 확인하고, 그에 따라 ExerciseDTO를 수정하여 반환
                exerciseDTO
            } else {
                // 서버 응답이 실패했을 때의 처리
                exerciseDTO
            }
        } catch (e: IOException) {
            // 서버 전송 실패 시의 처리
            exerciseDTO
        }
    }
}
