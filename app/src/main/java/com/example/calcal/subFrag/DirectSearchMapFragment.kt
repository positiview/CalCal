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
import androidx.fragment.app.DialogFragment
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

    fun setCurrentLocation(location: CoordinateDTO) {
        currentLocation = LatLng(location.latidute, location.longitude)
    }

    private var waypointTextView: TextView? = null

    fun setWaypointTextView(textView: TextView) {
        waypointTextView = textView
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
        currentLocation?.let {
            val cameraPosition = CameraPosition(
                LatLng(it.latitude, it.longitude),
                16.0
            )
            naverMap.cameraPosition = cameraPosition
        }
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
}
