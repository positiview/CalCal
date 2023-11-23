package com.example.calcal.signlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.retrofit.RequestFactory
import retrofit2.Call
import retrofit2.Callback

class LoginActivity : AppCompatActivity() {

    private val apiService = RequestFactory.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //로그인 버튼 클릭
        val button = findViewById<Button>(R.id.btnlogin)
        button.setOnClickListener {

            // 입력된 이메일과 비밀번호 가져오기
            val emailEditText = findViewById<EditText>(R.id.email)
            val passwordEditText = findViewById<EditText>(R.id.password)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // 이메일과 비밀번호 유효성 검사
            if (email.isEmpty()) {
                emailEditText.error = "Email을 입력해주세요."
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "비밀번호를 입력해주세요."
                return@setOnClickListener
            }

            // 로그인 요청
            val call: Call<String> = apiService.login(MemberDTO(email = email, phone = "", password = password, password2 = ""))
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                    if (response.isSuccessful) {
                        // 로그인 성공
                        val responseBody: String? = response.body()
                        if (responseBody == "Success") {
                            // 로그인 성공 처리
                            Toast.makeText(applicationContext, "반갑습니다!", Toast.LENGTH_SHORT).show()
                            // 로그인 후 작업 수행
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // 로그인 실패 처리
                            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // 서버 응답 실패
                        Toast.makeText(applicationContext, "이메일, 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // 통신 실패
                    Log.d("$$", "onFailure 발생")
                    Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
                }
            })


        }

        //회원가입페이지로 이동
        val register = findViewById<TextView>(R.id.registerTv)
        register.setOnClickListener{
            val intent = Intent(this@LoginActivity, SignActivity::class.java)
            startActivity(intent)
        }
    }
}
