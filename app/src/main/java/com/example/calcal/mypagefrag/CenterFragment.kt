package com.example.calcal.mypagefrag

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import com.example.calcal.MainActivity
import com.example.calcal.R
import com.example.calcal.databinding.FragmentCenterBinding
import com.example.calcal.databinding.FragmentMypageBinding


class CenterFragment : Fragment() {
    private lateinit var binding:FragmentCenterBinding
    private lateinit var btn_back : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCenterBinding.inflate(inflater, container, false)

        btn_back = binding.btnBack
        btn_back.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
        binding.centerCall.findViewById<TextView>(R.id.center_call).setOnClickListener {
            val phoneNumber = "010.1234.5678" // 전화 걸 번호
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
        binding.centerEmail.findViewById<TextView>(R.id.center_email).setOnClickListener {
            val email = "calcal2@naver.com" // 이메일 주소
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
            }
            startActivity(intent)
        }



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

}