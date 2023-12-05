package com.example.calcal.subFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.R
import com.example.calcal.adapter.ExInfoExpandableListAdapter
import com.example.calcal.databinding.FragmentExerciseInfoBinding
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.repository.ExerciseRepositoryImpl
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

        val list = listOf("러닝", "축구")

        val navController = findNavController()
        val adapter = ExInfoExpandableListAdapter(requireContext(), list, navController)
        binding.exerciseInfoRecycler.setAdapter(adapter)

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