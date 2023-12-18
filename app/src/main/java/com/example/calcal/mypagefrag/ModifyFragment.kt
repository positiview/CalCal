package com.example.calcal.mypagefrag

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.FragmentMainBinding
import com.example.calcal.databinding.FragmentModifyBinding
import com.example.calcal.repository.MemberRepositoryImpl
import com.example.calcal.retrofit.RequestFactory
import com.example.calcal.signlogin.AgeActivity
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.MemberViewModel
import com.example.calcal.viewModelFactory.MemberViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ModifyFragment : Fragment() {
    // binding의 타입을 FragmentModifyBinding으로 선언합니다.
    private lateinit var binding: FragmentModifyBinding
    private val apiService = RequestFactory.create()
    private lateinit var memberViewModel: MemberViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = MemberRepositoryImpl()
        memberViewModel = ViewModelProvider(this, MemberViewModelFactory(repository))[MemberViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentModifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val userEmail = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")

        if (userEmail != null) {
            memberViewModel.getMemberInfo(userEmail)
        }

        // getMemberInfo 라이브 데이터 관찰
        memberViewModel.getMemberInfo.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // 로딩 중일 때 UI 업데이트
                }

                is Resource.Success -> {
                    // 요청이 성공했을 때 UI 업데이트
                    val memberDTO = resource.data
                    binding.id2.text = memberDTO?.email
                    binding.phone2.text = memberDTO?.phone
                    binding.age2.text = memberDTO?.age.toString()
                    binding.gender2.text = memberDTO?.gender
                    binding.length2.text = memberDTO?.length.toString()
                    binding.weight2.text = memberDTO?.weight.toString()
                }

                is Resource.Error -> {
                    // 요청이 실패했을 때 UI 업데이트
                }

                else -> {}
            }
            binding.id2.setOnClickListener {
            }
            binding.phone2.setOnClickListener {
            }
            binding.age2.setOnClickListener {
            }
            binding.gender2.setOnClickListener {
            }
            binding.length2.setOnClickListener {
            }
            binding.weight2.setOnClickListener {
            }
        }
    }



    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation()
    }

}