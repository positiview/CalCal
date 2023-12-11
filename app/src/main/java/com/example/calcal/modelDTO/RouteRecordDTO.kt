package com.example.calcal.modelDTO

import java.time.LocalDateTime

data class RouteRecordDTO (
    val courseName: String,
    val calorie:Double,
    val distance: String,
    val ratList: List<RouteAndTimeDTO>,
    val regDate: String
)
