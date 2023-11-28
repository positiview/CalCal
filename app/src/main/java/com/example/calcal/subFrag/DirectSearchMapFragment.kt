import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.calcal.R
import com.example.calcal.databinding.FragmentDirectSearchMapBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class DirectSearchMapFragment : DialogFragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentDirectSearchMapBinding
    private lateinit var mapFragment: MapFragment
    private lateinit var naverMap: NaverMap
    private lateinit var marker: Marker
    private lateinit var addressTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    }

    private fun getAddressFromLatLng(latLng: LatLng): String {
        // 좌표를 주소로 변환하는 로직을 구현하세요.
        // 예시로 주소를 "서울특별시 강남구"로 가정합니다.
        return "서울특별시 강남구"
    }
}
