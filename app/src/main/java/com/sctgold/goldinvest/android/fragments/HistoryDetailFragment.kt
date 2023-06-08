package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.goldinvest.android.util.ProgressDialog
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.sctgold.android.tradeplus.util.ManageData


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var lb1: TextView
    private lateinit var vl1: TextView
    private lateinit var lb2: TextView
    private lateinit var vl2: TextView
    private lateinit var lb3: TextView
    private lateinit var vl3: TextView
    private lateinit var lb4: TextView
    private lateinit var vl4: TextView
    private lateinit var lb5: TextView
    private lateinit var vl5: TextView
    private lateinit var lb6: TextView
    private lateinit var vl6: TextView
    private lateinit var un6: TextView
    private lateinit var lb7: TextView
    private lateinit var vl7: TextView
    private lateinit var un7: TextView
    private lateinit var lb8: TextView
    private lateinit var vl8: TextView
    private lateinit var un8: TextView
    private lateinit var lb9: TextView
    private lateinit var vl9: TextView
    private lateinit var lb10: TextView
    private lateinit var vl10: TextView
    private lateinit var lb11: TextView
    private lateinit var vl11: TextView
    private lateinit var lb12: TextView
    private lateinit var vl12: TextView
    var state: String = "HistoryDetailFragment"
    var tradeType: String = ""
    var orderDate: String = ""
    var productName: String = ""
    var quNumber: String = ""
    var orderNumber: String = ""
    var tradeSide: String = ""
    var quantity: String = ""
    var price: String = ""
    var total: String = ""
    var dueDate: String = ""
    var paymentStatus: String = ""
    var matchStatus: String = ""
    var productUnit: String = ""
    var fifoStatus: String = ""
    var currencyCode: String = ""

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
        val v = inflater.inflate(R.layout.fragment_history_detail, container, false)

        lb1 = v.findViewById(R.id.row_detail_label1)
        vl1 = v.findViewById(R.id.row_detail_value1)
        lb2 = v.findViewById(R.id.row_detail_label2)
        vl2 = v.findViewById(R.id.row_detail_value2)
        lb3 = v.findViewById(R.id.row_detail_label3)
        vl3 = v.findViewById(R.id.row_detail_value3)
        lb4 = v.findViewById(R.id.row_detail_label4)
        vl4 = v.findViewById(R.id.row_detail_value4)
        lb5 = v.findViewById(R.id.row_detail_label5)
        vl5 = v.findViewById(R.id.row_detail_value5)
        lb6 = v.findViewById(R.id.row_detail_label6)
        vl6 = v.findViewById(R.id.row_detail_value6)
        un6 = v.findViewById(R.id.row_detail_unit6)
        lb7 = v.findViewById(R.id.row_detail_label7)
        vl7 = v.findViewById(R.id.row_detail_value7)
        un7 = v.findViewById(R.id.row_detail_unit7)
        lb8 = v.findViewById(R.id.row_detail_label8)
        vl8 = v.findViewById(R.id.row_detail_value8)
        un8 = v.findViewById(R.id.row_detail_unit8)
        lb9 = v.findViewById(R.id.row_detail_label9)
        vl9 = v.findViewById(R.id.row_detail_value9)
        lb10 = v.findViewById(R.id.row_detail_label10)
        vl10 = v.findViewById(R.id.row_detail_value10)
        lb11 = v.findViewById(R.id.row_detail_label11)
        vl11 = v.findViewById(R.id.row_detail_value11)
        lb12 = v.findViewById(R.id.row_detail_label12)
        vl12 = v.findViewById(R.id.row_detail_value12)

        type = arguments?.getInt("type")!!
        orderId = arguments?.getString("orderId").toString()
        tradeType = arguments?.getString("tradeType").toString()
        orderDate = arguments?.getString("orderDate").toString()
        productName = arguments?.getString("productName").toString()
        //  var tradeType = arguments?.getString("tradeType")
        quNumber = arguments?.getString("ivNumber").toString()
        orderNumber = arguments?.getString("orderNumber").toString()
        tradeSide = arguments?.getString("tradeSide").toString()
        quantity = arguments?.getString("quantity").toString()
        price = arguments?.getString("price").toString()
        total = arguments?.getString("total").toString()
        dueDate = arguments?.getString("dueDate").toString()
        paymentStatus = arguments?.getString("paymentStatus").toString()
        matchStatus = arguments?.getString("matchStatus").toString()
        productUnit = arguments?.getString("productUnit").toString()
        fifoStatus = arguments?.getString("fifoStatus").toString()
        currencyCode = arguments?.getString("currencyCode").toString()

        if (type != null) {

            vl1.setTextColor(ContextCompat.getColor(context, type))
            vl2.setTextColor(ContextCompat.getColor(context, type))
            vl3.setTextColor(ContextCompat.getColor(context, type))
            vl4.setTextColor(ContextCompat.getColor(context, type))
            vl5.setTextColor(ContextCompat.getColor(context, type))
            vl6.setTextColor(ContextCompat.getColor(context, type))
            vl7.setTextColor(ContextCompat.getColor(context, type))
            vl8.setTextColor(ContextCompat.getColor(context, type))
            vl9.setTextColor(ContextCompat.getColor(context, type))
            vl10.setTextColor(ContextCompat.getColor(context, type))
            vl11.setTextColor(ContextCompat.getColor(context, type))
            vl12.setTextColor(ContextCompat.getColor(context, type))
        }


        val prefix = ": "
        val listPrefix = arrayOf(
            getString(R.string.usr_dat) + prefix,
            getString(R.string.usr_typ) + prefix,
            getString(R.string.usr_qu) + prefix,
            getString(R.string.usr_bs) + prefix,
            getString(R.string.usr_ord) + prefix,
            getString(R.string.usr_qty) + prefix,
            getString(R.string.usr_pri) + prefix,
            getString(R.string.usr_tot) + prefix,
            getString(R.string.usr_due) + prefix,
            getString(R.string.usr_sta) + prefix,
            getString(R.string.usr_fif) + prefix,
            getString(R.string.usr_pay) + prefix
        )

        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = productName


        vl1.text = orderDate

        when (tradeType) {
            context.resources.getString(R.string.un_rt) -> {
                vl2.text = context.resources.getString(R.string.un_rt)
            }
            context.resources.getString(R.string.un_po) -> {
                vl2.text = context.resources.getString(R.string.un_po)
            }
            else -> {
                vl2.text = context.resources.getString(R.string.un_so)
            }
        }


        vl3.text = quNumber
        vl3.textSize = resources.getDimension(R.dimen.lb_tag)
        vl4.text = orderNumber
        vl4.textSize = resources.getDimension(R.dimen.lb_tag)
        vl5.text = tradeSide + " " + productName
        vl6.text = quantity
        vl7.text = price
        vl8.text = total
        vl9.text = dueDate
        vl10.text = matchStatus
        vl11.text = fifoStatus
        vl12.text = paymentStatus

        lb1.text = listPrefix[0]
        lb2.text = listPrefix[1]
        lb3.text = listPrefix[2]
        lb4.text = listPrefix[3]
        lb5.text = listPrefix[4]
        lb6.text = listPrefix[5]
        lb7.text = listPrefix[6]
        lb8.text = listPrefix[7]
        lb9.text = listPrefix[8]
        lb10.text = listPrefix[9]
        lb11.text = listPrefix[10]
        lb12.text = listPrefix[11]


        var unitQty = ""
        when (productUnit) {
            "g" -> {
                unitQty = getString(R.string.un_gram)
            }
            "BG" -> {
                unitQty = getString(R.string.un_baht)
            }
            "Kg" -> {
                unitQty = getString(R.string.un_kilo)
            }
        }
        un6.text = unitQty
        if(currencyCode == "USD"){
            un7.text = getString(R.string.un_usd)
        }else{
            un7.text = getString(R.string.un_mb)
        }
        un8.text = getString(R.string.un_mb)
        return v
    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//
//
//    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var type: Int = 0
        lateinit var orderId: String
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val item = menu.findItem(R.id.to_delete)
        item.isVisible = tradeType !=  context.resources.getString(R.string.un_rt)


        if(item.isVisible){

            if(matchStatus=="MATCH" ){
                item.isVisible = false
            }

        }
        PropertiesKotlin.checkTradeType =              item.isVisible
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.to_delete -> {

                ManageData().hasInternetConnection().subscribe { hasInternet ->
                    if (hasInternet) {
                        val builder: AlertDialog.Builder? = activity?.let {
                            AlertDialog.Builder(it)
                        }

                        builder
                            ?.setTitle(getString(R.string.msg_confirm_delete_order))
                            ?.setPositiveButton(getString(R.string.btn_confirm),
                                DialogInterface.OnClickListener { dialog, id ->
                                    ServiceApiSpot.deleteHistory(orderId, context) {
                                        if (it != null) {
                                            val builderResponse: AlertDialog.Builder? = activity?.let {
                                                AlertDialog.Builder(it)
                                            }

                                            when (it.response) {
                                                "SUCCESS" -> {
                                                    builderResponse
                                                        ?.setTitle(context.resources.getString(R.string.title_dialog_success))
                                                        ?.setPositiveButton(getString(R.string.btn_close),
                                                            DialogInterface.OnClickListener { dialog, id ->
                                                                (activity as HomeActivity).onBackPressed()
                                                                builder.create().dismiss()
                                                            })

                                                }
                                                "RECORD_NOT_FOUND" -> {
                                                    builderResponse?.setTitle(it.response)?.setPositiveButton(
                                                        getString(R.string.btn_ok)
                                                    ) { dialog, which ->
                                                        builder.create().dismiss()
                                                    }

                                                }
                                                else -> {
                                                    builderResponse?.setTitle(it.response)?.setPositiveButton(
                                                        getString(R.string.btn_ok)
                                                    ) { dialog, which ->
                                                        builder.create().dismiss()
                                                    }

                                                }
                                            }
                                            builderResponse?.create()?.show()
                                        }
                                    }
                                })
                            ?.setNegativeButton(
                                getString(R.string.btn_close),
                                DialogInterface.OnClickListener { dialog, id ->
                                    builder.create().dismiss()
                                })

                        builder?.create()?.show()
                    } else {
                        var dialogNetwork = ProgressDialog.networkDialog(requireContext())
                        dialogNetwork.show()
                    }
                }


            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.thisdetail)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

}
