package com.example.calcal.subFrag

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.adapter.RecordListAdapter
import com.example.calcal.databinding.FragmentHistoryBinding
import com.example.calcal.modelDTO.RouteAndTimeDTO
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.repository.RecordRepository
import com.example.calcal.repository.RecordRepositoryImpl
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.subFrag.MapFragment.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.example.calcal.util.CustomLoading
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.RecordViewModel
import com.example.calcal.viewModelFactory.RecordViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource

class HistoryFragment :Fragment(), OnMapReadyCallback {
    private lateinit var recordListAdapter: RecordListAdapter
    private lateinit var mNaverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val recordRepository: RecordRepository = RecordRepositoryImpl()
    private val recordViewModelFactory = RecordViewModelFactory(recordRepository)
    private val recordViewModel: RecordViewModel by viewModels() { recordViewModelFactory }
    private lateinit var sharedPreferences: SharedPreferences

    lateinit var behavior: BottomSheetBehavior<LinearLayout>

    private lateinit var binding : FragmentHistoryBinding
            override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater,container, false)
        binding.txtEmpty.visibility = View.GONE
        val view = binding.root

        val options = NaverMapOptions()
            .mapType(NaverMap.MapType.Terrain)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        sharedPreferences = requireContext().getSharedPreferences(LoginActivity.PREF_NAME, Context.MODE_PRIVATE)
        val storedEmail = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")
        if (storedEmail != null){
            recordViewModel.getRecord(storedEmail)
        }



        initEvent()


        val recyclerView = binding.recyclerRecord
        recyclerView.layoutManager = LinearLayoutManager(context)







        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel.getRecentRecord.observe(viewLifecycleOwner){

            val list: List<RouteAndTimeDTO> = it
            Log.d("$$","이동한 경로 : $list")
            val coords: List<LatLng> = list.map { LatLng(it.latitude, it.longitude) }
            val cameraUpdate = CameraUpdate.fitBounds(calculateBounds(coords), 100)
            val path = PathOverlay()
            path.coords = coords
            path.color = Color.GREEN
            path.passedColor = Color.GRAY
            path.width = 10
            path.patternImage = OverlayImage.fromResource(R.drawable.shevron)
            path.patternInterval = 10

            val startTime = list.first().recordTime
            val endTime = list.last().recordTime

            val timeRange = endTime - startTime

            for (dto in list) {
                val currentTime = dto.recordTime // 현재 DTO의 시간 값을 가져옵니다.

                val progress = if (timeRange > 0) {
                    (currentTime - startTime).toFloat() / timeRange
                } else {
                    0F
                }

                // progress 값에 따라 경로의 진척률을 업데이트합니다.
                path.progress = progress.toDouble()

            }
            path.map = mNaverMap
            mNaverMap.moveCamera(cameraUpdate)
        }
        recordViewModel.getRecord.observe(viewLifecycleOwner){
            Log.d("$$","getRecord Observe@!!")
            when(it){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    if(it.data == null){
                        Toast.makeText(requireContext(),"기록이 없습니다",Toast.LENGTH_SHORT).show()
                        binding.txtEmpty.visibility = View.VISIBLE
                    }else{
                        Log.d("$$","getRecord 성공성공!!")
                        recordListAdapter = RecordListAdapter(it.data,this)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(),it.string,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initEvent() {
        persistentBottomSheetEvent()
//        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

    }


    private fun persistentBottomSheetEvent() {
        // BottomSheetBehavior 초기화
        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.isFitToContents = false




        // 드래그 이벤트 처리
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 이벤트 처리
                // slideOffset은 0 (완전히 닫힘)에서 1 (완전히 열림)까지의 값입니다.
                // 여기에 슬라이드에 따른 추가적인 로직을 넣을 수 있습니다.
            }
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // 상태가 변경될 때 호출됩니다.

                // 여기에 상태에 따른 추가적인 로직을 넣을 수 있습니다.
                when(newState){
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("$$", "onStageChanged: 접음")
                    }
                    BottomSheetBehavior.STATE_DRAGGING ->{
                        Log.d("$$","onStateChanged: 드래그")
                    }
                    BottomSheetBehavior.STATE_EXPANDED ->{
                        Log.d("$$","onStateChanged: 펼침")
                    }
                    BottomSheetBehavior.STATE_HIDDEN ->{
                        Log.d("$$","onStageChanged: 숨김")
                    }
                    BottomSheetBehavior.STATE_SETTLING->{
                        Log.d("$$","onStageChanged: 고정됨")
                    }
                }

            }

        })
    }
    fun onItemClick(routeAndTimeDTO: RouteRecordDTO) {

    }
    fun calculateBounds(coords: List<LatLng>): LatLngBounds {
        var minLat = Double.MAX_VALUE
        var maxLat = Double.MIN_VALUE
        var minLng = Double.MAX_VALUE
        var maxLng = Double.MIN_VALUE

        for (coord in coords) {
            minLat = minOf(minLat, coord.latitude)
            maxLat = maxOf(maxLat, coord.latitude)
            minLng = minOf(minLng, coord.longitude)
            maxLng = maxOf(maxLng, coord.longitude)
        }

        return LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng))
    }
    override fun onMapReady(nMap: NaverMap) {
        mNaverMap = nMap

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mNaverMap.locationSource = locationSource


        // 지도상에 이동한 경로를 보여준다.

    }
    override fun onResume() {
        super.onResume()

        (activity as? MainActivity)?.hideBottomNavigation()

    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation()
    }


}