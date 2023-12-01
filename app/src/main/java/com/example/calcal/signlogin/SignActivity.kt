package com.example.calcal.signlogin



import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.ActivitySignBinding
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.viewModel.MemberViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest


class SignActivity : AppCompatActivity() {
    private val apiService = RequestFactory.create()
    private lateinit var binding: ActivitySignBinding
    private val viewModel: MemberViewModel by viewModels()

    //구글로그인
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result?.data)


        try {
            val account = task.getResult(ApiException::class.java)

            // 이름, 이메일 등이 필요하다면 아래와 같이 account를 통해 각 메소드를 불러올 수 있다.
            val userName = account.givenName
            val serverAuth = account.serverAuthCode

            // 회원가입 완료 후 이동할 액티비티로 변경해야 함
            moveMainActivity()


        } catch (e: ApiException) {
            Log.e(SignActivity::class.java.simpleName, e.stackTraceToString())
        }
    }


    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkInfo: NetworkInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)


        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인터넷 연결 상태를 확인하기 위한 코드 추가
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = connectivityManager.activeNetworkInfo!!

        addListener()




        //일반 회원가입
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
            viewModel.updateEmail(email)

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

            // 비밀번호 해시 암호화
            val hashedPassword = hashPassword(password)
            val hashedPassword2 = hashPassword(password2)

            //값 반영
            val memberDTO = MemberDTO(email,phone,hashedPassword,hashedPassword2)
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

    private fun hashPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val passwordBytes = password.toByteArray()
        val hashedBytes = messageDigest.digest(passwordBytes)
        return bytesToHex(hashedBytes)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789ABCDEF"
        val hexBuilder = StringBuilder(bytes.size * 2)
        for (byte in bytes) {
            val higherNibble = byte.toInt() ushr 4 and 0x0F
            val lowerNibble = byte.toInt() and 0x0F
            hexBuilder.append(hexChars[higherNibble])
            hexBuilder.append(hexChars[lowerNibble])
        }
        return hexBuilder.toString()
    }

    //구글로그인
    private fun addListener() {
        binding.google.setOnClickListener {
            requestGoogleLogin()
        }
    }

    private fun requestGoogleLogin() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }

    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // 이메일 스코프 요청
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    // 구글 로그인 성공 후 호출되는 메서드
    private fun moveMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val email = account.email ?: "" // 사용자 이메일 주소, null인 경우 빈 문자열로 대체
            val displayName = account.displayName // 사용자 이름
            // 추가로 필요한 사용자 정보도 가져올 수 있습니다.

            // 회원 정보를 데이터베이스에 저장하기 위한 API 요청
            val memberDTO = MemberDTO(email, "", "", "") // memberDTO에 필요한 정보 추가
            val call: Call<String> = apiService.memberData(memberDTO)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        // 회원 정보 저장 성공
                        val responseBody: String? = response.body()
                        if (responseBody == "Success") {
                            Toast.makeText(getApplicationContext(), "반갑습니다!", Toast.LENGTH_SHORT).show()
                        } else {
                            // 회원 정보 저장 실패 처리
                        }
                    } else {
                        // 회원 정보 저장 실패
                        Log.d("$$", "onResponse 실패 response : ${response.code()}")
                        Toast.makeText(getApplicationContext(), "회원 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // 회원 정보 저장 실패
                    Log.d("$$", "onFailure 발생")
                    Toast.makeText(getApplicationContext(), "회원 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    // 인터넷 연결 상태를 확인하는 함수
    private fun isNetworkConnected(): Boolean {
        return networkInfo != null && networkInfo.isConnected
    }

}

