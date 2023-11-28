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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AddressListAdapter(private val address: MutableList<ItemDTO>, private val location: CoordinateDTO, private val listener: SearchAddressDialog): RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {



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
        fun bind(addressDTO: ItemDTO,location: CoordinateDTO){
            placeTitleTextView.text = addressDTO.title
            roadAddress.text = addressDTO.roadAddress
            distance.text = haversine(location.latidute,location.longitude,addressDTO.mapx.toDouble(),addressDTO.mapy.toDouble()).toString()+"km"


        }

        private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val R = 6371.0 // 지구의 반지름 (단위: km)

            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)

            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return R * c
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