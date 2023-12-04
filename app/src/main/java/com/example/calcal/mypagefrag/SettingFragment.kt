package com.example.calcal.mypagefrag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calcal.MainActivity
import com.example.calcal.adapter.MypageAdapter
import com.example.calcal.adapter.SettingAdapter
import com.example.calcal.databinding.FragmentSettingBinding



class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var btn_back : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
    }

        val recyclerView = binding.settingRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)

        val list = ArrayList<String>()
        list.add("전체 알림 수신 동의")
        list.add("알림1 수신")
        list.add("알림2 수신")
        list.add("미구현")



        val adapter = SettingAdapter(list, this)
        recyclerView.adapter = adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.showBottomNavigation()
    }

    fun onItemClick(position: Int) {

    }


}
