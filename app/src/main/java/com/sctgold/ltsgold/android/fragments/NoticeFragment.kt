package com.sctgold.ltsgold.android.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sctgold.ltsgold.android.ExpandableLayout
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.rest.ServiceApiSpot
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.dto.NotificationDetailDTO
import com.sctgold.ltsgold.android.util.ProgressDialog
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.row_notice.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NoticeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NoticeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var noticeView: RecyclerView
    val item: ArrayList<NotificationDetailDTO> = ArrayList()

    lateinit var layoutManager: LinearLayoutManager
    var notLoading = true
    var dialog: Dialog? = null
    var state: String = "NoticeFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_notice, container, false)
//
//        val toolbar = v.findViewById<Toolbar>(R.id.main_toolbar)
//        toolbar.setNavigationOnClickListener { onBack() }

        refresh = v.findViewById(R.id.notice_refresh)
        refresh.setColorSchemeResources(R.color.step1, R.color.step2, R.color.step3, R.color.step4)
        refresh.setOnRefreshListener { loadData() }

        noticeView = v.findViewById(R.id.notice_view)
        getNoticeMessageList(0)
        getNoticeMessageScrollListener()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = ProgressDialog.progressDialog(context)
        dialog?.show()
        noticeView.setHasFixedSize(true)
        layoutManager =
            activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }!!
        noticeView.layoutManager = layoutManager
        noticeView.itemAnimator = DefaultItemAnimator()
        noticeView.adapter?.notifyDataSetChanged()
        noticeView.adapter = NoticeAdapter(getNoticeLists(), context) { id: String ->
            partItemClicked(
                id
            )
        }

    }

    private fun partItemClicked(id: String) {
        noticeRead(id)
    }

//    data class DataNotice(
//        var read: Boolean,
//        var title: String,
//        var body: String,
//        var post: String
//    )

    private fun getNoticeLists(): ArrayList<NotificationDetailDTO> {


//        item.add(
//            DataNotice(
//                false,
//                "De Finibus Bonorum et Malorum",
//                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like).",
//                "Published on 88/88/8888 88:88"
//            )
//        )
//        item.add(
//            DataNotice(
//                false,
//                "The standard Lorem Ipsum passage, used since the 1500s",
//                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.",
//                "Published on 88/88/8888 88:88"
//            )
//        )
//        item.add(
//            DataNotice(
//                false,
//                "1914 translation by H. Rackham",
//                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English.",
//                "Published on 88/88/8888 88:88"
//            )
//        )

        return item
    }

    private class NoticeAdapter(
        private val lists: List<NotificationDetailDTO>,
        val context: Context,
        val clickListener: (String) -> Unit
    ) :
        RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {

        private val expandedPositionSet: HashSet<Int> = HashSet()

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_notice, parent, false)
            )
        }

        override fun getItemCount() = lists.size

        override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
            val cells: NotificationDetailDTO = lists[position]
            holder.itemView.notice_title.text = cells.notificationTitle
            holder.itemView.notice_body.text = cells.notificationMessage
            holder.itemView.notice_post.text = cells.notificationTime

            if (!cells.notificationRead) {
                holder.itemView.card_notice.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.white)
                )

                holder.itemView.notice_icon_title.visibility = View.VISIBLE
                holder.itemView.notice_icon_title.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_v_email_unread)
                )
                holder.itemView.notice_icon_title.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.tint), PorterDuff.Mode.SRC_IN
                )

                holder.itemView.notice_title.isSingleLine = true
                holder.itemView.notice_title.maxLines = 1
                holder.itemView.notice_title.ellipsize = TextUtils.TruncateAt.END
                holder.itemView.notice_title.setTextColor(
                    ContextCompat.getColor(context, R.color.tint)
                )

                holder.itemView.notice_icon_expand.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_v_down)
                )
                holder.itemView.notice_icon_expand.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.tint), PorterDuff.Mode.SRC_IN
                )

            } else {
                holder.itemView.card_notice.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.white)
                )

                holder.itemView.notice_icon_title.visibility = View.VISIBLE
                holder.itemView.notice_icon_title.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_v_email)
                )
                holder.itemView.notice_icon_title.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.text), PorterDuff.Mode.SRC_IN
                )

                holder.itemView.notice_title.isSingleLine = true
                holder.itemView.notice_title.maxLines = 1
                holder.itemView.notice_title.ellipsize = TextUtils.TruncateAt.END
                holder.itemView.notice_title.setTextColor(
                    ContextCompat.getColor(context, R.color.text)
                )

                holder.itemView.notice_icon_expand.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_v_down)
                )
                holder.itemView.notice_icon_expand.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.text), PorterDuff.Mode.SRC_IN
                )
            }

            holder.itemView.expand_layout.setOnExpandListener(object :
                ExpandableLayout.OnExpandListener {
                override fun onExpand(expanded: Boolean) {
                    if (expandedPositionSet.contains(position)) {
                        expandedPositionSet.remove(position)
                        cells.notificationRead = true

                        holder.itemView.card_notice.setCardBackgroundColor(
                            ContextCompat.getColor(context, R.color.white)
                        )

                        holder.itemView.notice_icon_title.visibility = View.VISIBLE
                        holder.itemView.notice_icon_title.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_v_email)
                        )
                        holder.itemView.notice_icon_title.colorFilter = PorterDuffColorFilter(
                            ContextCompat.getColor(context, R.color.text), PorterDuff.Mode.SRC_IN
                        )

                        holder.itemView.notice_title.isSingleLine = true
                        holder.itemView.notice_title.maxLines = 1
                        holder.itemView.notice_title.ellipsize = TextUtils.TruncateAt.END
                        holder.itemView.notice_title.setTextColor(
                            ContextCompat.getColor(context, R.color.text)
                        )

                        holder.itemView.notice_icon_expand.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_v_down)
                        )
                        holder.itemView.notice_icon_expand.colorFilter = PorterDuffColorFilter(
                            ContextCompat.getColor(context, R.color.text), PorterDuff.Mode.SRC_IN
                        )

                    } else {
                        expandedPositionSet.add(position)
                        cells.notificationRead = true

                        holder.itemView.card_notice.setCardBackgroundColor(
                            ContextCompat.getColor(context, R.color.tint)
                        )

                        holder.itemView.notice_icon_title.visibility = View.GONE

                        holder.itemView.notice_title.isSingleLine = false
                        holder.itemView.notice_title.maxLines = Int.MAX_VALUE
                        holder.itemView.notice_title.ellipsize = null
                        holder.itemView.notice_title.setTextColor(
                            ContextCompat.getColor(context, R.color.white)
                        )

                        holder.itemView.notice_icon_expand.setImageDrawable(
                            ContextCompat.getDrawable(context, R.drawable.ic_v_up)
                        )
                        holder.itemView.notice_icon_expand.colorFilter = PorterDuffColorFilter(
                            ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN
                        )
                        clickListener(cells.notificationId.toString())
                    }
                }

            })
            holder.itemView.expand_layout.setExpand(expandedPositionSet.contains(position))
        }


    }

    private fun loadData() {
        refresh.isRefreshing = true
        // request reload data below
//        item.clear()
//        getTradeHistory(0)
//        noticeView.adapter?.notifyDataSetChanged()
        refresh.isRefreshing = false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NoticeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            NoticeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = NoticeFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }


    private fun getNoticeMessageList(i: Int) {
        ServiceApiSpot.getNoticeMessageList("0", "50", context) {
            if (it != null) {
                if (dialog?.isShowing == true) {
                    dialog?.dismiss()
                }
                for (i in it.mobileNotificationList) {
                    var item2: ArrayList<NotificationDetailDTO> = ArrayList()
                    item2.add(
                        NotificationDetailDTO(
                            i.notificationId,
                            i.notificationTitle,
                            i.notificationMessage,
                            i.notificationTime,
                            i.notificationRead,
                        )
                    )

                    item.addAll(item2)
                }
                noticeView.adapter?.notifyDataSetChanged()

            }
        }

    }

    private fun getNoticeMessageScrollListener() {
        noticeView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (notLoading && layoutManager.findLastCompletelyVisibleItemPosition() == item.size - 1) {
                    notLoading = false
                    var firstSize = item.size
                    var maxSize = item.size + 50

                    ServiceApiSpot.getNoticeMessageList(
                        firstSize.toString(),
                        maxSize.toString(),
                        context
                    ) {
                        if (it != null) {
                            for (i in it.mobileNotificationList) {
                                var item2: ArrayList<NotificationDetailDTO> = ArrayList()
                                item2.add(
                                    NotificationDetailDTO(
                                        i.notificationId,
                                        i.notificationTitle,
                                        i.notificationMessage,
                                        i.notificationTime,
                                        i.notificationRead,
                                    )
                                )
                                item.addAll(item2)

                            }
                            noticeView.adapter?.notifyDataSetChanged()
                            notLoading = true
                        }
                    }
                }

            }
        })
    }

    private fun noticeRead(id: String) {
        ServiceApiSpot.noticeRead(id, context) {
            if (it != null) {
                it.response?.let { it1 -> Log.i("noticeRead", it1) }
            }
        }
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = getString(R.string.tnoti)
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
//
//
//    }

    override fun onOptionsItemSelected(itemMenu: MenuItem): Boolean {
        when (itemMenu.itemId) {
            R.id.to_email_open -> {
                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }
                builder?.setTitle(getString(R.string.title_mask_as_read))
                    ?.setMessage(getString(R.string.msg_mask_as_read))
                    ?.setPositiveButton(getString(R.string.btn_ok),
                        DialogInterface.OnClickListener { dialog, id ->
                            ServiceApiSpot.noticeReadall(context) { it ->
                                if (it != null) {
                                    item.forEach { _ ->
                                        item.find { !it.notificationRead }?.notificationRead = true
                                    }
                                    noticeView.adapter?.notifyDataSetChanged()
                                    builder.create().dismiss()
                                }
                            }

                        })
                    ?.setNegativeButton(getString(R.string.btn_close),
                        DialogInterface.OnClickListener { dialog, id ->
                            builder.create().dismiss()
                        })
                builder?.create()?.show()

                return true
            }
        }
        return super.onOptionsItemSelected(itemMenu)
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.tnoti)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }


}