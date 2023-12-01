package com.example.calcal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.modelDTO.CoordinateDTO
import com.example.calcal.modelDTO.ItemDTO
import com.example.calcal.subFrag.SearchAddressDialog
import com.naver.maps.geometry.LatLng
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AddressListAdapter(private val address: MutableList<ItemDTO>, private val location: LatLng, private val listener: SearchAddressDialog): RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {



    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private val placeTitleTextView: TextView = view.findViewById(R.id.place_title)
        private val roadAddress :TextView = view.findViewById(R.id.address)
        private val distance: TextView = view.findViewById(R.id.distance)
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(address[position])
            }
        }
        fun bind(addressDTO: ItemDTO,location: LatLng){
            placeTitleTextView.text = addressDTO.title
            roadAddress.text = addressDTO.roadAddress
            distance.text = haversine(location.latitude,location.longitude,addressDTO.mapy.toDouble(),addressDTO.mapx.toDouble()).toString()+"km"
            Log.d("$$","distance 값 : ${distance.text}")

        }

        private fun haversine(lat1: Double, lon1: Double, lat22: Double, lon22: Double): Double {
            val R = 6371.0 // 지구의 반지름 (단위: km)
            val lat2 = lat22/ 10000000.0
            val lon2 = lon22/ 10000000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)

//            val c = 2 * asin(sqrt(a))
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            val result = R * c

            return if (result >= 10) {
                result.toInt().toDouble() // 10 이상인 경우 소수점을 표시하지 않고 정수 부분만 반환
            } else {
                String.format("%.2f", result).toDouble() // 10 미만인 경우 소수점 두 자리까지 표시
            }
        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = AddressItemCardviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item_cardview, parent, false)



        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressDTO = address[position]

        Log.d("$$","onBindViewHolder 작동")
        holder.bind(addressDTO,location)
    }

    override fun getItemCount(): Int = address.size




    /*fun setAddressList(locations: CoordinateDTO, addressList: List<ItemDTO>){
        this.address = addressList
        this.location= locations
        notifyDataSetChanged()
    }
*/


}