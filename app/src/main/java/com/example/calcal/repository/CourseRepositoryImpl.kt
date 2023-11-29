package com.example.calcal.repository

import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.util.Resource

class CourseRepositoryImpl: CourseRepository {
    override suspend fun saveCourse(
        courseList: List<CoordinateDTO>,
        result: (Resource<List<CoordinateDTO>>) -> Unit
    ) {
        // 코스를 데이터베이스에 저장
    }
}