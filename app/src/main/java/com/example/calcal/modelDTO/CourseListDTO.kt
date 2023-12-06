package com.example.calcal.modelDTO

data class CourseListDTO(
    val email: String,
    val courseNo: Long,
    val courseName: String,
    val placeList: List<CoordinateDTO>
//    val coordinateCount: Int // 새로 추가된 필드
)
