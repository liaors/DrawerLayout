package com.rs.drawerlayout

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.rs.drawerlayout.databinding.ActivityDoubledrawerBinding

class DoubleDrawerLayoutActivity:AppCompatActivity() {
    private lateinit var binding: ActivityDoubledrawerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoubledrawerBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.firstBt.setOnClickListener {
            binding.doubleDrawerLayout.openFirstDrawer()
        }
        binding.secondBt.setOnClickListener {
            binding.doubleDrawerLayout.openSecondDrawer()
        }
    }
}