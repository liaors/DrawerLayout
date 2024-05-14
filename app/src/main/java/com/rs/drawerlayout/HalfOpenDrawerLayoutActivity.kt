package com.rs.drawerlayout

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import com.rs.drawerlayout.databinding.ActivitySearchBinding

class HalfOpenDrawerLayoutActivity:Activity() {
  private lateinit var binding: ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.openBt.setOnClickListener {
            binding.halfDrawerLayout.open()
        }
        binding.closeBt.setOnClickListener {
            binding.halfDrawerLayout.close()
        }
    }
}