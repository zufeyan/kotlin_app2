package com.sctgold.wgold.android

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_action_sheet.*

class ActionSheetListViewDialog(
    private var activity: Activity,
    private var adapter: RecyclerView.Adapter<*>
) : Dialog(activity), View.OnClickListener {

    //var dialog: Dialog? = null

    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_action_sheet)

        //recyclerView = action_sheet_list
        layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter

       close_action_sheet.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.close_action_sheet -> dismiss()
            else -> {}
        }
        dismiss()
    }

}
