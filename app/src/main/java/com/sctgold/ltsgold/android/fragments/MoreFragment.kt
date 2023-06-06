package com.sctgold.ltsgold.android.fragments

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.rest.ServiceApiSpot
import com.sctgold.ltsgold.android.rest.ServiceApiSpot.getNotifications
import com.sctgold.ltsgold.android.util.PropertiesKotlin


class MoreFragment : MainFragment() {

    lateinit var context: AppCompatActivity
    private lateinit var moreView: RecyclerView
    lateinit var layoutManager: GridLayoutManager
    val REQUEST_TAKE_PHOTO = 1001
    val SELECT_IMAGE = 1002
    var state: String = "MoreFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_more, container, false)
        val signOut = v.findViewById<Button>(R.id.sign_out)

        moreView = v.findViewById(R.id.more_grid_view)

        signOut?.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setMessage(R.string.msg_confirm_out)
                .setTitle(R.string.btn_out)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_confirm) { _, id ->
                    // Delete selected note from database
                    ServiceApiSpot.logout(context)
                }
                .setNegativeButton(R.string.btn_cancel) { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
        moreView.layoutManager = layoutManager
        moreView.itemAnimator = DefaultItemAnimator()
        moreView.adapter = MoreAdapter(getMoreLinks(), context)

//
//        moreView.apply {
//            moreView.layoutManager =
//                GridLayoutManager(activity, 4, GridLayoutManager.VERTICAL, false)
//            moreView.itemAnimator = DefaultItemAnimator()
//            adapter = MoreAdapter(getMoreLinks(), context)
//        }

    }

    private fun getNotificationsAll(): Int {
        getNotifications(context) {
            if (it != null) {

                noticeCount = 3
                //Log.e(" getNotifications count ====  ", noticeCount.toString())

            }
        }
        return noticeCount
    }


    data class DataMore(
        var icons: Int,
        var names: String,
        var iconsNotice: Int
    )

    private fun getMoreLinks(): ArrayList<Any> {
        val item: ArrayList<Any> = ArrayList()

        item.add(DataMore(R.drawable.ic_v_setting, getString(R.string.tsett), 0))
        item.add(DataMore(R.drawable.ic_v_alert, getString(R.string.tpric), 0))
//        item.add(DataMore(R.drawable.ic_v_news, getString(R.string.tnews), 0))
//        item.add(DataMore(R.drawable.ic_v_bar_graph, getString(R.string.tanal), 0))
//        item.add(DataMore(R.drawable.ic_v_line_graph, getString(R.string.tgrap), 0))
        item.add(DataMore(R.drawable.ic_v_marker, getString(R.string.tcont), 0))
        item.add(DataMore(R.drawable.ic_v_pay, getString(R.string.tpaym), 0))
//        item.add(DataMore(R.drawable.ic_v_book, getString(R.string.tguid), 0))
        item.add(DataMore(R.drawable.ic_v_bell, getString(R.string.tnoti), R.drawable.ic_v_noti))
//        item.add(DataMore(R.drawable.ic_v_match, getString(R.string.tclea), 0))

        return item
    }

    class MoreAdapter(
        private val lists: List<Any>, val context: Context
    ) : RecyclerView.Adapter<MoreAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_more_links, parent, false)
            )
        }

        override fun getItemCount() = lists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cells: DataMore = lists[position] as DataMore
            holder.bind(
                DataMore(
                    cells.icons,
                    cells.names,
                    cells.iconsNotice
                )
            )
            holder.itemView.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val fm: FragmentManager = act.supportFragmentManager

                when (position) {
                    0 -> {
//                        fm.beginTransaction().setCustomAnimations(
//                            R.anim.slide_in_left,
//                            R.anim.slide_out_left,
//                            R.anim.slide_out_right,
//                            R.anim.slide_in_right
//                        ).replace(R.id.frag_more, SettingsFragment())
//                            .addToBackStack("MORE").commit()

                        fm.beginTransaction().remove(MoreFragment())
                            .add(R.id.frag_more, SettingsFragment()).addToBackStack(null).commit()
                    }
                    1 -> {
                        fm.beginTransaction().replace(R.id.frag_more, PriceAlertFragment())
                            .addToBackStack("MORE").commitAllowingStateLoss()
                    }
//                    2 -> {
//                        fm.beginTransaction().replace(R.id.frag_more, NewsFragment())
//                            .addToBackStack("MORE").commit()
//
//                    }
//                    3 -> {
//                        fm.beginTransaction().replace(R.id.frag_more, AnalysisFragment())
//                            .addToBackStack("MORE").commit()
//                    }
//                    4 -> {
//                        fm.beginTransaction().replace(R.id.frag_more, GraphFragment())
//                            .addToBackStack("MORE").commit()
//                    }
                    2 -> {
                        fm.beginTransaction().replace(R.id.frag_more, ContactFragment())
                            .addToBackStack("MORE").commit()
                    }
                   3 -> {
                        fm.beginTransaction().replace(R.id.frag_more, PaymentFragment())
                            .addToBackStack("MORE").commit()
                    }
//                    7 -> {
//                        fm.beginTransaction().replace(R.id.frag_more, GuideFragment())
//                            .addToBackStack("MORE").commit()
//                    }
                    4 -> {
                        fm.beginTransaction().remove(MoreFragment())
                            .add(R.id.frag_more, NoticeFragment()).addToBackStack(null).commit()
                    }
//                    5 -> {
//
//                        fm.beginTransaction().remove(MoreFragment())
//                            .add(R.id.frag_more, ClearPortFragment()).addToBackStack(null).commit()
////                        fm.beginTransaction().replace(R.id.frag_more, ClearPortFragment())
////                            .addToBackStack("MORE").commit()
//                    }
                    else -> {
                        fm.beginTransaction().replace(R.id.frag_more, MoreFragment()).commit()
                    }
                }
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(link: DataMore) {
                itemView.apply {
                    val v: View = this

                    val wgIcons = v.findViewById<ImageView>(R.id.more_icons)
                    wgIcons.setImageDrawable(ContextCompat.getDrawable(v.context, link.icons))
                    wgIcons.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(v.context, R.color.more),
                        PorterDuff.Mode.SRC_IN
                    )


                    val wgNames = v.findViewById<TextView>(R.id.more_names)
                    wgNames.text = link.names

//                    Log.e("aaaaaaaaaaaaaaaaaa s  ", noticeCount.toString())
//                    if (noticeCount != 0) {
//                        if (link.iconsNotice != 0) {
//                            val wgIconsN = v.findViewById<ImageView>(R.id.more_icons_notice)
//                            wgIconsN.setImageDrawable(
//                                ContextCompat.getDrawable(
//                                    v.context,
//                                    link.iconsNotice
//                                )
//                            )
//                            wgIconsN.colorFilter = PorterDuffColorFilter(
//                                ContextCompat.getColor(v.context, R.color.l_red),
//                                PorterDuff.Mode.SRC_IN
//
//                            )
//                            val marginParams = wgIconsN.layoutParams as MarginLayoutParams
//                            marginParams.setMargins(0, 0, 0, -140)
//                            wgIconsN.layoutParams = marginParams
//                            wgIconsN.layoutParams.height = 140
//                            wgIconsN.layoutParams.width = 140
//                        }
//
//                    }


                }
            }
        }


    }

    companion object {
        fun newInstance(): MoreFragment = MoreFragment()
        var   noticeCount = 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }



    override fun stateChange(viewId: Int) {

    }

    override fun onResume() {
        super.onResume()

        getNotificationsAll()
        moreView.adapter?.notifyDataSetChanged()
        PropertiesKotlin.state = state

        val v = (activity as HomeActivity)
       v.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val tabLayout = v.findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.VISIBLE
        val selectOrders: RadioGroup = v.findViewById(R.id.clear_port_sl_order)
        selectOrders.visibility = View.GONE
//        val title = v.findViewById<TextView>(R.id.title_main_text)
//        title.text =context.resources.getString(R.string.tab_more)
    }

}