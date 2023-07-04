package com.sctgold.goldinvest.android.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import android.content.Intent
import android.widget.FrameLayout
import androidx.core.content.ContextCompat


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TradingConfirmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TradingConfirmFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var frame: FrameLayout
    private lateinit var lb1: TextView
    private lateinit var vl1: TextView
    private lateinit var lb2: TextView
    private lateinit var vl2: TextView
    private lateinit var lb3: TextView
    private lateinit var vl3: TextView
    private lateinit var lb4: TextView
    private lateinit var vl4: TextView
    private lateinit var un4: TextView
    private lateinit var lb5: TextView
    private lateinit var vl5: TextView
    private lateinit var un5: TextView
    private lateinit var lb6: TextView
    private lateinit var vl6: TextView
    private lateinit var un6: TextView
    private lateinit var lb7: TextView
    private lateinit var vl7: TextView
    private lateinit var un7: TextView
    private lateinit var lb8: TextView
    private lateinit var vl8: TextView

    private lateinit var ok: Button
    var state: String = "TradingConfirmFragment"
    var pdName: String = ""

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
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_trading_confirm, container, false)

        frame = v.findViewById(R.id.frag_trading_confirm)
        lb1 = v.findViewById(R.id.row_confirm_label1)
        vl1 = v.findViewById(R.id.row_confirm_value1)
        lb2 = v.findViewById(R.id.row_confirm_label2)
        vl2 = v.findViewById(R.id.row_confirm_value2)
        lb3 = v.findViewById(R.id.row_confirm_label3)
        vl3 = v.findViewById(R.id.row_confirm_value3)
        lb4 = v.findViewById(R.id.row_confirm_label4)
        vl4 = v.findViewById(R.id.row_confirm_value4)
        un4 = v.findViewById(R.id.row_confirm_unit4)
        lb5 = v.findViewById(R.id.row_confirm_label5)
        vl5 = v.findViewById(R.id.row_confirm_value5)
        un5 = v.findViewById(R.id.row_confirm_unit5)
        lb6 = v.findViewById(R.id.row_confirm_label6)
        vl6 = v.findViewById(R.id.row_confirm_value6)
        un6 = v.findViewById(R.id.row_confirm_unit6)
        lb7 = v.findViewById(R.id.row_confirm_label7)
        vl7 = v.findViewById(R.id.row_confirm_value7)
        un7 = v.findViewById(R.id.row_confirm_unit7)
        ok = v.findViewById(R.id.btn_confirm_trade_close)

        lb8 = v.findViewById(R.id.row_confirm_label8)
        vl8 = v.findViewById(R.id.row_confirm_value8)

        val prefix = ": "
        val listPrefix = arrayOf(
            getString(R.string.usr_acc) + prefix,
                    getString(R.string.usr_qu) + prefix,
            getString(R.string.usr_typ) + prefix,
            getString(R.string.usr_ord) + prefix,
            getString(R.string.usr_spo) + prefix,
            getString(R.string.usr_pri) + prefix,
            getString(R.string.usr_qty) + prefix,
            getString(R.string.usr_tot) + prefix
        )
        lb1.text = listPrefix[0]
        lb8.text = listPrefix[1]
        lb2.text = listPrefix[2]
        lb3.text = listPrefix[3]
        lb4.text = listPrefix[4]
        lb5.text = listPrefix[5]
        lb6.text = listPrefix[6]
        lb7.text = listPrefix[7]


        val accounts = arrayOf(
            "1005990",
            "1005678",
            "1001001"
        )
        val types = arrayOf(
            getString(R.string.un_rt),
            getString(R.string.un_po),
            getString(R.string.un_so)
        )
        val sides = arrayOf(
            getString(R.string.sell),
            getString(R.string.buy)
        )

        val units = arrayOf(
            getString(R.string.un_usd),
            getString(R.string.un_mb),
            getString(R.string.un_baht),
            getString(R.string.un_kilo),
            getString(R.string.un_thb)
        )
        val counts = 5
        val infos = arrayOf(
            getString(R.string.do_lead) + " " + counts.toString() + " " + getString(R.string.do_trail),
            getString(R.string.do_sec),
        )

        var tradeType = arguments?.getString("tradeType").toString()
        pdName = arguments?.getString("productName").toString()
        var type = ""
        when (tradeType) {
            "REALTIME" -> {
                type = getString(R.string.un_rt)
                frame.setBackgroundResource(R.color.bg_de_red)
            }
            "PLACE_ORDER" -> {
                type = getString(R.string.un_po)
                frame.setBackgroundResource(R.color.bg_de_red)
            }
            "STOP_ORDER" -> {
                type = getString(R.string.un_so)
                frame.setBackgroundResource(R.color.bg_de_red)
            }
        }

        var tradeSide = arguments?.getString("tradeSide").toString()
        var side = ""
        when (tradeSide) {
            "S" -> {
                side = getString(R.string.sell)
                vl1.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))
                vl2.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))
                vl3.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))
                vl4.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))
                vl5.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))
                vl6.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))
                vl7.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))
                vl8.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell_yellow))

            }
            "B" -> {
                side = getString(R.string.buy)
                vl1.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))
                vl2.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))
                vl3.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))
                vl4.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))
                vl5.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))
                vl6.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))
                vl7.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))
                vl8.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy_yellow))

            }
        }
        var priceSpot = arguments?.getString("priceSpot").toString()
        var priceUse = arguments?.getString("price").toString()
        var quantity = arguments?.getString("quantity").toString()
        var amount = arguments?.getString("amount").toString()
        var ivNumber = arguments?.getString("ivNumber").toString()
        var qtyUnit=  arguments?.getString("qtyUnit").toString()
        vl1.text = PropertiesKotlin.account
        vl8.text = ivNumber

        vl2.text = type
        vl3.text = side
        vl4.text = priceSpot
        vl5.text = priceUse
        vl6.text = quantity
        vl7.text = amount

        if (PropertiesKotlin.currency == "USD") {
            un4.text = units[0]
            un5.text = units[0]
//            un6.text = units[3]
            un7.text = units[1]

        } else {
            un4.text = units[0]
            un5.text = units[1]
//            un6.text = units[2]
            un7.text = units[1]
        }
        un6.text = qtyUnit

        ok.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)  }
        return v
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TradingConfirmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TradingConfirmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = "Gold 96.5%"
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
        title.text = pdName
    }

}
