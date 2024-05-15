package com.rs.drawerlayout

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.rs.drawerlayout.databinding.ActivityDrawerlayoutBinding

class DrawerLayoutActivity :Activity(){
    private lateinit var binding: ActivityDrawerlayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawerlayoutBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val actionBarDrawerToggle = object :ActionBarDrawerToggle(this,binding.drawerLayout,R.string.app_name,R.string.app_name){

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                val slideX = drawerView.width * slideOffset
//                binding.contentFl.translationX = slideX  // 内容跟着移动
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                // 打开时
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                // 关闭时
            }

            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                // 抽屉状态改变时调用
            }
        }

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle)
//        binding.drawerlayout.setScrimColor(Color.RED) // 阴影颜色
        binding.openBt.setOnClickListener {
            // GravityCompat.START 与xml中菜单中内容中的  android:layout_gravity="left"对应
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
}