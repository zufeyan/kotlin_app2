package com.sctgold.wgold.android.adapter

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.sctgold.wgold.android.DragListener
import com.sctgold.wgold.android.R
import kotlin.collections.ArrayList


interface CustomListener  {
    fun setEmptyList(visibility: Int, recyclerView: Int, emptyTextView: Int)
}


class CustomAdapter(private var list: ArrayList<String>, private val listener: CustomListener)
    : RecyclerView.Adapter<CustomAdapter.CustomViewHolder?>(), View.OnTouchListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return  CustomViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = list.size

    fun updateList(list: ArrayList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun getList(): MutableList<String> = this.list.toMutableList()

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
//                val data = ClipData.newPlainText("", "")
//                val shadowBuilder = View.DragShadowBuilder(v)
//                v?.startDragAndDrop(data, shadowBuilder, v, 0)
                val shadowBuilder = View.DragShadowBuilder(v)
                val item = ClipData.Item(v?.tag.toString())
                val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                val data = ClipData(v?.tag.toString(), mimeTypes, item)
                v?.startDragAndDrop(data, shadowBuilder, v, 0)

                return true
            }
        }
        return false
    }

//    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//        val mShadow = CustomScaledDragShadowBuilder(v!!, 0.26f)
//        val item = ClipData.Item(v?.tag.toString())
//        val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
//        val data = ClipData(v?.tag.toString(), mimeTypes, item)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            v?.startDragAndDrop(data, mShadow, null, 0)
//        } else {
//            v?.startDrag(data, mShadow, null, 0)
//        }
//
//        return false
//    }

    val dragInstance: DragListener?
        get() = if (listener != null) {
            DragListener(listener)
        } else {
            Log.e(javaClass::class.simpleName, "Listener not initialized")
            null
        }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.text?.text = list[position]
        holder.frameLayout?.tag = position
        holder.frameLayout?.setOnTouchListener(this)
        holder.frameLayout?.setOnDragListener(DragListener(listener))
    }



    class CustomViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_edit_product, parent, false)) {
        var text: TextView? = null
        var frameLayout: LinearLayout? = null
        var displayList: RecyclerView? = null
        init {
            text = itemView.findViewById(R.id.edit_product_name)
            frameLayout = itemView.findViewById(R.id.edit_product_cell)
            displayList = itemView.findViewById(R.id.display_view)

            ButterKnife.bind(this, itemView)
        }
    }

}
