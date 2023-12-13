package com.example.calcal.subFrag

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.R
import com.example.calcal.databinding.FragmentCalcheckBinding
import com.example.calcal.repository.ExerciseRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModel.ExnameViewModel
import com.example.calcal.viewModel.TargetCalViewModel
import com.example.calcal.viewModelFactory.ExerciseViewModelFactory


class CalcheckFragment : Fragment() {
    private lateinit var binding: FragmentCalcheckBinding
    private lateinit var btn_back : Button
    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var exnameViewModel: ExnameViewModel
    private lateinit var targetCalViewModel:TargetCalViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private var pauseOffset: Long = 0
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exerciserepository = ExerciseRepositoryImpl()
        exerciseViewModel = ViewModelProvider(this, ExerciseViewModelFactory(exerciserepository))[ExerciseViewModel::class.java]



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalcheckBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val userEmail =  sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        exnameViewModel = ViewModelProvider(this).get(ExnameViewModel::class.java)
        targetCalViewModel = ViewModelProvider(this).get(TargetCalViewModel::class.java)

        exnameViewModel.selectedItem.observe(viewLifecycleOwner) { item ->
            // selectedItem 값이 변경될 때마다 binding.exnameText.text에 값을 설정
            binding.exnameText.text = item
        }
        binding.btnStart.setOnClickListener{
            startChronometer()
            binding.btnStart.visibility = View.GONE
            binding.btnPause.visibility = View.VISIBLE
            binding.btnComplete.visibility = View.VISIBLE
        }
        binding.btnPause.setOnClickListener{
            toggleChronometer()
        }
        binding.btnComplete.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("운동 완료")
            builder.setMessage("정말 완료하시겠습니까?")

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                stopChronometer()
                findNavController().navigate(R.id.action_calcheckFragment_to_graphFragment)
                dialog.dismiss()
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->

                dialog.dismiss()
            }

            builder.show()
        }

        return binding.root
    }

    private fun startChronometer() {
        if (!isRunning) {
            binding.chronometer.base = SystemClock.elapsedRealtime() - pauseOffset
            binding.chronometer.start()
            isRunning = true
        }
    }

    private fun stopChronometer() {
        if (isRunning) {
            binding.chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - binding.chronometer.base
            isRunning = false
        }
    }

    private fun toggleChronometer() {
        if (isRunning) {
            stopChronometer()
            binding.btnPause.isChecked = true
        } else {
            startChronometer()
            binding.btnPause.isChecked = false
        }
    }
}