package com.example.calcal.modelDTO

data class CalDTO (
    val recordId:Int,
    val courseName : String,
    val exname: String,
    val goalCalorie : Double,
    val goalExCalorie : Double,
    val calorie : Double,
    val exCalorie : Double,
    val distance: String,
    val time: Double,
    val exTime: Double,
    val longitude: Double,
    val latitude:Double,
    val regDate: String,
    val countDays: Int
)