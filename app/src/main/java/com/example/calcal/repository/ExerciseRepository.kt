package com.example.calcal.repository

import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.util.Resource

interface ExerciseRepository{
    suspend fun saveExercise(exerciseDTO: ExerciseDTO, result: (Resource<Boolean>)->Unit)

    suspend fun getExercise(email: String, result: (Resource<ExerciseDTO>) -> Unit)
    suspend fun updateExercise(exerciseDTO: ExerciseDTO): ExerciseDTO


}