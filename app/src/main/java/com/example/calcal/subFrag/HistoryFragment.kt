package com.example.calcal.subFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calcal.databinding.FragmentHistoryBinding
import com.example.calcal.databinding.FragmentMapBinding

class HistoryFragment :Fragment(){

    private lateinit var binding : FragmentHistoryBinding
            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container, false)
        val view = binding.root



        return  view

    }
}