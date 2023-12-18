package com.example.calcal.subFrag

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.adapter.EtcAdapter
import com.example.calcal.databinding.FragmentEtcExerciseBinding
import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.repository.ExerciseRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModelFactory.ExerciseViewModelFactory

class EtcExerciseFragment : Fragment(), EtcAdapter.OnDataChangedListener {
    private lateinit var binding:FragmentEtcExerciseBinding
    private lateinit var btn_back : Button
    private val exerciseRepository: ExerciseRepository = ExerciseRepositoryImpl()
    private val viewModelFactory = ExerciseViewModelFactory(exerciseRepository)
    private val viewModel: ExerciseViewModel by viewModels() { viewModelFactory }
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEtcExerciseBinding.inflate(inflater, container, false)
        val recyclerView = binding.etcrecycler
        recyclerView.layoutManager = LinearLayoutManager(context)

        val list = ArrayList<String>()
        list.add("운동이름")
        list.add("운동에 대한 설명")
        list.add("시간당 칼로리 소모량 설정")
        list.add("권장 시간 설정")
        list.add("이동 여부")

        // exercises 리스트를 초기화합니다.
        val exercises = MutableList(list.size) { ExerciseDTO() }

        val adapter = EtcAdapter(list, this, exerciseRepository, exercises,sharedPreferences)

        recyclerView.adapter = adapter
        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.btnStartBotton.setOnClickListener {
            val exercise = adapter.getExercises()
            val email = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "") ?: ""
            // 가져온 이메일 값을 exercise 객체에 설정합니다.
            exercise.email = email

            viewModel.saveExercise(exercise)

            // 다른 프래그먼트로 이동합니다.
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_etcExerciseFragment_to_exerciseInfoFragment)
        }



        return binding.root
    }
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation()
    }
    override fun onItemClick(position: Int) {

    }


    override fun onDataChanged(isAllFilled: Boolean) {
        if (isAllFilled) {
            binding.btnStartBotton.setBackgroundColor(Color.RED)
        } else {
            binding.btnStartBotton.setBackgroundColor(Color.GRAY)
        }
    }


}