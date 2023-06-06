package com.sctgold.ltsgold.android.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.dto.TradeHistoryDetailDTO
import com.sctgold.ltsgold.android.rest.ServiceApiSpot
import com.sctgold.ltsgold.android.util.ProgressDialog
import com.sctgold.android.tradeplus.util.ManageData
import java.util.*

class HistoryFragment : MainFragment() {

    lateinit var context: AppCompatActivity
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var historyView: RecyclerView
    private lateinit var stateLayout: LinearLayout
    lateinit var layoutManager: LinearLayoutManager
    private lateinit var textNoData: TextView
    var item: ArrayList<Any> = ArrayList()
    var notLoading = true

    var dialog: Dialog? = null

    var textColor = 0
    var pay = ""
    var status = ""
    var typeOrder = ""
    var state: String = "HistoryFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_history, container, false)
        refresh = v.findViewById(R.id.history_refresh)
        historyView = v.findViewById(R.id.history_lists)
        stateLayout = v.findViewById(R.id.layout_post_statement)
        textNoData = v.findViewById(R.id.text_no_data)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh.setColorSchemeResources(R.color.step1, R.color.step2, R.color.step3, R.color.step4)
        refresh.setOnRefreshListener { loadData() }
        layoutManager = activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }!!
        historyView.layoutManager = layoutManager
        historyView.itemAnimator = DefaultItemAnimator()
        historyView.adapter = HistoryAdapter(getHistoryLists(), context)

//        (activity as HomeActivity?)?.setFragmentRefreshListener(object :
//            HomeActivity.FragmentRefreshListener {
//            override fun onRefresh() {
//                getTradeHistory(0)
//                getTradeHistoryScrollListener()
//                Log.e("refresh ", "HistoryFragment")
//            }
//        })

    }

    private fun getHistoryLists(): ArrayList<Any> {
        return item
    }


    class HistoryViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_history, parent, false)) {
        private var wgHistoryContent: LinearLayout? = null
        private var wgHistoryColor: View? = null
        private var wgHistoryType: TextView? = null
        private var wgHistoryPrice: TextView? = null
        private var wgHistoryState: TextView? = null
        private var wgHistoryDate: TextView? = null
        private var wgHistoryNext: ImageView? = null

        init {
            wgHistoryContent = itemView.findViewById(R.id.history_row_container)
            wgHistoryColor = itemView.findViewById(R.id.history_row_color)
            wgHistoryType = itemView.findViewById(R.id.history_row_type)
            wgHistoryPrice = itemView.findViewById(R.id.history_row_price)
            wgHistoryState = itemView.findViewById(R.id.history_row_state)
            wgHistoryDate = itemView.findViewById(R.id.history_row_date)
            wgHistoryNext = itemView.findViewById(R.id.history_row_next)
        }

        fun bind(data: TradeHistoryDetailDTO) {
            val view: View = this.itemView
            val colors = ContextCompat.getColor(view.context, data.type)
            val boxes = ContextCompat.getDrawable(view.context, R.drawable.corner_view)
            boxes?.setTint(colors)
            val types = data.tradeSide!!.uppercase(Locale.ROOT) + " - " + data.productName
//            val atPrice = "@ " + formats.format(data.prices).toString()
            val atPrice = "@ " + data.priceUnit



            wgHistoryContent?.id = layoutPosition
            wgHistoryContent?.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val historyDetailFragment: Fragment = HistoryDetailFragment()
                val bundle = Bundle()
                bundle.putInt("type", data.type)
                bundle.putString("orderId", data.orderId)
                bundle.putString("orderDate", data.orderDate)
                bundle.putString("productName", data.productName)
                bundle.putString("tradeType", data.tradeType)
                bundle.putString("ivNumber", data.ivNumber)
                bundle.putString("orderNumber", data.orderNumber)
                bundle.putString("tradeSide", data.tradeSide)
                bundle.putString("quantity", data.quantity)
                bundle.putString("price", data.price)
                bundle.putString("total", data.total)
                bundle.putString("dueDate", data.dueDate)
                bundle.putString("groupOrderStatus", data.groupOrderStatus)
                bundle.putString("paymentStatus", data.paymentStatus)
                bundle.putString("matchStatus", data.matchStatus)
                bundle.putString("productUnit", data.productUnit)
                bundle.putString("fifoStatus", data.fifoStatus)
                bundle.putString("currencyCode", data.currencyCode)
                historyDetailFragment.arguments = bundle

                val fm: FragmentManager = act.supportFragmentManager
                fm.beginTransaction().remove(HistoryFragment())
                    .add(R.id.frag_history,historyDetailFragment).addToBackStack(null).commit()
//                fm.beginTransaction().replace(R.id.frag_history, historyDetailFragment)
//                    .addToBackStack("HISTORY").commit()

            }
            wgHistoryColor?.background = boxes
            wgHistoryType?.text = types
            wgHistoryType?.setTextColor(colors)
            wgHistoryPrice?.text = atPrice
            wgHistoryState?.text = data.paymentStatus!!.uppercase(Locale.ROOT)
            wgHistoryDate?.text = data.orderDate
        }
    }

    private class HistoryAdapter(val list: List<Any>, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val cell: Int = 0

        override fun getItemCount(): Int {
            return list.size
        }

        override fun getItemViewType(position: Int): Int {
            when (list[position]) {
                is TradeHistoryDetailDTO -> {
                    return cell
                }
            }
            return cell
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                cell -> {
                    return HistoryViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
            }
            return HistoryViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                cell -> {
                    val cells = holder as HistoryViewHolder
                    configureCell(cells, position)
                }
            }

        }

        private fun configureCell(holder: HistoryViewHolder, position: Int) {
            val cells: TradeHistoryDetailDTO = list[position] as TradeHistoryDetailDTO
            holder.bind(
                TradeHistoryDetailDTO(
                    cells.type,
                    cells.orderId,
                    cells.orderDate,
                    cells.tradeType,
                    cells.ivNumber,
                    cells.orderNumber,
                    cells.tradeSide,
                    cells.productCode,
                    cells.productName,
                    cells.quantity,
                    cells.productUnit,
                    cells.price,
                    cells.total,
                    cells.dueDate,
                    cells.approveStatus,
                    cells.matchStatus,
                    cells.paymentStatus,
                    cells.groupOrderStatus,
                    cells.deleteEnable,
                    cells.fifoStatus,
                    cells.priceUnit,
                    cells.currencyCode
                )
            )
        }
    }

    companion object {
        fun newInstance(): HistoryFragment = HistoryFragment()
    }


    private fun getTradeHistoryScrollListener() {
        historyView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (notLoading && layoutManager.findLastCompletelyVisibleItemPosition() == item.size - 1) {
                    notLoading = false
                    var firstSize = item.size
                    var maxSize = item.size + 50
                    ServiceApiSpot.getTradeHistory(
                        firstSize.toString(),
                        maxSize.toString(),
                        context
                    ) {
                        if (it != null) {

                            var item2: ArrayList<Any>
                            var tradeSide = ""
                            for (history in it.mobileTradeHistoryList) {
                                item2 = ArrayList()
                                tradeSide = if (history.tradeSide == "B") {
                                    context.resources.getString(R.string.BUY)
                                } else {
                                    context.resources.getString(R.string.SELL)
                                }
                                historyStatus(history)

                                item2.add(

                                    TradeHistoryDetailDTO(
                                        textColor,
                                        history.orderId,
                                        history.orderDate,
                                        typeOrder,
                                        history.ivNumber,
                                        history.orderNumber,
                                        tradeSide,
                                        history.productCode,
                                        history.productName,
                                        history.quantity,
                                        history.productUnit,
                                        history.price,
                                        history.total,
                                        history.dueDate,
                                        history.approveStatus,
                                        status,
                                        pay,
                                        history.groupOrderStatus,
                                        history.deleteEnable,
                                        history.fifoStatus,
                                        history.price+" ("+history.quantity+")",
                                        history.currencyCode
                                    )
                                )

                                item.addAll(item2)

                            }
                            historyView.adapter?.notifyDataSetChanged()
                            notLoading = true
                        }


                    }

                }
            }
        })
    }

    private fun getTradeHistory(i: Int) {
        item.clear()
        historyView.adapter?.notifyDataSetChanged()
        ServiceApiSpot.getTradeHistory("0", "50", context) {
            if (it != null) {

                if (it.response == "SUCCESS") {
                    if (dialog?.isShowing == true) {
                        dialog?.dismiss()
                    }
                    var tradeSide = ""

                    var item2: ArrayList<Any> = ArrayList()
                    item2.clear()
                    historyView.adapter?.notifyDataSetChanged()

                    if (it.mobileTradeHistoryList.isNotEmpty()){
                        stateLayout.removeView(textNoData)
                        for (history in it.mobileTradeHistoryList) {
                            item2.clear()
                            tradeSide = if (history.tradeSide == "B") {
                                context.resources.getString(R.string.BUY)
                            } else {
                              context.resources.getString(R.string.SELL)
                            }
                            historyStatus(history)

                           item2.add(
                                TradeHistoryDetailDTO(
                                    textColor,
                                    history.orderId,
                                    history.orderDate,
                                    typeOrder,
                                    history.ivNumber,
                                    history.orderNumber,
                                    tradeSide,
                                    history.productCode,
                                    history.productName,
                                    history.quantity,
                                    history.productUnit,
                                    history.price,
                                    history.total,
                                    history.dueDate,
                                    history.approveStatus,
                                    status,
                                    pay,
                                    history.groupOrderStatus,
                                    history.deleteEnable,
                                    history.fifoStatus,
                                    history.price+" ("+history.quantity+")",
                                    history.currencyCode
                                )
                            )
                            item.addAll(item2)

                        }
                        historyView.adapter?.notifyDataSetChanged()

                        if(item.size >=50){
                            getTradeHistoryScrollListener()
                        }

                    }else{
                        stateLayout.removeView(historyView)
                        textNoData.visibility = View.VISIBLE
                        textNoData.text =  context.resources.getString(R.string.port_not_data)
                    }


                } else {
                    if (dialog?.isShowing == true) {
                        dialog?.dismiss()
                    }
                }

                historyView.adapter?.notifyDataSetChanged()
            }
        }
    }


    private fun historyStatus(history: TradeHistoryDetailDTO) {
        textColor = if (history.approveStatus == "") {
            R.color.pm_blue
        } else {
            when (history.paymentStatus) {
                "Y" -> {
                    R.color.pm_paid
                }
                "P" -> {
                    R.color.pm_partial
                }
                else -> {
                    if (history.tradeSide == "B") {
                        R.color.pm_buy
                    } else {
                        R.color.pm_sell
                    }

                }
            }
        }



        pay = when (history.paymentStatus) {
            "Y" -> {
                context.resources.getString(R.string.PAID)
            }
            "P" -> {
                context.resources.getString(R.string.PARTIAL)
            }
            else -> {
                context.resources.getString(R.string.UNPAID)
            }
        }

        status = if (history.matchStatus == "M") {
            if (history.approveStatus == "") {
                context.resources.getString(R.string.UNMATCH)
            } else {
            context.resources.getString(R.string.MATCH)
            }
        } else {
            context.resources.getString(R.string.UNMATCH)
        }

        typeOrder = when (history.tradeType) {
            "R" -> {
                context.resources.getString(R.string.un_rt)
            }
            "P" -> {
                context.resources.getString(R.string.un_po)
            }
            else -> {
                context.resources.getString(R.string.un_so)
            }
        }

        if(history.groupOrderStatus == "Settled"){
            textColor = R.color.pm_settled
            pay = "Settled"
        }
    }

    private fun loadData() {
        item.clear()
        historyView.adapter?.notifyDataSetChanged()
        refresh.isRefreshing = false
        getTradeHistory(0)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onResume() {
        super.onResume()
        ManageData().hasInternetConnection().subscribe { hasInternet ->
            if (hasInternet) {
                dialog = ProgressDialog.progressDialog(context)
                dialog?.show()
                getTradeHistory(0)

            } else {
                var dialogNetwork = ProgressDialog.networkDialog(requireContext())
                dialogNetwork.show()
            }
        }

//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = getString(R.string.tab_hist)
//        Log.e("title tab_hist : ", title.text.toString())
    }

    override fun stateChange(viewId: Int) {
    }


}