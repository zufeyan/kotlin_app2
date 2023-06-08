package com.sctgold.goldinvest.android.listener

import com.sctgold.goldinvest.android.dto.PortfolioDTO

interface OnPortfolioChangeListener {
    fun onPortfolioSelected(portfolioDetail: PortfolioDTO)

}
