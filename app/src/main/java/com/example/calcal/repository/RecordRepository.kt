package com.example.calcal.repository

import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.util.Resource

interface RecordRepository {

    suspend fun saveRecord(myRouteRecords: List<RouteAndTimeDTO>, courseName : String,
                           email: String, calorie:Double, distance:String, result: (Resource<String>) -> Unit)

    suspend fun getRecord(email: String, result: (Resource<List<RouteRecordDTO>?>) -> Unit)
}