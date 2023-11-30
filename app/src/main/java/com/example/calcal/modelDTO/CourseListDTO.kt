package com.example.calcal.modelDTO

data class CourseListDTO(
    var cid:Long,
    var courseName:String,
    var placeList: List<CoordinateDTO>
)
