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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.R
import com.example.calcal.databinding.FragmentCalcheckBinding
import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.repository.ExerciseRepositoryImpl
import com.example.calcal.repository.MemberRepositoryImpl
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModel.MemberViewModel
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.ExerciseViewModelFactory
import com.example.calcal.viewModelFactory.MemberViewModelFactory
import com.example.calcal.viewModelFactory.RecordViewModelFactory
import kotlin.math.pow


class CalcheckFragment : Fragment() {
    private lateinit var binding: FragmentCalcheckBinding
    private lateinit var btn_back : Button
    private lateinit var memberViewModel:MemberViewModel
    private lateinit var exerciseViewModel: ExerciseViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }
    private var selectedItem = arguments?.getString("selectedItem")
    private var userInputValue = arguments?.getDouble("userInputValue")
    private var excal = arguments?.getInt("excal")
    private var pauseOffset: Long = 0
    private var isRunning = false
    private var memberWeight : Int? = 70
    private var calories : Double? = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val exerciserepository = ExerciseRepositoryImpl()
        exerciseViewModel = ViewModelProvider(this, ExerciseViewModelFactory(exerciserepository))[ExerciseViewModel::class.java]

        val memberRepository = MemberRepositoryImpl()
        memberViewModel = ViewModelProvider(this, MemberViewModelFactory(memberRepository))[MemberViewModel::class.java]

        val recordRepository = RecordRepositoryImpl()
//        recordViewModel = ViewModelProvider(this, RecordViewModelFactory(recordRepository))[RecordViewModel::class.java]


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalcheckBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val userEmail =  sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        selectedItem = arguments?.getString("selectedItem")
        userInputValue = arguments?.getDouble("userInputValue")
        excal = arguments?.getInt("excal")
        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }

        memberViewModel.getMemberInfo.observe(viewLifecycleOwner){
            if(it is Resource.Success){
                memberWeight = it.data.weight
            }
        }

        binding.exnameText.text = selectedItem
        binding.totalTv.text = userInputValue.toString()
        binding.btnStart.setOnClickListener{
            startChronometer()
            binding.btnStart.visibility = View.GONE
            binding.btnPause.visibility = View.VISIBLE
            binding.btnComplete.visibility = View.VISIBLE
        }
        binding.btnPause.setOnClickListener{
            toggleChronometer()
        }
        binding.chronometer.setOnChronometerTickListener { chronometer ->
            val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
            calories = excal?.let { calculateCalories(elapsedMillis.toDouble(), it) }
            binding.calorieTv.text = calories?.let { String.format("%.2f", it) } ?: "0.00"

            if (userInputValue != null && calories != null && calories!! >= userInputValue!!) {
                stopChronometer()
                toggleChronometer()
            }
        }


        binding.btnComplete.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("운동 완료")
            builder.setMessage("정말 완료하시겠습니까?")

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                val resource = recordViewModel.getExRecord.value
                if (resource is Resource.Success) {
                    val exRecord = resource.data ?: listOf()
                    userInputValue?.let { it1 ->
                        calories?.let { it2 ->
                            if (userEmail != null) {
                                selectedItem?.let { it3 ->
                                    recordViewModel.saveExRecord(exRecord,userEmail, it3,
                                        it1, it2
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Resource의 상태가 Success가 아닌 경우에 대한 처리
                }
                stopChronometer()
                dialog.dismiss()
                findNavController().navigate(R.id.action_calcheckFragment_to_graphFragment)
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
        val currentCalories = calories
        val targetCalories = userInputValue
        if (isRunning) {
            stopChronometer()
            binding.btnPause.isChecked = true
        } else {
            if (currentCalories != null && targetCalories != null && currentCalories < targetCalories) {
                startChronometer()
                binding.btnPause.isChecked = false
            }
        }
    }

    private fun calculateCalories(elapsedMillis : Double, excal: Int): Double {
        val elapsedHours = elapsedMillis / 3600000 // 밀리초를 시간으로 변환
        val cal = (memberWeight?.toDouble() ?: return 0.0) * elapsedHours * excal
        Log.d("$$", "elapsedMilliselapsedMillis : ${elapsedHours}")
        return cal
    }
}