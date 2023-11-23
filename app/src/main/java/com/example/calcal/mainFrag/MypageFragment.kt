package com.example.calcal.mainFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.adapter.MypageAdapter
import com.example.calcal.databinding.FragmentMypageBinding


class MypageFragment : Fragment() {

    private lateinit var binding: FragmentMypageBinding
    private lateinit var btn_back : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.mypagerecycler)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val list = ArrayList<String>()
        list.add("프로필 수정")
        list.add("알림설정")
        list.add("고객센터")
        list.add("환경설정")
        list.add("회원탈퇴")

        val adapter = MypageAdapter(list, this)
        recyclerView.adapter = adapter

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }

        return view

        }
    fun onItemClick(position: Int) {
        // 여기서 프래그먼트 네비게이션 라이브러리를 사용하여 다른 프래그먼트로 이동합니다.
        when (position) {
            0 -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mypageFragment_to_modifyFragment)

            1 -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mypageFragment_to_notisetFragment)

            2 -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mypageFragment_to_centerFragment)

            3 -> NavHostFragment.findNavController(this)
                .navigate(R.id.action_mypageFragment_to_settingFragment)

//            4 -> delete~기능 넣어야 함

        }
    }
}
