package com.example.calcal.request

import com.example.calcal.modelDTO.TestDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/tests")
    fun saveData(
        @Body test: TestDTO,
    ): Call<String>

}