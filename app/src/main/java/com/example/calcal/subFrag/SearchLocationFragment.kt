package com.example.calcal.subFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.calcal.adapter.LocationSearchAdapter
import com.example.calcal.databinding.FragmentSearchLocationBinding

class SearchLocationFragment:Fragment() {
    private lateinit var binding : FragmentSearchLocationBinding
    private lateinit var locationSearchAdapter: LocationSearchAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchLocationBinding.inflate(inflater,container,false)
        val view = binding.root

        locationSearchAdapter = LocationSearchAdapter()


        return view
    }
}