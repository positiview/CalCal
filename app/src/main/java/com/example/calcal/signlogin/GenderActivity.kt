package com.example.calcal.signlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calcal.R
import com.example.calcal.databinding.ActivityGenderBinding
import com.example.calcal.databinding.ActivityLoginBinding
import com.example.calcal.retrofit.RequestFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenderActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGenderBinding
    private var selectedGender: String? = null
    private val apiService = RequestFactory.create()
    private lateinit var memberDTO: MemberDTO

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderBinding.inflate(layoutInflater)
        memberDTO = intent.getSerializableExtra("memberDTO") as MemberDTO
        setContentView(binding.root)


        val btnNext = findViewById<ImageView>(R.id.btn_next)

        binding.btnFemale.setOnClickListener {
            it.isSelected = true
            binding.btnMale.isSelected = false
            selectedGender = "female"
            updateBackground()
        }
        binding.btnMale.setOnClickListener {
            it.isSelected = true
            binding.btnFemale.isSelected = false
            selectedGender = "male"
            updateBackground()
        }


        btnNext.setOnClickListener {
            memberDTO.gender = selectedGender.toString()
            val call: Call<String> = apiService.updateMemberData(memberDTO)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("$$", "onResponse 응답 response : $response")
                    if (response.isSuccessful) {
                        // 서버 응답이 성공적으로 받아졌을 때
                        val responseBody: String? = response.body()

                        // 젠더페이지로 이동
                        val intent = Intent(this@GenderActivity, AgeActivity::class.java)
                        intent.putExtra("memberDTO", memberDTO)
                        startActivity(intent)


                        // responseBody에서 "Success" 등의 값을 확인하거나 원하는 처리를 수행
                        if (responseBody == "Success") {
                            // 성공 처리
                        } else {
                            // 다른 응답 처리
                        }
                    } else {
                        // 서버 응답이 실패했을 때
                        Log.d("$$", "onResponse 실패 response : ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("$$", "onFailure 발생")
                }
            })


        }


    }

    private fun updateBackground() {
        binding.btnFemale.setBackgroundResource(if (binding.btnFemale.isSelected) R.drawable.female_on else R.drawable.female)
        binding.btnMale.setBackgroundResource(if (binding.btnMale.isSelected) R.drawable.male_on else R.drawable.male)
    }
}
