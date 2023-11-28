package com.example.calcal.signlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.calcal.R


class AgeActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gender)


        val btnBack = findViewById<Button>(R.id.btn_back)


        btnBack.setOnClickListener {
            val intent = Intent(this, GenderActivity::class.java)
            startActivity(intent)
        }

        // 나머지 버튼 클릭 이벤트 처리 등을 추가하면 됩니다.
    }
}

