package com.example.calcal.repository

import com.example.calcal.modelDTO.CalDTO
import com.example.calcal.modelDTO.ExRecordDTO
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.util.Resource

interface RecordRepository {

    suspend fun saveRecord(myRouteRecords: List<RouteAndTimeDTO>, courseName : String,
                           email: String, goalCalorie:Double, calorie:Double, distance:String, result: (Resource<String>) -> Unit)

    suspend fun getRecord(email: String, result: (Resource<List<RouteRecordDTO>?>) -> Unit)

    suspend fun saveExRecord(exRecords: List<ExRecordDTO>,
                             email: String, exname: String, goalCalorie:Double, calorie:Double, result: (Resource<String>) -> Unit)
    suspend fun getExRecord(email: String, result: (Resource<List<ExRecordDTO>?>) -> Unit)
    suspend fun getTodayRecord(email: String, result: (Resource<Map<String, List<CalDTO>>?>) -> Unit)



}