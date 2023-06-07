package com.sctgold.wgold.android.fragments

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.activities.HomeActivity
import com.sctgold.wgold.android.rest.ServiceApiSpot
import com.sctgold.wgold.android.dto.MatchingSelectListDTO
import com.sctgold.wgold.android.dto.ProcessClearPortDTO
import com.sctgold.wgold.android.util.OnItemClickListener
import com.sctgold.wgold.android.util.ProgressDialog
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.sctgold.android.tradeplus.util.ManageData
import kotlinx.android.synthetic.main.row_matching.view.*
import java.text.DecimalFormat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClearPortActionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClearPortActionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var matchSellView: RecyclerView
    private lateinit var matchBuyView: RecyclerView
    private lateinit var manageView: LinearLayout
    private lateinit var sellView: TextView
    private lateinit var buyView: TextView
    private lateinit var labelSelect: TextView
    private lateinit var selectTf: RadioGroup
    private lateinit var myPort: RadioButton
    private lateinit var bankTf: RadioButton
    private lateinit var totals: TextView
    private lateinit var totalText: TextView
    private lateinit var oks: Button
    var productsCode = ""


    private var tracker: SelectionTracker<Long>? = null
    private var itemSell: ArrayList<DataMatch> = ArrayList()
    private var itemBuy: ArrayList<DataMatch> = ArrayList()
    var sellSelect: ArrayList<DataMatch>? = ArrayList()
    var sellUnSelect: ArrayList<DataMatch>? = ArrayList()

    var buySelect: ArrayList<DataMatch>? = ArrayList()
    var buyUnSelect: ArrayList<DataMatch>? = ArrayList()

    var dialog: Dialog? = null
    var state: String = "ClearPortActionFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (savedInstanceState != null)
//            tracker?.onRestoreInstanceState(savedInstanceState)
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
        val v = inflater.inflate(R.layout.fragment_clear_port_action, container, false)

        refresh = v.findViewById(R.id.clear_port_action_refresh)
        refresh.setColorSchemeResources(R.color.step1, R.color.step2, R.color.step3, R.color.step4)
        refresh.setOnRefreshListener { loadData() }

        matchSellView = v.findViewById(R.id.clear_port_action_sell)
        matchBuyView = v.findViewById(R.id.clear_port_action_buy)
        manageView = v.findViewById(R.id.clear_port_action_bottom)
        sellView = v.findViewById(R.id.clear_port_side_sell)
        buyView = v.findViewById(R.id.clear_port_side_buy)
        labelSelect = v.findViewById(R.id.clear_port_lb_select)
        selectTf = v.findViewById(R.id.clear_port_type_transfer)
        myPort = v.findViewById(R.id.clear_port_my_port)
        bankTf = v.findViewById(R.id.clear_port_bank_transfer)
        totals = v.findViewById(R.id.clear_port_total)
        oks = v.findViewById(R.id.clear_port_ok)
        totalText = v.findViewById(R.id.clear_port_total_text)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = ProgressDialog.progressDialog(context)
        dialog?.show()
        matchBuyView.isVisible = false

        oks.isEnabled = false
        oks.isClickable = false


        sumSell = 0.00
        sumBuy = 0.00
        val df = DecimalFormat("#,###.##")
        val white = ContextCompat.getColor(context, R.color.white)
        val tint = ContextCompat.getColor(context, R.color.tint)
        val tintDis = ContextCompat.getColor(context, R.color.text_tag)

        val min = ContextCompat.getColor(context, R.color.m_red)
        val max = ContextCompat.getColor(context, R.color.m_green)
        val bgOK = ContextCompat.getDrawable(context, R.drawable.border_ok)
        bgOK?.colorFilter = PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)


        matchSellView.layoutManager =
            activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
        matchSellView.itemAnimator = DefaultItemAnimator()

        matchSellView.adapter = MatchingAdapter(itemSell, context, object : OnItemClickListener {
            override fun onSumUnit(value: Double) {
                sellView.text = ManageData().numberFormatToDecimal(value)

                when {
                    sellView.text.toString() == "0.00" -> {
                        oks.isEnabled = false
                        oks.isClickable = false
                        bgOK?.colorFilter = PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                    }
                    buyView.text.toString() == sellView.text.toString() -> {
                        oks.isEnabled = true
                        oks.isClickable = true
                        bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                    }
                    else -> {
                        oks.isEnabled = false
                        oks.isClickable = false
                        bgOK?.colorFilter = PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                    }
                }
            }

            override fun onListSelect(select: ArrayList<DataMatch>) {
                sellSelect?.addAll(select)
            }

            override fun onListUnSelect(unSelect: ArrayList<DataMatch>) {
                //  sellUnSelect?.addAll(unSelect)

                for (x in unSelect) {
                    if (x.sid == R.string.sell) {

                        sellUnSelect?.add(x)
                        sellSelect!!.remove(x)
                    }
                }
            }

            override fun onButtonDis(btnDisable: Boolean) {
                oks.isEnabled = btnDisable
                if (btnDisable) {
                    bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                } else {
                    bgOK?.colorFilter = PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)

                }
                oks.background = bgOK
                oks.isClickable = btnDisable
            }

            override fun onSumTotal(total: Double) {
                totals.text = ManageData().numberFormatToDecimal(total)
                when {
                    total > 0.00 -> {
                        totals.text =
                            "+" + ManageData().numberFormatToDecimal(total)
                        totals.setTextColor(max)

                        totalText.setTextColor(max)
                    }
                    total == 0.00 -> {
                        totals.setTextColor(tint)
                        totalText.setTextColor(tint)
                    }
                    else -> {
                        totals.text = ManageData().numberFormatToDecimal(total)
                        totals.setTextColor(min)
                        totalText.setTextColor(min)
                    }
                }

            }

        })

        matchSellView.adapter?.notifyDataSetChanged()


        matchBuyView.layoutManager =
            activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
        matchBuyView.itemAnimator = DefaultItemAnimator()
        matchBuyView.adapter = MatchingAdapter(itemBuy, context, object : OnItemClickListener {
            override fun onSumUnit(value: Double) {

                buyView.text = ManageData().numberFormatToDecimal(value)


                when {
                    buyView.text.toString() == "0.00" -> {
                        oks.isEnabled = false
                        oks.isClickable = false

                        bgOK?.colorFilter = PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                    }
                    buyView.text.toString() == sellView.text.toString() -> {
                        oks.isEnabled = true
                        oks.isClickable = true
                        bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                    }
                    else -> {
                        oks.isEnabled = false
                        oks.isClickable = false
                        bgOK?.colorFilter = PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                    }
                }
            }

            override fun onListSelect(select: ArrayList<DataMatch>) {
            }

            override fun onListUnSelect(unSelect: ArrayList<DataMatch>) {
            }

            override fun onButtonDis(btnDisable: Boolean) {
                oks.isEnabled = btnDisable
                oks.isClickable = btnDisable
                if (btnDisable) {
                    bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                } else {
                    bgOK?.colorFilter = PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)

                }
                oks.background = bgOK
            }

            override fun onSumTotal(total: Double) {
            }
        })

        matchBuyView.adapter?.notifyDataSetChanged()


        manageView.setBackgroundColor(
            ContextCompat.getColor(context, R.color.white)
        )

        sellView.text = "0.00"
        sellView.setBackgroundColor(
            ContextCompat.getColor(context, R.color.pm_sell)
        )

        buyView.text = "0.00"
        buyView.setBackgroundColor(
            ContextCompat.getColor(context, R.color.pm_buy)
        )

        selectTf.setOnCheckedChangeListener { group, _ ->

            when (group.checkedRadioButtonId) {
                R.id.clear_port_my_port -> {
//                    Toast.makeText(context, "myport", Toast.LENGTH_SHORT).show()
                    myPort.setTextColor(white)
                    bankTf.setTextColor(tint)
                    payType = "PORT"
                    when {
                        ManageData().numberFormatToDouble(sellView.text.toString()) == 0.00 && ManageData().numberFormatToDouble(
                            buyView.text.toString()
                        )
                                == 0.00 -> {
                            bgOK?.colorFilter =
                                PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = false
                            oks.isClickable = false
                            oks.background = bgOK
                        }
                        ManageData().numberFormatToDouble(sellView.text.toString()) == ManageData().numberFormatToDouble(buyView.text.toString()) -> {
                            bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = true
                            oks.isClickable = true
                            oks.background = bgOK
                        }
                        ManageData().numberFormatToDouble(sellView.text.toString()) > ManageData().numberFormatToDouble(buyView.text.toString())
                                && ManageData().numberFormatToDouble(buyView.text.toString()) == 0.00 -> {
                            bgOK?.colorFilter =
                                PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = true
                            oks.isClickable = true
                            oks.background = bgOK

                        }
                        ManageData().numberFormatToDouble(sellView.text.toString()) < ManageData().numberFormatToDouble(buyView.text.toString()) -> {
                            bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = true
                            oks.isClickable = true
                            oks.background = bgOK
                        }
                        ManageData().numberFormatToDouble(sellView.text.toString()) > ManageData().numberFormatToDouble(buyView.text.toString())
                                && ManageData().numberFormatToDouble(buyView.text.toString()) > 0.00 -> {
                            bgOK?.colorFilter =
                                PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = false
                            oks.isClickable = false
                            oks.background = bgOK

                        }
                    }
                }
                R.id.clear_port_bank_transfer -> {
//                    Toast.makeText(context, "bank", Toast.LENGTH_SHORT).show()
                    myPort.setTextColor(tint)
                    bankTf.setTextColor(white)
                    payType = "TR"

                    when {
                        ManageData().numberFormatToDouble( sellView.text.toString()) == 0.00 &&ManageData().numberFormatToDouble( buyView.text.toString()) == 0.00 -> {
                            bgOK?.colorFilter =
                                PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = false
                            oks.isClickable = false
                            oks.background = bgOK
                        }
                        ManageData().numberFormatToDouble(sellView.text.toString()) == ManageData().numberFormatToDouble(buyView.text.toString()) -> {
                            bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = true
                            oks.isClickable = true
                            oks.background = bgOK
                        }
                        ManageData().numberFormatToDouble(   sellView.text.toString()) <ManageData().numberFormatToDouble( buyView.text.toString()) -> {
                            bgOK?.colorFilter = PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = true
                            oks.isClickable = true
                            oks.background = bgOK
                        }
                        ManageData().numberFormatToDouble( sellView.text.toString()) > ManageData().numberFormatToDouble(buyView.text.toString()) -> {
                            bgOK?.colorFilter =
                                PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)
                            oks.isEnabled = false
                            oks.isClickable = false
                            oks.background = bgOK
                        }
                    }
                }
            }
        }

//        val result = "Total: " + df.format(rdTotal).toString()
        val result = df.format(0.00).toString()
        totals.text = result
        totals.gravity = Gravity.LEFT
        when {
            totals.text.toString().toDouble() > 0.00 -> {
                totals.setTextColor(max)
            }
            totals.text.toString().toDouble() == 0.00 -> {
                totals.setTextColor(tint)
            }
            else -> {
                totals.setTextColor(min)
            }
        }

        oks.setTextColor(white)
        oks.background = bgOK

        oks.setOnClickListener {
            clearPort()
        }
    }


    data class DataMatch(
        var sid: Int,
        var cel: Int,
        var col: Int,
        var orderId: Int,
        var code: String,
        var dueDate: String,
        var productName: String,
        var qty: String,
        var price: String,
        var total: String,
        var overdue: String,
        var qtyUnit: String,
        var selectC: Boolean,
        var productCode: String,
//        var selectN: Int,
    )

    class MatchingAdapter(
        private val lists: List<DataMatch>,
        val context: Context,
        private var onItemClickListener: OnItemClickListener
    ) :
        RecyclerView.Adapter<MatchingAdapter.ViewHolder>() {
        var items: ArrayList<DataMatch>? = ArrayList()


        var tracker: SelectionTracker<Long>? = null
//        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        init {
            setHasStableIds(true)

        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            init {
                itemView.setOnClickListener {
                    for (i in lists.indices) {
                        itemView.isSelected = false
                    }
                    itemView.isSelected = true
                    notifyDataSetChanged()

                }
            }

            fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
                object : ItemDetailsLookup.ItemDetails<Long>() {
                    override fun getPosition(): Int = adapterPosition
                    override fun getSelectionKey(): Long? = itemId
                }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_matching, parent, false)
            )
        }

        override fun getItemCount() = lists.size


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var data: DataMatch = lists[position]


            holder.itemView.matching_row_code.text = data.code
            holder.itemView.matching_row_code.setTextColor(
                ContextCompat.getColor(context, data.col)
            )

//            holder.itemView.matching_row_code.setTextColor(
//                ContextCompat.getColor(context, R.color.white)
//            )

            val borderAll = ContextCompat.getDrawable(context, R.drawable.border_all)
            val borderLeft = ContextCompat.getDrawable(context, R.drawable.border_bottom_left)
            val borderRight = ContextCompat.getDrawable(context, R.drawable.border_bottom_right)
            borderAll?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, data.cel), PorterDuff.Mode.SRC_IN
            )
//            borderLeft?.colorFilter = PorterDuffColorFilter(
//                ContextCompat.getColor(context, data.cel), PorterDuff.Mode.SRC_IN
//            )
//            borderRight?.colorFilter = PorterDuffColorFilter(
//                ContextCompat.getColor(context, data.cel), PorterDuff.Mode.SRC_IN
//            )

            holder.itemView.matching_cell_1.background = borderAll
            holder.itemView.matching_cell_2.background = borderAll
            holder.itemView.matching_cell_3.background = borderAll
            holder.itemView.matching_cell_4.background = borderAll
            holder.itemView.matching_cell_5.background = borderAll
            holder.itemView.matching_cell_6.background = borderAll

            holder.itemView.matching_lb_due.text = context.resources.getString(R.string.cp_due)
            holder.itemView.matching_lb_product.text = context.resources.getString(R.string.cp_pro)
            holder.itemView.matching_lb_qty.text = context.resources.getString(R.string.cp_qty)
            holder.itemView.matching_lb_price.text = context.resources.getString(R.string.cp_pri)
            holder.itemView.matching_lb_total.text = context.resources.getString(R.string.cp_tot)
            holder.itemView.matching_lb_over.text = context.resources.getString(R.string.cp_ovd)

            holder.itemView.matching_val_due.text = data.dueDate
            holder.itemView.matching_val_product.text = data.productName
            holder.itemView.matching_val_qty.text = data.qty + " " + data.qtyUnit
            holder.itemView.matching_val_price.text = data.price
            holder.itemView.matching_val_total.text = data.total
            holder.itemView.matching_val_over.text = data.overdue



            holder.itemView.matching_val_due.setTextColor(
                ContextCompat.getColor(context, data.col)
            )
            holder.itemView.matching_val_product.setTextColor(
                ContextCompat.getColor(context, data.col)
            )
            holder.itemView.matching_val_qty.setTextColor(
                ContextCompat.getColor(context, data.col)
            )
            holder.itemView.matching_val_price.setTextColor(
                ContextCompat.getColor(context, data.col)
            )
            holder.itemView.matching_val_total.setTextColor(
                ContextCompat.getColor(context, data.col)
            )
            holder.itemView.matching_val_over.setTextColor(
                ContextCompat.getColor(context, data.col)
            )

            holder.itemView.matching_row_container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.bd_match)
            )


            if (side == "S") {
                if (data.selectC) {
                    highlightView(holder, data)
                }
                holder.itemView.isSelected = data.selectC

                holder.itemView.setOnClickListener {
                    val tint = ContextCompat.getColor(context, R.color.tint)
                    val tintDis = ContextCompat.getColor(context, R.color.text_tag)
                    val bgOK = ContextCompat.getDrawable(context, R.drawable.border_ok)
                    holder.itemView.isSelected = !holder.itemView.isSelected

                    if (!holder.itemView.isSelected) {
                        selectedSell!!.remove(data)

                        data.selectC = false
                        unSelectedSell?.add(data)
                        unhighlightView(holder, data)
                        tracker?.deselect(position.toLong())
                        if (sumSell > ManageData().numberFormatToDouble(data.qty)) {
                            sumSell -= ManageData().numberFormatToDouble(data.qty)
                        } else {
                            sumSell = ManageData().numberFormatToDouble(data.qty) - sumSell
                        }
                        var subOverdue: Double = if (data.overdue != null) {

                            ManageData().numberFormatToDouble(data.total) - ManageData().numberFormatToDouble(
                                data.overdue
                            )
                        } else {
                            ManageData().numberFormatToDouble(data.total)
                        }

                        sumTotal -= ManageData().numberFormatToDouble(subOverdue.toString())
                    } else {
                        selectedSell?.clear()
                        selectedSell = ArrayList()
                        sumSell += ManageData().numberFormatToDouble(data.qty)
                        data.selectC = true
                        selectedSell!!.add(data)
                        highlightView(holder, data)
                        tracker?.isSelected(position.toLong())
                        unSelectedSell?.remove(data)

                        var subOverdue: Double = if (data.overdue != null) {
                            ManageData().numberFormatToDouble(data.total) - ManageData().numberFormatToDouble(
                                data.overdue
                            )
                        } else {
                            ManageData().numberFormatToDouble(data.total)
                        }
                        sumTotal += ManageData().numberFormatToDouble(subOverdue.toString())

                    }

                    selectedSell!!.sortBy { data.orderId }
                    onItemClickListener.onSumUnit(sumSell)
                    onItemClickListener.onListSelect(selectedSell!!)
                    onItemClickListener.onListUnSelect(unSelectedSell!!)
                    onItemClickListener.onSumTotal(sumTotal)
                    if (sumSell != 0.00) {
                        when {
                            sumBuy == sumSell -> {
                                ckBtn = true
                            }
                            sumBuy > sumSell -> {
                                ckBtn = true

                            }
                            sumBuy < sumSell && sumBuy == 0.00 -> {
                                ckBtn = payType == "PORT"
                            }

                            sumBuy < sumSell && sumBuy != 0.00 -> {
                                ckBtn = false
                            }
                            sumBuy == 0.00 -> {
                                ckBtn = true

                            }
                        }
                    } else {
                        ckBtn = sumBuy != 0.00

                    }
                    onItemClickListener.onButtonDis(ckBtn)


                }


            } else {
                if (data.selectC) {
                    highlightView(holder, data)
                }
                holder.itemView.isSelected = data.selectC
                holder.itemView.setOnClickListener {
                    holder.itemView.isSelected = !holder.itemView.isSelected
                    if (!holder.itemView.isSelected) {
                        selectedBuy!!.remove(data)

                        data.selectC = false
                        unSelectedBuy?.add(data)
                        unhighlightView(holder, data)
                        tracker?.deselect(position.toLong())
                        if (sumBuy > ManageData().numberFormatToDouble(data.qty)) {
                            sumBuy -= ManageData().numberFormatToDouble(data.qty)
                        } else {
                            sumBuy = ManageData().numberFormatToDouble(data.qty) - sumBuy
                        }
                        var subOverdue: Double = if (data.overdue != null) {

                            ManageData().numberFormatToDouble(data.total) + ManageData().numberFormatToDouble(
                                data.overdue
                            )
                        } else {
                            ManageData().numberFormatToDouble(data.total)
                        }

                        sumTotal += ManageData().numberFormatToDouble(subOverdue.toString())
                    } else {
                        selectedBuy?.clear()
                        sumBuy += ManageData().numberFormatToDouble(data.qty)
                        data.selectC = true
                        selectedBuy!!.add(data)
                        highlightView(holder, data)
                        tracker?.isSelected(position.toLong())
                        unSelectedBuy?.remove(data)
                        var subOverdue: Double = if (data.overdue != null) {
                            ManageData().numberFormatToDouble(data.total) + ManageData().numberFormatToDouble(
                                data.overdue
                            )
                        } else {
                            ManageData().numberFormatToDouble(data.total)
                        }
                        sumTotal -= ManageData().numberFormatToDouble(subOverdue.toString())
                    }
                    selectedBuy!!.sortBy { data.orderId }
                    onItemClickListener.onSumUnit(sumBuy)
                    onItemClickListener.onListSelect(selectedBuy!!)
                    onItemClickListener.onListUnSelect(unSelectedBuy!!)
                    onItemClickListener.onSumTotal(sumTotal)
                    if (sumBuy != 0.00) {
                        when {
                            sumBuy == sumSell -> {
                                ckBtn = true
                            }
                            sumBuy < sumSell -> {
                                ckBtn = false
                            }
                            sumBuy > sumSell && sumSell == 0.00 -> {
                                ckBtn = true

                            }
                            sumSell == 0.00 -> {
                                ckBtn = true

                            }
                        }
                    } else {
                        ckBtn = sumSell != 0.00

                    }
                    onItemClickListener.onButtonDis(ckBtn)


                }


            }


        }


        private fun highlightView(holder: ViewHolder, data: DataMatch) {
            holder.itemView.matching_lb_due.setTextColor(
                ContextCompat.getColor(context, R.color.black_norm)
            )
            holder.itemView.matching_lb_product.setTextColor(
                ContextCompat.getColor(context, R.color.black_norm)
            )
            holder.itemView.matching_lb_qty.setTextColor(
                ContextCompat.getColor(context, R.color.black_norm)
            )
            holder.itemView.matching_lb_price.setTextColor(
                ContextCompat.getColor(context, R.color.black_norm)
            )
            holder.itemView.matching_lb_total.setTextColor(
                ContextCompat.getColor(context, R.color.black_norm)
            )
            holder.itemView.matching_lb_over.setTextColor(
                ContextCompat.getColor(context, R.color.black_norm)
            )

            holder.itemView.matching_row_code.setTextColor(
                ContextCompat.getColor(context, R.color.white)
            )
            holder.itemView.matching_row_container.setBackgroundColor(
                ContextCompat.getColor(context, data.col)
            )

            val borderAllSelect = ContextCompat.getDrawable(context, R.drawable.border_all)

            if (data.sid == R.string.sell) {
                borderAllSelect?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.pm_sell_light),
                    PorterDuff.Mode.SRC_IN
                )
            } else {
                borderAllSelect?.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(context, R.color.pm_buy_light),
                    PorterDuff.Mode.SRC_IN
                )
            }

            holder.itemView.matching_cell_1.background = borderAllSelect
            holder.itemView.matching_cell_2.background = borderAllSelect
            holder.itemView.matching_cell_3.background = borderAllSelect
            holder.itemView.matching_cell_4.background = borderAllSelect
            holder.itemView.matching_cell_5.background = borderAllSelect
            holder.itemView.matching_cell_6.background = borderAllSelect


        }

        private fun unhighlightView(holder: ViewHolder, data: DataMatch) {

            holder.itemView.matching_lb_due.setTextColor(
                ContextCompat.getColor(context, R.color.white_norm)
            )
            holder.itemView.matching_lb_product.setTextColor(
                ContextCompat.getColor(context, R.color.white_norm)
            )
            holder.itemView.matching_lb_qty.setTextColor(
                ContextCompat.getColor(context, R.color.white_norm)
            )
            holder.itemView.matching_lb_price.setTextColor(
                ContextCompat.getColor(context, R.color.white_norm)
            )
            holder.itemView.matching_lb_total.setTextColor(
                ContextCompat.getColor(context, R.color.white_norm)
            )
            holder.itemView.matching_lb_over.setTextColor(
                ContextCompat.getColor(context, R.color.white_norm)
            )



            holder.itemView.matching_row_code.setTextColor(
                ContextCompat.getColor(context, data.col)
            )

            holder.itemView.matching_row_container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.bd_match)
            )
            val borderAll = ContextCompat.getDrawable(context, R.drawable.border_all)
//            val borderLeft = ContextCompat.getDrawable(context, R.drawable.border_bottom_left)
//            val borderRight = ContextCompat.getDrawable(context, R.drawable.border_bottom_right)

            borderAll?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, data.cel), PorterDuff.Mode.SRC_IN
            )
//            borderLeft?.colorFilter = PorterDuffColorFilter(
//                ContextCompat.getColor(context, data.cel), PorterDuff.Mode.SRC_IN
//            )
//            borderRight?.colorFilter = PorterDuffColorFilter(
//                ContextCompat.getColor(context, data.cel), PorterDuff.Mode.SRC_IN
//            )


            holder.itemView.matching_cell_1.background = borderAll
            holder.itemView.matching_cell_2.background = borderAll
            holder.itemView.matching_cell_3.background = borderAll
            holder.itemView.matching_cell_4.background = borderAll
            holder.itemView.matching_cell_5.background = borderAll
            holder.itemView.matching_cell_6.background = borderAll


        }

    }


    private fun loadData() {
        refresh.isRefreshing = true
        // request reload data below
        refresh.isRefreshing = false
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val v = (activity as HomeActivity)
        v.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val title = v.findViewById<TextView>(R.id.title_main_text)
        title.visibility = View.GONE


        val tabLayout = v.findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE

        val white = ContextCompat.getColor(context, R.color.white)
        val sell = ContextCompat.getColor(context, R.color.pm_sell)
        val buy = ContextCompat.getColor(context, R.color.pm_buy)

        val tint = ContextCompat.getColor(context, R.color.tint)
        val tintDis = ContextCompat.getColor(context, R.color.text_tag)
        val bgOK = ContextCompat.getDrawable(context, R.drawable.border_ok)

        val selectOrders: RadioGroup = v.findViewById(R.id.clear_port_sl_order)
        selectOrders.visibility = View.VISIBLE
        selectOrders.check(R.id.clear_port_sl_sell)
        val sellOrders: RadioButton = v.findViewById(R.id.clear_port_sl_sell)
        val buyOrders: RadioButton = v.findViewById(R.id.clear_port_sl_buy)
        val max = ContextCompat.getColor(context, R.color.m_green)
        val min = ContextCompat.getColor(context, R.color.m_red)

        selectOrders.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.clear_port_sl_sell -> {
                    side = "S"
                    sellAll = ArrayList()
                    sellAll.clear()
                    matchSellView.isVisible = true
                    matchBuyView.isVisible = false
                    sellOrders.setTextColor(white)
                    buyOrders.setTextColor(buy)

                    if (savedInstanceState != null) {
                        tracker?.onSaveInstanceState(savedInstanceState)
                    }

                    sellSelect = sellSelect?.distinctBy { it.orderId } as ArrayList<DataMatch>?
                    var newUnSell = sellUnSelect!!.distinctBy { it.orderId }
                    newUnSell = newUnSell.minus(sellSelect!!.toSet())
                    if (sellSelect?.size == 0) {
                        sellAll = ArrayList()
                    } else {
                        sellSelect?.let { sellAll.addAll(it) }
                        newUnSell.let { sellAll.addAll(it) }
                    }


                    var ss: List<DataMatch> = if (sellAll.size != 0) {
                        sellAll
                    } else {
                        itemSell
                    }


                    var adapter =
                        MatchingAdapter(
                            ss,
                            context,
                            object : OnItemClickListener {
                                override fun onSumUnit(value: Double) {
                                    sellView.text = ManageData().numberFormatToDecimal(value)
                                    if (value == ManageData().numberFormatToDouble(buyView.text.toString())) {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                        oks.isEnabled = true
                                        oks.isClickable = true
                                        oks.background = bgOK
                                    } else if (value < ManageData().numberFormatToDouble(buyView.text.toString()
                                          ) && value == 0.00
                                    ) {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                        oks.isEnabled = true
                                        oks.isClickable = true
                                        oks.background = bgOK
                                    } else if (value > ManageData().numberFormatToDouble(buyView.text.toString()
                                          ) && ManageData().numberFormatToDouble(buyView.text.toString()
                                          ) == 0.00
                                    ) {
                                        if (payType == "PORT") {
                                            bgOK?.colorFilter =
                                                PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                            oks.isEnabled = true
                                            oks.isClickable = true
                                            oks.background = bgOK
                                        } else {
                                            oks.isEnabled = false
                                            oks.isClickable = false
                                            bgOK?.colorFilter = PorterDuffColorFilter(
                                                tintDis,
                                                PorterDuff.Mode.SRC_IN
                                            )
                                            oks.background = bgOK
                                        }
                                    } else if (value < ManageData().numberFormatToDouble(buyView.text.toString()
                                         ) && value != 0.00
                                    ) {

                                        oks.isEnabled = false
                                        oks.isClickable = false
                                        bgOK?.colorFilter = PorterDuffColorFilter(
                                            tintDis,
                                            PorterDuff.Mode.SRC_IN
                                        )
                                        oks.background = bgOK
                                    } else if (value >ManageData().numberFormatToDouble( buyView.text.toString()
                                           ) && ManageData().numberFormatToDouble(buyView.text.toString()
                                          ) != 0.00
                                    ) {
                                        oks.isEnabled = false
                                        oks.isClickable = false
                                        bgOK?.colorFilter = PorterDuffColorFilter(
                                            tintDis,
                                            PorterDuff.Mode.SRC_IN
                                        )
                                        oks.background = bgOK
                                    }
                                }

                                override fun onListSelect(select: ArrayList<DataMatch>) {
                                    sellSelect?.addAll(select)

                                }

                                override fun onListUnSelect(unSelect: ArrayList<DataMatch>) {

                                    var newUnList: ArrayList<DataMatch>? = ArrayList()
                                    for (x in unSelect) {
                                        if (x.sid == R.string.sell) {

                                            newUnList?.add(x)
                                            sellSelect!!.remove(x)
                                        }
                                    }

                                    if (newUnList != null) {
                                        sellUnSelect!!.addAll(newUnList)

                                    }
                                }

                                override fun onButtonDis(btnDisable: Boolean) {
                                    oks.isEnabled = btnDisable
                                    oks.isClickable = btnDisable
                                    if (btnDisable) {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                    } else {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)

                                    }
                                    oks.background = bgOK
                                }

                                override fun onSumTotal(total: Double) {

                                    when {
                                        total > 0.00 -> {
                                            totals.text =
                                                "+" + ManageData().numberFormatToDecimal(total)
                                            totals.setTextColor(max)
                                            totalText.setTextColor(max)
                                        }
                                        total == 0.00 -> {
                                            totals.setTextColor(tint)
                                            totalText.setTextColor(tint)
                                        }
                                        else -> {

                                            totals.text = ManageData().numberFormatToDecimal(total)
                                            totals.setTextColor(min)
                                            totalText.setTextColor(min)
                                        }
                                    }
                                }

                            })

                    matchSellView.adapter = adapter

                }
                R.id.clear_port_sl_buy -> {


                    side = "B"
                    buyAll = ArrayList()
                    buyAll.clear()
                    matchSellView.isVisible = false
                    matchBuyView.isVisible = true
                    sellOrders.setTextColor(sell)
                    buyOrders.setTextColor(white)

                    if (savedInstanceState != null) {
                        tracker?.onSaveInstanceState(savedInstanceState)
                    }

                    buySelect = buySelect?.distinctBy { it.orderId } as ArrayList<DataMatch>?
                    var newUnBuy = buyUnSelect!!.distinctBy { it.orderId }
                    newUnBuy = newUnBuy.minus(buySelect!!)
                    if (buySelect?.size == 0) {
                        buyAll = ArrayList()
                        buyAll.clear()
                    } else {
                        buySelect?.let { buyAll.addAll(it) }
                        newUnBuy.let { buyAll.addAll(it) }
                    }


                    var bb: List<DataMatch> = if (buyAll.size != 0) {
                        buyAll
                    } else {

                        itemBuy
                    }

                    var adapter =
                        MatchingAdapter(
                            bb,
                            context,
                            object : OnItemClickListener {
                                override fun onSumUnit(value: Double) {
                                    buyView.text = ManageData().numberFormatToDecimal(value)
                                    if (value == ManageData().numberFormatToDouble(sellView.text.toString())) {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                        oks.isEnabled = true
                                        oks.isClickable = true
                                        oks.background = bgOK
                                    } else if (value >ManageData().numberFormatToDouble( sellView.text.toString()) && ManageData().numberFormatToDouble(sellView.text.toString()
                                       ) == 0.00
                                    ) {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                        oks.isEnabled = true
                                        oks.isClickable = true
                                        oks.background = bgOK
                                    } else if (value < ManageData().numberFormatToDouble(sellView.text.toString()
                                            ) && value == 0.00
                                    ) {
                                        if (payType == "PORT") {
                                            bgOK?.colorFilter =
                                                PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                            oks.isEnabled = true
                                            oks.isClickable = true
                                            oks.background = bgOK
                                        } else {
                                            oks.isEnabled = false
                                            oks.isClickable = false
                                            bgOK?.colorFilter = PorterDuffColorFilter(
                                                tintDis,
                                                PorterDuff.Mode.SRC_IN
                                            )
                                            oks.background = bgOK
                                        }

                                    } else if (value < ManageData().numberFormatToDouble(sellView.text.toString()
                                          ) && value != 0.00
                                    ) {
                                        oks.isEnabled = false
                                        oks.isClickable = false
                                        bgOK?.colorFilter = PorterDuffColorFilter(
                                            tintDis,
                                            PorterDuff.Mode.SRC_IN
                                        )
                                        oks.background = bgOK
                                    } else if (value > ManageData().numberFormatToDouble(sellView.text.toString()
                                        ) && ManageData().numberFormatToDouble(sellView.text.toString()
                                        ) != 0.00
                                    ) {
                                        oks.isEnabled = false
                                        oks.isClickable = false
                                        bgOK?.colorFilter = PorterDuffColorFilter(
                                            tintDis,
                                            PorterDuff.Mode.SRC_IN
                                        )
                                        oks.background = bgOK
                                    }
                                }

                                override fun onListSelect(select: ArrayList<DataMatch>) {
                                    buySelect?.addAll(select)
                                }

                                override fun onListUnSelect(unSelect: ArrayList<DataMatch>) {

                                    if (unSelect.size != 0) {
                                        buySelect =
                                            buySelect?.minus(unSelect) as ArrayList<DataMatch>?
                                    }

                                    buyUnSelect?.addAll(unSelect)
                                }

                                override fun onButtonDis(btnDisable: Boolean) {
                                    oks.isEnabled = btnDisable
                                    oks.isClickable = btnDisable
                                    if (btnDisable) {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN)
                                    } else {
                                        bgOK?.colorFilter =
                                            PorterDuffColorFilter(tintDis, PorterDuff.Mode.SRC_IN)

                                    }
                                    oks.background = bgOK
                                }

                                override fun onSumTotal(total: Double) {
                                    totals.text = ManageData().numberFormatToDecimal(total)
                                    when {
                                        total > 0.00 -> {
                                            totals.text =
                                                "+" + ManageData().numberFormatToDecimal(total)
                                            totals.setTextColor(max)
                                            totalText.setTextColor(max)
                                        }

                                        total == 0.00 -> {
                                            totals.setTextColor(tint)
                                            totalText.setTextColor(tint)
                                        }
                                        else -> {
                                            totals.text = ManageData().numberFormatToDecimal(total)
                                            totals.setTextColor(min)
                                            totalText.setTextColor(min)
                                        }
                                    }
                                }

                            })

                    matchBuyView.adapter = adapter
                }
            }
        }
    }


    private fun getOrderForMatch() {
        ServiceApiSpot.orderForMatch(productsCode, context) {
            if (it != null) {
                if (dialog?.isShowing == true) {
                    dialog?.dismiss()
                }
                if (it.mobileMatchingSelectSellList != null) {
                    var itemS: ArrayList<DataMatch>
                    for (sell in it.mobileMatchingSelectSellList) {


                        itemS = ArrayList()
                        itemS.clear()
                        itemS.add(
                            DataMatch(
                                R.string.sell,
                                R.color.white,
                                R.color.pm_sell,
                                sell.orderId,
                                sell.code!!,
                                sell.dueDate!!,
                                sell.productName!!,
                                sell.qty!!,
                                sell.price!!,
                                sell.total!!,
                                sell.overdue!!,
                                sell.qtyUnit!!,
                                false,
                                sell.productCode!!
                            )
                        )


                        itemSell.addAll(itemS)
                    }
                    matchSellView.adapter?.notifyDataSetChanged()
                }
                unSelectedSell?.addAll(itemSell)
                if (it.mobileMatchingSelectBuyList != null) {
                    var itemB: ArrayList<DataMatch>
                    for (buy in it.mobileMatchingSelectBuyList) {
                        itemB = ArrayList()
                        itemB.clear()
                        itemB.add(
                            DataMatch(
                                R.string.buy,
                                R.color.white,
                                R.color.pm_buy,
                                buy.orderId,
                                buy.code!!,
                                buy.dueDate!!,
                                buy.productName!!,
                                buy.qty!!,
                                buy.price!!,
                                buy.total!!,
                                buy.overdue!!,
                                buy.qtyUnit!!,
                                false,
                                buy.productCode!!
                            )
                        )


                        itemBuy.addAll(itemB)
                    }
                    matchBuyView.adapter?.notifyDataSetChanged()
                }
                unSelectedBuy?.addAll(itemBuy)

            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        itemSell.clear()
        itemBuy.clear()
        sumSell = 0.00
        sumBuy = 0.00
        selectedSell?.clear()
        selectedBuy?.clear()
        unSelectedSell?.clear()
        unSelectedBuy?.clear()
        sellAll.clear()
        buyAll.clear()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClearPortActionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String, un_sell: ArrayList<DataMatch>) =
            ClearPortActionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }

            }

        var side = "S"
        var selectedSell: ArrayList<DataMatch>? = ArrayList()
        var selectedBuy: ArrayList<DataMatch>? = ArrayList()
        var unSelectedSell: ArrayList<DataMatch>? = ArrayList()
        var unSelectedBuy: ArrayList<DataMatch>? = ArrayList()
        var sellAll: ArrayList<DataMatch> = ArrayList()
        var buyAll: ArrayList<DataMatch> = ArrayList()
        var sumSell = 0.00
        var sumBuy = 0.00
        var ckBtn = false
        var payType = "PORT"
        var sumTotal = 0.00
    }

    private fun clearPort() {

        var data = ProcessClearPortDTO()

        var sellSelectList: ArrayList<MatchingSelectListDTO> = ArrayList()
        var orderSell: MatchingSelectListDTO
        for (sell in sellSelect!!) {
            orderSell = MatchingSelectListDTO()
            orderSell.orderId = sell.orderId
            orderSell.code = sell.code
            orderSell.dueDate = sell.dueDate
            orderSell.productCode = sell.productCode
            orderSell.productName = sell.productName
            orderSell.qty = sell.qty
            orderSell.price = sell.price
            orderSell.total = sell.total
            orderSell.overdue = sell.overdue
            orderSell.side = "S"
            orderSell.qtyUnit = sell.qtyUnit
            sellSelectList.add(orderSell)
        }

        var buySelectList: ArrayList<MatchingSelectListDTO> = ArrayList()
        var orderBuy: MatchingSelectListDTO
        for (buy in buySelect!!) {
            orderBuy = MatchingSelectListDTO()
            orderBuy.orderId = buy.orderId
            orderBuy.code = buy.code
            orderBuy.dueDate = buy.dueDate
            orderBuy.productCode = buy.productCode
            orderBuy.productName = buy.productName
            orderBuy.qty = buy.qty
            orderBuy.price = buy.price
            orderBuy.total = buy.total
            orderBuy.overdue = buy.overdue
            orderBuy.side = "B"
            orderBuy.qtyUnit = buy.qtyUnit
            buySelectList.add(orderBuy)
        }

        data.payType = payType
        data.sellSelect = sellSelectList
        data.buySelect = buySelectList

        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder
            ?.setMessage(getString(R.string.msg_confirm_order))
            ?.setPositiveButton(getString(R.string.btn_confirm),
                DialogInterface.OnClickListener { dialog, id ->
                    ServiceApiSpot.processClearPort(data, context) {

                        var msgResponse = ""
                        if (it != null) {
                            val builderResponse: AlertDialog.Builder? = activity?.let {
                                AlertDialog.Builder(it)
                            }
                            if (it.response == "SUCCESS") {
                                msgResponse =
                                    context.resources.getString(R.string.title_dialog_success)
                                builderResponse?.setTitle(msgResponse)
                                    ?.setPositiveButton(getString(R.string.btn_ok),
                                        DialogInterface.OnClickListener { dialog, id ->
                                            (activity as HomeActivity).onBackPressed()
                                        })
                            } else {
                                msgResponse = it.message.toString()
                                builderResponse
                                    ?.setTitle(msgResponse)
                                    ?.setPositiveButton(getString(R.string.btn_ok),
                                        DialogInterface.OnClickListener { dialog, id ->
                                            builderResponse.create().dismiss()
                                        })


                            }


                            builderResponse?.create()?.show()
                        }

                    }
                })
            ?.setNegativeButton(
                getString(R.string.btn_cancel),
                DialogInterface.OnClickListener { dialog, id ->
                    builder.create().dismiss()
                    // User cancelled the dialog
//                                getDialog().cancel()

                })

//                }
        builder?.create()?.show()

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        val selectOrders: RadioGroup =
            (activity as HomeActivity).findViewById(R.id.clear_port_sl_order)
        selectOrders.visibility = View.VISIBLE
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.visibility = View.GONE
        productsCode = arguments?.getString("productsCode").toString()
        PropertiesKotlin.state = state
        PropertiesKotlin.pdMatch = productsCode
        getOrderForMatch()

        side = "S"
    }

    override fun onStop() {
        super.onStop()
        sumSell = 0.00
        sumBuy = 0.00
        ckBtn = false
        payType = "PORT"
        sumTotal = 0.00
    }


}
