package com.sctgold.wgold.android.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.rest.ServiceApiSpot
import com.sctgold.wgold.android.dto.PortfolioProductDetailDTO
import com.sctgold.wgold.android.util.ProgressDialog
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.sctgold.android.tradeplus.util.ManageData
import kotlinx.android.synthetic.main.fragment_portfolio.*
import kotlinx.android.synthetic.main.row_port_folio.view.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import com.sctgold.wgold.android.activities.HomeActivity
import com.google.android.material.tabs.TabLayout


/**
 * A simple [Fragment] subclass.
 * Use the [PortfolioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortfolioFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity

    private lateinit var conUnr: LinearLayout
    private lateinit var lbUnr: TextView
    private lateinit var valUnr: TextView

    private lateinit var chart: PieChart

    private lateinit var conRea: LinearLayout
    private lateinit var lbRea: TextView
    private lateinit var valRea: TextView

    private lateinit var conHead: LinearLayout
    private lateinit var lbHeadSell: TextView
    private lateinit var sideHeadSell: TextView
    private lateinit var lbHeadBuy: TextView
    private lateinit var sideHeadBuy: TextView

    private lateinit var portFolioView: RecyclerView

    private var rdRes: Double = 0.00
    var i: ArrayList<DataPortfolio> = ArrayList()

    var dialog: Dialog? = null
    var state: String = "PortfolioFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_portfolio, container, false)

        conUnr = v.findViewById(R.id.port_folio_con_un)
        lbUnr = v.findViewById(R.id.port_folio_lb_un)
        valUnr = v.findViewById(R.id.port_folio_val_un)
        conRea = v.findViewById(R.id.port_folio_con_re)
        lbRea = v.findViewById(R.id.port_folio_lb_re)
        valRea = v.findViewById(R.id.port_folio_val_re)
        chart = v.findViewById(R.id.port_folio_pie_chart)

        conHead = v.findViewById(R.id.port_folio_hd_row)
        lbHeadSell = v.findViewById(R.id.port_folio_hc_sell)
        sideHeadSell = v.findViewById(R.id.port_folio_ht_sell)
        lbHeadBuy = v.findViewById(R.id.port_folio_hc_buy)
        sideHeadBuy = v.findViewById(R.id.port_folio_ht_buy)

        portFolioView = v.findViewById(R.id.port_folio_view)


        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rdColorUnr = arrayOf(R.color.pm_sell, R.color.pm_buy, R.color.tint)
        val rdColorRea = arrayOf(R.color.pm_sell, R.color.pm_buy, R.color.tint)

        conUnr.setBackgroundColor(ContextCompat.getColor(view.context, rdColorUnr.random()))
        lbUnr.text = getString(R.string.port_unr)
        conRea.setBackgroundColor(ContextCompat.getColor(view.context, rdColorRea.random()))
        lbRea.text = getString(R.string.port_rea)

        // pie chart part
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.setExtraOffsets(5f, 10f, 5f, 5f)
        chart.dragDecelerationFrictionCoef = 0.95f
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(ContextCompat.getColor(context, R.color.white))
        chart.holeRadius = ResourcesCompat.getFloat(view.resources, R.dimen.hole_radius) //40f
        chart.transparentCircleRadius =
            ResourcesCompat.getFloat(view.resources, R.dimen.hole_border) //61f
        chart.setDrawCenterText(true)
        chart.rotationAngle = 270f
        // enable rotation of the chart by touch
        chart.isRotationEnabled = true
        chart.isHighlightPerTapEnabled = true

        chart.animateY(1400, Easing.EaseInOutQuad)

        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 30f
        l.yEntrySpace = 0f
        l.yOffset = 0f
        l.textColor = ContextCompat.getColor(context, R.color.black)

        val bg = ContextCompat.getDrawable(view.context, R.drawable.gd_header)
        conHead.background = bg
        lbHeadSell.text = getString(R.string.port_cre)
        sideHeadSell.text = getString(R.string.sell)
        lbHeadBuy.text = getString(R.string.port_cre)
        sideHeadBuy.text = getString(R.string.buy)

        portFolioView.layoutManager =
            activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
        portFolioView.itemAnimator = DefaultItemAnimator()
        portFolioView.adapter?.notifyDataSetChanged()
        portFolioView.adapter = PortfolioAdapter(getPortLists(), context)

    }


    data class DataPortfolio(
        var productName: String,
        var color: String,
        var result: Double,
        var minSell: Double,
        var maxSell: Double,
        var minBuy: Double,
        var maxBuy: Double,
        var seAverageCost: String,
        var buAverageCost: String,
        var seProductAmount: String,
        var buProductAmount: String,
        var seMarketPrice: String,
        var buMarketPrice: String,
        var seProfitLoss: Double,
        var buProfitLoss: Double,
        var realizePl: String
    )

    private fun getPortLists(): ArrayList<DataPortfolio> {
        val i: ArrayList<DataPortfolio> = ArrayList()
//
//        rdRes = Random.nextDouble(-88888888.00, 99999999.99)
//        val rdMin: Double = Random.nextDouble(1111.00, 6666.99)
//        val rdMax: Double = Random.nextDouble(8888.00, 9999.99)
//        val rdMin2: Double = Random.nextDouble(1111.00, 6666.99)
//        val rdMax2: Double = Random.nextDouble(8888.00, 9999.99)
//        val rdMin3: Double = Random.nextDouble(2111.00, 6666.99)
//        val rdMax3: Double = Random.nextDouble(8888.00, 9999.99)
//
//        i.add(DataPortfolio("Gold 99.99%", "#EFD56F", rdRes, rdMin3, rdMax3, rdMin, rdMax))
//
//        i.add(DataPortfolio("Gold 96.5%", "#E4BC1E", rdRes, rdMin2, rdMax2, rdMin, rdMax))
//
//        i.add(DataPortfolio("Silver 99.99%", "#BCBCBC", rdRes, rdMin2, rdMax2, rdMin3, rdMax3))

        return i
    }


    class PortfolioAdapter(private val lists: List<DataPortfolio>, val context: Context) :
        RecyclerView.Adapter<PortfolioAdapter.ViewHolder>() {


        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun getItemCount() = lists.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_port_folio, parent, false)
            )
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data: DataPortfolio = lists[position]

            val df = DecimalFormat("#,###.##")
            val rdProdColor = arrayOf(R.color.pm_sell, R.color.pm_buy, R.color.tint)

            holder.itemView.port_folio_cell_prod.setBackgroundColor(Color.parseColor(data.color))
            holder.itemView.port_folio_lb_prod.text = data.productName

            val lbResult = context.getString(R.string.port_res)
            holder.itemView.port_folio_res_lb.text = lbResult
            when (data.result) {
                in 0.00..Double.MAX_VALUE -> {
                    holder.itemView.port_folio_res_con.setBackgroundColor(
                        ContextCompat.getColor(context, rdProdColor[1])
                    )
                }
                0.00 -> {
                    holder.itemView.port_folio_res_con.setBackgroundColor(
                        ContextCompat.getColor(context, rdProdColor[2])
                    )
                }
                else -> {
                    holder.itemView.port_folio_res_con.setBackgroundColor(
                        ContextCompat.getColor(context, rdProdColor[0])
                    )
                }
            }
            holder.itemView.port_folio_res_val.text = df.format(data.result).toString()
            // pie chart sell
            var sellPie = (data.minSell / data.maxSell) * 100

            if (sellPie.isNaN()) {
                sellPie = 0.00
            }
            val sellEntry: ArrayList<PieEntry> = ArrayList()
            sellEntry.add(PieEntry(sellPie.toFloat()))
            var sellDis =  100.minus(sellPie)
            sellEntry.add(PieEntry(sellDis.toFloat()))
            val dataSetSell = PieDataSet(sellEntry, "")
            dataSetSell.valueLinePart1OffsetPercentage = 10.0f;
            dataSetSell.valueLinePart1Length = 0.43f;
            dataSetSell.valueLinePart2Length = .1f;
            dataSetSell.setDrawIcons(false)
            dataSetSell.setDrawValues(false)
            // colors sell
            val colorsSell: ArrayList<Int> = ArrayList()
            colorsSell.add(ContextCompat.getColor(context, R.color.pm_sell))
            colorsSell.add(ContextCompat.getColor(context, R.color.pm_track))
            dataSetSell.colors = colorsSell
            // set sell
            val dataSell = PieData(dataSetSell)
            dataSell.setValueFormatter(PercentFormatter())
            dataSell.setValueTextSize(8f)
            dataSell.setValueTextColor(Color.WHITE)
            holder.itemView.pie_chart_sell.data = dataSell
            holder.itemView.pie_chart_sell.setNoDataText(context.getString(R.string.port_not))
            holder.itemView.pie_chart_sell.setDrawEntryLabels(false)
            holder.itemView.pie_chart_sell.setUsePercentValues(false)
            holder.itemView.pie_chart_sell.description.isEnabled = false
            val descSell = Description()
            descSell.text = ""
            holder.itemView.pie_chart_sell.description = descSell
            holder.itemView.pie_chart_sell.legend.isEnabled = false

            holder.itemView.pie_chart_sell.isDrawHoleEnabled = true
            holder.itemView.pie_chart_sell.setHoleColor(
                ContextCompat.getColor(
                    context,
                    R.color.bg_de2_start
                    //R.color.white
                )
            )
            holder.itemView.pie_chart_sell.setTransparentCircleColor(
                ContextCompat.getColor(
                    context,
                    R.color.black_10
                )
            )
            holder.itemView.pie_chart_sell.setTransparentCircleAlpha(110)
            holder.itemView.pie_chart_sell.holeRadius =
                ResourcesCompat.getFloat(context.resources, R.dimen.hole_radius_side) //40f
            holder.itemView.pie_chart_sell.transparentCircleRadius =
                ResourcesCompat.getFloat(context.resources, R.dimen.hole_border) //61f
            holder.itemView.pie_chart_sell.setDrawCenterText(true)
            holder.itemView.pie_chart_sell.rotationAngle = 270f
            holder.itemView.pie_chart_sell.isRotationEnabled = false
            holder.itemView.pie_chart_sell.isHighlightPerTapEnabled = false
            holder.itemView.pie_chart_sell.animateY(1400, Easing.EaseInOutQuad)

            val strSell = ManageData().numberFormatToDecimal(sellPie) + "%"
            holder.itemView.pie_chart_percent_sell.text = strSell
            holder.itemView.pie_chart_percent_sell.setTextColor(
                ContextCompat.getColor(context, R.color.pm_sell)
            )

            holder.itemView.pie_chart_use_sell.text = df.format(data.minSell).toString()
            holder.itemView.pie_chart_use_sell.setTextColor(
                ContextCompat.getColor(context, R.color.pm_sell)
            )

            holder.itemView.pie_chart_divider_sell.setBackgroundColor(
                ContextCompat.getColor(context, R.color.black)
            )

            val mxSell = df.format(data.maxSell).toString()
            holder.itemView.pie_chart_max_sell.text = mxSell
            holder.itemView.pie_chart_max_sell.setTextColor(
                ContextCompat.getColor(context, R.color.bg_1)
            )

            // pie chart buy
            var buyPie = (data.minBuy / data.maxBuy) * 100

            if (buyPie.isNaN()) {
                buyPie = 0.00
            }

            val buyEntry: ArrayList<PieEntry> = ArrayList()
            buyEntry.add(PieEntry(buyPie.toFloat()))
            var buyDis =  100.minus(buyPie)
            buyEntry.add(PieEntry(buyDis.toFloat()))
            val dataSetBuy = PieDataSet(buyEntry, "")
            dataSetBuy.valueLinePart1OffsetPercentage = 10.0f;
            dataSetBuy.valueLinePart1Length = 0.43f;
            dataSetBuy.valueLinePart2Length = .1f;
            dataSetBuy.setDrawIcons(false)
            dataSetBuy.setDrawValues(false)
            dataSetBuy.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            // colors buy
            val colorsBuy: ArrayList<Int> = ArrayList()
            colorsBuy.add(ContextCompat.getColor(context, R.color.pm_buy))
            colorsBuy.add(ContextCompat.getColor(context, R.color.pm_track))
            dataSetBuy.colors = colorsBuy
            // set buy
            val dataBuy = PieData(dataSetBuy)
            dataBuy.setValueFormatter(PercentFormatter())
            dataBuy.setValueTextSize(10f)
            dataBuy.setValueTextColor(Color.WHITE)
            holder.itemView.pie_chart_buy.data = dataBuy
            holder.itemView.pie_chart_buy.setNoDataText(context.getString(R.string.port_not))
            holder.itemView.pie_chart_buy.setDrawEntryLabels(false)
            holder.itemView.pie_chart_buy.setUsePercentValues(false)
            holder.itemView.pie_chart_buy.description.isEnabled = false
            val descBuy = Description()
            descBuy.text = ""
            holder.itemView.pie_chart_buy.description = descBuy
            holder.itemView.pie_chart_buy.legend.isEnabled = false

            holder.itemView.pie_chart_buy.isDrawHoleEnabled = true
            holder.itemView.pie_chart_buy.setHoleColor(
                ContextCompat.getColor(
                    context,
                    R.color.bg_de2_start
                    //R.color.white
                )
            )
            holder.itemView.pie_chart_buy.setTransparentCircleColor(
                ContextCompat.getColor(
                    context,
                    R.color.black_10
                )
            )
            holder.itemView.pie_chart_buy.setTransparentCircleAlpha(110)
            holder.itemView.pie_chart_buy.holeRadius =
                ResourcesCompat.getFloat(context.resources, R.dimen.hole_radius_side) //40f
            holder.itemView.pie_chart_buy.transparentCircleRadius =
                ResourcesCompat.getFloat(context.resources, R.dimen.hole_border) //61f
            holder.itemView.pie_chart_buy.setDrawCenterText(true)
            holder.itemView.pie_chart_buy.rotationAngle = 270f
            holder.itemView.pie_chart_buy.isRotationEnabled = false
            holder.itemView.pie_chart_buy.isHighlightPerTapEnabled = false
            holder.itemView.pie_chart_buy.animateY(1400, Easing.EaseInOutQuad)


            val strBuy = ManageData().numberFormatToDecimal(buyPie) + "%"
            holder.itemView.pie_chart_percent_buy.text = strBuy
            holder.itemView.pie_chart_percent_buy.setTextColor(
                ContextCompat.getColor(context, R.color.pm_buy)
            )

            holder.itemView.pie_chart_use_buy.text = df.format(data.minBuy).toString()
            holder.itemView.pie_chart_use_buy.setTextColor(
                ContextCompat.getColor(context, R.color.pm_buy)
            )

            holder.itemView.pie_chart_divider_buy.setBackgroundColor(
                ContextCompat.getColor(context, R.color.black)
            )

            val mxBuy = df.format(data.maxBuy).toString()
            holder.itemView.pie_chart_max_buy.text = mxBuy
            holder.itemView.pie_chart_max_buy.setTextColor(
                ContextCompat.getColor(context, R.color.bg_1)
            )

            holder.itemView.setOnClickListener { v ->
                onClickDetail(data)
            }

            holder.itemView.pie_chart_buy.onChartGestureListener = object : OnChartGestureListener {
                override fun onChartGestureStart(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }

                override fun onChartGestureEnd(
                    me: MotionEvent?,
                    lastPerformedGesture: ChartTouchListener.ChartGesture?
                ) {
                }

                override fun onChartLongPressed(me: MotionEvent?) {
                    TODO("Not yet implemented")
                }

                override fun onChartDoubleTapped(me: MotionEvent?) {
                    TODO("Not yet implemented")
                }

                override fun onChartSingleTapped(me: MotionEvent?) {
                    onClickDetail(data)
                }

                override fun onChartFling(
                    me1: MotionEvent?,
                    me2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ) {
                    TODO("Not yet implemented")
                }

                override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                    TODO("Not yet implemented")
                }

                override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                    TODO("Not yet implemented")
                }
            }

            holder.itemView.pie_chart_sell.onChartGestureListener =
                object : OnChartGestureListener {
                    override fun onChartGestureStart(
                        me: MotionEvent?,
                        lastPerformedGesture: ChartTouchListener.ChartGesture?
                    ) {
                    }

                    override fun onChartGestureEnd(
                        me: MotionEvent?,
                        lastPerformedGesture: ChartTouchListener.ChartGesture?
                    ) {
                    }

                    override fun onChartLongPressed(me: MotionEvent?) {
                        TODO("Not yet implemented")
                    }

                    override fun onChartDoubleTapped(me: MotionEvent?) {
                        TODO("Not yet implemented")
                    }

                    override fun onChartSingleTapped(me: MotionEvent?) {
                        onClickDetail(data)
                    }

                    override fun onChartFling(
                        me1: MotionEvent?,
                        me2: MotionEvent?,
                        velocityX: Float,
                        velocityY: Float
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                        TODO("Not yet implemented")
                    }

                    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                        TODO("Not yet implemented")
                    }
                }
        }


        fun onClickDetail(data: DataPortfolio) {
            val act = context as AppCompatActivity
            val fm: FragmentManager = act.supportFragmentManager
            val portDetailFragment: Fragment = PortDetailFragment()
            val bundle = Bundle()
            bundle.putString("productName", data.productName)
            bundle.putString("seAverageCost", data.seAverageCost)
            bundle.putString("buAverageCost", data.buAverageCost)
            bundle.putString("seProductAmount", data.seProductAmount)
            bundle.putString("buProductAmount", data.buProductAmount)
            bundle.putString("seMarketPrice", data.seMarketPrice)
            bundle.putString("buMarketPrice", data.buMarketPrice)
            bundle.putDouble("seProfitLoss", data.seProfitLoss)
            bundle.putDouble("buProfitLoss", data.buProfitLoss)
            bundle.putString("realizePl", data.realizePl)

            portDetailFragment.arguments = bundle

            fm.beginTransaction().replace(R.id.frag_port_pager, portDetailFragment)
                .addToBackStack(null).commit()
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PortfolioFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PortfolioFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = PortfolioFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    private fun getPortfolio() {
        i = ArrayList()
        i.clear()
        ServiceApiSpot.getPortfolio(context) {

            if (it != null) {

                if (it.response == "SUCCESS") {
                    if (dialog?.isShowing == true) {
                        dialog?.dismiss()
                    }


                    if (it.totalProfitLoss.isNullOrEmpty()) {
                        valUnr.text = "0.00"
                    } else {
                        valUnr.text = it.totalProfitLoss
                    }

                    if (it.realizeAmountSummary.isNullOrEmpty()) {
                        valRea.text = "0.00"
                    } else {
                        valRea.text = it.realizeAmountSummary
                    }

                    PropertiesKotlin.mobileCusStocks = it.mobileCusStocksList
                    PropertiesKotlin.mobileCustomerPortAccruedBacklog =
                        it.mobileCustomerPortAccruedBacklogList

                    if (it.cashDepositMargin.isNullOrEmpty()) {
                        PropertiesKotlin.cashDepositMargin = "0.00"
                    } else {
                        PropertiesKotlin.cashDepositMargin = it.cashDepositMargin!!
                    }
                    if (it.cashDeposit.isNullOrEmpty()) {
                        PropertiesKotlin.cashDeposit = "0.00"
                    } else {
                        PropertiesKotlin.cashDeposit = it.cashDeposit!!
                    }
                    if (it.cashDepositBuy.isNullOrEmpty()) {
                        PropertiesKotlin.cashDepositBuy = "0.00"
                    } else {
                        PropertiesKotlin.cashDepositBuy =
                            PropertiesKotlin.cashDepositBuy
                    }
                    if (it.holdMoney.isNullOrEmpty()) {
                        PropertiesKotlin.holdMoney = "0.00"
                    } else {
                        PropertiesKotlin.holdMoney = PropertiesKotlin.holdMoney
                    }

                    if (it.holdMoneyTmp.isNullOrEmpty()) {
                        PropertiesKotlin.holdMoneyTmp = "0.00"
                    } else {
                        PropertiesKotlin.holdMoneyTmp = it.holdMoneyTmp!!
                    }

                    if (it.arAmount.isNullOrEmpty()) {
                        PropertiesKotlin.arAmount = "0.00"
                    } else {
                        PropertiesKotlin.arAmount = it.arAmount!!
                    }


                    if (it.apAmount.isNullOrEmpty()) {
                        PropertiesKotlin.apAmount = "0.00"
                    } else {
                        PropertiesKotlin.apAmount = it.apAmount!!
                    }

                  //  Log.e("it.mobilePortfolioProductList :  " ,it.mobilePortfolioProductList.toString())
                    if (!it.mobilePortfolioProductList.isNullOrEmpty()) {
                        setData(it.mobilePortfolioProductList)
                    }
                    if (!it.mobilePortfolioList.isNullOrEmpty()) {

                        var format: NumberFormat =
                            NumberFormat.getInstance(Locale.getDefault())

                        for (port in it.mobilePortfolioList) {
                            var portListDetail: ArrayList<DataPortfolio> = ArrayList()
                            var result = port.buProfitLoss.toBigDecimal()
                                .plus(port.seProfitLoss.toBigDecimal())

                            var color = "#AAAAAA"
                            if (!port.productColor.isNullOrEmpty()) {
                                color = "#" + port.productColor
                            }

                            var seAverageCost: String = if (port.seAverageCost.isNullOrEmpty()) {
                                "0.00"
                            } else {
                                port.seAverageCost!!
                            }

                            var buAverageCost: String = if (port.buAverageCost.isNullOrEmpty()) {
                                "0.00"
                            } else {
                                port.buAverageCost!!
                            }

                            var seProductAmount: String =
                                if (port.seProductAmount.isNullOrEmpty()) {
                                    "0.00"
                                } else {
                                    port.seProductAmount!!
                                }

                            var buProductAmount: String =
                                if (port.buProductAmount.isNullOrEmpty()) {
                                    "0.00"
                                } else {
                                    port.buProductAmount!!
                                }

                            var seMarketPrice: String = if (port.seMarketPrice.isNullOrEmpty()) {
                                "0.00"
                            } else {
                                port.seMarketPrice!!
                            }


                            var buMarketPrice: String = if (port.buMarketPrice.isNullOrEmpty()) {
                                "0.00"
                            } else {
                                port.buMarketPrice!!
                            }

                            var seProfitLoss: Double = port.seProfitLoss

                            var buProfitLoss: Double = port.buProfitLoss



                            port.realizePl?.let { it1 ->
                                DataPortfolio(
                                    port.productName!!,
                                    color,
                                    result.toDouble(),
                                    format.parse(port.pointSell).toDouble(),
                                    format.parse(port.origPointSell).toDouble(),
                                    format.parse(port.pointBuy).toDouble(),
                                    format.parse(port.origPointBuy).toDouble(),
                                    seAverageCost,
                                    buAverageCost,
                                    seProductAmount,
                                    buProductAmount,
                                    seMarketPrice,
                                    buMarketPrice,
                                    seProfitLoss,
                                    buProfitLoss,
                                    it1
                                )
                            }?.let { it2 ->
                                portListDetail.add(
                                    it2
                                )
                            }
                            i.addAll(portListDetail)
                        }
                    }

                    portFolioView.adapter = PortfolioAdapter(i, context)
                } else {
                    if (dialog?.isShowing == true) {
                        dialog?.dismiss()
                    }
                }


            }
        }
    }

    @SuppressLint("ResourceType")
    private fun setData(dataList: List<PortfolioProductDetailDTO>) {
        //private fun setData(count: Int, range: Float) {

        if (!dataList.isNullOrEmpty()) {
            val entries: ArrayList<PieEntry> = ArrayList()
            val colors: ArrayList<Int> = ArrayList()

            for (i in dataList) {
                val entriesData: ArrayList<PieEntry> = ArrayList()
                entriesData.add(PieEntry(i.portProductPercent, i.productName))
                entries.addAll(entriesData)

                val colorsData: ArrayList<Int> = ArrayList()

                if (!i.productColor.isNullOrEmpty()) {
                    var colorString = "#" + i.productColor
                    var myColor = Color.parseColor(colorString)
                    colorsData.add(myColor)
                } else {
                    var myColor = Color.parseColor("#AAAAAA")
                    colorsData.add(myColor)
                }
                colors.addAll(colorsData)
            }
            val dataSet = PieDataSet(entries, "")
            dataSet.setDrawIcons(false)
            dataSet.colors = colors
            //dataSet.setSelectionShift(0f)

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(11f)
            data.setValueTextColor(Color.WHITE)
            chart.data = data
//            chart.setNoDataText(getString(R.string.port_not))
            chart.setDrawEntryLabels(false)

            // undo all highlights
            chart.highlightValues(null)

            chart.invalidate()
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = getString(R.string.tab_port)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.VISIBLE
        ManageData().hasInternetConnection().subscribe { hasInternet ->
            if (hasInternet) {
                dialog = ProgressDialog.progressDialog(context)
                dialog?.show()
                getPortfolio()
            } else {
                var dialogNetwork = ProgressDialog.networkDialog(requireContext())
                dialogNetwork.show()
            }
        }
        PropertiesKotlin.state = state

    }

}
