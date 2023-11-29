package com.example.calcal.repository

import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.util.Resource

interface CourseRepository {

    suspend fun saveCourse(courseList: List<CoordinateDTO>, result: (Resource<List<CoordinateDTO>>) -> Unit)
}