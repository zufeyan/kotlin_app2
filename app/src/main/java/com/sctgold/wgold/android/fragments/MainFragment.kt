package com.sctgold.wgold.android.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.activities.HomeActivity
import com.sctgold.wgold.android.listener.OnStateChangeListener


abstract class  MainFragment: Fragment(), OnStateChangeListener {
    private val childFragment: Fragment? = null
    protected var currentFragment: Fragment? = null
    protected open fun displayChild(newChildFragment: Fragment, tagName: String, isBack: Boolean) {
      //  LogUtil.debug(TAG, "displayChild")
        if (newChildFragment.isAdded) {
            return
        }
        currentFragment = newChildFragment
        HomeActivity().currentFragment = newChildFragment
       // LogUtil.debug(TAG, "old fragment tagName : {}", tagName)
        var childfragment: Fragment? = null
        childfragment = try {
            childFragmentManager.findFragmentByTag(tagName)
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        if (childfragment == null) {
          //  LogUtil.debug(TAG, "Add child")
            childFragmentManager
                .beginTransaction()
                .add(R.id.parentLayout, newChildFragment, tagName)
                .commitAllowingStateLoss()
          //  LogUtil.debug(TAG, "Add child complete")
        } else {
         //   LogUtil.debug(TAG, "new fragment tagName : {}", newChildFragment.javaClass.name)
            if (childfragment.javaClass.name != newChildFragment.javaClass.name) {
             //   LogUtil.debug(TAG, "old childfragment : {}", childfragment.javaClass.name)
               // LogUtil.debug(TAG, "remove and Add child")
                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val ft: FragmentTransaction = childFragmentManager.beginTransaction()
                if (isBack) {
                    ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                } else {
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                }
                ft.remove(childfragment).add(R.id.parentLayout, newChildFragment, tagName)
                    .commitAllowingStateLoss()
             //   LogUtil.debug(TAG, "remove and Add child complete")
            } else {
//                if(childfragment instanceof OnReloadDataFragment) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((OnReloadDataFragment) childfragment).onReloadData();
//                        }
//                    }, 400);
//                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (childFragment != null && childFragment.javaClass != MainFragment::class.java) {
            stateChange(R.id.backButton)

        }
    }
}
