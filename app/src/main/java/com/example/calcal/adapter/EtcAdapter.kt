package com.example.calcal.adapter

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.calcal.R
import com.example.calcal.modelDTO.ExerciseDTO
import com.example.calcal.repository.ExerciseRepository
import com.example.calcal.signlogin.LoginActivity
import com.example.calcal.subFrag.EtcExerciseFragment

class EtcAdapter(
    private val mData: ArrayList<String>,
    private val listener: OnDataChangedListener,
    private val exerciseRepository: ExerciseRepository,
    private val exercises: MutableList<ExerciseDTO>,
    sharedPreferences: SharedPreferences
) :
    RecyclerView.Adapter<EtcAdapter.ViewHolder>() {
    val newEmail = sharedPreferences.getString(LoginActivity.KEY_EMAIL, "")

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val exEtcContent: TextView = itemView.findViewById(R.id.ex_etc_content)
        val exEtcInput: TextView = itemView.findViewById(R.id.ex_etc_input)
        val exEtcSwitch: SwitchCompat = itemView.findViewById(R.id.ex_etc_switch)
        var textWatcher: TextWatcher? = null


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

    override fun getItemCount(): Int = mData.size

    interface OnDataChangedListener {
        fun onDataChanged(isAllFilled: Boolean)
        abstract fun onItemClick(position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (position < mData.size) {
            val text = mData[position]

            holder.exEtcContent.text = text

            val exercise = exercises[0]  // exercises 리스트의 첫 번째 객체에 모든 데이터를 저장

            holder.textWatcher?.let { holder.exEtcInput.removeTextChangedListener(it) }

            holder.textWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    when (position) {
                        0 -> exercise.exname = s.toString()
                        1 -> exercise.excontent = s.toString()
                        2 -> exercise.excal = s?.toString()?.toIntOrNull() ?: 0
                        3 -> exercise.extime = s?.toString()?.toIntOrNull() ?: 0

                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            }.also { holder.exEtcInput.addTextChangedListener(it) }

            when (position) {
                0 -> {
                    holder.exEtcInput.visibility = View.VISIBLE
                    holder.exEtcSwitch.visibility = View.GONE
                    holder.exEtcInput.setText(exercise.exname)
                }
                1 -> {
                    holder.exEtcInput.visibility = View.VISIBLE
                    holder.exEtcSwitch.visibility = View.GONE
                    holder.exEtcInput.setText(exercise.excontent)
                }
                2 -> {
                    holder.exEtcInput.visibility = View.VISIBLE
                    holder.exEtcSwitch.visibility = View.GONE
                    holder.exEtcInput.inputType = InputType.TYPE_CLASS_NUMBER
                    exercise.excal.toString().toIntOrNull()?.let { holder.exEtcInput.setText(it.toString()) }
                }
                3 -> {
                    holder.exEtcInput.visibility = View.VISIBLE
                    holder.exEtcSwitch.visibility = View.GONE
                    holder.exEtcInput.inputType = InputType.TYPE_CLASS_NUMBER
                    exercise.extime.toString().toIntOrNull()?.let { holder.exEtcInput.setText(it.toString()) }

                }
                4 -> {
                    holder.exEtcInput.visibility = View.GONE
                    holder.exEtcSwitch.visibility = View.VISIBLE
                    holder.exEtcSwitch.isChecked = exercise.exmove
                    holder.exEtcSwitch.setOnCheckedChangeListener { _, isChecked ->
                        exercise.exmove = isChecked
                    }
                }
            }
        }
    }



    fun getExercises(): ExerciseDTO {

        return exercises[0]
    }

}



