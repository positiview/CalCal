package com.example.calcal.signlogin


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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

        //회원가입
        val btnRegister = findViewById<TextView>(R.id.btnRegister)
        btnRegister.setOnClickListener{
            val emailEditText = findViewById<EditText>(R.id.email)
            val phoneEditText = findViewById<EditText>(R.id.phone)
            val passwordEditText = findViewById<EditText>(R.id.password1)
            val password2EditText = findViewById<EditText>(R.id.password2)

            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val password = passwordEditText.text.toString()
            val password2 = password2EditText.text.toString()

            // EditText 값이 비어있는지 확인하고 메시지 표시
            if (email.isEmpty()) {
                emailEditText.error = "Email을 입력해주세요."
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                phoneEditText.error = "전화번호를 입력해주세요."
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "비밀번호를 입력해주세요."
                return@setOnClickListener
            }
            if (password2.isEmpty()) {
                password2EditText.error = "비밀번호 확인을 입력해주세요."
                return@setOnClickListener
            }
            if (password != password2) {
                passwordEditText.error = "비밀번호가 일치하지 않습니다."
                password2EditText.error = "비밀번호가 일치하지 않습니다."
                return@setOnClickListener
            }

            //값 반영
            val memberDTO = MemberDTO(email,phone,password,password2)
            val call: Call<String> = apiService.memberData(memberDTO)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("$$","onResponse 응답 response : $response")
                    if (response.isSuccessful) {
                        // 서버 응답이 성공적으로 받아졌을 때
                        val responseBody: String? = response.body()

                        // 로그인페이지로 이동
                        val intent = Intent(this@SignActivity, LoginActivity::class.java)
                        startActivity(intent)


                        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();

                        // responseBody에서 "Success" 등의 값을 확인하거나 원하는 처리를 수행
                        if (responseBody == "Success") {
                            // 성공 처리
                        } else {
                            // 다른 응답 처리
                        }
                    } else {
                        // 서버 응답이 실패했을 때
                        Log.d("$$", "onResponse 실패 response : ${response.code()}")
                        Toast.makeText(getApplicationContext(), "이미 가입되어있는 이메일 입니다.", Toast.LENGTH_SHORT).show();

                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("$$","onFailure 발생")
                }
            })


        }

        //로그인으로 돌아가기
        val loginTv = findViewById<TextView>(com.example.calcal.R.id.loginTv)
        loginTv.setOnClickListener{
            val intent = Intent(this@SignActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}
