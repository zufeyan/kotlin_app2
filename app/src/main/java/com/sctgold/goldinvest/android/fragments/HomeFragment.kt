package com.sctgold.goldinvest.android.fragments


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Base64
import android.util.Log
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
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.dto.*
import com.sctgold.goldinvest.android.listener.OnPriceFeedListener
import com.sctgold.goldinvest.android.listener.OnReloadDataFragment
import com.sctgold.goldinvest.android.listener.OnSystemUpdateListener
import com.sctgold.goldinvest.android.receiver.MobilePricingReceiver
import com.sctgold.goldinvest.android.receiver.MobileSystemReceiver
import com.sctgold.goldinvest.android.rest.NewsService
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.goldinvest.android.service.MobilePricingService
import com.sctgold.goldinvest.android.service.MobileSystemService
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.google.gson.*
import com.sctgold.android.tradeplus.util.ManageData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.lang.ref.WeakReference
import java.lang.reflect.Type
import java.util.concurrent.CopyOnWriteArrayList


class HomeFragment : MainFragment(), OnReloadDataFragment, OnPriceFeedListener,
    OnSystemUpdateListener {

    lateinit var context: AppCompatActivity
    protected val pricingReceiver: MobilePricingReceiver = MobilePricingReceiver()
    protected val systemReceiver: MobileSystemReceiver = MobileSystemReceiver()

    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var productView: RecyclerView
    private lateinit var newsView: RecyclerView
    private lateinit var dialog: Dialog
    private lateinit var newsAll: TextView
    private lateinit var chatBtn: LinearLayout

    var myProduct: CopyOnWriteArrayList<DetailRealtimeSpotDTO> = CopyOnWriteArrayList()
    var myProductTemp: CopyOnWriteArrayList<DetailRealtimeSpotDTO> = CopyOnWriteArrayList()

    var itemNews: ArrayList<NewsRow> = ArrayList()
    var item: ArrayList<Any> = ArrayList()

    var currentStatus: String = ""
    var checkCall: Boolean = false

    //    private lateinit var ws: WebSocket
    var state: String = "HomeFragment"
    var beforeState: String = "HomeFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)

        productView = v.findViewById(R.id.home_product)
        newsView = v.findViewById(R.id.home_news)
        newsAll = v.findViewById(R.id.home_news_all)
        chatBtn = v.findViewById(R.id.chat_line)
        refresh = v.findViewById(R.id.home_refresh)

//        dialog = ProgressDialog.progressDialog(context)
//        dialog.show()

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        productView.apply {
        productView.layoutManager =
            activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
        productView.itemAnimator = DefaultItemAnimator()

        productView.adapter =
            ListTradeAdapter(getTradeLists(), context) { partItem: DetailRealtimeSpotDTO ->
                partItemClicked(
                    partItem
                )
            }
//        }

        var itemP: ArrayList<Any> = ArrayList()
        itemP.clear()
        item.clear()
        item.add(
            TradingHeader(
                context.resources.getString(R.string.prod),
                context.resources.getString(R.string.sell),
                context.resources.getString(R.string.buy)
            )
        )

        productView.adapter?.notifyDataSetChanged()




        newsAll.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        newsAll.isClickable = true
        newsAll.setOnClickListener {
            // go to all news fragment
//            parentFragmentManager.beginTransaction().setCustomAnimations(
//                R.anim.slide_in_left,
//                R.anim.slide_out_left,
//                R.anim.slide_out_right,
//                R.anim.slide_in_right
//            ).replace(R.id.frag_home, NewsFragment(), "NEWS")
//                .addToBackStack("HOME").commit()

            parentFragmentManager.beginTransaction().add(R.id.frag_home, NewsFragment())
                .addToBackStack(null).commit()
        }

        newsView.apply {


            newsView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false) }
            newsView.itemAnimator = DefaultItemAnimator()
            adapter = ListTradeAdapter(getNewsLists(), context) { partItem: DetailRealtimeSpotDTO ->
                partItemClicked(
                    partItem
                )
            }
        }

        chatBtn.isClickable = true
        chatBtn.setOnClickListener {
            chatLine()
        }
        //  refresh.setColorSchemeResources(R.color.step1, R.color.step2, R.color.step3, R.color.step4)
        refresh.setOnRefreshListener {

            Handler().postDelayed({
                loadData()
            }, 3000)
        }

    }

    fun loadData() {
        myProduct.clear()
        //  getSocket()
        // getNews()
        onReloadData()
        productView.adapter?.notifyDataSetChanged()
        newsView.adapter?.notifyDataSetChanged()
        refresh.isRefreshing = false
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val ftr: FragmentTransaction = requireFragmentManager().beginTransaction()
            ftr.detach(this).attach(this).commit()
        }
    }

    data class TradingHeader(var name: String, var sell: String, var buy: String)
    data class NewsRow(
        var photo: String,
        var title: String,
        var sub: String,
        var date: String,
        var id: Int
    )

    private fun chatLine() {
        val uri = Uri.parse("https://lin.ee/QSLwSEf") // missing 'http://' will cause crashed
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }


    private fun partItemClicked(partItem: DetailRealtimeSpotDTO) {
        val fm: FragmentManager = context.supportFragmentManager
        val tradingFragment: Fragment = TradingFragment()
        val bundle = Bundle()
        PropertiesKotlin.pdName = partItem.fullProductName.toString()
        PropertiesKotlin.pdCode = partItem.productCode.toString()
        PropertiesKotlin.currency = partItem.currency.toString()
//        bundle.putString("productName", partItem.fullProductName)
//        bundle.putString("productCode", partItem.productCode)
        tradingFragment.arguments = bundle
        if (partItem.buyPriceNew != "") {

            partItem.buyPriceNew?.let { ManageData().numberFormatToDouble(it) }
            var buyNum = partItem.buyPriceNew?.let { ManageData().numberFormatToDouble(it) }
            PropertiesKotlin.spotPriceBuy =
                buyNum?.let { ManageData().numberFormatToDecimal(it) }.toString()
        }

        if (partItem.sellPriceNew != "") {
            var sellNum = partItem.sellPriceNew?.let { ManageData().numberFormatToDouble(it) }
            PropertiesKotlin.spotPriceSell =
                sellNum?.let { ManageData().numberFormatToDecimal(it)  }.toString()
        }


        //   PropertiesKotlin.state = "TradingFragment"

//        fm.beginTransaction().remove(this).commit()
        fm.beginTransaction().add(R.id.frag_home, tradingFragment)
            .addToBackStack(null).commit()
    }


    private fun getNewsLists(): ArrayList<NewsRow> {
        return itemNews
    }

    class HomeHeaderViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_home_header, parent, false)) {
        private var secContent: LinearLayout? = null
        private var secName: TextView? = null
        private var secSell: TextView? = null
        private var secBuy: TextView? = null

        init {
            secContent = itemView.findViewById(R.id.home_sec_container)
            secName = itemView.findViewById(R.id.home_sec_name)
            secSell = itemView.findViewById(R.id.home_sec_sell)
            secBuy = itemView.findViewById(R.id.home_sec_buy)
        }

        fun bind(tradingHeader: TradingHeader) {
            secName?.text = tradingHeader.name
            secSell?.text = tradingHeader.sell
            secBuy?.text = tradingHeader.buy
        }

    }

    class HomeProductViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_home_product, parent, false)) {
        private var wgProductContent: LinearLayout? = null
        private var wgProductName: TextView? = null
        private var wgProductSell: TextView? = null
        private var wgProductBuy: TextView? = null
        private var wgProductTrade: ImageView? = null

        init {
            wgProductContent = itemView.findViewById(R.id.home_product_cell)
            wgProductName = itemView.findViewById(R.id.home_product_name)
            wgProductSell = itemView.findViewById(R.id.home_product_sell)
            wgProductBuy = itemView.findViewById(R.id.home_product_buy)
            wgProductTrade = itemView.findViewById(R.id.home_product_trade)
        }

        @SuppressLint("ResourceAsColor", "RestrictedApi")
        fun bind(
            tradingRow: DetailRealtimeSpotDTO,
            clickListener: (DetailRealtimeSpotDTO) -> Unit
        ) {
            val view: View = this.itemView
            wgProductContent?.id = layoutPosition

            var colorSell: String
            colorSell = if (!PropertiesKotlin.checkMode) {
                "#000000"
            } else {
                "#ffffff"
            }

            var colorBuy: String

            colorBuy = if (!PropertiesKotlin.checkMode) {
                "#000000"
            } else {
                "#ffffff"
            }
            if (!tradingRow.sellPriceNew.isNullOrEmpty()) {
                var priceSell = ManageData().numberFormatToDouble(tradingRow.sellPriceNew!!)
                var priceBuy = tradingRow.buyPriceNew?.let { ManageData().numberFormatToDouble(it) }
                wgProductSell?.text = ManageData().numberFormatToDecimal(priceSell)
                wgProductBuy?.text = priceBuy?.let { ManageData().numberFormatToDecimal(it) }
                wgProductBuy?.setTextSize(1, 18F)
                wgProductSell?.setTextSize(1, 18F)

                if (!tradingRow.changeSell) {
                    colorSell = if (tradingRow.sellUp) {
                        "#007C00"
                    } else {
                        "#990000"
                    }
                }

                if (!tradingRow.changeBuy) {
                    colorBuy = if (tradingRow.buyUp) {
                        "#007C00"
                    } else {
                        "#990000"
                    }
                }

                var myColorSell = Color.parseColor(colorSell)
                var myColorBuy = Color.parseColor(colorBuy)

                wgProductSell?.setTextColor(myColorSell)
                wgProductBuy?.setTextColor(myColorBuy)

            } else {
                wgProductSell?.text = ""
                wgProductBuy?.text = tradingRow.buyPriceNew
                wgProductBuy?.setTextSize(1, 15F)
                var myColorBuy = Color.parseColor(colorBuy)
                wgProductBuy?.setTextColor(myColorBuy)
            }

            wgProductName?.text = tradingRow.fullProductName

            if (!tradingRow.viewable) {

                if (tradingRow.buyPriceNew == "Market Closed" || tradingRow.buyPriceNew == "Updating Data...") {
                    wgProductTrade?.visibility = View.INVISIBLE
                    val disabled: Drawable =
                        ContextCompat.getDrawable(
                            view.context,
                            R.drawable.bg_cell_disabled
                        ) as Drawable
                    wgProductContent?.background = disabled
                } else {
                    wgProductTrade?.visibility = View.VISIBLE
                    wgProductContent?.setOnClickListener { v ->
                        clickListener(tradingRow)

                    }
                    val enabled: Drawable =
                        ContextCompat.getDrawable(view.context, R.drawable.bg_cell) as Drawable
                    wgProductContent?.background = enabled
                }


            } else {
                wgProductTrade?.visibility = View.INVISIBLE
                val disabled: Drawable =
                    ContextCompat.getDrawable(view.context, R.drawable.bg_cell_disabled) as Drawable
                wgProductContent?.background = disabled
            }
        }


    }

    class HomeNewsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_home_news, parent, false)) {
        private var wgNewsContent: LinearLayout? = null
        private var wgNewsPhoto: ImageView? = null
        private var wgNewsTitle: TextView? = null
        private var wgNewsSub: TextView? = null
        private var wgNewsDate: TextView? = null
        private var wgNewsNext: ImageView? = null

        init {
            wgNewsContent = itemView.findViewById(R.id.home_news_container)
            wgNewsPhoto = itemView.findViewById(R.id.home_news_photo)
            wgNewsTitle = itemView.findViewById(R.id.home_news_title)
            wgNewsSub = itemView.findViewById(R.id.home_news_sub)
            wgNewsDate = itemView.findViewById(R.id.home_news_date)
            wgNewsNext = itemView.findViewById(R.id.home_news_read)
        }

        fun bind(newsRow: NewsRow, context: Context) {
            wgNewsContent?.id = layoutPosition

            wgNewsContent?.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val newsDetailFragment: Fragment = NewsDetailFragment()
                val bundle = Bundle()
                bundle.putString("title", newsRow.title)
                bundle.putString("sub", newsRow.sub)
                bundle.putString("date", newsRow.date)
                bundle.putString("photo", newsRow.photo)
                newsDetailFragment.arguments = bundle
                val fm: FragmentManager = act.supportFragmentManager
                fm.beginTransaction().replace(R.id.frag_home, newsDetailFragment)
                    .addToBackStack("HOME").commit()
            }

            wgNewsPhoto?.let { Glide.with(context).load(newsRow.photo).into(it) }
            wgNewsTitle?.text = newsRow.title
            wgNewsSub?.text = newsRow.sub
            val post: String = context.getString(R.string.pf_post) + " " + newsRow.date
            wgNewsDate?.text = post
        }

    }

    private class ListTradeAdapter(
        val list: List<Any>?,
        val context: Context,
        val clickListener: (DetailRealtimeSpotDTO) -> Unit
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val sections: Int = 0
        private val rows: Int = 1
        private val news: Int = 2

        override fun getItemCount(): Int {
            return list!!.size
        }

        override fun getItemViewType(position: Int): Int {
            when (list?.get(position)) {
                is TradingHeader -> {
                    return sections
                }
                is DetailRealtimeSpotDTO -> {
                    return rows
                }
                is NewsRow -> {
                    return news
                }
            }
            return rows
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                sections -> {
                    return HomeHeaderViewHolder(LayoutInflater.from(parent.context), parent)
                }
                rows -> {
                    return HomeProductViewHolder(LayoutInflater.from(parent.context), parent)
                }
                news -> {
                    return HomeNewsViewHolder(LayoutInflater.from(parent.context), parent)
                }
            }
            return HomeProductViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                sections -> {
                    val head = holder as HomeHeaderViewHolder
                    configureHeader(head, position)
                }
                rows -> {
                    val row = holder as HomeProductViewHolder
                    configureRow(row, position)
                }
                news -> {
                    val news = holder as HomeNewsViewHolder
                    configureNews(news, position)
                }
            }
        }

        private fun configureHeader(holder: HomeHeaderViewHolder, position: Int) {
            val header: TradingHeader = list?.get(position) as TradingHeader
            holder.bind(TradingHeader(header.name, header.sell, header.buy))
        }

        private fun configureRow(holder: HomeProductViewHolder, position: Int) {
            holder.bind(list?.get(position) as DetailRealtimeSpotDTO, clickListener)
        }

        private fun configureNews(holder: HomeNewsViewHolder, position: Int) {
            val news: NewsRow = list?.get(position) as NewsRow
            holder.bind(NewsRow(news.photo, news.title, news.sub, news.date, news.id), context)
        }
    }

    companion object {
        fun newInstance(): HomeFragment = HomeFragment()
        var select_theme: Int = 0
    }

    private fun getNews() {
        val client: OkHttpClient = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(PropertiesKotlin.sctUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(NewsService::class.java)

        retrofit.getNewsDaily().enqueue(object : Callback<NewsListDTO> {
            override fun onResponse(call: Call<NewsListDTO>, response: Response<NewsListDTO>) {

                when (response.body()?.response) {

                    "OPERATION_SUCCEED" -> {
                        itemNews.clear()
                        if (response.body()!!.newsDTO.isNotEmpty()) {

                            for (news in response.body()!!.newsDTO) {
                                val itemNewsData: ArrayList<NewsRow> = ArrayList()
                                itemNewsData.add(
                                    NewsRow(
                                        news.picture,
                                        news.title,
                                        Html.fromHtml(news.description, 0).toString(),
                                        news.postdate,
                                        news.id
                                    )
                                )


                                itemNews.addAll(itemNewsData)
                            }
                        }
                    }
                }
                newsView.adapter?.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<NewsListDTO>, t: Throwable) {
                Log.e("ERROR ", t.message.toString())
            }

        })
    }

    @SuppressLint("SetTextI18n")
    fun onInitialHomeComplete(result: ActionResult) {
        var itemP: ArrayList<Any>
        item.clear()
        for (myPd in myProduct) {
            //   itemP = ArrayList()
            item.add(
                DetailRealtimeSpotDTO(
                    0, myPd.productCode,
                    myPd.fullProductName,
                    true,
                    myPd.freeze,
                    myPd.sellPriceOld,
                    myPd.buyPriceOld,
                    "",
                    " Updating Data...",
                    myPd.changeSell,
                    myPd.changeBuy,
                    myPd.sellUp,
                    myPd.buyUp,
                    myPd.checkStatus,
                    myPd.currentStatus,
                    myPd.colorBuy,
                    myPd.colorSell,
                    myPd.currency
                )
            )
//            item.addAll(itemP)
            //      itemP.clear()

        }
        productView.adapter?.notifyDataSetChanged()
//        result.getNextIntent()?.let {
//            activity?.let { it1 ->
//                IntentActivityUtils().launchActivity(
//                    it1, it
//                )
//            }
//        }
//        Log.e(" onInitialHomeComplete  : ",   PropertiesKotlin.backupPriceProductListDTO.toString())
            updatePrices(PropertiesKotlin.backupPriceProductListDTO)
    }


    fun updatePrices(productPrices: CopyOnWriteArrayList<BackupPriceProductDTO>?) {
        var systemUpdate: SystemDTO = PropertiesKotlin.systemInfo
//        var myProductHold: ArrayList<DetailRealtimeSpotDTO> = ArrayList()
//        myProductHold.addAll(myProduct)
        var myProductTemp: CopyOnWriteArrayList<DetailRealtimeSpotDTO> = CopyOnWriteArrayList()
        myProductTemp.clear()
        myProductTemp = CopyOnWriteArrayList()

        for (value in myProduct) {

            value.buyPriceOld = value.buyPriceNew
            value.sellPriceOld = value.sellPriceNew
            value.currency = value.currency

            if (productPrices != null) {
                for (pd in productPrices) {

//                    Log.e("pd :   " ,pd.toString())
//                    Log.e("pd.productCode :   " ,pd.productCode.toString())
                    if (pd.productCode == value.productCode) {

                        currentStatus = "N"
                        if (systemUpdate.normalTradeEnable) {
                            checkCall = true
                            if (systemUpdate.tradeFreezing) {
                                currentStatus = "UpdateSystem"
                                checkCall = false
                            }


                        } else {
                            currentStatus = "CloseSystem"
                            if (systemUpdate.frontClose) {
                                currentStatus = "frontClose"
                                ServiceApiSpot.logout(context)
                            }
                        }



                        value.freeze = pd.statusFreeze
                        value.buyPriceNew = pd.buyPriceNew
                        value.sellPriceNew = pd.sellPriceNew

                        if (value.sellPriceOld != "0.00") {
                            if (value.sellPriceOld == value.sellPriceNew) {
                                value.changeSell = true
                            } else {
                                value.changeSell = false

                                if (value.sellPriceOld!! > value.sellPriceNew.toString()) {
                                    value.sellUp = false
                                } else if (value.sellPriceOld!! < value.sellPriceNew.toString()) {
                                    value.sellUp = true
                                }
                            }
                        } else {
                            value.changeSell = true
                        }

                        if (value.buyPriceOld != "0.00") {
                            if (value.buyPriceOld == value.buyPriceNew) {
                                value.changeBuy = true
                            } else {
                                value.changeBuy = false
                                if (value.buyPriceOld!! > value.buyPriceNew.toString()) {
                                    value.buyUp = false
                                } else if (value.buyPriceOld!! < value.buyPriceNew.toString()) {
                                    value.buyUp = true
                                }
                            }
                        } else {
                            value.changeBuy = true
                        }

                        value.checkStatus =
                            value.freeze && value.buyPriceNew == "0.00" && value.sellPriceNew == "0.00"
                        value.currentStatus = currentStatus

                    }

                }
            }
            myProductTemp.add(value)


        }

//            if (myProduct.size != 0) {
//                if (myProduct[0].currentStatus != currentStatus) {
//                    //   myProductTemp = ArrayList()
//                    myProductTemp.clear()
//                    myProductTemp = ArrayList()
//                    for (value in myProduct) {
//                        value.currentStatus = currentStatus
//                        myProductTemp.add(value)
//                    }
//                    myProduct.clear()
//                    myProduct = myProductTemp
//                }
//            }
        getTradeLists()
    }


    private fun getTradeLists(): ArrayList<Any> {
        var itemP: ArrayList<Any> = ArrayList()
        itemP.clear()
        item.clear()

        item.add(
            TradingHeader(
                context.resources.getString(R.string.prod),
                context.resources.getString(R.string.sell),
                context.resources.getString(R.string.buy)
            )
        )

        for (myPd in myProduct) {
            itemP = ArrayList()

            var statusSystem = ""

            if (currentStatus == "CloseSystem") {
                statusSystem = "Market Closed"
            } else if (currentStatus == "UpdateSystem") {
                statusSystem = "Updating Data..."
            }
//            Log.e(" fullProductName : " , myPd.fullProductName.toString())
//            Log.e(" statusSystem : " , statusSystem)
//Log.e(" myPd.viewable : " , myPd.viewable.toString())
            if (currentStatus == "N") {
                itemP.clear()
                itemP.add(
                    DetailRealtimeSpotDTO(
                        0, myPd.productCode,
                        myPd.fullProductName,
                        myPd.viewable,
                        myPd.freeze,
                        myPd.sellPriceOld,
                        myPd.buyPriceOld,
                        myPd.sellPriceNew,
                        myPd.buyPriceNew,
                        myPd.changeSell, myPd.changeBuy,
                        myPd.sellUp, myPd.buyUp,
                        myPd.checkStatus, myPd.currentStatus,
                        myPd.colorBuy,
                        myPd.colorSell, myPd.currency
                    )
                )
                item.addAll(itemP)

                itemP.clear()
//                activity?.runOnUiThread(Runnable {
//                    productView.adapter?.notifyDataSetChanged()
//
//                })
            } else {
                if (currentStatus == "CloseSystem" || currentStatus == "UpdateSystem") {

//                    Log.e("ddddddddddddddddddd : " , myPd.viewable.toString())

                    if(!myPd.viewable){
                        itemP.add(
                            DetailRealtimeSpotDTO(
                                0, myPd.productCode,
                                myPd.fullProductName,
                                myPd.viewable,
                                myPd.freeze,
                                myPd.sellPriceOld,
                                myPd.buyPriceOld,
                                "",
                                statusSystem,
                                myPd.changeSell, myPd.changeBuy,
                                myPd.sellUp, myPd.buyUp,
                                myPd.checkStatus,
                                myPd.currentStatus,
                                myPd.colorBuy,
                                myPd.colorSell,
                                myPd.currency
                            )
                        )
                        item.addAll(itemP)
                        itemP.clear()
                    }else{
                        itemP.add(
                            DetailRealtimeSpotDTO(
                                0, myPd.productCode,
                                myPd.fullProductName,
                                myPd.viewable,
                                myPd.freeze,
                                myPd.sellPriceOld,
                                myPd.buyPriceOld,
                                myPd.sellPriceNew,
                                myPd.buyPriceNew,
                                myPd.changeSell, myPd.changeBuy,
                                myPd.sellUp, myPd.buyUp,
                                myPd.checkStatus, myPd.currentStatus,
                                myPd.colorBuy,
                                myPd.colorSell, myPd.currency
                            )
                        )
                        item.addAll(itemP)
                        itemP.clear()
                    }

                }
            }


        }
        val insertIndex = myProduct.size
        activity?.runOnUiThread(Runnable {
            productView.adapter?.notifyItemRemoved(insertIndex)
            productView.adapter?.notifyDataSetChanged()

        })
        productView.adapter?.notifyDataSetChanged()

        return item
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onDetach() {
        super.onDetach()
        this.context = context
    }

    override fun stateChange(viewId: Int) {

    }


    @SuppressLint("UseRequireInsteadOfGet")
    fun registerPricingReceiver() {
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            pricingReceiver,
            IntentFilter(MobilePricingService.FEED_UPDATE_NOTIFICATION)
        )

    }

    fun unRegisterPricingReceiver() {
        //LogUtil.debug(TAG, "Unregister Pricing Receiver Service")
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(pricingReceiver)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun registerSystemReceiver() {
        // LogUtil.debug(TAG, "Register System Receiver Service")
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            systemReceiver, IntentFilter(MobileSystemService.SYSTEM_NOTIFICATION)
        )

    }

    fun unRegisterSystemReceiver() {
        // LogUtil.debug(TAG, "Unregister System Receiver Service")
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(systemReceiver)
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = getString(R.string.tab_home)
        myProduct = CopyOnWriteArrayList()
        myProductTemp = CopyOnWriteArrayList()
        productView.adapter?.notifyDataSetChanged()
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.VISIBLE
        var sharedPreferences: SharedPreferences? =
            requireActivity().getSharedPreferences(
                getString(R.string.key_prefs_session_initialized),
                Context.MODE_PRIVATE
            )


        if (sharedPreferences != null) {
            select_theme = sharedPreferences.getInt("select_theme", 0)
            if (select_theme == 0) {
                PropertiesKotlin.checkMode = isUsingNightModeResources()
            } else PropertiesKotlin.checkMode = select_theme != 1
        }

        MobilePricingReceiver.onPriceFeedListener = this
        MobileSystemReceiver.onSystemUpdateListener = this
        startHomeProductTask()
        registerPricingReceiver()
        registerSystemReceiver()

        PropertiesKotlin.state = state
        PropertiesKotlin.beforeState = beforeState
        getNews()
    }

    override fun onPause() {
        super.onPause()
        unRegisterPricingReceiver()
        unRegisterSystemReceiver()
        myProduct = CopyOnWriteArrayList()
        myProductTemp = CopyOnWriteArrayList()
        productView.adapter?.notifyDataSetChanged()
    }

    override fun onReloadData() {
        startHomeProductTask()
    }

    private fun startHomeProductTask() {
        val asyncTask = ProductAsyncTask(this)
        asyncTask.execute()
    }


    private class ProductAsyncTask(productListFragment: HomeFragment) :
        AsyncTask<Void?, Void?, ActionResult>() {
        var myWeakContext: WeakReference<HomeFragment>? = WeakReference(productListFragment)
        var res: Resources? = null


        override fun doInBackground(vararg params: Void?): ActionResult {
            val actionResult = ActionResult()
            myWeakContext?.get()?.let { it ->
                ServiceApiSpot.getMyProduct(it.context) {
                    if (it != null) {

                        myWeakContext?.get()?.myProduct = CopyOnWriteArrayList()
                        var detail: DetailRealtimeSpotDTO
                        var backupPricePD: CopyOnWriteArrayList<BackupPriceProductDTO>
                        for (products in it.mobileMyProductList) {
                            detail = DetailRealtimeSpotDTO()
                            detail.viewable = products.viewable
                            detail.productCode = products.code
                            detail.fullProductName = products.name
                            detail.currency = products.currency

                            if (PropertiesKotlin.backupPriceProductListDTO.isNotEmpty()) {

                                backupPricePD = PropertiesKotlin.backupPriceProductListDTO
                                for (backUp in backupPricePD) {
                                    if (products.code == backUp.productCode) {
                                        if (backUp.status == "buy") {
                                            detail.buyPriceNew = backUp.buyPriceNew
                                        }
                                        if (backUp.status == "sell") {
                                            detail.sellPriceNew = backUp.sellPriceNew
                                        }
                                    }
                                }


                            }


                            myWeakContext?.get()?.myProduct!!.add(detail)

                        }
                        //
                        actionResult.setNewIntent(FALSE)
                        actionResult.setReloadRequired(TRUE)
                    }
                    myWeakContext!!.get()?.onInitialHomeComplete(actionResult)
//                    Log.e("ProductAsyncTask", "ProductAsyncTask  ")
//                    Log.e("=result===  ", myWeakContext!!.get()?.myProduct.toString())
                }
            }
            return actionResult
        }


        override fun onPostExecute(actionResult: ActionResult?) {
            if (myWeakContext != null && myWeakContext?.get() != null && myWeakContext?.get()?.isAdded == true) {
                super.onPostExecute(actionResult)
                if (actionResult != null) {
                    //myWeakContext!!.get()?.onInitialHomeComplete(actionResult)
                }
            }
        }

        init {
            if (productListFragment != null && productListFragment.isAdded) {
                res = productListFragment.resources
            }
        }


    }

    @SuppressLint("SetTextI18n")
    override fun onPricePush(productPrices: CopyOnWriteArrayList<BackupPriceProductDTO>) {
        updatePrices(productPrices)
    }

    override fun onSystemUpdate(systemDTO: SystemDTO?) {
        if (systemDTO?.normalTradeEnable != PropertiesKotlin.systemInfo.normalTradeEnable
            || systemDTO.placeOrderTradeEnable != PropertiesKotlin.systemInfo.placeOrderTradeEnable
            || systemDTO.tradeFreezing != PropertiesKotlin.systemInfo.tradeFreezing
        ) {
            updatePrices(PropertiesKotlin.backupPriceProductListDTO)
        }
        if (systemDTO != null) {
            PropertiesKotlin.systemInfo = systemDTO
        }


    }


}


class ByteArrayToBase64Adapter : JsonSerializer<ByteArray?>,
    JsonDeserializer<ByteArray?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ByteArray {
        return Base64.decode(json.asString, Base64.NO_WRAP)
    }

    override fun serialize(
        src: ByteArray?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP))
    }


}
