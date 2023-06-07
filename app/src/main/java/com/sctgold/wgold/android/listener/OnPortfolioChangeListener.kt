package com.sctgold.wgold.android.listener

import com.sctgold.wgold.android.dto.PortfolioDTO

interface OnPortfolioChangeListener {
    fun onPortfolioSelected(portfolioDetail: PortfolioDTO)

}
