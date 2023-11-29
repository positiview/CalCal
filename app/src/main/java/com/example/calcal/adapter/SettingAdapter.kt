package com.example.calcal.adapter;

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View;
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R

import com.example.calcal.mypagefrag.SettingFragment;

class SettingAdapter(private val mData: List<String>, private val listener:SettingFragment): RecyclerView.Adapter<SettingAdapter.ViewHolder>()   {
    inner class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val settingList: TextView = itemView.findViewById(R.id.setting_list)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val settingSwitch: Switch = itemView.findViewById(R.id.setting_switch)
        val settingListView:View = itemView.findViewById(R.id.setting_list_view)
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.setting_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = mData[position]
        holder.settingList.text = text


    }

}