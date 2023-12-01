package com.example.calcal.modelDTO



data class TMapRouteRequest(
    val startX: Double,
    val startY: Double,
    val angle: Int,
    val speed: Int,
    val endX: Double,
    val endY: Double,
    val passList: String,
    val reqCoordType: String,
    val startName: String,
    val endName: String,
    val searchOption: Int,
    val resCoordType: String
)