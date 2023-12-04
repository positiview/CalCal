package com.example.calcal.mainFrag

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import retrofit2.Call

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.R
import com.example.calcal.adapter.MypageAdapter
import com.example.calcal.databinding.FragmentMypageBinding
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.signlogin.MemberDTO
import retrofit2.Callback
import retrofit2.Response


class MypageFragment : Fragment() {


    private lateinit var binding: FragmentMypageBinding
    private lateinit var btn_back : Button

    private lateinit var sharedPreferences: SharedPreferences
    private var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
// SharedPreferences 초기화
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val recyclerView = binding.mypagerecycler
        recyclerView.layoutManager = LinearLayoutManager(context)

        val list = ArrayList<String>()
        list.add("프로필 수정")
        list.add("환경설정")
        list.add("고객센터")
        list.add("로그아웃")
        list.add("회원탈퇴")

        val adapter = MypageAdapter(list, this)
        recyclerView.adapter = adapter

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }

        return binding.root

        }
    fun onItemClick(position: Int) {
        // 여기서 프래그먼트 네비게이션 라이브러리를 사용하여 다른 프래그먼트로 이동합니다.
        when (position) {
            0 -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mypageFragment_to_modifyFragment)

            1 -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mypageFragment_to_settingFragment)

            2 -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mypageFragment_to_centerFragment)

            3 -> {
                // 로그아웃 확인 창 표시
                showLogoutConfirmationDialog()
            }
            4 -> {
                // 회원탈퇴 확인 창 표시
                showWithdrawConfirmationDialog()
            }
        }
    }
    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("로그아웃")
        alertDialogBuilder.setMessage("정말 로그아웃 하시겠습니까?")
        alertDialogBuilder.setPositiveButton("예") { _, _ ->
            // 사용자가 "예"를 선택한 경우, 로그아웃 처리
            performLogout()
        }
        alertDialogBuilder.setNegativeButton("취소", null)
        alertDialogBuilder.create().show()
    }

    private fun performLogout() {

        isLoggedIn = false // 로그인 상태를 false로 변경

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)

        // 현재 Fragment를 종료하고 백 스택에서 제거
        requireActivity().finish()
    }
    companion object {
        private const val PREF_NAME = "login_pref"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_EMAIL = "email" // KEY_EMAIL 상수 정의
    }


    private fun showWithdrawConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("회원탈퇴")
        alertDialogBuilder.setMessage("정말 회원탈퇴 하시겠습니까?")
        alertDialogBuilder.setPositiveButton("예") { _, _ ->
            // 사용자가 "예"를 선택한 경우, 회원탈퇴 처리
            performWithdraw()

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
            // 현재 액티비티 종료 (선택 사항)

        }
        alertDialogBuilder.setNegativeButton("취소", null)
        alertDialogBuilder.create().show()
    }
    private val apiService = RequestFactory.create()
    private fun performWithdraw() {

        val email = sharedPreferences.getString(KEY_EMAIL, "")
        // 로그인된 사용자의 이메일을 가져옴
        Log.d("Email", "Current user email: $email")

        val memberDTO = MemberDTO(
            email = email ?: "", // null인 경우 빈 문자열로 설정
            phone = "", // 필요 없는 값이므로 빈 문자열로 설정
            password = "", // 필요 없는 값이므로 빈 문자열로 설정
            password2 = "", // 필요 없는 값이므로 빈 문자열로 설정
            weight = null,
            length = null,
            age = null,
            gender = ""

        )

        val call: Call<String> = apiService.withdraw(memberDTO)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody: String? = response.body()
                    if (responseBody == "Success") {
                        Log.d("$$", "회원 탈퇴 성공")
                        isLoggedIn = false // 로그인 상태를 false로 변경
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)

                    } else {
                        Log.d("$$", "회원 탈퇴 실패")
                    }
                } else {
                    Log.d("$$", "onResponse 실패 response : ${response.code()}")
                    val errorBody: String? = response.errorBody()?.string()
                    Log.d("$$", "onResponse 실패 errorBody : $errorBody")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("$$", "onFailure 발생", t)
            }
        })
    }


}
