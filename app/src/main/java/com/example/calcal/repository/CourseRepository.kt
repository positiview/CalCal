package com.example.calcal.repository

import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.util.Resource

interface CourseRepository {

    suspend fun saveCourse(courseName:String, courseList: List<CoordinateDTO>, result: (Resource<CourseListDTO>) -> Unit)

    suspend fun getCourses(result: (Resource<List<CourseListDTO>?>)->Unit)

    suspend fun deleteCourse(num: Int)
}