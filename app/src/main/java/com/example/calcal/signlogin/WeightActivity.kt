package com.example.calcal.signlogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calcal.R
import com.example.calcal.databinding.ActivityWeightBinding
import com.example.calcal.repository.MemberRepository
import com.example.calcal.repository.MemberRepositoryImpl
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModel.MemberViewModel
import com.example.calcal.viewModelFactory.ExerciseViewModelFactory
import com.example.calcal.viewModelFactory.MemberViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeightActivity : AppCompatActivity() {
    private lateinit var numberPicker: NumberPicker
    private lateinit var binding:ActivityWeightBinding
    private val apiService = RequestFactory.create()
    private lateinit var memberDTO: MemberDTO
    private lateinit var viewModel: MemberViewModel
    @SuppressLint("WrongViewCast", "SoonBlockedPrivateApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        memberDTO = intent.getSerializableExtra("memberDTO") as MemberDTO
        numberPicker = findViewById<NumberPicker>(R.id.WeightNumberPicker)
        val repository = MemberRepositoryImpl()
        val viewModelFactory = MemberViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MemberViewModel::class.java)



        numberPicker.minValue = 20  // 최소 키 값
        numberPicker.maxValue = 200 // 최대 키 값
        numberPicker.value = 60
        numberPicker.wrapSelectorWheel = false

        val btnBack = findViewById<ImageView>(R.id.btn_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, GenderActivity::class.java)
            intent.putExtra("memberDTO", memberDTO)
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            memberDTO.weight = numberPicker.value
            viewModel.updateMemberInfo(memberDTO)
            val call: Call<String> = apiService.updateMemberData(memberDTO)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("$$", "onResponse 응답 response : $response")
                    if (response.isSuccessful) {
                        // 서버 응답이 성공적으로 받아졌을 때
                        val responseBody: String? = response.body()

                        // 젠더페이지로 이동
                        val intent = Intent(this@WeightActivity, LoginActivity::class.java)
                        intent.putExtra("memberDTO", memberDTO)
                        startActivity(intent)


                        // responseBody에서 "Success" 등의 값을 확인하거나 원하는 처리를 수행
                        if (responseBody == "Success") {
                            Log.d("$$", "onResponse Success response : ${response.code()}")
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
}

