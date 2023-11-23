package com.example.calcal.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.calcal.databinding.LocationFinderCardviewBinding
import com.example.calcal.modelDTO.LocationDTO

class LocationSearchAdapter: RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>() {
    private var location = listOf<LocationDTO>()
    class ViewHolder(val binding: LocationFinderCardviewBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(locationDTO: LocationDTO, clickListener: (LocationDTO) -> Unit){
            binding.locationList.text = locationDTO.name

            binding.root.setOnClickListener {
                clickListener(locationDTO)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
       val view = LocationFinderCardviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationDTO = location[position]


        holder.bind(locationDTO) { clickedLocation ->
            // 아이템 클릭 시 수행할 동작을 여기에 추가
            // clickedLocation을 이용해 클릭된 아이템에 접근 가능
        }
    }

    override fun getItemCount(): Int {

        return maxOf(2, location.size)
    }


    fun setLocationList(locationList : List<LocationDTO>){
        this.location = locationList
        notifyDataSetChanged()
    }
}