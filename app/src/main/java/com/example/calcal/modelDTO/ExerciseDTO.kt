package com.example.calcal.modelDTO

import java.io.Serializable

data class ExerciseDTO(
    var email: String = "",
    var excal: Int? = null,
    var excontent: String = "",
    var exicon: String = "",
    var exmove: Boolean = false,
    var exname: String = "",
    var extime: Int? = null
) : Serializable
