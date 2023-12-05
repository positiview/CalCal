package com.example.calcal.subFrag

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.adapter.ExStartAdapter
import com.example.calcal.adapter.GraphAdapter
import com.example.calcal.databinding.FragmentExercisestartBinding
import com.example.calcal.viewModel.ExerciseViewModel


class ExercisestartFragment : Fragment() {
    private lateinit var binding: FragmentExercisestartBinding
    private lateinit var btn_back: Button
    private lateinit var viewModel: ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ExerciseViewModel::class.java)

    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExercisestartBinding.inflate(inflater, container, false)
        viewModel.exerciseList.observe(viewLifecycleOwner) { exercises ->

        }
        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.btnExTitle.setOnClickListener {
            val items = arrayOf("항목 1", "항목 2", "항목 3", "항목 4")
            val builder = AlertDialog.Builder(requireContext(), R.style.DialogTheme)
            val inflater = LayoutInflater.from(requireContext())
            val view = inflater.inflate(R.layout.ex_info_dialog, null, false)

            val spinner = view.findViewById<Spinner>(R.id.dialog_input)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            builder.setView(view)
            val dialog = builder.create()

            val okButton = view.findViewById<Button>(R.id.dialog_ok)
            val cancelButton = view.findViewById<Button>(R.id.dialog_cancel)
            val etcGo = view.findViewById<TextView>(R.id.ex_etc_go)

            okButton.setOnClickListener {
                val position = spinner.selectedItemPosition
                binding.btnExTitle.text = items[position]

                val list = when (position) {
                    0 -> arrayListOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","남은 전체 목표 칼로리","예상 소요 시간","예상 소요 거리")
                    1 -> arrayListOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","남은 전체 목표 칼로리","예상 소요 시간","예상 소요 거리")
                    else -> arrayListOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","남은 전체 목표 칼로리","예상 소요 시간")
                }
                val recyclerView = binding.exStartRecycler
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val navController = NavHostFragment.findNavController(this@ExercisestartFragment)
                val navAdapter = ExStartAdapter(list, this@ExercisestartFragment, navController)
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
        binding.exInfoGo.setOnClickListener{
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_exercisestartFragment_to_exerciseInfoFragment)
        }
        binding.btnStartBotton.setOnClickListener{
            //if문으로 다른 프래그먼트로 보내야함
            NavHostFragment.findNavController(this)
            .navigate(R.id.action_exercisestartFragment_to_fragment_search_location)
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


