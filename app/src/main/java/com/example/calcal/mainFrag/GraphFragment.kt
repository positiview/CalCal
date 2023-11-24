package com.example.calcal.mainFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.R
import com.example.calcal.adapter.GraphAdapter
import com.example.calcal.adapter.MypageAdapter
import com.example.calcal.databinding.FragmentGraphBinding
import com.example.calcal.databinding.FragmentMypageBinding


class GraphFragment : Fragment() {
    private lateinit var binding: FragmentGraphBinding
    private lateinit var btn_back : Button
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraphBinding.inflate(inflater, container, false)


        val recyclerView = binding.graphrecycler
        recyclerView.layoutManager = LinearLayoutManager(context)

        val list = ArrayList<String>()
        list.add("그래프")
        list.add("디비값")
        list.add("요소 추가")

        val adapter = GraphAdapter(list, this)
        recyclerView.adapter = adapter

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        return binding.root
    }

    fun onItemClick(position: Int) {
        // 여기서 프래그먼트 네비게이션 라이브러리를 사용하여 다른 프래그먼트로 이동합니다.
        when (position) {


        }
    }
}