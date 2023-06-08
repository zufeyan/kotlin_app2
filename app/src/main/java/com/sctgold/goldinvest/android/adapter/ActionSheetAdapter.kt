package com.sctgold.goldinvest.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.goldinvest.android.R
import kotlinx.android.synthetic.main.row_action_sheet.view.*

class ActionSheetAdapter(
    val array: Array<String>,
    internal var recyclerViewItemClickListener : RecyclerViewItemClickListener
) : RecyclerView.Adapter<ActionSheetAdapter.ActionSheetViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActionSheetViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.row_action_sheet, parent, false
        )
        return ActionSheetViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ActionSheetViewHolder,
        position: Int
    ) {
        holder.texts.text = array[position]
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class ActionSheetViewHolder(
        v: View
    ) : RecyclerView.ViewHolder(v),
        View.OnClickListener {
        var texts: TextView = v.text1

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            recyclerViewItemClickListener.clickOnItem(
                array[this.absoluteAdapterPosition]
            )
        }
    }

    interface RecyclerViewItemClickListener {
        fun clickOnItem(data: String)
    }
}
