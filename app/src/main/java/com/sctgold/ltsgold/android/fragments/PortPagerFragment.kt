package com.sctgold.ltsgold.android.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.listener.OnBackPressedListener
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PortPagerFragment : MainFragment() , OnBackPressedListener {

    lateinit var context: AppCompatActivity
    private lateinit var portPager: ViewPager2
    private lateinit var portTab: TabLayout

    private val currentState: String? = null
    var state: String = "PortPagerFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_port_pager, container, false)

        portPager = v.findViewById(R.id.portViewPager)
        portTab = v.findViewById(R.id.portTabLayout)

        portTab.setSelectedTabIndicator(null)
        createPortPager()

        return v
    }

    override fun stateChange(viewId: Int) {

    }

    private fun createPortPager() {
        portPager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    1 -> {
                        PropertiesKotlin.checkPortTab = 1
                        PortStatementFragment.newInstance()

                    }
                    2 -> {
                        PropertiesKotlin.checkPortTab = 2
                        PortMoneyFragment.newInstance()
                    }
                    3 -> {
                        PropertiesKotlin.checkPortTab = 3
                        PortDeptFragment.newInstance()
                    }
                    4 -> {
                        PropertiesKotlin.checkPortTab = 4
                        PortSendingFragment.newInstance()
                    }
                    else -> {
                        PropertiesKotlin.checkPortTab = 0
                        PortfolioFragment.newInstance()
                    }
                }
            }

            override fun getItemCount(): Int = 5
        }
        TabLayoutMediator(portTab, portPager) { tab, position -> }.attach()

    }

    companion object {
        fun newInstance() = PortPagerFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onBackPressed() {
        Log.e("onBackPressed   ", "currentState :$currentState")

    }


}