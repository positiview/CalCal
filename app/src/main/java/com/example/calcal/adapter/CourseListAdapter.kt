package com.example.calcal.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.modelDTO.CourseListDTO
import com.example.calcal.subFrag.SearchLocationFragment

class CourseListAdapter(private val courseList: List<CourseListDTO>, private val listener: SearchLocationFragment): RecyclerView.Adapter<CourseListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private val courseName: TextView = view.findViewById(R.id.course_name)
        private val courseSummary : TextView = view.findViewById(R.id.course_summary)
        private val courseDelete : ImageButton = view.findViewById(R.id.course_delete)

        init {
            view.setOnClickListener(this)
            courseDelete.setOnClickListener {
                showDeleteClickDialog(adapterPosition)
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(courseList[position])
            }
        }

        fun bind(cList: CourseListDTO) {
            Log.d("CourseListAdapter", "bind - 코스 이름: ${cList.courseName}, 루트 개수: ${cList.placeList.size}")
            courseName.text = cList.courseName
            courseSummary.text = "${cList.placeList.size} 개 루트"
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

    private fun showDeleteClickDialog(position: Int) {
        val course = courseList[position]
        val alertDialogBuilder = AlertDialog.Builder(listener.requireContext())
        alertDialogBuilder.setTitle("삭제")
        alertDialogBuilder.setMessage("정말로 '${course.courseName}'을(를) 삭제하시겠습니까?")
        alertDialogBuilder.setPositiveButton("예") { _, _ ->
            // 해당 position에 대한 삭제 처리 로직 작성
            // ...
        }
        alertDialogBuilder.setNegativeButton("취소", null)
        alertDialogBuilder.create().show()
    }
}
