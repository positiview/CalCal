import android.content.Context
import android.graphics.Point
import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.FragmentDirectSearchMapBinding
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.DeviceSizeDTO
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import java.util.Locale

class DirectSearchMapFragment<Location> : DialogFragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentDirectSearchMapBinding
    private lateinit var mapFragment: MapFragment
    private lateinit var naverMap: NaverMap
    private lateinit var marker: Marker
    private lateinit var addressTextView: TextView
    private var currentLocation: LatLng? = null
    private var selectedAddress: String? = null

    fun setSelectedAddress(address: String) {
        selectedAddress = address
    }


    fun setCurrentLocation(location: CoordinateDTO) {
        currentLocation = LatLng(location.latidute, location.longitude)
    }

    private var waypointTextView: TextView? = null

    fun setWaypointTextView(textView: TextView) {
        waypointTextView = textView
    }
    private fun getLatLngFromAddress(address: String): LatLng {
        val geocoder = Geocoder(requireContext(), Locale.KOREA)
        val addresses = geocoder.getFromLocationName(address, 1)

        if (addresses != null && addresses.isNotEmpty()) {
            val location = addresses[0]
            return LatLng(location.latitude, location.longitude)
        }

        return LatLng(0.0, 0.0) // 주소를 찾을 수 없을 경우 기본 값 반환
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.let { window ->
            val params = window.attributes
            // 다이얼로그를 화면의 상단에 위치하도록 설정
            params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            // 화면의 상단부터 일정 거리를 띄우고 싶다면 아래와 같이 설정할 수 있습니다.
            params.y = (resources.displayMetrics.heightPixels * .18).toInt() // 상단으로부터 20% 지점에 위치
            window.attributes = params
        }
        binding = FragmentDirectSearchMapBinding.inflate(inflater, container, false)

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment
        mapFragment.getMapAsync(this)

        // Arguments로부터 전달된 주소값을 가져옴
        val selectedAddress = arguments?.getString("selectedAddress")


        addressTextView = binding.addressTextView

        return binding.root
    }


    override fun onMapReady(map: NaverMap) {
        naverMap = map


        // 중앙에 마커 추가
        marker = Marker()
        marker.position = naverMap.cameraPosition.target
        marker.map = naverMap



        // 카메라 이동 이벤트 감지
        naverMap.addOnCameraChangeListener { _, _ ->
            marker.position = naverMap.cameraPosition.target
            val address = getAddressFromLatLng(marker.position)
            addressTextView.text = address
        }


        // 현재 위치를 가져오는 로직 추가
//        currentLocation?.let {
//            val cameraPosition = CameraPosition(
//                LatLng(it.latitude, it.longitude),
//                16.0
//            )
//            naverMap.cameraPosition = cameraPosition
        val latLng = selectedAddress?.let { getLatLngFromAddress(it) } // 검색된 주소를 좌표로 변환
        val cameraPosition = latLng?.let { CameraPosition(it, 16.0) } // 변환된 좌표를 중심으로 카메라 위치 설정
        if (cameraPosition != null) {
            naverMap.cameraPosition = cameraPosition
        } // 카메라 위치 적용

    }


    private fun getAddressFromLatLng(latLng: LatLng): String {
        val geocoder = Geocoder(requireContext(), Locale.KOREA) // Locale을 한국어로 설정
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses != null) {
            return if (addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "주소를 찾을 수 없습니다."
            }
        }

        return "주소를 찾을 수 없습니다."
    }


    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNavigation()
        fragmentSize { deviceSizeDTO ->
            val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
            val deviceWidth = deviceSizeDTO.deviceWidth
            params?.width = (deviceWidth * 0.9).toInt()
            dialog?.window?.attributes = params as WindowManager.LayoutParams

            // 확인 버튼 클릭 시 주소 가져와서 텍스트 표시
            binding.addressOk.setOnClickListener {
                val address = getAddressFromLatLng(marker.position)
                waypointTextView?.text = address
                dismiss()
            }
        }
    }

    private fun fragmentSize(callback: (DeviceSizeDTO) -> Unit){
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)

        val deviceSizeDTO = DeviceSizeDTO(deviceWidth = size.x, deviceHeight = size.y)
        callback(deviceSizeDTO)

    }


    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation()
    }

}
