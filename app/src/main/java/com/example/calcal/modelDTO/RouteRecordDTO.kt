package com.example.calcal.modelDTO

import java.util.Date

data class RouteRecordDTO (
    val courseName: String,
    val calorie:Double,
    val ratList: List<RouteAndTimeDTO>,
    val regDate:Date
)
