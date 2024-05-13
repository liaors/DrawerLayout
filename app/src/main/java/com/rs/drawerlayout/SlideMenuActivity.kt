package com.rs.drawerlayout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rs.drawerlayout.databinding.ActivitySlidemenuBinding

class SlideMenuActivity:AppCompatActivity() {
    private lateinit var binding: ActivitySlidemenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlidemenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}