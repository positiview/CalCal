package com.example.calcal.repository

import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.util.Resource

interface RecordRepository {

    suspend fun saveRecord(myRouteRecords: List<RouteAndTimeDTO>, courseName : String, email: String,result: (Resource<List<RouteAndTimeDTO>>) -> Unit)

    suspend fun getRecord(result: (Resource<List<RouteAndTimeDTO>>) -> Unit)
}