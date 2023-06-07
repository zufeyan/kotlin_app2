package com.sctgold.wgold.android.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.SwipeToDelete
import com.sctgold.wgold.android.activities.HomeActivity
import com.sctgold.wgold.android.fcm.NotifyMessagingService
import com.sctgold.wgold.android.listener.OnPriceAlertListener
import com.sctgold.wgold.android.receiver.PriceAlertReceiver
import com.sctgold.wgold.android.rest.MobileFrontService
import com.sctgold.wgold.android.rest.ServiceApiSpot
import com.sctgold.wgold.android.dto.ActionResult
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.sctgold.wgold.android.dto.MyProductDTO
import com.sctgold.wgold.android.dto.PriceAlertsDTO
import com.sctgold.wgold.android.dto.ResponseDTO
import com.google.android.material.tabs.TabLayout
import com.sctgold.android.tradeplus.util.ManageData
import kotlinx.android.synthetic.main.dialog_action_sheet.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER/
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PriceAlertFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PriceAlertFragment : Fragment(), OnPriceAlertListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var orderTypes: RadioGroup
    private lateinit var sellSide: RadioButton
    private lateinit var buySide: RadioButton
    private lateinit var btnSelectProduct: Button
    private lateinit var btnConfirm: Button
    private lateinit var priceList: RecyclerView
    private lateinit var price: EditText

    private var products = arrayOf("")
    private lateinit var productCode: MyProductDTO
    var sides = ""

    var mobileMyProductList: ArrayList<MyProductDTO> = ArrayList()
    var state: String = "PriceAlertFragment"
    private val priceAlertReceiver: PriceAlertReceiver = PriceAlertReceiver()

    //    private val asyncDeletePriceTask: WeakReference<DeletePriceAlertTask>? = null
    private var asyncGetPriceAlertListTask: WeakReference<GetPriceAlertListTask>? = null
    private var asyncAddPriceTask: WeakReference<AddPriceAlertTask>? = null

    private val mobilePriceAlertList: List<PriceAlertsDTO> = ArrayList<PriceAlertsDTO>()
    val item: ArrayList<Any> = ArrayList()

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
        val v = inflater.inflate(R.layout.fragment_price_alert, container, false)

        orderTypes = v.findViewById(R.id.price_alert_sl_order)
        sellSide = v.findViewById(R.id.price_alert_sl_sell)
        buySide = v.findViewById(R.id.price_alert_sl_buy)
        btnSelectProduct = v.findViewById(R.id.price_alert_btn_sl_product)
        btnConfirm = v.findViewById(R.id.price_alert_confirm)
        priceList = v.findViewById(R.id.price_alert_lists)
        price = v.findViewById(R.id.price_alert_add_price)
//        getProduct()
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val white = ContextCompat.getColor(context.applicationContext, R.color.white)
        val sell = ContextCompat.getColor(context.applicationContext, R.color.pm_sell)
        val buy = ContextCompat.getColor(context.applicationContext, R.color.pm_buy)
        val sideSell =
            ContextCompat.getDrawable(context.applicationContext, R.drawable.bg_btn_primary_sell)
        val sideBuy =
            ContextCompat.getDrawable(context.applicationContext, R.drawable.bg_btn_primary_buy)
        val colorDis = ContextCompat.getColor(context.applicationContext, R.color.s999)
        val colorFirm = ContextCompat.getColor(context.applicationContext, R.color.text)


        //   priceAlertsList()

        sides = context.resources.getString(R.string.sell)

        orderTypes.setOnCheckedChangeListener { group, _ ->
            when (group.checkedRadioButtonId) {
                R.id.price_alert_sl_sell -> {
                    sellSide.setTextColor(white)
                    buySide.setTextColor(buy)
                    btnSelectProduct.setTextColor(sell)
                    btnSelectProduct.background = sideSell
                    sides = context.resources.getString(R.string.SELL)
                }
                R.id.price_alert_sl_buy -> {
                    sellSide.setTextColor(sell)
                    buySide.setTextColor(white)
                    btnSelectProduct.setTextColor(buy)
                    btnSelectProduct.background = sideBuy
                    sides = context.resources.getString(R.string.BUY)
                }
            }
        }
        price.isEnabled = false
        btnConfirm.isEnabled = false
        btnConfirm.setTextColor(colorDis)

        btnSelectProduct.setTextColor(sell)
        btnSelectProduct.background = sideSell
        btnSelectProduct.setOnClickListener {
            selectedProduct()
        }


        price.addTextChangedListener(object : TextWatcher {
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
                btnConfirm.isEnabled = !s.toString().isNullOrEmpty()


                if (!s.toString().isNullOrEmpty()) {

                    if (PropertiesKotlin.checkMode) {
                        btnConfirm.setTextColor(
                            ContextCompat.getColor(
                                context.applicationContext,
                                R.color.white
                            )
                        )
                    } else {
                        btnConfirm.setTextColor(colorFirm)
                    }


                } else {
                    btnConfirm.setTextColor(colorDis)
                }

            }
        })


        btnConfirm.setOnClickListener {
            when {
                btnSelectProduct.text == getString(R.string.btn_select) -> {
                    Toast.makeText(context, "Please Product Select!!!", Toast.LENGTH_SHORT).show()
                }
                price.text.toString() == "" -> {
                    Toast.makeText(context, "Please Fill Price!!!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    //  newPriceAlert()

                    startAddPriceAlertTask()

                }
            }
        }

        priceList.apply {
            priceList.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            priceList.itemAnimator = DefaultItemAnimator()
            adapter = PriceAlertAdapter(addPriceAlert(), context)
        }


        val swipeHandler = object : SwipeToDelete(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val adapter = recyclerView.adapter as SimpleAdapter
//                adapter.removeAt(viewHolder.adapterPosition)
                (priceList.adapter as PriceAlertAdapter).apply {
                    removeAt(viewHolder.adapterPosition)
                }
            }
        }
//        val swipeHandler = object : SwipeToDelete(context) {
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                (priceList.adapter as PriceAlertAdapter).apply {
//
//                    Log.e("onSwiped : ","55555555555555555555")
//               //     removeAt(viewHolder.adapterPosition)
//
//                }
//            }
//        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(priceList)
    }


    private fun selectedProduct() {
        val view = LayoutInflater.from(this.context).inflate(R.layout.dialog_action_sheet, null)
        val builder = AlertDialog.Builder(this.context)
        builder.setView(view)
        val alert = builder.show()
        val adapters = ArrayAdapter(this.context, R.layout.row_action_sheet, products)
        adapters.notifyDataSetChanged()
        builder.setCancelable(false)
        view.title_action_sheet.text = context.resources.getString(R.string.ws_product)
        view.action_sheet_list.adapter = adapters
        view.action_sheet_list.onItemClickListener =
            AdapterView.OnItemClickListener { _, view, i, l ->
                btnSelectProduct.text = products[i]
                productCode =
                    mobileMyProductList.find { actor -> btnSelectProduct.text.toString() == actor.name }!!
                alert.dismiss()
                price.isEnabled = true
            }
        view.close_action_sheet.setOnClickListener {
            alert.dismiss()
        }
        builder.create()
    }

    data class DataPriceAlert(
        var symbols: String,
        var prices: String,
        var sides: String,
        var products: String,
        var id: String,
    )

    private fun addPriceAlert(): ArrayList<Any> {

//        item.add(
//            DataPriceAlert(
//                "฿", price.text.toString(), type, btnSelectProduct.text.toString()
//            )
//        )
        return item
    }

    private class PriceAlertAdapter(
        private val lists: MutableList<Any>, val context: Context
    ) : RecyclerView.Adapter<PriceAlertAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(data: DataPriceAlert) {

                itemView.apply {
                    val v: View = this
                    val sell = ContextCompat.getColor(v.context, R.color.pm_sell)
                    val buy = ContextCompat.getColor(v.context, R.color.pm_buy)

                    val wgPrice = v.findViewById<TextView>(R.id.price_alert_text_price)
                    val wgSide = v.findViewById<TextView>(R.id.price_alert_text_side)
                    val wgProduct = v.findViewById<TextView>(R.id.price_alert_text_product)

                    val getPrices = data.symbols + " " + data.prices
                    val getSides = " " + data.sides + " "

                    wgPrice.text = getPrices
                    wgSide.text = getSides.uppercase(Locale.ROOT)

                    if (data.sides == context.resources.getString(R.string.sell)) wgSide.setTextColor(
                        sell
                    ) else wgSide.setTextColor(
                        buy
                    )
                    wgProduct.text = data.products
                }
            }
        }

        fun itemList(data: DataPriceAlert) {
            lists.add(data)
            notifyItemInserted(lists.size)
        }

        fun addItem(data: DataPriceAlert) {
            lists.add(0, data)
            notifyItemInserted(lists.size)
        }

        fun removeAllItems(listItems: List<DataPriceAlert>) {
            lists.removeAll(listItems)
            notifyDataSetChanged()
        }

        fun removeAt(position: Int) {
            var dataPrice: DataPriceAlert = lists[position] as DataPriceAlert
            ServiceApiSpot.deletePriceAlert(dataPrice.id, context) {
                if (it != null) {
                    if (it.response == "SUCCESS") {
                        if(lists.size > 0){
                            lists.removeAt(position)
                        }

                        notifyItemRemoved(position)
                    }
                    //   PriceAlertFragment().createMessageDialog(it,context)
                }
            }


        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_price_alert, parent, false)
            )
        }

        override fun getItemCount() = lists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val cells: DataPriceAlert = lists[position] as DataPriceAlert
            holder.bind(
                DataPriceAlert(
                    cells.symbols, cells.prices, cells.sides, cells.products, cells.id
                )
            )
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
        item4.isVisible = false
        val item5 = menu.findItem(R.id.to_email_open)
        item5.isVisible = false
        val item6 = menu.findItem(R.id.to_delete)
        item6.isVisible = false
        val item7 = menu.findItem(R.id.to_www)
        item7.isVisible = false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PriceAlertFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PriceAlertFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }

            }

    }

    private fun getProduct() {
        ServiceApiSpot.getMyProduct(context) {
            if (it != null) {
                if (it.mobileMyProductList != null) {
                    mobileMyProductList = it.mobileMyProductList
                    val myArrayList = ArrayList<String>()
                    for (pd in it.mobileMyProductList) {
                        myArrayList.add("${pd.name}")

                    }
                    products = myArrayList.toTypedArray()
                }

            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    fun createMessageDialog(response: ResponseDTO, context: Context) {
        val builderResponse: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        var msgResponse = ""
        if (response.response == "SUCCESS") {

            msgResponse = context.resources.getString(R.string.title_dialog_success)
            builderResponse
                ?.setTitle(msgResponse)
                ?.setPositiveButton(context.resources.getString(R.string.btn_close),
                    DialogInterface.OnClickListener { dialog, id ->
                        priceList.adapter?.notifyDataSetChanged()
                        builderResponse.create().dismiss()
                    })


        } else {
            msgResponse = response.message.toString()
            builderResponse?.setTitle(msgResponse)?.setPositiveButton(
                context.resources.getString(R.string.btn_ok)
            ) { dialog, which ->
                builderResponse.create().dismiss()
            }

        }

        builderResponse?.create()?.show()
    }

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId =
                "all_notifications" // You should create a String resource for this instead of storing in a variable
            val mChannel = NotificationChannel(
                channelId,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "This is default channel used for all other notifications"

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    protected fun registerReceiver() {
        //  LogUtil.debug(PriceAlertFragment.TAG, "Register Price Alert Receiver")
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            priceAlertReceiver, IntentFilter(NotifyMessagingService().PRICE_ALERT)
        )
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = context.resources.getString(R.string.tpric)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        getProduct()
        PriceAlertReceiver.onPriceAlertListener = this
        val adapter = priceList.adapter as PriceAlertAdapter

        startGetPriceListTask()
        registerReceiver()
        PropertiesKotlin.state = state
        sides = context.resources.getString(R.string.SELL)
    }

    override fun onPause() {
        super.onPause()
        state = ""
    }

    //    private fun removeItem(position: Int) {
//        val newPosition: Int = holder.getAdapterPosition()
//        model.remove(newPosition)
//        notifyItemRemoved(newPosition)
//        notifyItemRangeChanged(newPosition, model.size())
//    }
    fun onGetPriceListComplete(
        actionResult: ActionResult,
        mobilePriceAlert: PriceAlertsDTO
    ) {
        if (mobilePriceAlert != null) {

            val adapter = priceList.adapter as PriceAlertAdapter
            var itemD: ArrayList<DataPriceAlert> = ArrayList()


            item.clear()
            itemD.clear()
            priceList.adapter?.notifyDataSetChanged()
            for (priceAlert in mobilePriceAlert.mobilePriceAlertList) {
                var aSide = ""
                aSide = if (priceAlert.type == "SE") {
                    context.resources.getString(R.string.sell)
                } else {
                    context.resources.getString(R.string.buy)
                }


                itemD.add(
                    DataPriceAlert(
                        "฿",
                        priceAlert.price!!,
                        aSide,
                        priceAlert.productName!!,
                        priceAlert.id!!
                    )
                )


//                adapter.itemList(
//                    DataPriceAlert(
//                        "฿",
//                        priceAlert.price,
//                        aSide,
//                        priceAlert.productName,
//                        priceAlert.id
//                    )
//                )

            }

            item.addAll(itemD)
        }
        priceList.adapter?.notifyDataSetChanged()
//        if (actionResult.isNewIntent()) {
//            if (actionResult.getMessageId() !== -1) {
//                StandardDialog.showMessageDialog(
//                    getContext(),
//                    R.string.title_dialog_error,
//                    actionResult.getMessageId(),
//                    actionResult
//                )
//            } else {
//                IntentActivityUtils.launchActivity(activity, actionResult.getNextIntent())
//            }
//        } else {
//            if (actionResult.getMessageId() !== -1) {
//                StandardDialog.showMessageDialog(
//                    getContext(),
//                    R.string.title_dialog_error,
//                    actionResult.getMessageId()
//                )
//            } else {
//                mobilePriceAlertList = mobilePriceAlertList
//                priceAlertListAdapter = PriceAlertListAdapter(
//                    activity,  /*R.layout.price_alert_item*/
//                    0,
//                    mobilePriceAlertList,
//                    this
//                )
//                paListView.setAdapter(priceAlertListAdapter)
//            }
//        }
    }

    override fun priceAlert() {
        startGetPriceListTask()
    }


    private fun startGetPriceListTask() {
        if (!(((this.asyncGetPriceAlertListTask != null) && (this.asyncGetPriceAlertListTask!!.get() != null) &&
                    (this.asyncGetPriceAlertListTask!!.get()!!
                        .status != AsyncTask.Status.FINISHED)))
        ) {
            val asyncTask = GetPriceAlertListTask(this)
            this.asyncGetPriceAlertListTask =
                WeakReference<GetPriceAlertListTask>(
                    asyncTask
                )
            asyncTask.execute()

        }
    }

    private fun startAddPriceAlertTask() {
        var setType = ""

        setType = if (sides ==context.resources.getString(R.string.SELL)) {
            "SE"
        } else {
            "BU"
        }
        if (!(asyncAddPriceTask != null && asyncAddPriceTask!!.get() != null &&
                    asyncAddPriceTask!!.get()?.status != AsyncTask.Status.FINISHED)
        ) {
            val asyncTask = AddPriceAlertTask(this, mobilePriceAlertList)
            this.asyncAddPriceTask = WeakReference(asyncTask)
            //            LogUtil.debug(TAG, "save order type {}", orderType);
            val bigDecimal: Double = ManageData().numberFormatToDouble(price.text.toString())

            val priceT = ManageData().numberFormatToDecimal(bigDecimal)

            asyncTask.execute(productCode.code, setType, priceT)
        }
    }


    fun onAddPriceAlertComplete(
        actionResult: ActionResult,
        mobilePriceAlert: PriceAlertsDTO?
    ) {
        val adapter = priceList.adapter as PriceAlertAdapter
        val layoutManager = activity?.let { LinearLayoutManager(it) }
//            layoutManager.reverseLayout = true
        if (layoutManager != null) {
            layoutManager.stackFromEnd = false
        }
//            priceList.setHasFixedSize(true)
        priceList.layoutManager = layoutManager
        if (mobilePriceAlert != null) {
            for (priceAlert in mobilePriceAlert.mobilePriceAlertList) {
                var aSide = ""
                aSide = if (priceAlert.type == "SE") {
                    context.resources.getString(R.string.sell)
                } else {
                    context.resources.getString(R.string.buy)
                }
                adapter.addItem(
                    DataPriceAlert(
                        "฿",
                        priceAlert.price!!,
                        aSide,
                        priceAlert.productName!!,
                        priceAlert.id!!
                    )

                )
            }
        }
        btnSelectProduct.text = context.resources.getString(R.string.btn_select)
        price.setText("")
        orderTypes.check(R.id.price_alert_sl_sell)
        price.isEnabled = false
        btnConfirm.isEnabled = false
//        if (actionResult.isNewIntent ()) {
//            if (actionResult.getMessageId() !== -1) {
////                StandardDialog.showMessageDialog(
////                    activity,
////                    R.string.title_dialog_error,
////                    actionResult.getMessageId(),
////                    actionResult
////                )
//            } else {
//                IntentActivityUtils.launchActivity(activity, actionResult.getNextIntent())
//            }
//        } else {
//            if (mobilePriceAlert != null) {
//                priceAlertListAdapter.insert(mobilePriceAlert, 0)
//                priceAlertListAdapter.notifyDataSetChanged()
//                clearAll()
//            }
//        }
    }

    private class GetPriceAlertListTask(priceAlertFragment: PriceAlertFragment?) :
        AsyncTask<Void?, Void?, ActionResult>() {
        private val myWeakContext: WeakReference<PriceAlertFragment?>?
        private var progressDialog: ProgressDialog? = null
        private var priceAlertList: PriceAlertsDTO? = null

//        override fun onPreExecute() {
//            if (!progressDialog!!.isShowing) {
//                progressDialog!!.show()
//            }
//        }


        override fun doInBackground(vararg p0: Void?): ActionResult {
            val actionResult = ActionResult()
            try {

                if (myWeakContext?.get() != null
                //AppUtil.isConnected(myWeakContext.get()!!.activity)
                ) {


                    var text = ManageData().getDataCoding(myWeakContext.get()!!.context)
                    CoroutineScope(Dispatchers.IO).launch {
                        text.let { MobileFrontService().getPriceAlertsList(it) }
                            .enqueue(object : retrofit2.Callback<PriceAlertsDTO> {
                                override fun onResponse(
                                    call: Call<PriceAlertsDTO>,
                                    response: Response<PriceAlertsDTO>
                                ) {
                                    if (response.code() == 200) {
                                        var data = response.body()
                                        if (data != null) {
                                            when (data.response) {
                                                "SUCCESS" -> {
                                                    priceAlertList = data
                                                    actionResult.setNewIntent(false)
                                                    actionResult.setMessageId(-1)
                                                    myWeakContext.get()!!.onGetPriceListComplete(
                                                        actionResult,
                                                        priceAlertList!!
                                                    )
                                                }
                                                "INVALID_CREDENTIAL" -> {
                                                    actionResult.setNewIntent(true)
                                                    ManageData().checkSessionAuthority(
                                                        data.response,
                                                        myWeakContext.get()!!.context
                                                    )
                                                }
                                                else -> {
                                                    actionResult.setNewIntent(false)
                                                    actionResult.setMessageId(R.string.msg_price_alert_list_error)
                                                }
                                            }
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<PriceAlertsDTO>, t: Throwable) {
                                    Log.e("ERROR", t.message.toString())

                                }
                            })

                    }


                } else {
                    actionResult.setNewIntent(false)
                    actionResult.setMessageId(R.string.msg_price_alert_list_error)
                }
            } catch (ex: Exception) {
                actionResult.setNewIntent(false)
                actionResult.setMessageId(R.string.msg_price_alert_list_error)
            }
            return actionResult
        }

        override fun onPostExecute(actionResult: ActionResult) {
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss()
//            }
            super.onPostExecute(actionResult)
            if (myWeakContext?.get() != null) {
                priceAlertList?.let {
                    myWeakContext.get()!!.onGetPriceListComplete(
                        actionResult,
                        it
                    )
                }
            }
        }

        init {
            myWeakContext = WeakReference(priceAlertFragment)
            //    progressDialog = StandardDialog.createProgressDialog(myWeakContext.get()!!.activity)
        }


    }

    private class AddPriceAlertTask(
        priceAlertFragment: PriceAlertFragment?,
        mobilePriceAlertList: List<PriceAlertsDTO?>?
    ) :
        AsyncTask<String?, Void?, ActionResult>() {
        private val myWeakContext: WeakReference<PriceAlertFragment?>?
        private var progressDialog: ProgressDialog? = null
        private var mobilePriceAlert: PriceAlertsDTO? = null

//        override fun onPreExecute() {
//            if (!progressDialog!!.isShowing) {
//                progressDialog!!.show()
//            }
//        }


        override fun doInBackground(vararg params: String?): ActionResult {
            val actionResult = ActionResult()
            try {

                if (myWeakContext?.get() != null
                //AppUtil.isConnected(myWeakContext.get()!!.activity)
                ) {

                    var text = ManageData().getDataCoding(myWeakContext.get()!!.context)
                    val jsonObj = JSONObject()

                    jsonObj.put("productCode", params[0])
                    jsonObj.put("type", params[1])
                    jsonObj.put("price", params[2])
                    jsonObj.put("mobileEnable", true)
                    jsonObj.put("smsEnable", false)
                    jsonObj.put("emailEnable", false)
                    jsonObj.put("alerted", false)

                    val jsonObjectString = jsonObj.toString()
                    val requestBody =
                        jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
                    CoroutineScope(Dispatchers.IO).launch {
                        text.let { MobileFrontService().addNewPriceAlert(it, requestBody) }
                            .enqueue(object : retrofit2.Callback<PriceAlertsDTO> {
                                override fun onResponse(
                                    call: Call<PriceAlertsDTO>,
                                    response: Response<PriceAlertsDTO>
                                ) {
                                    if (response.code() == 200) {
                                        var data = response.body()
                                        if (data != null) {

                                            when (data.response) {

                                                "SUCCESS" -> {
                                                    mobilePriceAlert = data
                                                    actionResult.setNewIntent(false)
                                                    myWeakContext.get()!!.onAddPriceAlertComplete(
                                                        actionResult,
                                                        mobilePriceAlert
                                                    )
                                                }
                                                "INVALID_CREDENTIAL" -> {
                                                    actionResult.setNewIntent(true)
                                                    actionResult.setMessageId(R.string.msg_main_INVALID_CREDENTIAL)
                                                    ManageData().checkSessionAuthority(
                                                        data.response.toString(),
                                                        myWeakContext.get()!!.context
                                                    )
                                                }
                                                else -> {
                                                    actionResult.setNewIntent(false)
                                                    actionResult.setMessageId(R.string.msg_price_alert_add_error)
                                                }
                                            }
                                        } else {
                                            actionResult.setNewIntent(false)
                                            actionResult.setMessageId(R.string.msg_dialog_noconnectivity_found)
                                        }
                                    }

                                }

                                override fun onFailure(call: Call<PriceAlertsDTO>, t: Throwable) {
                                    Log.e("ERROR priceAlert", t.message.toString())

                                }

                            })

                    }
                }
            } catch (ex: Exception) {
                actionResult.setNewIntent(false)
                actionResult.setMessageId(R.string.msg_price_alert_add_error)
            }
            return actionResult
        }

        override fun onPostExecute(actionResult: ActionResult) {
//            if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss()
//            }
            super.onPostExecute(actionResult)
            if (myWeakContext?.get() != null) {
                myWeakContext.get()!!.onAddPriceAlertComplete(actionResult, mobilePriceAlert)
            }
        }

        init {
            myWeakContext = WeakReference(priceAlertFragment)
            //  progressDialog = StandardDialog.createProgressDialog(myWeakContext.get()!!.activity)
        }


    }


}
