package com.example.sample.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sample.fragments.ChatFragment
import com.example.sample.fragments.SettingsFragment
import com.example.sample.fragments.VoiceChannelFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            1 -> VoiceChannelFragment()
            2 -> SettingsFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}