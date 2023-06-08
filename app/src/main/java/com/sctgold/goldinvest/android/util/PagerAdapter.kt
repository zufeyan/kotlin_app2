package com.sctgold.goldinvest.android.util

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.sctgold.goldinvest.android.activities.TabMenu

class PagerAdapter(fm: FragmentManager, NumOfTabs: Int) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ){
    private val TAG = "PagerAdapter"
    var selectedFragment: Fragment? = null
    var mNumOfTabs =NumOfTabs
    var tabMenus = TabMenu.values()
    var allFragment = arrayOfNulls<Fragment>(tabMenus.size)
    var currentPosition = 0

    override fun instantiateItem(viewGroup: ViewGroup, position: Int): Fragment {
        var createdFragment: Fragment?
        try {
            createdFragment = super.instantiateItem(viewGroup, position) as Fragment
            if (currentPosition >= 0 && currentPosition < allFragment.size) {
                allFragment[currentPosition] = createdFragment
            }
        } catch (e: Exception) {
            createdFragment = if (position >= 0 && position < allFragment.size) {
                allFragment[position]
            } else {
                allFragment[0]
            }
        }
        return createdFragment!!
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }

    override fun getItem(position: Int): Fragment {
        currentPosition = position
        return if (position < tabMenus.size) {
            selectedFragment = tabMenus[position].getFragment()
            //            if(selectedFragment.isAdded()) {
            //                return null;
            //            }
            selectedFragment!!
        } else {
            tabMenus[0].getFragment()!!
        }
    }


}
