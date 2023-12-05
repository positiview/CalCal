package com.example.calcal.signlogin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.ActivityLoginBinding
import com.example.calcal.repository.MemberRepository
import com.example.calcal.viewModelFactory.MemberViewModelFactory
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

class LoginActivity : AppCompatActivity() {

    // Retrofit을 이용해 서버와 통신하기 위한 인스턴스 생성
    private val apiService = RequestFactory.create()
    private lateinit var binding: ActivityLoginBinding // View 바인딩을 위한 변수
    private lateinit var memberViewModel: MemberViewModel
    // SharedPreferences를 사용하여 로그인 상태를 유지하기 위한 변수
    private lateinit var sharedPreferences: SharedPreferences
    private var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()




    // 구글 로그인을 위한 클라이언트 초기화
    private val viewModel: MemberViewModel by viewModels()
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

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val repository = MemberRepository(sharedPreferences)
        val viewModelFactory = MemberViewModelFactory(repository)
        memberViewModel = ViewModelProvider(this, viewModelFactory).get(MemberViewModel::class.java)

        // 인터넷 연결 상태를 확인하기 위한 코드
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = connectivityManager.activeNetworkInfo!!

        addListener()

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

            viewModel.updateEmail(email)

            val hashedPassword = hashPassword(password)

            // Retrofit을 통한 로그인 요청
            val call: Call<String> = apiService.login(MemberDTO(email = email, phone = "", password = hashedPassword, password2 = "", weight = null, length = null,age = null, gender = "" ))
            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val responseBody: String? = response.body()
                        if (responseBody == "Success") {
                            isLoggedIn = true // 로그인 상태를 저장
                            sharedPreferences.edit().putString(KEY_EMAIL, email).apply()
                            // 로그인 성공 시 MainActivity로 이동
                            val welcomeMessage = " ${email} 님, 반갑습니다!" // 이메일을 활용한 환영 메시지 생성

                            Toast.makeText(applicationContext, welcomeMessage, Toast.LENGTH_SHORT).show()
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
            })
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

        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val email = account.email ?: "" // 사용자 이메일 주소, null인 경우 빈 문자열로 대체
            val displayName = account.displayName // 사용자 이름
            // 추가로 필요한 사용자 정보도 가져올 수 있습니다.

            // 회원 정보를 데이터베이스에 저장하기 위한 API 요청
            val memberDTO = MemberDTO(email, "", "", "",null,null,null,"") // memberDTO에 필요한 정보 추가
            val call: Call<String> = apiService.memberData(memberDTO)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        // 회원 정보 저장 성공
                        val responseBody: String? = response.body()
                        if (responseBody == "Success") {
                            val welcomeMessage = " ${email} 님, 반갑습니다!" // 이메일을 활용한 환영 메시지 생성

                            Toast.makeText(applicationContext, welcomeMessage, Toast.LENGTH_SHORT).show()
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
        return networkInfo.isConnected
    }

    companion object {
        private const val PREF_NAME = "login_pref"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_EMAIL = "email" // KEY_EMAIL 상수 정의
    }

}