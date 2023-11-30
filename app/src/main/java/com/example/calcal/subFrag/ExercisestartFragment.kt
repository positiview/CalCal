package com.example.calcal.subFrag

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.adapter.ExStartAdapter
import com.example.calcal.adapter.GraphAdapter
import com.example.calcal.databinding.FragmentExercisestartBinding


class ExercisestartFragment : Fragment() {
    private lateinit var binding: FragmentExercisestartBinding
    private lateinit var btn_back: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExercisestartBinding.inflate(inflater, container, false)

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.btnExTitle.setOnClickListener {
            val items = arrayOf("항목 1", "항목 2", "항목 3", "항목 4"
        )
            val builder = AlertDialog.Builder(requireContext())
            binding.exStartRecycler.visibility = View.GONE
            binding.exStartRecycler.layoutManager = LinearLayoutManager(context)
            builder.setTitle(null)
            builder.setItems(items) { dialog, which ->
                binding.btnExTitle.text = items[which]

                val list = when (which) {
                    0 -> arrayListOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","남은 전체 목표 칼로리","예상 소요 시간","예상 소요 거리")
                    1 -> arrayListOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","남은 전체 목표 칼로리","예상 소요 시간","예상 소요 거리")
                    // 이외의 경우
                    else -> arrayListOf("시간당 예상 소모 칼로리", "목표 소모 칼로리 ","남은 전체 목표 칼로리","예상 소요 시간")
                }
                val recyclerView = binding.exStartRecycler
                val navController = NavHostFragment.findNavController(this)
                val adapter = ExStartAdapter(list, this, navController)
                recyclerView.adapter = adapter
                recyclerView.visibility = View.VISIBLE
            }
            builder.show()

        }
        binding.btnStartBotton.setOnClickListener{
            //if문으로 다른 프래그먼트로 보내야함
            NavHostFragment.findNavController(this)
            .navigate(R.id.action_exercisestartFragment_to_fragment_search_location)}


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


