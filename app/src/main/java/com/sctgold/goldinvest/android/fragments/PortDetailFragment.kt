package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.dto.PortfolioDTO
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.row_port_state.view.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PortDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var headerCon: LinearLayout
    private lateinit var headerLb: TextView
    private lateinit var headerVal: TextView
    private lateinit var portDetailView: RecyclerView

    var seAverageCost: String = "0.00"
    var buAverageCost: String = "0.00"
    var seMarketPrice: String = "0.00"
    var buMarketPrice: String = "0.00"
    var seProfitLoss: Double = 0.00
    var buProfitLoss: Double = 0.00
    var seProductAmount: String = "0.00"
    var buProductAmount: String = "0.00"
    var realizePl: String = "0.00"
    var productName = ""

    val i: ArrayList<DataPortDetail> = ArrayList()
    var state: String = "PortDetailFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_port_detail, container, false)

        headerCon = v.findViewById(R.id.port_detail_header_con)
        headerLb = v.findViewById(R.id.port_detail_header_lb)
        headerVal = v.findViewById(R.id.port_detail_header_val)
        portDetailView = v.findViewById(R.id.port_detail_view)

        productName = arguments?.getString("productName").toString()

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val df = DecimalFormat("#,###.##")
        val rdVal: Double = Random.nextDouble(-88888888.00, 99999999.99)
        val rdProdColor = arrayOf(R.color.pm_sell, R.color.pm_buy, R.color.tint)

        when (rdVal) {
            in 0.00..Double.MAX_VALUE -> {
                headerCon.setBackgroundColor(
                    ContextCompat.getColor(context, rdProdColor[1])
                )
            }
            0.00 -> {
                headerCon.setBackgroundColor(
                    ContextCompat.getColor(context, rdProdColor[2])
                )
            }
            else -> {
                headerCon.setBackgroundColor(
                    ContextCompat.getColor(context, rdProdColor[0])
                )
            }
        }

        seAverageCost = arguments?.getString("seAverageCost").toString()
        buAverageCost = arguments?.getString("buAverageCost").toString()
        seProductAmount = arguments?.getString("seProductAmount").toString()
        buProductAmount = arguments?.getString("buProductAmount").toString()
        seMarketPrice = arguments?.getString("seMarketPrice").toString()
        buMarketPrice = arguments?.getString("buMarketPrice").toString()
        seProfitLoss = arguments?.getDouble("seProfitLoss")!!
        buProfitLoss = arguments?.getDouble("buProfitLoss")!!

        if (seAverageCost.isNullOrEmpty()) {
            seAverageCost = "0.00"
        }
        if (buAverageCost.isNullOrEmpty()) {
            buAverageCost = "0.00"
        }
        if (seProductAmount.isNullOrEmpty()) {
            seProductAmount = "0.00"
        }
        if (buProductAmount.isNullOrEmpty()) {
            buProductAmount = "0.00"
        }
        if (seMarketPrice.isNullOrEmpty()) {
            seMarketPrice = "0.00"
        }

        if (buMarketPrice.isNullOrEmpty()) {
            buMarketPrice = "0.00"
        }

        if (seProfitLoss == null) {
            seProfitLoss = 0.00
        }

        if (buProfitLoss == null) {
            buProfitLoss = 0.00
        }

        realizePl = arguments?.getString("realizePl")!!


        headerLb.text = getString(R.string.port_rea)
        headerVal.text = realizePl

        portDetailView.apply {
            portDetailView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            portDetailView.itemAnimator = DefaultItemAnimator()
            adapter?.notifyDataSetChanged()
            adapter = PortDetailAdapter(getPortDetailLists(), context)
        }
        i.clear()
        i.add(
            DataPortDetail(
                getString(R.string.sell), R.color.pm_sell,
                getString(R.string.port_avg), seAverageCost,
                getString(R.string.port_qty), seProductAmount,
                getString(R.string.port_mar), seMarketPrice,
                getString(R.string.port_prl), seProfitLoss,
            )
        )
        portDetailView.adapter?.notifyDataSetChanged()

        i.add(
            DataPortDetail(
                getString(R.string.buy), R.color.pm_buy,
                getString(R.string.port_avg), buAverageCost,
                getString(R.string.port_qty), buProductAmount,
                getString(R.string.port_mar), buMarketPrice,
                getString(R.string.port_prl), buProfitLoss,
            )
        )
        portDetailView.adapter?.notifyDataSetChanged()

    }

    //    data class DataPortDetail(
//        var side: String, var color: Int,
//        var label1: String, var value1: Double,
//        var label2: String, var value2: Double,
//        var label3: String, var value3: Double,
//        var label4: String, var value4: Double,
//    )
    data class DataPortDetail(
        var side: String, var color: Int,
        var label1: String, var value1: String,
        var label2: String, var value2: String,
        var label3: String, var value3: String,
        var label4: String, var value4: Double,
    )


    private fun getPortDetailLists(): ArrayList<DataPortDetail> {
//        val i: ArrayList<DataPortDetail> = ArrayList()
//
//        val rdValA: Double = Random.nextDouble(-88888888.00, 99999999.99)
//        val rdValQ: Double = Random.nextDouble(-88888888.00, 99999999.99)
//        val rdValM: Double = Random.nextDouble(-88888888.00, 99999999.99)
//        val rdValPL: Double = Random.nextDouble(-88888888.00, 99999999.99)

//        i.add(
//            DataPortDetail(
//                getString(R.string.sell), R.color.pm_sell,
//                getString(R.string.port_avg), rdValA,
//                getString(R.string.port_qty), rdValQ,
//                getString(R.string.port_mar), rdValM,
//                getString(R.string.port_prl), rdValPL,
//            )
//        )
//
//        i.add(
//            DataPortDetail(
//                getString(R.string.buy), R.color.pm_buy,
//                getString(R.string.port_avg), rdValA,
//                getString(R.string.port_qty), rdValQ,
//                getString(R.string.port_mar), rdValM,
//                getString(R.string.port_prl), rdValPL,
//            )
//        )

        return i
    }

    private class PortDetailAdapter(private val lists: List<DataPortDetail>, val context: Context) :
        RecyclerView.Adapter<PortDetailAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_port_state, parent, false)
            )
        }

        override fun getItemCount() = lists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data: DataPortDetail = lists[position]

            val df = DecimalFormat("#,###.##")
            val rdProdColor = arrayOf(R.color.pm_sell, R.color.pm_buy, R.color.tint)
            var plus = ""
            plus = if (data.value4 == 0.0) {
                ""
            } else {
                "+"
            }
            var totalPlus = plus + df.format(data.value4).toString()

            holder.itemView.port_state_row_container.setBackgroundColor(
                ContextCompat.getColor(context, data.color)
            )
            holder.itemView.port_state_prod.text = data.side.uppercase(Locale.ROOT)
            holder.itemView.port_state_prod.setTextColor(
                ContextCompat.getColor(context, R.color.port_white_text)
            )

            holder.itemView.port_state_lb1.text = data.label1
            holder.itemView.port_state_lb2.text = data.label2
            holder.itemView.port_state_lb3.text = data.label3
            holder.itemView.port_state_lb4.text = data.label4

            holder.itemView.port_state_val1.text = data.value1
            holder.itemView.port_state_val2.text = data.value2
            holder.itemView.port_state_val3.text = data.value3
            when (data.value4) {
                in 0.00..Double.MAX_VALUE -> {
                    holder.itemView.port_state_val4.text = totalPlus
                    holder.itemView.port_state_val4.setTextColor(
                        ContextCompat.getColor(context, rdProdColor[1])
                    )
                }
                0.00 -> {
                    holder.itemView.port_state_val4.text = df.format(data.value4).toString()
                    holder.itemView.port_state_val4.setTextColor(
                        ContextCompat.getColor(context, rdProdColor[2])
                    )
                }
                else -> {
                    holder.itemView.port_state_val4.text = df.format(data.value4).toString()
                    holder.itemView.port_state_val4.setTextColor(
                        ContextCompat.getColor(context, rdProdColor[0])
                    )
                }
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PortDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PortDetailFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = PortDetailFragment()
        fun newInstance(portfolioDTO: PortfolioDTO) = PortDetailFragment()


    }


//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = productName
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = productName
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }
}
