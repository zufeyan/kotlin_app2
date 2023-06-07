package com.sctgold.wgold.android.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.UiModeManager
import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.activities.HomeActivity
import com.sctgold.wgold.android.activities.IntentActivityUtils.GoldSpotFragment
import com.sctgold.wgold.android.activities.IntentActivityUtils.GoldSpotIntent
import com.sctgold.wgold.android.dto.*
import com.sctgold.wgold.android.listener.OnPriceFeedListener
import com.sctgold.wgold.android.listener.OnStateChangeListener
import com.sctgold.wgold.android.listener.OnSystemUpdateListener
import com.sctgold.wgold.android.receiver.MobilePricingReceiver
import com.sctgold.wgold.android.receiver.MobileSystemReceiver
import com.sctgold.wgold.android.rest.ServiceApiSpot
import com.sctgold.wgold.android.service.MobilePricingService
import com.sctgold.wgold.android.service.MobileSystemService
import com.sctgold.wgold.android.util.AppConstants
import com.sctgold.wgold.android.util.DecimalDigitsInputFilter
import com.sctgold.wgold.android.util.ProgressDialog
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.sctgold.android.tradeplus.util.ManageData
import kotlinx.android.synthetic.main.dialog_action_sheet.view.*
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList


class TradingFragment : Fragment(), OnPriceFeedListener, OnSystemUpdateListener {

    private var sideSell: Boolean = false
    private var sideBuy: Boolean = false

    private lateinit var context: AppCompatActivity
    private lateinit var frame: ScrollView

    //    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var selectTypes: RadioGroup
    private lateinit var real: RadioButton
    private lateinit var place: RadioButton
    private lateinit var stop: RadioButton
    private lateinit var tsell: TextView
    private lateinit var tbuy: TextView
    private lateinit var sell: Button
    private lateinit var buy: Button
    private lateinit var pcSell: TextView
    private lateinit var pcBuy: TextView
    private lateinit var pgSell: ProgressBar
    private lateinit var pgBuy: ProgressBar
    private lateinit var cdSell: TextView
    private lateinit var cdBuy: TextView
    private lateinit var maxSell: TextView
    private lateinit var maxBuy: TextView
    private lateinit var qtyLabel: TextView
    private lateinit var qtyBtn: TextView
    private lateinit var qtyUnit: TextView
    private lateinit var priceLabel: TextView
    private lateinit var priceEdit: EditText
    private lateinit var priceUnit: TextView
    private lateinit var cfBtn: Button
    private lateinit var titleQty: TextView

    private lateinit var qtyEdit: EditText
    private lateinit var child1: LinearLayout
    private lateinit var dialog: Dialog

    var quantity = arrayOf("")

    var pdCode: String? = ""
    var weightBuy: String? = ""
    var weightSell: String? = ""

    var tradeType: String? = ""
    var tradeSide: String? = ""
    var price: String = "0.00"
    var priceRealtime: String = "0.00"
    var pdName: String? = ""
    var weight: String? = ""
    var weightUnit: String? = ""
    var qtyUse: String = ""
    var enableDecimal: Boolean = false
    var decimalPlace: Int = 0
    var checkTrade: Boolean = true
    var select_theme = 0
    private lateinit var sharedPreferences: SharedPreferences

    private var productTask: WeakReference<GetProductAsyncTask>? = null

    //    private var createOrderTask: WeakReference<CreateOrderAsyncTask>? = null
    protected val pricingReceiver = MobilePricingReceiver()
    protected val systemReceiver = MobileSystemReceiver()
    private val stateChangeListener: OnStateChangeListener? = null
    var state: String = "TradingFragment"
    var beforeState: String = "TradingFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        var sharedPreferences: SharedPreferences? =
            context?.getSharedPreferences(
                getString(R.string.key_prefs_session_initialized),
                Context.MODE_PRIVATE
            )

        if (sharedPreferences != null) {
            select_theme = sharedPreferences.getInt("select_theme", 0)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_trading, container, false)
        tradeType = context.resources.getString(R.string.un_rt)

//        frame = v.findViewById(R.id.frag_trade)
        frame = v.findViewById(R.id.frag_trade)
//        refresh = v.findViewById(R.id.home_refresh)
        selectTypes = v.findViewById(R.id.select_trade_type)
        real = v.findViewById(R.id.select_real_time)
        real.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.white))
        place = v.findViewById(R.id.select_place_order)
        stop = v.findViewById(R.id.select_stop_order)
        tsell = v.findViewById(R.id.title_side_sell)
        tbuy = v.findViewById(R.id.title_side_buy)
        sell = v.findViewById(R.id.btn_side_sell)
        buy = v.findViewById(R.id.btn_side_buy)
        pcSell = v.findViewById(R.id.sell_percent)
        pcBuy = v.findViewById(R.id.buy_percent)
        pgSell = v.findViewById(R.id.sell_bar)
        pgBuy = v.findViewById(R.id.buy_bar)
        cdSell = v.findViewById(R.id.sell_credit)
        cdBuy = v.findViewById(R.id.buy_credit)
        maxSell = v.findViewById(R.id.sell_max)
        maxBuy = v.findViewById(R.id.buy_max)
        qtyLabel = v.findViewById(R.id.text_qty_label)
        qtyBtn = v.findViewById(R.id.btn_qty_select)
        qtyUnit = v.findViewById(R.id.text_qty_unit)
        priceLabel = v.findViewById(R.id.text_price_label)
        priceEdit = v.findViewById(R.id.edit_price_select)
        priceUnit = v.findViewById(R.id.text_price_unit)
        cfBtn = v.findViewById(R.id.btn_confirm_trade)
        titleQty = v.findViewById(R.id.title_select_amount)


        qtyEdit = v.findViewById(R.id.edit_qty)
//        qtyUnit2=v.findViewById(R.id.text_qty_unit2)
//        qtyLabel2=v.findViewById(R.id.text_qty_label2)


        child1 = v.findViewById(R.id.layout1)
//         child2 = v.findViewById(R.id.layout2)


        return v
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uiManager: UiModeManager =
            context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager


        val white = ContextCompat.getColor(context.applicationContext, R.color.white)
        val tint = ContextCompat.getColor(context.applicationContext, R.color.tint)
        val sellNormal = ContextCompat.getColor(context.applicationContext, R.color.m_red)
        val buyNormal = ContextCompat.getColor(context.applicationContext, R.color.m_green)
        frame.setBackgroundResource(R.color.bg_de)

        if (select_theme == 0) {
            if (uiManager.nightMode == 1) {
                real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
            } else {
                //system dark
                real.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
            }
        } else if (select_theme == 1) {
            real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))

        } else {
            real.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
        }

        selectTypes.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.select_real_time -> {

                    if (select_theme == 0) {
                        if (uiManager.nightMode == 1) {
                            real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))

                        } else {
                            //system dark
                            real.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                            place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        }
                    } else if (select_theme == 1) {
                        real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                    } else {
                        real.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                        place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                    }

                    frame.setBackgroundResource(R.color.bg_de)
                    setDefaultUI()
                    tradeType = context.resources.getString(R.string.un_rt)
                    quantity = arrayOf("")

                }
                R.id.select_place_order -> {
                    Log.e("checkedRadioButtonId : ", "select_place_order")

                    if (select_theme == 0) {
                        if (uiManager.nightMode == 1) {
                            real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        } else {
                            //system dark
                            real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            place.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                            stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        }
                    } else if (select_theme == 1) {
                        real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                    } else if (select_theme == 2) {
                        real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        place.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                        stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                    }
                    frame.setBackgroundResource(R.color.bg_po)
                    setDefaultUI()
                    tradeType = context.resources.getString(R.string.un_po)
                }
                R.id.select_stop_order -> {
                    Log.e("checkedRadioButtonId : ", "select_stop_order")


                    if (select_theme == 0) {
                        if (uiManager.nightMode == 1) {
                            real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        } else {
                            //system dark
                            real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                            stop.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                        }
                    } else if (select_theme == 1) {
                        real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        stop.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                    } else if (select_theme == 2) {
                        real.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        place.setTextColor(Color.parseColor(PropertiesKotlin.colorWhite))
                        stop.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                    }

                    frame.setBackgroundResource(R.color.bg_so)
                    setDefaultUI()
                    tradeType = context.resources.getString(R.string.un_so)

                }
            }
        }

//        val rdSell: Double = Random.nextDouble(20000.00, 99999.99)
//        val rdBuy: Double = Random.nextDouble(20000.00, 99999.99)
//
        sell.text = ""
        buy.text = ""

        sell.setOnClickListener {
//            if (checkNetwork) {
            selectedSell()
//            }
        }
        buy.setOnClickListener {
//            if (checkNetwork) {
            selectedBuy()
//            }
        }


        pcSell.setTextColor(sellNormal)
        pcBuy.setTextColor(buyNormal)

        cdSell.setTextColor(sellNormal)
        cdBuy.setTextColor(buyNormal)

        qtyLabel.text = getString(R.string.un_qty)
        qtyBtn.setOnClickListener {
//            if (checkNetwork) {
            selectedQTY()
//            }
        }

        qtyEdit.visibility = View.INVISIBLE
        priceLabel.text = getString(R.string.un_price)
        //priceEdit.text = Edita
//        priceUnit.text = getString(R.string.un_mb)

        if (PropertiesKotlin.currency == "USD") {
            priceUnit.text = getString(R.string.un_usd)
        } else {
            priceUnit.text = getString(R.string.un_mb)
        }
        priceEdit.visibility = View.INVISIBLE
        priceLabel.visibility = View.INVISIBLE
        priceUnit.visibility = View.INVISIBLE
        cfBtn.visibility = View.INVISIBLE

        qtyBtn.visibility = View.INVISIBLE
        qtyUnit.visibility = View.INVISIBLE
        qtyLabel.visibility = View.INVISIBLE
        titleQty.visibility = View.INVISIBLE


        priceEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (!s.toString().isNullOrEmpty()) {
                    cfBtn.visibility = View.VISIBLE
                } else {
                    cfBtn.visibility = View.INVISIBLE
                }
            }
        })


        qtyEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (weight.isNullOrEmpty()) {
                    if (decimalPlace == 0) {
                        qtyEdit.inputType =
                            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED

                    } else {
                        qtyEdit.filters =
                            arrayOf<InputFilter>(DecimalDigitsInputFilter(decimalPlace))
                    }
                }
                if (tradeType == context.resources.getString(R.string.un_rt)) {
                    if (!s.toString().isNullOrEmpty()) {
                        cfBtn.visibility = View.VISIBLE
                    } else {
                        cfBtn.visibility = View.INVISIBLE
                    }
                } else {
                    if (!s.toString().isNullOrEmpty()) {
                        priceEdit.visibility = View.VISIBLE
                        priceLabel.visibility = View.VISIBLE
                        priceUnit.visibility = View.VISIBLE
                    } else {
                        priceEdit.visibility = View.INVISIBLE
                        priceLabel.visibility = View.INVISIBLE
                        priceUnit.visibility = View.INVISIBLE
                    }

                }

            }
        })


        var msgResponse = ""

        cfBtn.setOnClickListener {


            ManageData().hasInternetConnection().subscribe { hasInternet ->
                if (hasInternet) {
                    when {
                        sideSell -> {
                            tradeSide = "S"
                            priceRealtime = sell.text.toString()
                        }
                        sideBuy -> {
                            tradeSide = "B"
                            priceRealtime = buy.text.toString()
                        }
                        else -> {
//                            msgResponse = context.resources.getString(R.string.title_dialog_success)
                            msgResponse = "Select Trade Side"

                            createMsgDialog(msgResponse)
                        }
                    }
                    if (!tradeSide.isNullOrEmpty()) {


                        var qtyCheck: Boolean

                        if (weight == "") {
                            if (qtyEdit.text.toString().isNullOrEmpty()) {
                                qtyCheck = false

                            } else {
                                qtyUse = qtyEdit.text.toString().trim()
                                qtyCheck = true
                            }
                        } else {

                            if (qtyBtn.text.toString() == getString(R.string.btn_select)) {
                                qtyCheck = false
                            } else {
                                qtyCheck = true
                                qtyUse = qtyBtn.text.toString()
                            }

                        }




                        if (qtyCheck) {

                            price = if (tradeType == context.resources.getString(R.string.un_rt)) {
                                priceRealtime
                            } else {
                                priceEdit.text.toString()
                            }



                            if (price != "") {

                                var priceSpot = ManageData().numberFormatToDouble(priceRealtime)
                                var priceFill = ManageData().numberFormatToDouble(price)

                                var checkProcess =
                                    checkProcessOrder(
                                        tradeType.toString(), tradeSide.toString(),
                                        priceFill, priceSpot
                                    )


                                if (checkProcess.isNullOrEmpty()) {
                                    newTrade()
//                                    startCreateOrderTask(qtyUse, price)
                                } else {
                                    //Toast.makeText(context, checkProcess, Toast.LENGTH_SHORT).show()

                                    msgResponse = checkProcess
                                    createMsgDialog(msgResponse)
                                }
                            } else {
//                                Toast.makeText(context, "Fill Price", Toast.LENGTH_SHORT).show()
                                msgResponse = "Fill Price"


                                createMsgDialog(msgResponse)
                            }
                        } else {

//                            Toast.makeText(context, "Select Quantity", Toast.LENGTH_SHORT).show()
                            msgResponse = "Select Quantity"
                            createMsgDialog(msgResponse)
                        }


                    }


                } else {
                    var dialogNetwork = ProgressDialog.networkDialog(requireContext())
                    dialogNetwork.show()
                }
            }


        }
        frame.setOnTouchListener { v, event ->
            if (event != null && event.action === MotionEvent.ACTION_MOVE) {

                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val isKeyboardUp = imm.isAcceptingText
                if (isKeyboardUp) {
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
            false
        }

        priceEdit.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN) {
                    when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            val imm =
                                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            val isKeyboardUp = imm.isAcceptingText
                            if (isKeyboardUp) {
                                imm.hideSoftInputFromWindow(v?.windowToken, 0)
                            }
                            return true
                        }
                        else -> {}
                    }
                }
                return false
            }
        })
    }

    fun createMsgDialog(msg: String) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder?.setTitle(getString(R.string.title_dialog_error))
            ?.setMessage(msg)
            ?.setPositiveButton(getString(R.string.btn_ok),
                DialogInterface.OnClickListener { dialog, id ->
                    builder.create().dismiss()
                })
            ?.setNegativeButton(getString(R.string.btn_close),
                DialogInterface.OnClickListener { dialog, id ->
                    builder.create().dismiss()
                })
        builder?.create()?.show()
    }

    private fun selectedQTY() {
        val view = LayoutInflater.from(this.context).inflate(R.layout.dialog_action_sheet, null)
        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)
        val alert = builder.show()
        val adapters = ArrayAdapter(this.context, R.layout.row_action_sheet, quantity)

        adapters.notifyDataSetChanged()
        builder.setCancelable(false)
        view.title_action_sheet.text = getString(R.string.un_qtys)
        view.action_sheet_list.adapter = adapters

        view.action_sheet_list.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                if (quantity != null) {

                    qtyBtn.text = quantity[i]
                    alert.dismiss()


                    priceEdit.setText("")
                    if (tradeType == context.resources.getString(R.string.un_rt)) {
                        priceEdit.visibility = View.INVISIBLE
                        priceLabel.visibility = View.INVISIBLE
                        priceUnit.visibility = View.INVISIBLE
                        cfBtn.visibility = View.VISIBLE
                    } else {
                        priceEdit.visibility = View.VISIBLE
                        priceLabel.visibility = View.VISIBLE
                        priceUnit.visibility = View.VISIBLE
                    }


                }

            }

        view.close_action_sheet.setOnClickListener {
            alert.dismiss()
        }
        builder.create()
    }

//    private fun callDialogQTY() {
//        val quantity = arrayOf("1", "2", "5", "10", "20", "30", "50", "100", "200", "300")
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_action_sheet, null)
//        val builder = AlertDialog.Builder(context)
//        builder.setTitle(getString(R.string.un_qtys))
//        builder.setItems(quantity) { dialog, i ->
//            qtyBtn.text = quantity[i]
//            dialog.dismiss()
//        }
//        builder.setView(dialogView)
//        builder.setCancelable(false)
////        val dialog = builder.create()
////        dialog.show()
//        val dialog = builder.show()
//
//        dialogView.close_action_sheet.setOnClickListener {
//            dialog.dismiss()
//        }
//
//    }

    private fun setDefaultUI() {
        sideSell = false
        sideBuy = false
        tsell.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        tbuy.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        sell.setBackgroundResource(R.drawable.bg_btn_sell)
        buy.setBackgroundResource(R.drawable.bg_btn_buy)
        titleQty.visibility = View.INVISIBLE
        priceEdit.visibility = View.INVISIBLE
        priceLabel.visibility = View.INVISIBLE
        priceUnit.visibility = View.INVISIBLE
        titleQty.visibility = View.INVISIBLE
        qtyBtn.visibility = View.INVISIBLE
        qtyUnit.visibility = View.INVISIBLE
        qtyLabel.visibility = View.INVISIBLE
        cfBtn.visibility = View.INVISIBLE
        quantity = arrayOf("")
        qtyBtn.text = getString(R.string.btn_select)
        price = ""
        qtyEdit.visibility = View.INVISIBLE
        qtyEdit.setText("")
        priceEdit.setText("")
    }


    private fun selectedSell() {
        sideSell = true
        setSideUI(sideSell)
        qtyLabel.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        priceLabel.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))

        priceEdit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        priceUnit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        qtyBtn.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        qtyEdit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        qtyUnit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        val gd = GradientDrawable()

        gd.setColor(-0x1) // Changes this drawbale to use a single color instead of a gradient
        gd.cornerRadius = 10f
        gd.setStroke(1, ContextCompat.getColor(context.applicationContext, R.color.m_red))
        qtyBtn.setBackgroundDrawable(gd)


        tsell.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.d_red))
        tbuy.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        sell.setBackgroundResource(R.drawable.bg_btn_sell_active)
        buy.setBackgroundResource(R.drawable.bg_btn_buy)

        var result = weightSell?.split(",")?.map { it.trim() }
        val myArrayList = ArrayList<String>()
        result?.forEach {
            myArrayList.add("$it")
        }

        quantity = myArrayList.toTypedArray()
        titleQty.visibility = View.VISIBLE
        qtyUnit.visibility = View.VISIBLE
        qtyLabel.visibility = View.VISIBLE
        cfBtn.visibility = View.INVISIBLE
        priceEdit.visibility = View.INVISIBLE

        priceLabel.visibility = View.INVISIBLE
        priceUnit.visibility = View.INVISIBLE
        qtyBtn.text = getString(R.string.btn_select)
        qtyEdit.setText("")
        priceEdit.setText("")
        if (weight == "") {
            qtyEdit.visibility = View.VISIBLE
            qtyBtn.visibility = View.INVISIBLE
            child1.removeView(qtyBtn)
        } else {
            child1.removeView(qtyEdit)
            qtyBtn.visibility = View.VISIBLE
            qtyEdit.visibility = View.INVISIBLE
        }
    }

    private fun selectedBuy() {
        sideBuy = true

        setSideUI(sideBuy)

        priceLabel.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        priceEdit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        priceUnit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        qtyEdit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        qtyBtn.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
//        qtyBtn.setbo
        qtyUnit.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        qtyLabel.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_green))
        val gd = GradientDrawable()

        gd.setColor(-0x1) // Changes this drawbale to use a single color instead of a gradient
        gd.cornerRadius = 10f
        gd.setStroke(1, ContextCompat.getColor(context.applicationContext, R.color.m_green))
        qtyBtn.setBackgroundDrawable(gd)

        tsell.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.m_red))
        tbuy.setTextColor(ContextCompat.getColor(context.applicationContext, R.color.d_green))
        sell.setBackgroundResource(R.drawable.bg_btn_sell)
        buy.setBackgroundResource(R.drawable.bg_btn_buy_active)

        var result = weightBuy?.split(",")?.map { it.trim() }
        val myArrayList = ArrayList<String>()
        result?.forEach {
            myArrayList.add("$it")
        }

        quantity = myArrayList.toTypedArray()
        titleQty.visibility = View.VISIBLE

        qtyUnit.visibility = View.VISIBLE
        qtyLabel.visibility = View.VISIBLE
        cfBtn.visibility = View.INVISIBLE
        priceEdit.visibility = View.INVISIBLE
        priceLabel.visibility = View.INVISIBLE
        priceUnit.visibility = View.INVISIBLE
        qtyBtn.text = getString(R.string.btn_select)
        qtyEdit.setText("")
        priceEdit.setText("")

        if (weight == "") {
            qtyEdit.visibility = View.VISIBLE
            qtyBtn.visibility = View.INVISIBLE
            child1.removeView(qtyBtn)
        } else {
            child1.removeView(qtyEdit)
            qtyBtn.visibility = View.VISIBLE
            qtyEdit.visibility = View.INVISIBLE
        }
    }

    private fun setSideUI(side: Boolean): Boolean {
        return when (side) {
            sideSell -> {
                sideBuy = false
                side
            }
            sideBuy -> {
                sideSell = false
                side
            }
            else -> {
                setDefaultUI()
                side
            }
        }
    }

    companion object {
        fun newInstance(): TradingFragment = TradingFragment()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }


    private fun checkProcessOrder(
        processType: String,
        processSide: String,
        placeOrder: Double,
        compareOrder: Double
    ): String {
        var returnType: String = ""
        if (processSide == "S") {
            if (processType == context.resources.getString(R.string.un_so)) {
                returnType = if (placeOrder >= compareOrder) {
                    context.resources.getString(R.string.msg_price_less_than_realprice)
//                    "Price must less than Realtimes Price \n"
                } else {
                    ""
                }
            } else if (processType == context.resources.getString(R.string.un_po)) {
                returnType = if (placeOrder <= compareOrder) {
                    context.resources.getString(R.string.msg_price_grater_than_realprice)
//                    "Price must grater than Realtimes Price \n"
                } else {
                    ""
                }
            }
        } else {//"B"
            if (processType == context.resources.getString(R.string.un_so)) {
                returnType = if (placeOrder <= compareOrder) {
                    context.resources.getString(R.string.msg_price_grater_than_realprice)
//                    "Price must grater than Realtimes Price \n"
                } else {
                    ""
                }
            } else if (processType == context.resources.getString(R.string.un_po)) {
                returnType = if (placeOrder >= compareOrder) {
                    context.resources.getString(R.string.msg_price_less_than_realprice)
//                    "Price must less than Realtimes Price \n"
                } else {
                    ""
                }
            }
        }
        return returnType
    }

    fun updateCredit(result: ActionResult, creditLimit: CreditLimitDTO) {
        if (!result.isNewIntent()) {
            if (result.getMessageId() == R.string.msg_dialog_noconnectivity_found || result.getMessageId() == R.string.msg_nodata_avai) {
                // StandardDialog.showMessageDialog(goldSpotMainActivity, R.string.title_dialog_error, result.getMessageId());
            } else if (result.isReloadRequired()) {
                if (creditLimit != null) {
                    var detail: CreditLimitDetailDTO? =
                        creditLimit.mobileCreditLimit

                    if (detail != null) {
                        enableDecimal = detail.enableDecimal
                        decimalPlace = detail.decimalPlace

                        weight = detail.weight
                        weightUnit = detail.weightUnit
                    }


                    when (weightUnit) {
                        "g" -> {
                            qtyUnit.text = context.resources.getString(R.string.un_gram)
                        }
                        "BG" -> {
                            qtyUnit.text = context.resources.getString(R.string.un_baht)
                        }
                        "Kg" -> {
                            qtyUnit.text = context.resources.getString(R.string.un_kilo)
                        }
                    }

                    if (detail != null) {
                        if (!detail.weightSell.isNullOrEmpty()) {
                            weightSell = detail.weightSell
                        }
                        if (!detail.weightBuy.isNullOrEmpty()) {
                            weightBuy = detail.weightBuy

                        }

                        if (!detail.weight.isNullOrEmpty()) {
                            var result = detail.weight!!.split(",").map { it.trim() }
                            val myArrayList = ArrayList<String>()
                            result.forEach {
                                myArrayList.add("$it")
                            }

                            quantity = myArrayList.toTypedArray()
                        }


                        pcSell.text = detail.percentSell.toString() + "%"
                        pcBuy.text = detail.percentBuy.toString() + "%"

                        pgSell.progress = detail.percentSell.toInt()
                        pgBuy.progress = detail.percentBuy.toInt()

                        cdBuy.text = detail.pointBuy
                        cdSell.text = detail.pointSell

                        maxSell.text = "/" + detail.orgPointSell
                        maxBuy.text = "/" + detail.orgPointBuy

                        if (detail.canBuy) {
                            buy.isEnabled =
                                !(detail.pointBuy == "0.00" || detail.pointBuy == "0" || detail.percentBuy.equals(
                                    0.00
                                ))
                        } else {
                            buy.isEnabled = false
                        }
                        if (detail.canSell) {
                            sell.isEnabled =
                                !(detail.pointSell == "0.00" || detail.pointSell == "0" || detail.percentSell.equals(
                                    0.00
                                ))
                        } else {
                            sell.isEnabled = false
                        }

                    }


                } else {
                    sell.isEnabled = false
                    buy.isEnabled = false
                }
            }


        } else {
            if (result.getMessageId() > -1) {
                //     StandardDialog.showMessageDialog(this.getActivity(), R.string.title_dialog_error, result.getMessageId(), result);
            }
        }
    }


    fun startCreateOrderTask(quantity: String, price: String) {


//        val asyncTask = CreateOrderAsyncTask(this)
//        createOrderTask = WeakReference<Any>(asyncTask)
//        asyncTask.execute(mobileTrade, unitTrade, tradeType)
    }

    private fun newTrade() {

        when (tradeType) {
            context.resources.getString(R.string.un_rt) -> {
                tradeType = "REALTIME"
            }
            context.resources.getString(R.string.un_po) -> {
                tradeType = "PLACE_ORDER"
            }
            context.resources.getString(R.string.un_so) -> {
                tradeType = "STOP_ORDER"
            }
        }
        ServiceApiSpot.newTrade(
            pdCode.toString(),
            tradeType.toString(),
            tradeSide.toString(),
            price,
            qtyUse,
            context
        ) {

            if (it != null) {

                if (it.response == "SUCCESS") {
                    val act = view?.context as AppCompatActivity
                    val tradingDecisionFragment: Fragment = TradingDecisionFragment()
                    val bundle = Bundle()
                    bundle.putString("orderId", it.mobileTrade.orderId)
                    bundle.putString("productCode", it.mobileTrade.productCode)
                    bundle.putString("tradeType", it.mobileTrade.tradeType)
                    bundle.putString("tradeSide", it.mobileTrade.tradeSide)
                    bundle.putString("price", it.mobileTrade.price)
                    bundle.putString("quantity", it.mobileTrade.quantity)
                    bundle.putString("priceSpot", it.mobileTrade.spotPrice)
                    bundle.putString("amount", it.mobileTrade.amount)
                    bundle.putString(
                        "confirmInterval",
                        it.mobileTrade.confirmInterval
                    )
                    bundle.putString("productName", pdName)
                    bundle.putString("currencyCode", it.mobileTrade.currencyCode)
                    bundle.putString("qtyUnit", qtyUnit.text.toString())
                    tradingDecisionFragment.arguments = bundle
                    val fm: FragmentManager = act.supportFragmentManager
                    fm.beginTransaction()
                        .replace(R.id.frag_home, tradingDecisionFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    it.message?.let { it1 -> createMsgDialog(it1) }
                }


            }
        }
    }


//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = pdName
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
//    }


    protected fun registerPricingReceiver() {
        //   LogUtil.debug(TAG, "Register Pricing Receiver Service")
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            pricingReceiver, IntentFilter(MobilePricingService.FEED_UPDATE_NOTIFICATION)
        )
    }

    protected fun unRegisterPricingReceiver() {
        // LogUtil.debug(TAG, "Unregister Pricing Receiver Service")
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(pricingReceiver)
    }

    protected fun registerSystemReceiver() {
        //  LogUtil.debug(TAG, "Register System Receiver Service")
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            systemReceiver, IntentFilter(MobileSystemService.SYSTEM_NOTIFICATION)
        )
    }

    protected fun unRegisterSystemReceiver() {
        //   LogUtil.debug(TAG, "Unregister System Receiver Service")
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(systemReceiver)
    }

    @SuppressLint("CheckResult")
    override fun onResume() {
        super.onResume()
        pdName = PropertiesKotlin.pdName
        pdCode = PropertiesKotlin.pdCode
        PropertiesKotlin.state = state
        PropertiesKotlin.beforeState = beforeState
        sell.text = PropertiesKotlin.spotPriceSell
        buy.text = PropertiesKotlin.spotPriceBuy
        pdCode?.let { startGetProductTask(it) }

        MobilePricingReceiver.onPriceFeedListener = this
        MobileSystemReceiver.onSystemUpdateListener = this
        registerPricingReceiver()
        registerSystemReceiver()

        setDefaultUI()
        real.performClick()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = pdName
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        onSystemUpdate(PropertiesKotlin.systemInfo)
    }

    override fun onSystemUpdate(systemDTO: SystemDTO?) {
//        Log.e("onSystemUpdate : ", systemDTO.toString())
        if (systemDTO != null) {
            if (!systemDTO.normalTradeEnable || systemDTO.tradeFreezing) {
                (context as HomeActivity).onBackPressed()
            } else {


                if (systemDTO.placeOrderTradeEnable) {
                     real.width =
                        resources.getDimension(R.dimen.real_time_width_when_pace_holder_enable)
                            .toInt()

                    place.alpha = AppConstants.ALPHA_ENABLE
                    place.isEnabled = true
                    place.visibility = View.VISIBLE

                    stop.alpha = AppConstants.ALPHA_ENABLE
                    stop.isEnabled = true
                    stop.visibility = View.VISIBLE
                } else {
                    real.width =
                        resources.getDimension(R.dimen.real_time_width_when_pace_holder_disable)
                            .toInt()
                    place.alpha = AppConstants.ALPHA_DISABLE
                    place.isEnabled = false
                    place.visibility = View.GONE

                    stop.alpha = AppConstants.ALPHA_DISABLE
                    stop.isEnabled = false
                    stop.visibility = View.GONE
//
                    real.performClick()

//                    if(checkTrade){
//                        setDefaultUI()
//                    }
////
//
//                    checkTrade = false
                }
                // setDefaultUI()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        unRegisterPricingReceiver()
        unRegisterSystemReceiver()
    }


    private fun startGetProductTask(productCode: String) {
        if (!(this.productTask != null && this.productTask!!.get() != null &&
                    !this.productTask!!.get()?.status?.equals(AsyncTask.Status.FINISHED)!!)
        ) {
            val asyncTask = GetProductAsyncTask(this)
            this.productTask = WeakReference(asyncTask)
            asyncTask.execute(productCode)
        }
    }

    private class GetProductAsyncTask(orderFragment: TradingFragment) :
        AsyncTask<String?, Void?, ActionResult>() {
        private val myWeakContext: WeakReference<TradingFragment>?
        private var progressBar: ProgressDialog? = null
        private var mobileCreditLimit: CreditLimitDTO? = null
        override fun onPreExecute() {
            // progressBar.show()
        }


        override fun doInBackground(vararg params: String?): ActionResult {
            val actionResult = ActionResult()
            if (params != null && params.isNotEmpty()) {
                val productCode = params[0]

//                if (myWeakContext?.get() != null && AppUtil.isConnected(
//                        myWeakContext.get()!!.activity
//                    )
//                ) {
                if (myWeakContext?.get() != null) {
                    var retry = 0
                    while (retry++ < AppConstants.CONNECTION_maxRetry) {
                        try {
                            myWeakContext.get()!!.pdCode?.let { it ->
                                ServiceApiSpot.getCreditLimitByProductCode(
                                    it,
                                    myWeakContext.get()!!.context
                                ) { it ->
                                    if (it != null) {
                                        when (it.response) {

                                            "SUCCESS" -> {
                                                actionResult.setNewIntent(false)
                                                actionResult.setReloadRequired(true)
                                                mobileCreditLimit = it
                                                mobileCreditLimit?.let {
                                                    myWeakContext.get()!!
                                                        .updateCredit(actionResult, it)
                                                }
                                            }
                                            "INVALID_CREDENTIAL" -> {
                                                actionResult.setNewIntent(java.lang.Boolean.TRUE)
                                                actionResult.setNextIntent(GoldSpotIntent.LOGIN)
                                                actionResult.setMessageId(R.string.msg_main_INVALID_CREDENTIAL)
                                                actionResult.setNextFragment(GoldSpotFragment.LOGIN_FRAGMENT)
                                                //    LogUtil.debug(TAG, "mobile creditlimit: {}", mobileCreditLimit)
                                            }
                                            else -> {
                                                actionResult.setNewIntent(java.lang.Boolean.FALSE)
                                                actionResult.setMessageId(R.string.msg_nodata_avai)
                                            }
                                        }
                                    } else {
                                        actionResult.setNewIntent(false)
                                        actionResult.setMessageId(R.string.msg_nodata_avai)
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            if (retry == AppConstants.CONNECTION_maxRetry) {
                                actionResult.setNewIntent(false)
                                actionResult.setMessageId(R.string.msg_nodata_avai)
                            }
                        }
                    }
                } else {
                    actionResult.setNewIntent(false)
                    actionResult.setMessageId(R.string.msg_dialog_noconnectivity_found)
                }
            }
            return actionResult
        }

        override fun onPostExecute(actionResult: ActionResult) {
            super.onPostExecute(actionResult)
            if (myWeakContext?.get() != null) {
                mobileCreditLimit?.let { myWeakContext.get()!!.updateCredit(actionResult, it) }
            }
            //   progressBar.dismiss()
        }

        init {
            myWeakContext = WeakReference(orderFragment)
            // progressBar = StandardDialog.createProgressDialog(orderFragment.getActivity())
        }

    }

    override fun onPricePush(productPrices: CopyOnWriteArrayList<BackupPriceProductDTO>) {
//        Log.e("productPrices  onPricePush: ",productPrices.toString())
        var spotPriceData =
            productPrices.find { actor -> pdCode.equals(actor.productCode) }
        if (spotPriceData != null) {
            var priceSell =
                spotPriceData.sellPriceNew?.let { ManageData().numberFormatToDouble(it) }
            var priceBuy = spotPriceData.buyPriceNew?.let { ManageData().numberFormatToDouble(it) }
            if (spotPriceData.statusFreeze) {
                stateChangeListener?.stateChange(R.id.backButton)
            }

            activity?.runOnUiThread(Runnable {
                sell.text = priceSell?.let { ManageData().numberFormatToDecimal(it) }
                buy.text = priceBuy?.let { ManageData().numberFormatToDecimal(it) }
            })

        }


    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
        val item3 = menu.findItem(R.id.to_checked)
        item3.isVisible = false
        val item4 = menu.findItem(R.id.to_price_alert)
        item4.isVisible = true
        val item5 = menu.findItem(R.id.to_email_open)
        item5.isVisible = false
        val item6 = menu.findItem(R.id.to_delete)
        item6.isVisible = false
        val item7 = menu.findItem(R.id.to_www)
        item7.isVisible = false
    }


}
