package com.example.calcal.modelDTO

import java.time.LocalDateTime

data class RouteRecordDTO (
    val recordId:Int,
    val courseName: String,
    val goalCalorie: Double,
    val calorie:Double,
    val distance: String,
    val ratList: List<RouteAndTimeDTO>,
    val regDate: String
)
