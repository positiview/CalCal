package com.example.calcal.signlogin

import java.io.Serializable

data class MemberDTO(
    var email: String?,

    var phone: String,

    var password: String,

    var password2: String,

    var weight: Int?,

    var length: Int?,

    var age: Int?,

    var gender: String,

    var goalcal: Int?
): Serializable
