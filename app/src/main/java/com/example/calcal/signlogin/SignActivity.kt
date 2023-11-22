package com.example.calcal.signlogin


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calcal.R
import com.example.calcal.retrofit.RequestFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignActivity : AppCompatActivity() {
    private val apiService = RequestFactory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val btnRegister = findViewById<TextView>(R.id.btnRegister) //회원가입
        btnRegister.setOnClickListener{
            val memberDTO = MemberDTO("test","0100000","a","a")
            val call: Call<String> = apiService.memberData(memberDTO)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("$$","onResponse 응답 response : $response")
                    if (response.isSuccessful) {
                        // 서버 응답이 성공적으로 받아졌을 때
                        val responseBody: String? = response.body()


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
                    Log.d("$$","onFailure 발생")
                }
            })


            // 로그인페이지로 이동
            val intent = Intent(this@SignActivity, LoginActivity::class.java)
            startActivity(intent)


       Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }

        val loginTv = findViewById<TextView>(com.example.calcal.R.id.loginTv) //로그인으로 돌아가기
        loginTv.setOnClickListener{
            val intent = Intent(this@SignActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}
