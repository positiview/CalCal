package com.example.calcal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import at.grabner.circleprogress.CircleProgressView
import com.example.calcal.R
import com.example.calcal.mainFrag.NotiFragment
import com.example.calcal.subFrag.EtcExerciseFragment

class EtcAdapter(private val mData: List<String>, private val listener: EtcExerciseFragment): RecyclerView.Adapter<EtcAdapter.ViewHolder>()  {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val exEtcContent: TextView = itemView.findViewById(R.id.ex_etc_content)
        val exEtcInput:TextView = itemView.findViewById(R.id.ex_etc_input)
        val exEtcSwitch: SwitchCompat = itemView.findViewById(R.id.ex_etc_switch)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ex_etc_list, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = mData[position]
        holder.exEtcContent.text = text

        if (position == 4) {
            holder.exEtcInput.visibility = View.GONE
            holder.exEtcSwitch.visibility = View.VISIBLE

        } else {
            holder.exEtcInput.visibility = View.VISIBLE
            holder.exEtcSwitch.visibility = View.GONE
            holder.exEtcInput.setText("")
        }
    }

    override fun getItemCount(): Int = mData.size




}
