package com.ashutosh.techathon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ashutosh.techathon.databinding.ActivityMainBinding
import com.ashutosh.techathon.databinding.FragmentBasicDetailsBinding
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}