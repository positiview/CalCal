package com.example.calcal.repository

import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.util.Resource

interface CourseRepository {

    suspend fun saveCourse(email:String,courseName:String, courseList: List<CoordinateDTO>, result: (Resource<CourseListDTO>) -> Unit)

    suspend fun getCourses(email: String,result: (Resource<List<CourseListDTO>?>)->Unit)

    suspend fun deleteCourse(num: Double, result: (String) -> Unit)
}