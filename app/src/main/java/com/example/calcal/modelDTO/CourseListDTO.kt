package com.example.calcal.modelDTO

data class CourseListDTO(
    val cid: Long,
    val courseName: String,
    val placeList: List<CoordinateDTO>,
    val coordinateCount: Int // 새로 추가된 필드
)
