package com.ashutosh.techathon.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashutosh.techathon.R
import com.ashutosh.techathon.databinding.FragmentChatBinding
import com.ashutosh.techathon.databinding.FragmentHomeBinding

class ChatFragment : Fragment() {

    var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater,container,false)



        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume: ", )
    }

    override fun onPause() {
        super.onPause()
        Log.e("TAG", "onPause: ", )
    }
}