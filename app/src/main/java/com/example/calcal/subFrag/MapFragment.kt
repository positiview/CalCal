package com.example.calcal.subFrag

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.FragmentMapBinding
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.repository.CourseRepository
import com.example.calcal.repository.CourseRepositoryImpl
import com.example.calcal.util.Resource
import com.example.calcal.viewModel.CourseViewModel
import com.example.calcal.viewModelFactory.CourseViewModelFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.LocationButtonView


class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding : FragmentMapBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var mNaverMap: NaverMap
    private lateinit var uiSettings: UiSettings
    private lateinit var btn_back:ImageView

    private val courseRepository: CourseRepository = CourseRepositoryImpl()
    private val courseViewModelFactory = CourseViewModelFactory(courseRepository)
    private val viewModel: CourseViewModel by lazy {
        ViewModelProvider(this, courseViewModelFactory).get(CourseViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater,container, false)
        val view = binding.root
        val options = NaverMapOptions()
            .mapType(NaverMap.MapType.Terrain)
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(com.example.calcal.R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(com.example.calcal.R.id.map, it).commit()
            }

        mapFragment.getMapAsync(this)

        locationSource =
            FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        binding.apply {
            toggleCourse.textOff = null
            toggleCourse.textOn = null
            toggleCourse.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    toggleCourse.setBackgroundResource(R.drawable.ic_minus_shape)
                    courseRecode.visibility = View.VISIBLE
                }else{
                    toggleCourse.setBackgroundResource(R.drawable.ic_plus_shape)
                    toggleCourse.visibility = View.GONE
                }
            }

            selectCourse.setOnClickListener{
                findNavController().navigate(R.id.action_mapFragment_to_searchLocationFragment)
            }

        }

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }

        viewModel.getCourse.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    val courseList = it.data


                }

                is Resource.Loading -> {
                    // 로딩 중 처리
                }

                is Resource.Error -> {
                    // 에러 처리
                    Log.e("$$", "에러발생!!!")
                }
            }
        }



        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                mNaverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap
        mNaverMap.locationSource = locationSource
        uiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = false
        val locationButtonView: LocationButtonView = binding.locationView
        locationButtonView.map = mNaverMap
        // 초기 위치 설정
        mNaverMap.locationTrackingMode = LocationTrackingMode.Follow
        var initialPosition = LatLng(35.1798159, 129.0750222) // 부산 시청
        mNaverMap.addOnLocationChangeListener(object : NaverMap.OnLocationChangeListener {
            override fun onLocationChange(location: Location) {
                initialPosition = LatLng(location.latitude, location.longitude)

                // 내 위치를 설정한 후에 리스너를 제거
                mNaverMap.removeOnLocationChangeListener(this)
            }
        })

        val cameraPosition = CameraPosition(initialPosition, 17.0)
        mNaverMap.moveCamera(com.naver.maps.map.CameraUpdate.toCameraPosition(cameraPosition))
        mNaverMap.maxZoom = 18.0
        mNaverMap.minZoom = 5.0

    }



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
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