package com.example.calcal.mainFrag

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.R
import com.example.calcal.adapter.GraphAdapter
import com.example.calcal.adapter.MypageAdapter
import com.example.calcal.databinding.FragmentGraphBinding
import com.example.calcal.databinding.FragmentMypageBinding
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.RecordViewModelFactory
import com.google.android.material.button.MaterialButton


class GraphFragment : Fragment() {
    private lateinit var binding: FragmentGraphBinding
    private lateinit var btn_back : Button
    private lateinit var sharedPreferences: SharedPreferences
    private var userEmail: String? = null
    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraphBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        userEmail =  sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        if (userEmail != null) {
            recordViewModel.getTodayRecord(userEmail!!)
        }

        val recyclerView = binding.graphrecycler
        recyclerView.layoutManager = LinearLayoutManager(context)



        val navController = NavHostFragment.findNavController(this)


        recordViewModel.getTodayRecord.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading ->{

                }
                is Resource.Success ->{
                    Log.d("$$","it. data = ${it.data}")
                    val adapter = it.data?.let { res ->
                        Log.d("$$","adapter data = $res")
                        GraphAdapter(res, this, navController) }
                    recyclerView.adapter = adapter

                }
                else ->{

                }
            }
        }



        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        return binding.root
    }
    fun onModifyCalorieGoal() {
        Log.d("GraphFragment", "onModifyCalorieGoal executed")
        Log.d("$$","userEmail : $userEmail")
        userEmail?.let { showDialog(it) }
    }
    fun onItemClick(position: Int) {
        // 여기서 프래그먼트 네비게이션 라이브러리를 사용하여 다른 프래그먼트로 이동합니다.
        when (position) {


        }
    }

    private fun showDialog(email: String){
        Log.d("$$","showDialog 실행")
        val builder = AlertDialog.Builder(requireContext()).create()
        val dialogView = layoutInflater.inflate(R.layout.confirm_dialog,null)
        val confirmBtn = dialogView.findViewById<MaterialButton>(R.id.dialogConfirm)
        val cancelBtn = dialogView.findViewById<MaterialButton>(R.id.dialogCancel)

        confirmBtn.setOnClickListener{
           /* userCancelled = false
            editAuthEmail(email)*/
            builder.dismiss()
        }
        cancelBtn.setOnClickListener {
          /*  userCancelled = true*/
            builder.dismiss()
        }
        builder.setView(dialogView)
        builder.show()
    }


}