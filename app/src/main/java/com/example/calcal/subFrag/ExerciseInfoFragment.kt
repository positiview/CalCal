package com.example.calcal.subFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.calcal.adapter.ExInfoExpandableListAdapter
import com.example.calcal.databinding.FragmentExerciseInfoBinding


class ExerciseInfoFragment : Fragment() {
    private lateinit var binding:FragmentExerciseInfoBinding
    private lateinit var btn_back : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

        val adapter = ExInfoExpandableListAdapter(requireContext(), list)
        binding.exerciseInfoRecycler.setAdapter(adapter)

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