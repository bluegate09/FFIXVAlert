package com.example.ffixvalert.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ffixvalert.model.DataCenterLabel
import com.example.ffixvalert.ui.DataCenterFragment

class ViewPagerAdapter(private val location: DataCenterLabel, fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return location.dataCenter.values.size
    }

    override fun createFragment(position: Int): Fragment {
        return DataCenterFragment(position)
    }
}