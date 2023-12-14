package com.example.calcal.modelDTO

import java.io.Serializable

data class ExrecordDTO(
    val exrecordId:Int,
    val exname: String,
    val goalCalorie: Double,
    val calorie:Double,
    val regDate: String
) : Serializable
