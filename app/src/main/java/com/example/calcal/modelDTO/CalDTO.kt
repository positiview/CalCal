package com.example.calcal.modelDTO

data class CalDTO (
    val recordId:Int,
    val courseName : String,
    val goalCalorie : Double,
    val calorie : Double,
    val distance: String,
    val time: Double,
    val longitude: Double,
    val latitude:Double,
    val regDate: String,
    val countDays: Int
)