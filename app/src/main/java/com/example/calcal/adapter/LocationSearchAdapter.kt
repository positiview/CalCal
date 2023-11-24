package com.example.calcal.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.calcal.R
import com.example.calcal.databinding.LocationFinderCardviewBinding
import com.example.calcal.modelDTO.LocationDTO

class LocationSearchAdapter(private val onClickListener: (LocationDTO) -> Unit): RecyclerView.Adapter<LocationSearchAdapter.ViewHolder>() {
    private var locationList = listOf<LocationDTO>()
    class ViewHolder(private val binding: LocationFinderCardviewBinding, private val onClickListener: (LocationDTO) -> Unit): RecyclerView.ViewHolder(binding.root){

        fun bind(locations: LocationDTO){
            binding.locationList.text = locations.name + locations.location

            binding.root.setOnClickListener {
                onClickListener(locations)

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
       val view = LocationFinderCardviewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(view,onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locations = locationList[position]


        holder.bind(locations)
    }

    override fun getItemCount(): Int {

        return maxOf(2, locationList.size)
    }


    fun setLocationList(locationList : List<LocationDTO>){
        this.locationList = locationList
        notifyDataSetChanged()
    }
}