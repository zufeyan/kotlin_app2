package com.sctgold.goldinvest.android.util

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.goldinvest.android.fragments.ClearPortActionFragment

class MyItemDetailsLookup(private val recyclerView: RecyclerView) :
    ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as ClearPortActionFragment.MatchingAdapter.ViewHolder)
                .getItemDetails()
        }
        return null
    }
}
