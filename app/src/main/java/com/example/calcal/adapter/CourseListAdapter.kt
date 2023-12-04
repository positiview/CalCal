package com.example.calcal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.subFrag.SearchLocationFragment

class CourseListAdapter(private val courseList: MutableList<CourseListDTO>, private val listener: SearchLocationFragment): RecyclerView.Adapter<CourseListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private val courseName: TextView = view.findViewById(R.id.course_name)
        private val courseSummary : TextView = view.findViewById(R.id.course_summary)

        init{
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(courseList[position])
            }
        }

        fun bind(cList:CourseListDTO){
            courseName.text = cList.courseName
            // placeList가 null인지 확인
            val placeListSize = cList.placeList?.size ?: 0
            courseSummary.text = "$placeListSize 개 루트"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CourseListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item_cardview, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseListAdapter.ViewHolder, position: Int) {
        val cList = courseList[position]

        holder.bind(cList)
    }

    override fun getItemCount(): Int = courseList.size
}