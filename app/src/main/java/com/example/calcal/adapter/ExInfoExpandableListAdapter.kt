package com.example.calcal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import com.example.calcal.R
import com.example.calcal.modelDTO.ExerciseDTO

class ExInfoExpandableListAdapter(
    private val context: Context,
    private val list: List<ExerciseDTO>,
    private val navController: NavController
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return list.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        // 각 그룹에 대한 자식 수를 반환하는 코드를 추가해야 합니다.
        return 1 // 일단 각 그룹에 대해 하나의 자식 아이템이 있다고 가정했습니다.
    }

    override fun getGroup(groupPosition: Int): ExerciseDTO { // 반환 타입을 ExerciseDTO로 변경
        return list[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): ExerciseDTO {
        // 각 자식 항목을 반환하는 코드를 추가해야 합니다.
        return list[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_group, parent, false) // 여기서 your_group_layout은 그룹 뷰에 해당하는 레이아웃 파일 이름입니다.
        val exerciseDto = getGroup(groupPosition) as ExerciseDTO
        val titleText = view.findViewById<TextView>(R.id.ex_info_title_text)
        val titleBack = view.findViewById<LinearLayout>(R.id.ex_info_title)



        titleText.text = exerciseDto.exname


        if (isExpanded) {
            titleText.visibility = View.GONE
            titleBack.visibility = View.GONE
        } else {
            titleText.visibility = View.VISIBLE
            titleBack.visibility = View.VISIBLE
        }

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        val exerciseDto = getChild(groupPosition, childPosition) as ExerciseDTO

        val infoText = view.findViewById<TextView>(R.id.ex_info_info)
        infoText.text = exerciseDto.excontent

        val calText = view.findViewById<TextView>(R.id.ex_info_cal)
        calText.text = exerciseDto.excal.toString()

        val timeText = view.findViewById<TextView>(R.id.ex_info_time)
        timeText.text = exerciseDto.extime.toString()

        val imageView = view.findViewById<ImageView>(R.id.ex_info_img)


        // '운동하러가기' 텍스트뷰의 가시성 설정
        val goToExerciseText = view.findViewById<TextView>(R.id.ex_info_go)
        goToExerciseText.visibility = View.VISIBLE
        goToExerciseText.setOnClickListener {
            // 여기서 actionId는 네비게이션 그래프에서 해당 페이지로 이동하는 액션의 ID입니다.
            navController.navigate(R.id.action_exerciseInfoFragment_to_exercisestartFragment)
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}