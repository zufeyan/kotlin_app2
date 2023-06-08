package com.sctgold.goldinvest.android.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.util.ProgressDialog
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.sctgold.android.tradeplus.util.ManageData


class TradingDecisionFragment : Fragment() {

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
    private lateinit var info: TextView
    private lateinit var count: TextView
    private lateinit var second: TextView
    private lateinit var ok: Button

    lateinit var pdCode: String
    lateinit var tradeType: String
    lateinit var tradeSide: String
    private lateinit var priceSpot: String
    lateinit var priceUse: String
    lateinit var quantity: String
    private lateinit var orderId: String
    private lateinit var amount: String
    private lateinit var qtyUnit: String

    var confirmInterval: String = "5"
    var pdName: String = ""
    lateinit var currencyCode: String


    lateinit var countdownTimer: CountDownTimer
    var isRunning: Boolean = false
    var time_in_milli_seconds = 0L
    var state: String = "TradingDecisionFragment"
    var countClick = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_trading_decision, container, false)
        pdName = arguments?.getString("productName").toString()

        frame = v.findViewById(R.id.frag_trading_decision)
        lb1 = v.findViewById(R.id.row_trade_label1)
        vl1 = v.findViewById(R.id.row_trade_value1)
        lb2 = v.findViewById(R.id.row_trade_label2)
        vl2 = v.findViewById(R.id.row_trade_value2)
        lb3 = v.findViewById(R.id.row_trade_label3)
        vl3 = v.findViewById(R.id.row_trade_value3)
        lb4 = v.findViewById(R.id.row_trade_label4)
        vl4 = v.findViewById(R.id.row_trade_value4)
        un4 = v.findViewById(R.id.row_trade_unit4)
        lb5 = v.findViewById(R.id.row_trade_label5)
        vl5 = v.findViewById(R.id.row_trade_value5)
        un5 = v.findViewById(R.id.row_trade_unit5)
        lb6 = v.findViewById(R.id.row_trade_label6)
        vl6 = v.findViewById(R.id.row_trade_value6)
        un6 = v.findViewById(R.id.row_trade_unit6)
        lb7 = v.findViewById(R.id.row_trade_label7)
        vl7 = v.findViewById(R.id.row_trade_value7)
        un7 = v.findViewById(R.id.row_trade_unit7)
        info = v.findViewById(R.id.lb_trade_decision_warning)
        count = v.findViewById(R.id.lb_trade_decision_count)
        second = v.findViewById(R.id.lb_trade_decision_second)
        ok = v.findViewById(R.id.btn_confirm_trade_decision)

        val prefix = ": "
        val listPrefix = arrayOf(
            getString(R.string.usr_acc) + prefix,
            getString(R.string.usr_typ) + prefix,
            getString(R.string.usr_ord) + prefix,
            getString(R.string.usr_spo) + prefix,
            getString(R.string.usr_pri) + prefix,
            getString(R.string.usr_qty) + prefix,
            getString(R.string.usr_tot) + prefix
        )


        lb1.text = listPrefix[0]
        lb2.text = listPrefix[1]
        lb3.text = listPrefix[2]
        lb4.text = listPrefix[3]
        lb5.text = listPrefix[4]
        lb6.text = listPrefix[5]
        lb7.text = listPrefix[6]

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


        val infos = arrayOf(
            getString(R.string.do_lead) + " " + confirmInterval + " " + getString(R.string.do_trail),
            getString(R.string.do_sec),
        )

        pdCode = arguments?.getString("productCode").toString()
        tradeType = arguments?.getString("tradeType").toString()
        currencyCode = arguments?.getString("currencyCode").toString()

        Log.e("tradeType  : ",tradeType)

        if(tradeType ==""){

        }

        var type = ""
        when (tradeType) {
            "REALTIME" -> {
                type = getString(R.string.un_rt)
                frame.setBackgroundResource(R.color.bg_de)
            }
            "PLACE_ORDER" -> {
                type = getString(R.string.un_po)
                frame.setBackgroundResource(R.color.bg_po)
            }
            "STOP_ORDER" -> {
                type = getString(R.string.un_so)
                frame.setBackgroundResource(R.color.bg_so)
            }
        }

        tradeSide = arguments?.getString("tradeSide").toString()
        var side = ""
        when (tradeSide) {
            "S" -> {
                side = getString(R.string.sell)
                vl1.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell))
                vl2.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell))
                vl3.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell))
                vl4.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell))
                vl5.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell))
                vl6.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell))
                vl7.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_sell))

            }
            "B" -> {
                side = getString(R.string.buy)
                vl1.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy))
                vl2.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy))
                vl3.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy))
                vl4.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy))
                vl5.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy))
                vl6.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy))
                vl7.setTextColor(ContextCompat.getColor(requireContext(), R.color.pm_buy))
            }
        }

        priceSpot = arguments?.getString("priceSpot").toString()
        priceUse = arguments?.getString("price").toString()
        quantity = arguments?.getString("quantity").toString()
        orderId = arguments?.getString("orderId").toString()
        amount = arguments?.getString("amount").toString()
        confirmInterval = arguments?.getString("confirmInterval").toString()
        qtyUnit = arguments?.getString("qtyUnit").toString()


        vl1.text = PropertiesKotlin.account
        vl2.text = type
        vl3.text = side

        if (priceSpot != null) {
            vl4.text = priceSpot
        }
        if (priceUse != null) {
            vl5.text = priceUse
        }

        vl6.text = quantity
        vl7.text = amount

        if (PropertiesKotlin.currency == "USD") {
            un4.text = units[0]
            un5.text = units[0]
            //  un6.text = units[3]
            un7.text = units[1]

        } else {
            un4.text = units[0]
            un5.text = units[1]
            // un6.text = units[2]
            un7.text = units[1]
        }



        un6.text = qtyUnit
//        if(currencyCode == "USD"){
//
//            un4.text = units[0]
//            un5.text = units[0]
//            un6.text = units[3]
//            un7.text = units[1]
//        }else{


//        }


        info.text = infos[0]
        second.text = infos[1]


        ok.setOnClickListener {
            ManageData().hasInternetConnection().subscribe { hasInternet ->
                if (hasInternet) {
                    isPause = true
                    countdownTimer.cancel()
                    countClick++
                    if (countClick == 1) {
                        confirmTrade(orderId, pdCode, tradeSide)
                    }

                } else {
                    var dialogNetwork = ProgressDialog.networkDialog(requireContext())
                    dialogNetwork.show()
                }
            }

        }

        return v
    }

    private fun confirmTrade(orderId: String, productCode: String, side: String) {
        Log.e("confirmTrade :", "confirmTrade")
        context?.let { it ->
            ServiceApiSpot.confirmTrade(orderId, productCode, side, it) {
                Log.e("confirmTrade it : ", it.toString())
                var msgResponse = ""
                val builderResponse: android.app.AlertDialog.Builder =
                    android.app.AlertDialog.Builder(context)
                if (it != null) {
                    if (it.response == "SUCCESS") {
                        val act = context as AppCompatActivity
                        val tradingConfirm: Fragment = TradingConfirmFragment()
                        val bundle = Bundle()

                        bundle.putString("productCode", productCode)
                        bundle.putString("tradeType", tradeType)
                        bundle.putString("tradeSide", tradeSide)
                        bundle.putString("price", priceUse)
                        bundle.putString("quantity", quantity)
                        bundle.putString("priceSpot", priceSpot)
                        bundle.putString("amount", amount)
                        bundle.putString("productName", pdName)
                        bundle.putString("ivNumber", it.ivNumber)
                        bundle.putString("qtyUnit", qtyUnit)
                        tradingConfirm.arguments = bundle
                        val fm: FragmentManager = act.supportFragmentManager
                        fm.beginTransaction()
                            .replace(R.id.frag_home, tradingConfirm)
                            .addToBackStack(null)
                            .commit()
                        //     Toast.makeText(context, "SUCCESS", Toast.LENGTH_SHORT).show()

                    } else {

                        val builder: AlertDialog.Builder? = activity?.let {
                            AlertDialog.Builder(it)
                        }
                        msgResponse = it.message.toString()
                        builder?.setTitle(getString(R.string.failed))
                            ?.setMessage(msgResponse)
                            ?.setPositiveButton(getString(R.string.btn_ok),
                                DialogInterface.OnClickListener { dialog, id ->

                                    builder.create().dismiss()
                                    (activity as HomeActivity).onBackPressed()
                                })

                        builder?.create()?.show()


                        builderResponse.setMessage(it.message).setPositiveButton(
                            getString(R.string.btn_ok)
                        ) { dialog, which ->

                        }
                    }
                }


            }
        }
    }


    var isPause: Boolean = false
    private fun startTimer(time_in_seconds: Long) {
        countdownTimer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                val intent = Intent(context, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity?.startActivity(intent)

            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()

                if (isPause) {
                    countdownTimer.cancel()
                }


            }

        }
        countdownTimer.start()

//        isRunning = true
    }

    private fun updateTextUI() {
        val seconds = (time_in_milli_seconds / 1000) % 60

        count.text = "$seconds"
    }

    override fun onStart() {
        super.onStart()
        val countsT = confirmInterval.toInt() + 1
        time_in_milli_seconds = countsT.toString().toLong() * 1000L
        startTimer(time_in_milli_seconds)
    }


    override fun onStop() {
        super.onStop()
        countdownTimer.cancel()

    }

    override fun onPause() {
        super.onPause()
        countdownTimer.cancel()
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        ft.remove(this)
        ft.commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
    }


    companion object {
        fun newInstance(): TradingDecisionFragment = TradingDecisionFragment()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = pdName
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
        countClick = 0
    }


}
