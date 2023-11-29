package com.example.calcal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.calcal.R

class ExInfoExpandableListAdapter(
    private val context: Context,
    private val list: List<String>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return list.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        // 각 그룹에 대한 자식 수를 반환하는 코드를 추가해야 합니다.
        return 1 // 일단 각 그룹에 대해 하나의 자식 아이템이 있다고 가정했습니다.
    }

    override fun getGroup(groupPosition: Int): Any {
        return list[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        // 각 자식 항목을 반환하는 코드를 추가해야 합니다.
        return Any() // 일단 비어있는 객체를 반환하도록 설정했습니다.
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

        val titleText = view.findViewById<TextView>(R.id.ex_info_title_text)
        titleText.text = getGroup(groupPosition) as String

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
        val view = inflater.inflate(R.layout.list_item, parent, false) // 여기서 your_child_layout은 자식 뷰에 해당하는 레이아웃 파일 이름입니다.

        val infoText = view.findViewById<TextView>(R.id.ex_info_info)
        // infoText.text에 각 자식의 정보를 설정해야 합니다.

        val calText = view.findViewById<TextView>(R.id.ex_info_cal)
        // calText.text에 각 자식의 칼로리 정보를 설정해야 합니다.

        val timeText = view.findViewById<TextView>(R.id.ex_info_time)
        // timeText.text에 각 자식의 권장 시간을 설정해야 합니다.

        val imageView = view.findViewById<ImageView>(R.id.ex_info_img)
        // imageView에 각 자식의 이미지를 설정해야 합니다.

        // '운동하러가기' 텍스트뷰의 가시성 설정
        val goToExerciseText = view.findViewById<TextView>(R.id.ex_info_go)
        goToExerciseText.visibility = View.VISIBLE

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}