package com.example.calcal.subFrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.calcal.R
import com.example.calcal.databinding.FragmentHistoryBinding
import com.example.calcal.databinding.FragmentMapBinding
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.RecordViewModelFactory
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback

class HistoryFragment :Fragment(), OnMapReadyCallback {

    private lateinit var mNaverMap: NaverMap
    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }

    private lateinit var binding : FragmentHistoryBinding
            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container, false)
        val view = binding.root

        val options = NaverMapOptions()
            .mapType(NaverMap.MapType.Terrain)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)


        return  view

    }

    override fun onMapReady(nMap: NaverMap) {
        mNaverMap = nMap


        recordViewModel.getRecord.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> {

                }
                is Resource.Success -> {

                }
                else -> {

                }
            }
        }
    }
}