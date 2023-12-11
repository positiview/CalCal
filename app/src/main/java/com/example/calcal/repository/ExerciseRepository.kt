package com.example.calcal.repository

import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.util.Resource

interface ExerciseRepository{
    suspend fun saveExercise(exerciseDTO: ExerciseDTO, result: (Resource<Boolean>)->Unit)

    suspend fun getExercise(exname: String, result: (List<ExerciseDTO>) -> Unit)

    suspend fun updateExercise(exerciseDTO: ExerciseDTO): ExerciseDTO

    suspend fun deleteMember(exname: String, result: (Resource<Boolean>) -> Unit)

    suspend fun getAllExercises(): Resource<List<ExerciseDTO>>
}