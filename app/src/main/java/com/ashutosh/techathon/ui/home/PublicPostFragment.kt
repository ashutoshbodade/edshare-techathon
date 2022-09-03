package com.ashutosh.techathon.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ashutosh.techathon.R
import com.ashutosh.techathon.databinding.FragmentPublicPostBinding
import com.ashutosh.techathon.ui.post.NewPostActivity

class PublicPostFragment : Fragment() {

    var _binding: FragmentPublicPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublicPostBinding.inflate(inflater,container,false)

        binding.btnNewPost.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(),NewPostActivity::class.java))
        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}