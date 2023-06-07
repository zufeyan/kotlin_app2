package com.sctgold.wgold.android.adapter

import android.content.res.Resources
import com.sctgold.wgold.android.GoldSpotCache
import com.sctgold.wgold.android.dto.HomeProduct
import android.content.Context
import android.widget.ArrayAdapter


class HomeProductListAdapter(context: Context, resource: Int) :
    ArrayAdapter<HomeProduct>(context, resource) {


    val TAG = "HomeProductListAdapter"
    var cache: GoldSpotCache? = null
    var resources: Resources? = null
    var sellColor = 0
    var buyColor = 0
    var bgColor = 0
    var flashFontColor = 0
    var afterFlashFontColor = 0





//    fun HomeProductListAdapter(
//        context: Context,
//        textViewResourceId: Int,
//        items: List<HomeProduct?>?
//    ) {
//        super(context, textViewResourceId, items)
//        cache = GoldSpotCache.getInstance()
//        resources = context.getResources()
//        sellColor =
//            ContextCompat.getColor(getContext(), R.color.l_red) //resources.getColor(R.color.l_red);
//        buyColor = ContextCompat.getColor(
//            getContext(),
//            R.color.m_green
//        ) //resources.getColor(R.color.m_green);
//        bgColor =
//            ContextCompat.getColor(getContext(), R.color.png) //resources.getColor(R.color.wht);
//        flashFontColor =
//            ContextCompat.getColor(getContext(), R.color.content_before_flash_front_color)
//        afterFlashFontColor =
//            ContextCompat.getColor(getContext(), R.color.content_after_flash_front_color)
//    }

}
