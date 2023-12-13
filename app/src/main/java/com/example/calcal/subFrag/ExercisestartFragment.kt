package com.example.calcal.subFrag

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.adapter.ExStartAdapter
import com.example.calcal.databinding.FragmentExercisestartBinding
import com.example.calcal.repository.ExerciseRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModelFactory.ExerciseViewModelFactory


class ExercisestartFragment : Fragment() {
    private lateinit var binding: FragmentExercisestartBinding
    private lateinit var btn_back: Button
    private lateinit var viewModel: ExerciseViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private val list = arrayListOf<String>()
    private  var position: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = ExerciseRepositoryImpl()
        viewModel = ViewModelProvider(this, ExerciseViewModelFactory(repository))[ExerciseViewModel::class.java]
        viewModel.getAllExercises()
    }

    @SuppressLint("ResourceType", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExercisestartBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val userEmail =  sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        val selectedItem = viewModel.selectedItem.value

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }


        viewModel.exerciseList.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    val exercises = resource.data
                    // ExerciseDTO 객체의 리스트를 필터링하고, 해당 객체들의 이름을 가져옵니다.
                    val exerciseNames = exercises.filter { it.email == "admin" || it.email == userEmail }.map { it.exname }

                    binding.btnExTitle.setOnClickListener {
                        val items =  exerciseNames.toTypedArray()


                        val builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
                        val inflater = LayoutInflater.from(requireContext())
                        val view = inflater.inflate(R.layout.ex_info_dialog, null, false)

                        val spinner = view.findViewById<Spinner>(R.id.dialog_input)
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter

                        val selectedItemPosition = exerciseNames.indexOf(viewModel.selectedItem.value)
                        if (selectedItemPosition != -1) {
                            spinner.setSelection(selectedItemPosition)
                        }

                        builder.setView(view)
                        val dialog = builder.create()

                        val okButton = view.findViewById<Button>(R.id.dialog_ok)
                        val cancelButton = view.findViewById<Button>(R.id.dialog_cancel)
                        val etcGo = view.findViewById<TextView>(R.id.ex_etc_go)
                        binding.btnStartBotton.isEnabled = false
                        binding.btnStartBotton.setBackgroundColor(Color.GRAY)
                        okButton.setOnClickListener {
                            position = spinner.selectedItemPosition
                            binding.btnExTitle.text = items[position]

                            list.clear()
                            list.addAll(when (position) {
                                0, 1 -> listOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","전체 목표 칼로리","예상 소요 시간","예상 소요 거리")
                                else -> listOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","전체 목표 칼로리","예상 소요 시간")
                            })
                            val recyclerView = binding.exStartRecycler
                            recyclerView.layoutManager = LinearLayoutManager(requireContext())
                            val navController = NavHostFragment.findNavController(this@ExercisestartFragment)
                            val selectedExercise = exercises.find { it.exname == items[position] }
                            val excalValue = selectedExercise?.excal ?: 0
                            val contentList = MutableList(list.size) { 0.0 }
                            var navAdapter: ExStartAdapter? = null
                            navAdapter = ExStartAdapter(list, contentList, excalValue, this@ExercisestartFragment, navController, viewModel) { userInput ->
                                // userInput은 사용자가 입력한 값입니다.
                                // 여기에서 필요한 계산을 수행합니다.
                                val calculatedValue = userInput / excalValue
                                if (list.size > 3) {
                                    contentList[3] = calculatedValue
                                    navAdapter?.notifyItemChanged(3)
                                }
                                binding.btnStartBotton.isEnabled = true
                                binding.btnStartBotton.setBackgroundColor(Color.RED)
                                binding.btnStartBotton.setTextColor(Color.WHITE)
                            }
                            recyclerView.adapter = navAdapter
                            recyclerView.visibility = View.VISIBLE
                            dialog.dismiss()
                        }

                        cancelButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        etcGo.setOnClickListener {
                            findNavController().navigate(R.id.action_exercisestartFragment_to_etcExerciseFragment)
                            dialog.dismiss()
                        }


                        val dialogBack = inflater.inflate(R.layout.ex_info_dialog, null, false)
                        dialogBack.background = ColorDrawable(Color.TRANSPARENT)
                        dialog.show()
                    }

                }
                is Resource.Error -> {
                    // 오류 메시지를 표시하는 코드를 추가합니다.
                    Toast.makeText(context, "resource.message", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // 로딩 중임을 표시하는 코드를 추가합니다.
                }
            }
        })

        binding.exInfoGo.setOnClickListener{
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_exercisestartFragment_to_exerciseInfoFragment)
        }

        binding.btnStartBotton.setOnClickListener {
            if (position == 0 || position == 1) {
                // position이 0이나 1일 때의 동작
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_exercisestartFragment_to_fragment_search_location)
            } else {
                NavHostFragment.findNavController(this)
                    .navigate(R.id.action_exercisestartFragment_to_calcheckFragment)
            }
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

    fun onItemClick(position: Int) {

    }
}


