package com.example.calcal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.modelDTO.RouteRecordDTO
import com.example.calcal.subFrag.HistoryFragment
import java.text.SimpleDateFormat
import java.util.Locale

class RecordListAdapter(private val recordList: List<RouteRecordDTO>, private val listener: HistoryFragment) : RecyclerView.Adapter<RecordListAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{

        private val recordName: TextView = view.findViewById(R.id.record_name)
        private val calorieRecord: TextView= view.findViewById(R.id.record_calorie)
        private val dateRecord: TextView = view.findViewById(R.id.record_date)
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(recordList[position])

            }
        }

        fun bind(rList: RouteRecordDTO) {
            recordName.text = rList.courseName
            Log.d("$$","rlist courseName : ${rList.courseName}")
            calorieRecord.text = rList.calorie.toInt().toString()
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
            val outputFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.KOREA)

            val date = inputFormat.parse(rList.regDate)
            val formattedDate = outputFormat.format(date)
            dateRecord.text = formattedDate
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item_cardview,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordListAdapter.ViewHolder, position: Int) {
        val rList = recordList[position]
        holder.bind(rList)
        // 가장 최근 항목을 자동으로 클릭
        if (position == itemCount - 1) {
            holder.itemView.performClick()
        }
    }

    override fun getItemCount(): Int = recordList.size


}