package com.example.calcal.subFrag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.R
import com.example.calcal.adapter.ExInfoExpandableListAdapter
import com.example.calcal.databinding.FragmentExerciseInfoBinding
import com.example.calcal.repository.ExerciseRepositoryImpl
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.ExerciseViewModel
import com.example.calcal.viewModelFactory.ExerciseViewModelFactory


class ExerciseInfoFragment : Fragment() {
    private lateinit var binding:FragmentExerciseInfoBinding
    private lateinit var btn_back : Button
    private lateinit var viewModel: ExerciseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = ExerciseRepositoryImpl()
        viewModel = ViewModelProvider(this, ExerciseViewModelFactory(repository))[ExerciseViewModel::class.java]
        viewModel.getAllExercises()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseInfoBinding.inflate(inflater, container, false)

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }



        val navController = findNavController()

        viewModel.exerciseList.observe(viewLifecycleOwner, Observer { resource ->
            Log.d("ExerciseInfoFragment", "exerciseList resource: $resource")
            when(resource) {
                is Resource.Success -> {
                    val exercises = resource.data
                    // ExerciseDTO 객체의 리스트를 생성
                    val exerciseDTOs = exercises
                    val adapter = ExInfoExpandableListAdapter(requireContext(), exerciseDTOs, navController)
                    binding.exerciseInfoRecycler.setAdapter(adapter)
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


        binding.btnExPlus.setOnClickListener {
            findNavController().navigate(R.id.action_exerciseInfoFragment_to_etcExerciseFragment)
        }

        binding.exerciseInfoRecycler.setOnGroupExpandListener(object : ExpandableListView.OnGroupExpandListener {
            var previousGroup = -1

            override fun onGroupExpand(groupPosition: Int) {
                if (groupPosition != previousGroup)
                    binding.exerciseInfoRecycler.collapseGroup(previousGroup)
                previousGroup = groupPosition
            }
        })

        return binding.root
    }

    fun onItemClick(position: Int) {

    }


}