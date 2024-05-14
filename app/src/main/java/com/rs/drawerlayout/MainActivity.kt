package com.rs.drawerlayout

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.rs.drawerlayout.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        activityMainBinding.startSlideMenuBt.setOnClickListener {
            startActivity(Intent(this,SlideMenuActivity::class.java))
        }
        activityMainBinding.startDoubleDrawerBt.setOnClickListener {
            startActivity(Intent(this,DoubleDrawerLayoutActivity::class.java))
        }

        activityMainBinding.startSearchActivityBt.setOnClickListener {
            startActivity(Intent(this,HalfOpenDrawerLayoutActivity::class.java))
        }
    }
}