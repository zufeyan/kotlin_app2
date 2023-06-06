package com.sctgold.ltsgold.android.listener

import com.sctgold.ltsgold.android.dto.PortfolioDTO

interface OnPortfolioChangeListener {
    fun onPortfolioSelected(portfolioDetail: PortfolioDTO)

}