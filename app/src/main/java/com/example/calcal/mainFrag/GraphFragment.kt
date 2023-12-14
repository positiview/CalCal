package com.example.calcal.mainFrag

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.R
import com.example.calcal.adapter.GraphAdapter
import com.example.calcal.databinding.FragmentGraphBinding
import com.example.calcal.repository.MemberRepository
import com.example.calcal.repository.MemberRepositoryImpl
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.MemberViewModel
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.MemberViewModelFactory
import com.example.calcal.viewModelFactory.RecordViewModelFactory


class GraphFragment : Fragment() {
    private lateinit var binding: FragmentGraphBinding
    private lateinit var btn_back : Button
    private lateinit var sharedPreferences: SharedPreferences
    private var userEmail: String? = null
    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }

    private val memberRepository: MemberRepository = MemberRepositoryImpl()
    private val memberViewModelFactory = MemberViewModelFactory(memberRepository)
    private val memberViewModel: MemberViewModel by viewModels(){memberViewModelFactory }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraphBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        userEmail =  sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        if (userEmail != null) {
            recordViewModel.getTodayRecord(userEmail!!)
            memberViewModel.getMemberInfo(userEmail!!)
        }

        val recyclerView = binding.graphrecycler
        recyclerView.layoutManager = LinearLayoutManager(context)



        val navController = NavHostFragment.findNavController(this)
        var goalcal =1000

        memberViewModel.getMemberInfo.observe(viewLifecycleOwner){
            Log.d("$$","GoalCalViewmodel  it = $it")
            if (it is Resource.Success) {
                if(it.data.goalcal!=null){
                    goalcal = it.data.goalcal!!
                }
                recordViewModel.getTodayRecord.observe(viewLifecycleOwner){
                    when(it){
                        is Resource.Loading ->{

                        }
                        is Resource.Success ->{
                            Log.d("$$","it. data = ${it.data}")

                            val adapter = it.data?.let { res ->
                                Log.d("$$","adapter data = $res")
                                GraphAdapter(res, goalcal,this, navController) }
                            recyclerView.adapter = adapter

                        }
                        else ->{

                        }
                    }
                }
            }
        }





        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        return binding.root
    }
    fun onModifyCalorieGoal(calorieGoal: TextView) {
        Log.d("GraphFragment", "onModifyCalorieGoal executed")
        Log.d("$$","userEmail : $userEmail")
        userEmail?.let { showDialog(it,calorieGoal) }
    }
    fun onItemClick(position: Int) {
        // 여기서 프래그먼트 네비게이션 라이브러리를 사용하여 다른 프래그먼트로 이동합니다.
        when (position) {


        }
    }

    private fun showDialog(email: String, calorieGoal: TextView){
        Log.d("$$","showDialog 실행")
        val builder = AlertDialog.Builder(requireContext()).create()
        val dialogView = layoutInflater.inflate(R.layout.confirm_dialog,null)
        val confirmBtn = dialogView.findViewById<Button>(R.id.dialogConfirm)
        val cancelBtn = dialogView.findViewById<Button>(R.id.dialogCancel)
        val goalcalEditText = dialogView.findViewById<EditText>(R.id.goalcal)


        confirmBtn.setOnClickListener{
            val goalcal : Int = goalcalEditText.text.toString().toInt()
            memberViewModel.updateMemberGoalcal(email,goalcal)
            memberViewModel.updateGoalCal.observe(viewLifecycleOwner){
                if(it is Resource.Success){
                    calorieGoal.text = goalcal.toString()
                    Toast.makeText(requireContext(),it.data,Toast.LENGTH_SHORT).show()
                }
            }

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