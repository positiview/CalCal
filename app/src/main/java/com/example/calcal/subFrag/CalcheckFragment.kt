package com.example.calcal.subFrag

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.calcal.databinding.FragmentCalcheckBinding
import com.example.calcal.repository.ExerciseRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModelFactory.ExerciseViewModelFactory


class CalcheckFragment : Fragment() {
    private lateinit var binding: FragmentCalcheckBinding
    private lateinit var btn_back : Button
    private lateinit var ExerciseviewModel: ExerciseViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private var pauseOffset: Long = 0
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exerciserepository = ExerciseRepositoryImpl()
        ExerciseviewModel = ViewModelProvider(this, ExerciseViewModelFactory(exerciserepository))[ExerciseViewModel::class.java]


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
        binding.btnStart.setOnClickListener{
            startChronometer()
            binding.btnStart.visibility = View.GONE
            binding.btnStop.visibility = View.VISIBLE
            binding.btnComplete.visibility = View.VISIBLE
        }
        binding.btnStop.setOnClickListener{
            toggleChronometer()
        }
        binding.btnComplete.setOnClickListener {
            stopChronometer()
            binding.btnStop.visibility = View.GONE
            binding.btnComplete.visibility = View.GONE
            binding.btnStart.visibility = View.VISIBLE
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
            binding.btnStop.isChecked = true
        } else {
            startChronometer()
            binding.btnStop.isChecked = false
        }
    }
}