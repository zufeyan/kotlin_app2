package com.sctgold.ltsgold.android

import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.adapter.CustomAdapter
import com.sctgold.ltsgold.android.adapter.CustomListener
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import java.util.*


class DragListener internal constructor(private var listener: CustomListener) :
    View.OnDragListener {

    private var isDropped = false



    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DROP -> {
                isDropped = true
                var positionTarget = -1
                val viewSource = event.localState as View?
                val viewId = v.id
                val frameLayoutItem = R.id.edit_product_cell
                val recyclerView1 = R.id.display_view
                val recyclerView2 = R.id.hidden_view

                when (viewId) {
                    frameLayoutItem, recyclerView1, recyclerView2 -> {
                        val target: RecyclerView
                        when (viewId) {
                            recyclerView1 -> target =
                                v.rootView.findViewById<View>(recyclerView1) as RecyclerView
                            recyclerView2 -> target =
                                v.rootView.findViewById<View>(recyclerView2) as RecyclerView
                            else -> {
                                target = v.parent as RecyclerView
                                positionTarget = v.tag as Int
                            }
                        }

                        if (viewSource != null) {
                            val source = viewSource.parent as RecyclerView
                            val adapterSource = source.adapter as CustomAdapter?
                            val positionSource = viewSource.tag as Int

                            val sourceId = source.id

                            val list: String? = adapterSource?.getList()?.get(positionSource)
                            val listSource = adapterSource?.getList()?.apply {
                                removeAt(positionSource)
                            }
                            listSource?.let { adapterSource.updateList(it as ArrayList<String>) }
                            adapterSource?.notifyDataSetChanged()

                            val adapterTarget = target.adapter as CustomAdapter?
                            val customListTarget = adapterTarget?.getList()
                            if (positionTarget >= 0) {
                                list?.let { customListTarget?.add(positionTarget, it) }
                            } else {
                                list?.let { customListTarget?.add(it) }
                            }
                            customListTarget?.let { adapterTarget.updateList(it as ArrayList<String>) }
                            adapterTarget?.notifyDataSetChanged()
                            if (sourceId == recyclerView1) {
                                if (adapterSource?.itemCount == customListTarget?.size && adapterSource?.itemCount != listSource?.size) {
                                    if (customListTarget != null) {
                                        PropertiesKotlin.myProductUpdate = customListTarget
                                    }
                                } else {
                                    if (listSource != null) {
                                        PropertiesKotlin.myProductUpdate = listSource
                                    }
                                }

                            }

                            if (sourceId == recyclerView2) {
                                if(viewId == recyclerView1){
                                    if (customListTarget != null) {
                                        PropertiesKotlin.myProductUpdate = customListTarget
                                    }
                                }else{
                                    if(positionTarget < 0){
                                        if (customListTarget != null) {
                                            PropertiesKotlin.myProductUpdate = customListTarget
                                        }
                                    }else{
                                        if (customListTarget != null) {
                                            PropertiesKotlin.myProductUpdate = customListTarget
                                        }
//                                        if(positionSource == 0 && positionTarget == 0){
//                                            if (customListTarget != null) {
//                                                PropertiesKotlin.myProductUpdate = customListTarget
//                                            }
//                                        }
//                                        if( viewId !=  recyclerView1 && viewId !=  recyclerView2 ){
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!isDropped && event.localState != null) {
            (event.localState as View).visibility = View.VISIBLE
        }
        return true
    }
}

