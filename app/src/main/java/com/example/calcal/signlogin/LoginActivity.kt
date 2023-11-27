package com.example.calcal.signlogin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import com.example.calcal.signlogin.SignActivity
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.ActivityLoginBinding
import com.example.calcal.retrofit.RequestFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    // Retrofit을 이용해 서버와 통신하기 위한 인스턴스 생성
    private val apiService = RequestFactory.create()
    private lateinit var binding: ActivityLoginBinding // View 바인딩을 위한 변수

    // SharedPreferences를 사용하여 로그인 상태를 유지하기 위한 변수
    private lateinit var sharedPreferences: SharedPreferences
    private var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()


    // 네트워크 연결 상태를 확인하기 위한 변수 초기화
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkInfo: NetworkInfo

    // 구글 로그인을 위한 클라이언트 초기화
    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }
    private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result?.data)

        try {
            val account = task.getResult(ApiException::class.java)

            // 구글 계정에서 필요한 정보 추출
            val userName = account.givenName
            val serverAuth = account.serverAuthCode

            // MainActivity로 이동
            moveMainActivity()

        } catch (e: ApiException) {
            // ApiException 발생 시 로깅
            Log.e(LoginActivity::class.java.simpleName, e.stackTraceToString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View 바인딩 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 인터넷 연결 상태를 확인하기 위한 코드
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = connectivityManager.activeNetworkInfo!!


        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // 이미 로그인된 상태라면 MainActivity로 이동
        if (isLoggedIn) {
            moveMainActivity()
            finish() // LoginActivity 종료
        }

        // 각 버튼별 리스너 설정
        addListener()
    }

    // 버튼 클릭에 대한 리스너 설정
    private fun addListener() {
        // 로그인 버튼 클릭 시
        binding.btnlogin.setOnClickListener {
            Toast.makeText(applicationContext, "반갑습니다!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            // EditText로부터 이메일과 비밀번호 추출
            /*val emailEditText = findViewById<EditText>(R.id.email)
            val passwordEditText = findViewById<EditText>(R.id.password)
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val hashedPassword = hashPassword(password)

            // 이메일과 비밀번호 유효성 검사
            if (email.isEmpty()) {
                emailEditText.error = "Email을 입력해주세요."
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "비밀번호를 입력해주세요."
                return@setOnClickListener
            }

            // Retrofit을 통한 로그인 요청
            val call: Call<String> = apiService.login(MemberDTO(email = email, phone = "", password = hashedPassword, password2 = ""))
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val responseBody: String? = response.body()
                        if (responseBody == "Success") {
                            isLoggedIn = true // 로그인 상태를 저장
                            // 로그인 성공 시 MainActivity로 이동
                            Toast.makeText(applicationContext, "반갑습니다!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "이메일, 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("$$", "onFailure 발생")
                    Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
                }
            })*/
        }

        // 회원가입 TextView 클릭 시 SignActivity로 이동
        binding.registerTv.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignActivity::class.java)
            startActivity(intent)
        }

        // 구글 버튼 클릭 시 구글 로그인 요청
        binding.google.setOnClickListener {
            requestGoogleLogin()
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

    // 구글 로그인 요청 처리
    private fun requestGoogleLogin() {
        googleSignInClient.signOut()
        val signInIntent = googleSignInClient.signInIntent
        googleAuthLauncher.launch(signInIntent)
    }

    // 구글 클라이언트 초기화
    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    // MainActivity로 이동하며 회원 정보를 처리하는 메서드
    private fun moveMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        isLoggedIn = true // 로그인 상태를 저장
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val email = account.email ?: ""

            val memberDTO = MemberDTO(email, "", "", "")
            val call: Call<String> = apiService.memberData(memberDTO)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val responseBody: String? = response.body()
                        if (responseBody == "Success") {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "회원 정보 저장 성공", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            runOnUiThread {
                                Toast.makeText(applicationContext, "회원 정보 저장 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Log.d("$$", "onResponse 실패 response : ${response.code()}")
                        Toast.makeText(applicationContext, "회원 정보 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("$$", "onFailure 발생")
                    Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    companion object {
        private const val PREF_NAME = "login_pref"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
}