package com.example.calcal.modelDTO

import java.io.Serializable

data class ExerciseDTO(
    var exname: String,

    var exicon: String?,

    var excontent: String?,

    var excal: Int?,

    var extime: Int?,

    var email: String?
): Serializable
