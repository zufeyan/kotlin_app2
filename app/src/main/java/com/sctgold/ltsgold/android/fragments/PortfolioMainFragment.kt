package com.sctgold.ltsgold.android.fragments

import android.os.Bundle
import android.view.View
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.dto.PortfolioDTO
import com.sctgold.ltsgold.android.listener.OnBackPressedListener
import com.sctgold.ltsgold.android.listener.OnPortfolioChangeListener
import com.sctgold.ltsgold.android.listener.OnStateChangeListener
import com.google.gson.Gson

class PortfolioMainFragment : MainFragment(), OnStateChangeListener, OnPortfolioChangeListener,
    OnBackPressedListener {
    private val TAG = "PORTFOLIO-MAIN"

    private val currentState: String? = null
    private val PORTFOLIO_STATE = "portfolio_state"
    private val PORTFOLIO_TAG = "portfolio_view"
    private val PORTFOLIO_DETAIL = "portfolio_detail"
    private var state: String? = null
    private val initialState: String = "PORTFOLIO"
//    private val mobilePortfolioList: List<MobilePortfolio>? = null
//
//    private val dataset: MultipleCategorySeries? = null
//    private val renderer: DefaultRenderer? = null
    private val homeActivity: HomeActivity? = null
    private val thisView: View? = null
    private val thisSavedInstanceState: Bundle? = null
    private var portfolioDetail: PortfolioDTO? = null

    private val gson = Gson()

    override fun onPortfolioSelected(portfolioDetail: PortfolioDTO) {
        state = "PORTFOLIO_DETAIL"
        this.portfolioDetail = portfolioDetail
        displayChild(
            PortDetailFragment.newInstance(portfolioDetail),
       PORTFOLIO_TAG,
            false
        )

    }

    override fun stateChange(viewId: Int) {
        if (viewId == R.id.backButton) {
            state = "PORTFOLIO_DETAIL"
            displayChild(PortPagerFragment(), PORTFOLIO_TAG, true)
        }
    }

    override fun onBackPressed() {
        if (PORTFOLIO_DETAIL == currentState) {
            homeActivity?.navigationToolbarListener(null)
        }
    }

    companion object {
        fun newInstance(): PortfolioMainFragment = PortfolioMainFragment()

    }
}