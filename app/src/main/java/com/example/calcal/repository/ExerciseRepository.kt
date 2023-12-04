package com.example.calcal.repository

import android.util.Log

class ExerciseRepository() {
    fun getExercise(): String {
        // 이 부분에 운동 이름을 가져오는 코드를 작성하세요.
        // 예를 들어, 데이터베이스에서 운동 이름을 가져온다면, 데이터베이스 접근 코드를 여기에 작성하시면 됩니다.
        val exname = "exname"
        val exicon = "exicon"
        val excontent = "excontent"
        val excal = "excal"
        val extime = "extime"
        val email = "email"

        Log.d("ExerciseRepository", "getExname: $exname")
        return exname
    }


}