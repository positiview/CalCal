package com.example.calcal.modelDTO

data class CourseListDTO(
    val course_no: Long,
    val email: String,
    val courseName: String,
    val placeList: List<CoordinateDTO>
//    val coordinateCount: Int // 새로 추가된 필드
)
