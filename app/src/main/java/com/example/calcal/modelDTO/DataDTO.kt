package com.example.calcal.modelDTO

import retrofit2.http.Query

data class DataDTO (
    var startX: Double,
    var startY: Double,
    var endX: Double,
    var endY: Double,
    var startName: String,
    var endName: String
)