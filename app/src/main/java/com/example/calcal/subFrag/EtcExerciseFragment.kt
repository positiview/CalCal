package com.example.calcal.subFrag

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
import com.example.calcal.adapter.EtcAdapter
import com.example.calcal.databinding.FragmentEtcExerciseBinding

class EtcExerciseFragment : Fragment() {
    private lateinit var binding:FragmentEtcExerciseBinding
    private lateinit var btn_back : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        list.add("시간당 칼로리 소모량 설정")
        list.add("권장 시간 설정")
        list.add("이동 여부")

        val adapter = EtcAdapter(list, this)
        recyclerView.adapter = adapter
        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.btnStartBotton.setOnClickListener{
            //if문으로 다른 프래그먼트로 보내야함
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_exercisestartFragment_to_exerciseInfoFragment)
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