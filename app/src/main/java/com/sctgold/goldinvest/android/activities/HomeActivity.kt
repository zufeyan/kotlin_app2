package com.sctgold.goldinvest.android.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.bumptech.glide.manager.SupportRequestManagerFragment
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.dto.EventDTO
import com.sctgold.goldinvest.android.receiver.MobileEventReceiver
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.goldinvest.android.service.MobileEventService
import com.sctgold.goldinvest.android.util.OnItemSelectListener
import com.sctgold.goldinvest.android.util.PagerAdapter
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.sctgold.goldinvest.android.util.AppConstants
import com.sctgold.goldinvest.android.util.UploadUtility
import com.sctgold.goldinvest.android.fragments.*
import com.sctgold.goldinvest.android.listener.*
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Runnable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor


open class HomeActivity : AppCompatActivity(), OnEventUpdateListener {

    private lateinit var tabLayout: TabLayout


    private lateinit var titles: TextView
    private lateinit var viewPager: ViewPager2
    var intentActivity: IntentActivity? = null

    private var fragmentRefreshListener: FragmentRefreshListener? = null
    var TIMEOUT_IN_MILLI = 0
    var handler: Handler? = null
    var r: Runnable? = null

    var checkNightTheme: Boolean = false
    var checkAlwaysOn: Boolean = false

    var checkPostion = 0
    var select_theme = 0
    var language = ""

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    var value = true


    private val GALLERY_INTENT_CALLED = 3
    private val GALLERY_KITKAT_INTENT_CALLED = 4

    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101

    var imageUri: Uri? = null
    var imgPath: String? = null
    private val REQUEST_WRITE_PERMISSION = 0
    private val REQUEST_CAMERA_PERMISSION = 1
    private val IMAGE_CAPTURE_CODE = 2


    private val eventReceiver: MobileEventReceiver = MobileEventReceiver()
    var currentFragment: Fragment? = null

    val bundle = bundleOf(
        "KEY_HOME" to "Home",
        "KEY_PORT" to "Portfolio",
        "KEY_HIST" to "History",
        "KEY_MORE" to "More"
    )


    private val tabNames = arrayOf(
        R.string.tab_home,
        R.string.tab_port,
        R.string.tab_hist,
        R.string.tab_more
    )

    private val tabIcons = arrayOf(
        R.drawable.tab_home,
        R.drawable.tab_port,
        R.drawable.tab_history,
        R.drawable.tab_more
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

//        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

//        connectionLiveData = ConnectionLiveData(this)
//        connectionLiveData.observe(this) { isNetworkAvailable ->
//            isNetworkAvailable?.let {
//                Log.d("testddddddddddd  ", "asdasdasggggggggggg ")
//            }
//        }

        imageView2.setImageResource(R.drawable.ic_v_call)

        imageView2.setOnClickListener {
            val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1
            val mIntent = Intent(Intent.ACTION_CALL)
            val number = "tel:" + PropertiesKotlin.phoneNumber
            mIntent.data = Uri.parse(number)
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                )

                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //You already have permission
                try {

                    val builder: AlertDialog.Builder? = this.let {
                        AlertDialog.Builder(it)
                    }
                    builder?.setTitle(getString(R.string.btn_call_hotline))
                        ?.setMessage(PropertiesKotlin.phoneNumber)
                        ?.setPositiveButton(getString(R.string.btn_call),
                            DialogInterface.OnClickListener { dialog, id ->
                                startActivity(mIntent)
                                builder.create().dismiss()
                            })
                        ?.setNegativeButton(getString(R.string.btn_close),
                            DialogInterface.OnClickListener { dialog, id ->
                                builder.create().dismiss()
                            })
                    builder?.create()?.show()


                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }

        appContext = this
        titles = findViewById(R.id.title_main_text)
//        titles.text = getString(R.string.app_name)

        val selectOrders: RadioGroup = findViewById(R.id.clear_port_sl_order)
        selectOrders.visibility = View.GONE


        //tabLayout = findViewById(R.id.tabs)
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setSelectedTabIndicator(null)

        viewPager = findViewById(R.id.viewPager)
        viewPager.isUserInputEnabled = false
        createTab()
        var sharedPreferences: SharedPreferences? = getSharedPreferences(
            getString(R.string.key_prefs_session_initialized),
            MODE_PRIVATE
        )
        if (sharedPreferences != null) {
            checkNightTheme = sharedPreferences.getBoolean("NIGHT_THEME", false)
            select_theme = sharedPreferences.getInt("select_theme", 0)
            language = sharedPreferences.getString("LANGUAGE", "EN").toString()
            checkAlwaysOn = sharedPreferences.getBoolean("ALWAYS_ON", false)
        }
        setLocaleHomeAct(language)
        setAlwaysOn(checkAlwaysOn)
    }

    fun setLocaleHomeAct(lang: String): Boolean {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        return true
    }

    private fun createTab() {

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {

                return when (position) {
                    1 -> {
                        PortPagerFragment.newInstance()
                    }
                    2 -> {
                        HistoryFragment.newInstance()
                    }
                    3 -> {
                        MoreFragment.newInstance()
                    }
                    else -> {
                        HomeFragment.newInstance()
                    }
                }
            }

            override fun getItemCount(): Int = tabNames.size
        }



        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val nullParent: ViewGroup? = null
            val tabText =
                LayoutInflater.from(this).inflate(R.layout.tab_icon, nullParent) as TextView
            val tabIcon = AppCompatResources.getDrawable(
                this, tabIcons[position]
            ) as Drawable
            tabText.text = getString(tabNames[position])
            tabText.requestLayout()
            tabText.setTextColor(ContextCompat.getColorStateList(this, R.color.color_tab))
            tabText.setCompoundDrawablesWithIntrinsicBounds(null, tabIcon, null, null)
            tab.customView = tabText


        }.attach()
        val adapter = PagerAdapter(this.supportFragmentManager, tabLayout.tabCount)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager.currentItem = tab.position

                    currentFragment = adapter.instantiateItem(viewPager, viewPager.currentItem)
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

                if (tab != null) {
                    viewPager.currentItem = tab.position
                    //  supportFragmentManager.removeOnBackStackChangedListener { (this) }
                }
            }
        })


        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                checkPostion = position
//                val fragment = supportFragmentManager.findFragmentByTag("f" + viewPager.currentItem)
                when (position) {
                    1 -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        titles.text = getString(R.string.tab_port)
                        if (getFragmentRefreshListener() != null) {
                            getFragmentRefreshListener()?.onRefresh()

                        }


//                        supportFragmentManager.beginTransaction().remove(PortPagerFragment.newInstance())
//                            .attach(PortPagerFragment.newInstance()).detach(PortPagerFragment.newInstance()).addToBackStack(null).commit()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.frag_port_pager,
                            PortPagerFragment.newInstance()
                        ).addToBackStack(null).commit()

                    }
                    2 -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        titles.text = getString(R.string.tab_hist)
                        if (getFragmentRefreshListener() != null) {
                            getFragmentRefreshListener()?.onRefresh()
                        }
//                                supportFragmentManager.beginTransaction().replace(
//                                    R.id.frag_history,
//                                    HistoryFragment.newInstance()
//                                )
//                                    .addToBackStack(null).commit()

                    }
                    3 -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        titles.text = getString(R.string.tab_more)
                    }
                    else -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        titles.text = getString(R.string.tab_home)

                        if (getFragmentRefreshListener() != null) {
                            getFragmentRefreshListener()?.onRefresh()
                        }


                    }
                }

            }

        })

    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

//        Log.e("checkPostion   : ",checkPostion.toString())
//        Log.e("PropertiesKotlin.state   : ",PropertiesKotlin.state)
        //val myFragment = supportFragmentManager.findFragmentByTag("f" + viewPager.currentItem)
        when (checkPostion) {
            0 -> {
                when (PropertiesKotlin.state) {
                    "HomeFragment" -> {
                        val item1 = menu.findItem(R.id.to_call_hot_line)
                        item1.isVisible = false
                        val item2 = menu.findItem(R.id.to_edit_product)
                        item2.isVisible = true
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = true
                    }

                    "TradingFragment" -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false

                    }
                    "TradingConfirmFragment" -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "TradingDecisionFragment" -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "ManageFragment" -> {
                        val item1 = menu.findItem(R.id.to_call_hot_line)
                        item1.isVisible = false
                        val item2 = menu.findItem(R.id.to_edit_product)
                        item2.isVisible = false
                        val item3 = menu.findItem(R.id.to_checked)
                        item3.isVisible = true
                        val item4 = menu.findItem(R.id.to_price_alert)
                        item4.isVisible = false
                        val item5 = menu.findItem(R.id.to_email_open)
                        item5.isVisible = false
                        val item6 = menu.findItem(R.id.to_delete)
                        item6.isVisible = false
                        val item7 = menu.findItem(R.id.to_www)
                        item7.isVisible = false
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "SupportRequestManagerFragment" -> {
                        val item2 = menu.findItem(R.id.to_price_alert)
                        item2.isVisible = false
                        val item3 = menu.findItem(R.id.to_checked)
                        item3.isVisible = false
                        val item5 = menu.findItem(R.id.to_email_open)
                        item5.isVisible = false
                        val item6 = menu.findItem(R.id.to_delete)
                        item6.isVisible = false
                        val item7 = menu.findItem(R.id.to_www)
                        item7.isVisible = false
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }

                    "MoreFragment" -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "NewsFragment" -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "NewsDetailFragment" -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "" -> {

                        when (PropertiesKotlin.beforeState) {
                            "NewsFragment" -> {
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
                                val imageView2 = findViewById<ImageView>(R.id.imageView2)
                                imageView2.isVisible = false
                            }
                            "TradingFragment" -> {
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
                                val imageView2 = findViewById<ImageView>(R.id.imageView2)
                                imageView2.isVisible = false
                            }
                            else -> {
                                val item1 = menu.findItem(R.id.to_call_hot_line)
                                item1.isVisible = true
                                val item2 = menu.findItem(R.id.to_edit_product)
                                item2.isVisible = true
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
                                val imageView2 = findViewById<ImageView>(R.id.imageView2)
                                imageView2.isVisible = false
                            }
                        }
                    }
                    else ->{
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }


                }
            }
            else -> {

                when (PropertiesKotlin.state) {
                    "ContactFragment" -> {
                        val item1 = menu.findItem(R.id.to_call_hot_line)
                        item1.isVisible = true
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
                        item7.isVisible = true
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "NoticeFragment" -> {
                        val item1 = menu.findItem(R.id.to_call_hot_line)
                        item1.isVisible = false
                        val item2 = menu.findItem(R.id.to_edit_product)
                        item2.isVisible = false
                        val item3 = menu.findItem(R.id.to_checked)
                        item3.isVisible = false
                        val item4 = menu.findItem(R.id.to_price_alert)
                        item4.isVisible = false
                        val item5 = menu.findItem(R.id.to_email_open)
                        item5.isVisible = true
                        val item6 = menu.findItem(R.id.to_delete)
                        item6.isVisible = false
                        val item7 = menu.findItem(R.id.to_www)
                        item7.isVisible = false
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }

                    "HistoryDetailFragment" -> {
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
                        item6.isVisible = PropertiesKotlin.checkTradeType
                        val item7 = menu.findItem(R.id.to_www)
                        item7.isVisible = false
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "GraphFragment" -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    "ClearPortActionFragment" -> {
                        val title = this.findViewById<TextView>(R.id.title_main_text)
                        title.text = PropertiesKotlin.pdMatch
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                    else -> {
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
                        val imageView2 = findViewById<ImageView>(R.id.imageView2)
                        imageView2.isVisible = false
                    }
                }


            }
        }

        return true
    }

    override fun onBackPressed() {

        val isFragmentPopped = handleViewPagerBackStack()
        val fragment = supportFragmentManager.findFragmentByTag("f" + viewPager.currentItem)
        var backStackEntryCount = supportFragmentManager.backStackEntryCount

        if (currentFragment != null) {

            when (currentFragment) {
                is HomeFragment -> {
                    when (PropertiesKotlin.state) {
                        "HomeFragment" -> {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_HOME)
                            startActivity(intent)
                        }
                        "PriceAlertFragment" -> {
                            supportFragmentManager.popBackStackImmediate()
                        }
                        "TradingDecisionFragment" -> {

                        }
                        "TradingConfirmFragment" -> {

                        }
                        "NewsDetailFragment" -> {
                            when (PropertiesKotlin.beforeState) {
                                "HomeFragment" -> {
                                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                                    supportActionBar?.setDisplayShowHomeEnabled(false)
                                    supportActionBar?.setHomeButtonEnabled(false)
                                    val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                                    tabLayout.visibility = View.VISIBLE
                                    val title = this.findViewById<TextView>(R.id.title_main_text)
                                    title.text = getString(R.string.thome)
                                    supportFragmentManager.popBackStack()

//                                    PropertiesKotlin.beforeState = ""
                                }
                                "NewsFragment" -> {
                                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                                    supportActionBar?.setDisplayShowHomeEnabled(false)
                                    supportActionBar?.setHomeButtonEnabled(false)
                                    val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                                    tabLayout.visibility = View.GONE
                                    supportFragmentManager.popBackStack()
//                                    PropertiesKotlin.beforeState = ""
                                }
                                else -> {
                                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                                    supportActionBar?.setDisplayShowHomeEnabled(false)
                                    supportActionBar?.setHomeButtonEnabled(false)
                                    val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                                    tabLayout.visibility = View.VISIBLE
                                    val title = this.findViewById<TextView>(R.id.title_main_text)
                                    title.text = getString(R.string.thome)
                               supportFragmentManager.popBackStackImmediate()
                                }
                            }

                        }
                        else -> {

//                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
//                            supportActionBar?.setDisplayShowHomeEnabled(false)
//                            supportActionBar?.setHomeButtonEnabled(false)
//                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
//                            tabLayout.visibility = View.VISIBLE
                            //                super.onBackPressed()
                            //supportFragmentManager.popBackStack()
                        }
                    }
                }
                is PortPagerFragment -> {
                    when (PropertiesKotlin.state) {
                        "PortDetailFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStackImmediate()
                        }
                        "PortStatementFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStackImmediate()
                        }
                        "PortMoneyFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStackImmediate()
                        }
                        "PortDeptFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStackImmediate()
                        }
                        "PortSendingFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStackImmediate()
                        }
                        else -> {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_HOME)
                            startActivity(intent)
                        }
                    }

                }
                is HistoryFragment -> {
                    when (PropertiesKotlin.state) {
                        "" -> {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_HOME)
                            startActivity(intent)
                        }
                        "HistoryFragment" -> {
                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_HOME)
                            startActivity(intent)

                        }
                        else -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStackImmediate()
                        }
                    }
                }
                is MoreFragment -> {
                    when (PropertiesKotlin.state) {
                        "MoreFragment" -> {

                            val intent = Intent(Intent.ACTION_MAIN)
                            intent.addCategory(Intent.CATEGORY_HOME)
                            startActivity(intent)
                        }
                        "SettingsFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "PriceAlertFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }

                        "NewsFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "NewsDetailFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.GONE
                            supportFragmentManager.popBackStack()
                        }
                        "AnalysisFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "GraphFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "ContactFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "PaymentFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "GuideFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "ClearPortFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            supportFragmentManager.popBackStack()
                        }
                        "ChangePasswordFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.GONE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tsett)
                            supportFragmentManager.popBackStack()
//                          PropertiesKotlin.state ="SettingsFragment"
                        }
                        "ChangePinFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.GONE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tsett)
                            supportFragmentManager.popBackStack()
//                            PropertiesKotlin.state ="SettingsFragment"
                        }

                        "UserDeletionFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.GONE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tsett)
                            supportFragmentManager.popBackStack()
                        }
                        "ClearPortActionFragment" -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.GONE
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tclea)
                            supportFragmentManager.popBackStack()
                        }

                        else -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val title = this.findViewById<TextView>(R.id.title_main_text)
                            title.text = getString(R.string.tmore)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                            supportFragmentManager.popBackStack()

                        }
                    }

                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setDisplayShowHomeEnabled(false)
                    supportActionBar?.setHomeButtonEnabled(false)
                    val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                    tabLayout.visibility = View.VISIBLE
//                super.onBackPressed()
                    supportFragmentManager.popBackStack()
                }
            }
        } else {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
        }
        PropertiesKotlin.state = ""
    }

    private fun handleViewPagerBackStack(): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag("f" + viewPager.currentItem)
        val title = findViewById<TextView>(R.id.title_main_text)

        when (viewPager.currentItem) {
            1 -> {
                title.text = getString(R.string.tab_port)
                if (fragment != null) {
                    when (fragment) {
                        is SupportRequestManagerFragment -> {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                        }
                        is PortPagerFragment -> {
                            title.text = getString(R.string.tab_port)
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            supportActionBar?.setDisplayShowHomeEnabled(false)
                            supportActionBar?.setHomeButtonEnabled(false)
                            val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                            tabLayout.visibility = View.VISIBLE
                        }
                    }
                }

            }
            2 -> {
                val fm: FragmentManager = supportFragmentManager
                fm.beginTransaction().replace(R.id.frag_history, HistoryFragment()).commit()
                title.text = getString(R.string.tab_hist)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)
                supportActionBar?.setHomeButtonEnabled(false)
                val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                tabLayout.visibility = View.VISIBLE

            }
            3 -> {

                when (getCurrentFragment(supportFragmentManager, viewPager)) {
                    is ClearPortFragment -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                        tabLayout.visibility = View.VISIBLE
                        title.text = getString(R.string.tmore)
                    }
                    is ClearPortActionFragment -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)

                        val selectOrders: RadioGroup = this.findViewById(R.id.clear_port_sl_order)
                        selectOrders.visibility = View.INVISIBLE
                        title.visibility = View.VISIBLE
                        title.text = getString(R.string.tclea)
                    }
                    is NewsDetailFragment -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        title.text = getString(R.string.tnews)

                    }
                    is SettingsFragment -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        val selectOrders: RadioGroup = this.findViewById(R.id.clear_port_sl_order)
                        selectOrders.visibility = View.INVISIBLE

                        val title = this.findViewById<TextView>(R.id.title_main_text)
                        title.text = getString(R.string.tmore)
                        recreate()
                        // setLocale(language)
                    }
                    is HomeFragment -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)

                        val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                        tabLayout.visibility = View.VISIBLE
                        val selectOrders: RadioGroup = this.findViewById(R.id.clear_port_sl_order)
                        selectOrders.visibility = View.INVISIBLE

                        title.text = getString(R.string.thome)

                    }
                    is ChangePasswordFragment -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.setDisplayShowHomeEnabled(true)
                        supportActionBar?.setHomeButtonEnabled(true)
                        val selectOrders: RadioGroup = this.findViewById(R.id.clear_port_sl_order)
                        selectOrders.visibility = View.INVISIBLE

                        val title = this.findViewById<TextView>(R.id.title_main_text)
                        title.text = getString(R.string.tsett)
                    }
                    is ChangePinFragment -> {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        supportActionBar?.setDisplayShowHomeEnabled(true)
                        supportActionBar?.setHomeButtonEnabled(true)
                        val selectOrders: RadioGroup = this.findViewById(R.id.clear_port_sl_order)
                        selectOrders.visibility = View.INVISIBLE

                        val title = this.findViewById<TextView>(R.id.title_main_text)
                        title.text = getString(R.string.tsett)
                    }
                    else -> {
//                        val fm: FragmentManager = supportFragmentManager
//                        fm.beginTransaction().replace(R.id.frag_more, MoreFragment()).commit()
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        supportActionBar?.setDisplayShowHomeEnabled(false)
                        supportActionBar?.setHomeButtonEnabled(false)
                        val selectOrders: RadioGroup = this.findViewById(R.id.clear_port_sl_order)
                        selectOrders.visibility = View.INVISIBLE
                        val tabLayout = this.findViewById<TabLayout>(R.id.tabs)
                        tabLayout.visibility = View.VISIBLE
                        title.text = getString(R.string.tmore)

                    }
                }

            }
            else -> {
                when (PropertiesKotlin.state) {
                    "PriceAlertFragment" -> {

                    }
                    "NewsDetailFragment" -> {

                    }
                    else -> {
                        title.text = getString(R.string.thome)
                        finish()
                        startActivity(intent)
                    }
                }

            }
        }
        return fragment?.let { handleNestedFragmentBackStack(fragment.childFragmentManager) }
            ?: run { false }
    }

    private fun getCurrentFragment(
        fragmentManager: FragmentManager,
        viewPager: ViewPager2
    ): Fragment? {
        val fragmentList = fragmentManager.fragments
        val currentSize = fragmentList.size
        val maxSize = viewPager.adapter?.itemCount ?: 0
        val lastPosition = if (maxSize > 0) maxSize - 1 else 0
        val position = viewPager.currentItem
        var offset = viewPager.offscreenPageLimit
        if (offset < 0) {
            offset = 0
        }

        return when {
            maxSize == 0 -> null
            position <= offset -> fragmentList[position]
            position == lastPosition -> fragmentList[currentSize - 1]
            position > lastPosition - offset -> fragmentList[maxSize - offset]

            currentSize < maxSize -> fragmentList[offset]
            currentSize >= maxSize -> fragmentList[position]
            else -> null
        }
    }

    //    private fun handleNestedFragmentBackStack(fragmentManager: FragmentManager): Boolean {
//        val childFragmentList = fragmentManager.fragments
//        if (childFragmentList.size > 0) {
//            for (index in childFragmentList.size - 1 downTo 0) {
//                val fragment = childFragmentList[index]
//                return when (handleNestedFragmentBackStack(fragment.childFragmentManager)) {
//                    true -> {
//                        if (fragmentManager.backStackEntryCount > 0) {
//                            fragmentManager.popBackStack()
//                            true
//                        }
//                        true
//                    }
//                    false -> {
//                        false
//                    }
//                }
//            }
//        }
//        return false
//    }
    private fun handleNestedFragmentBackStack(fragmentManager: FragmentManager): Boolean {
        val childFragmentList = fragmentManager.fragments

        if (childFragmentList.size > 0) {
            for (index in childFragmentList.size - 1 downTo 0) {
                val fragment = childFragmentList[index]
                return when (handleNestedFragmentBackStack(fragment.childFragmentManager)) {
                    true -> {
                        if (fragmentManager.backStackEntryCount > 1) {
                            fragmentManager.popBackStack()
                            true
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
        return false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.home_menu, menu)
        return true
    }


    @SuppressLint("Range")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                //if (fm.backStackEntryCount > 0) {
                onBackPressed()
                //if (fm.findFragmentByTag("SIGN_IN") is SignInFragment) {
                //supportActionBar?.setDisplayHomeAsUpEnabled(false)
                //titles?.text = getString(R.string.app_name)
                //}
                return true
                //}
            }
            R.id.to_call_hot_line -> {


                val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1
                val mIntent = Intent(Intent.ACTION_CALL)
                val number = "tel:" + PropertiesKotlin.phoneNumber
                mIntent.data = Uri.parse(number)
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CALL_PHONE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.CALL_PHONE),
                        MY_PERMISSIONS_REQUEST_CALL_PHONE
                    )

                    // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                } else {
                    //You already have permission
                    try {

                        val builder: AlertDialog.Builder? = this.let {
                            AlertDialog.Builder(it)
                        }
                        builder?.setTitle(getString(R.string.btn_call_hotline))
                            ?.setMessage(PropertiesKotlin.phoneNumber)
                            ?.setPositiveButton(getString(R.string.btn_call),
                                DialogInterface.OnClickListener { dialog, id ->
                                    startActivity(mIntent)
                                    builder.create().dismiss()
                                })
                            ?.setNegativeButton(getString(R.string.btn_close),
                                DialogInterface.OnClickListener { dialog, id ->
                                    builder.create().dismiss()
                                })
                        builder?.create()?.show()


                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
            R.id.to_edit_product -> {
//
//                supportFragmentManager.beginTransaction().setCustomAnimations(
//                    R.anim.slide_in_left,
//                    R.anim.slide_out_left,
//                    R.anim.slide_out_right,
//                    R.anim.slide_in_right
//                ).replace(R.id.frag_home, ManageFragment(), "MANAGE")
//                    .addToBackStack(null).commit()

                supportFragmentManager.beginTransaction().replace(R.id.frag_home, ManageFragment())
                    .addToBackStack(null).commit()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)

                return true
            }
            R.id.to_price_alert -> {
//                supportFragmentManager.beginTransaction().setCustomAnimations(
//                    R.anim.slide_in_left,
//                    R.anim.slide_out_left,
//                    R.anim.slide_out_right,
//                    R.anim.slide_in_right
//                ).replace(R.id.frag_home, PriceAlertFragment(), "PRICEALERT")
//                    .addToBackStack(null).commit()
                supportFragmentManager.beginTransaction().replace(R.id.frag_home, PriceAlertFragment())
                    .addToBackStack(null).commit()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                titles.text = getString(R.string.manage)
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun getFragmentRefreshListener(): FragmentRefreshListener? {
        return fragmentRefreshListener
    }

    fun setFragmentRefreshListener(fragmentRefreshListener: FragmentRefreshListener?) {
        this.fragmentRefreshListener = fragmentRefreshListener
    }

    interface FragmentRefreshListener {
        fun onRefresh()
    }

//    override fun onUserInteraction() {
//        super.onUserInteraction()
//        startHandler();
//    }

    private fun stopHandler() {
        r?.let { handler?.removeCallbacks(it) }
    }

    private fun startHandler() {
        r?.let { handler?.removeCallbacks(it) }
        var sharedPreferences: SharedPreferences? =
            getSharedPreferences(
                getString(R.string.key_prefs_session_initialized),
                Context.MODE_PRIVATE
            )
        var checkAlwaysOn = sharedPreferences?.getBoolean("ALWAYS_ON", false)

        TIMEOUT_IN_MILLI = if (checkAlwaysOn == true) {
            172800
        } else {
            150000
        }
        r?.let { handler?.postDelayed(it, TIMEOUT_IN_MILLI.toLong()) }
    }


    companion object {
        var NOTIFICATION_ID = 1
        private val instance: HomeActivity? = null
        fun getInstance(): HomeActivity? {
            return instance
        }

        lateinit var appContext: Context
    }


    protected fun registerReceiver() {
        //   LogUtil.debug(TAG, "Register Event & Pricing Receiver Service")
        LocalBroadcastManager.getInstance(this).registerReceiver(
            eventReceiver,
            IntentFilter(MobileEventService.FORCE_LOGOUT_NOTIFICATION)
        )
    }

    protected fun unRegisterReceiver() {
        // LogUtil.debug(TAG, "Unregister Pricing Receiver Service")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(eventReceiver)
    }


    @Throws(IOException::class)
    fun getBytes(ist: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (ist.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }

    fun isFingerprintAvailable(context: Context): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        return fingerprintManager.hasEnrolledFingerprints()
    }

    fun onCheckAccessScan(onItemSelectListener: OnItemSelectListener) {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                val sharedPreferences: SharedPreferences = getSharedPreferences(
                    getString(R.string.key_prefs_session_initialized),
                    Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    value = false

                    SettingsFragment.checkScanAccess = false
                    editor.putBoolean("SCAN_ACCESS", false)
                    editor.apply()
                    onItemSelectListener.onCheckSwitch(false)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    value = true
                    SettingsFragment.checkScanAccess = true
                    editor.putBoolean("SCAN_ACCESS", true)
                    editor.apply()
                    onItemSelectListener.onCheckSwitch(true)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    value = false
                    SettingsFragment.checkScanAccess = false
                    editor.putBoolean("SCAN_ACCESS", false)
                    editor.apply()
                    onItemSelectListener.onCheckSwitch(false)
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .setConfirmationRequired(false)
            .build()

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        biometricPrompt.authenticate(promptInfo)
    }

    fun setLocale(lang: String) {
        IntentActivity.getInstance()?.someFunction(lang, resources)
        var settingsFragment = SettingsFragment()
        supportFragmentManager.beginTransaction().add(R.id.frag_settings, settingsFragment)
            .commit()

    }

    fun setThemeMode(mDayNightMode: Int) {
        when (mDayNightMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }

        }

        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration

        res.updateConfiguration(conf, dm)
        finish()
        startActivity(intent)
    }

    fun setAlwaysOn(status: Boolean) {
        if (status) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }

    override fun onEventUpdate(eventDTO: EventDTO?) {

        if (eventDTO?.accountNumber != null) {
            var isForceLogout = false
            if (eventDTO.accountNumber!!.contains(AppConstants.DELIMITOR_JSON_TEXT_WS)) {
                if (eventDTO.accountNumber!!.contains(PropertiesKotlin.account) && eventDTO.accountNumber!!.contains(
                        PropertiesKotlin.account
                    )
                ) {
                    isForceLogout = true
                }
            } else {
                if (eventDTO.accountNumber.equals(PropertiesKotlin.account)) {
                    isForceLogout = true
                }
            }
            if (isForceLogout) {
//                LogUtil.debug(TAG, "Start Force Logout")
                val forceLogoutAsyncTask = ForceLogoutAsyncTask(
                    this,
                    getSharedPreferences(
                        getString(R.string.key_prefs_session_initialized),
                        MODE_PRIVATE
                    ),
                    resources
                )
                forceLogoutAsyncTask.execute()
            }
        } else {
            Log.e("event : ", "accountNumber is null")
        }
    }

    private class ForceLogoutAsyncTask  //this.preferences = preferences;
        (
        private val mContext: Context,
        preferences: SharedPreferences, //private SharedPreferences preferences;
        private val resources: Resources
    ) :
        AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            (mContext as HomeActivity).removeAllFragment()
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            try {
                PropertiesKotlin().resetAuthenDataPin(mContext)
                ServiceApiSpot.logout(mContext)
            } catch (ex: Exception) {
                // LogUtil.error(TAG, "Cannot clear Login Session", ex)
            }
            return null
        }

        override fun onPostExecute(response: Void?) {
            ServiceApiSpot.logout(mContext)
        }

    }

    fun removeAllFragment() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null) {
                //  LogUtil.debug(TAG, "remove fragment :", fragment)
                //                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                supportFragmentManager.beginTransaction().detach(fragment).commit()
            }
        }
    }

    protected var currentTab: TabMenu? = null
    private var stateChangeListener: OnStateChangeListener? = null
    private var navigationIconListener: OnNavigationIconListener? = null
    private var backPressedListener: OnBackPressedListener? = null
    private var actionResultListener: OnActionResultListener? = null
    private var permissionCallbackListener: OnResultPermissionCallbackListener? = null
    private var rightMenuListener: OnRightMenuListener? = null
    fun navigationToolbarListener(v: View?) {
        if (currentTab === TabMenu.TAB_MORE || currentTab === TabMenu.TAB_HISTORY || currentTab === TabMenu.TAB_PORTFOLIO) {
//           LogUtil.debug(TAG, "in if")
            stateChangeListener?.stateChange(R.id.backButton)
        } else if (currentTab === TabMenu.TAB_HOME) {
            //  LogUtil.debug(TAG, "in else")
            navigationIconListener?.navigationEvent(v)
        }
    }

    fun setListener(fragment: Fragment?) {
        if (fragment == null) {
            //  LogUtil.debug(TAG, "fragment")
            return
        }
        // LogUtil.debug(TAG, "setListener : {}", fragment.javaClass.name)
        if (currentFragment?.equals(fragment) == true) {
            stateChangeListener = fragment as OnStateChangeListener
            if (fragment is OnActionResultListener) {
                actionResultListener = fragment
            }
            if (fragment is OnNavigationIconListener) {
                navigationIconListener = fragment
            }
            if (fragment is OnResultPermissionCallbackListener) {
                permissionCallbackListener = fragment
            }
            if (fragment is OnRightMenuListener) {
                rightMenuListener = fragment
            }
            backPressedListener = if (fragment is OnBackPressedListener) {
                fragment
            } else {
                null
            }
            //  LogUtil.debug(TAG, "Attach Complete")
        }
    }

    fun requestCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            myCameraPermission()
        } else {
            //changed here
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_WRITE_PERMISSION
                )
            }
        }

    }

    fun myCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else { //changed here
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }
    }

    private fun openCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val root = File("${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}")
        val dir = File(root.absolutePath.toString())
        dir.mkdirs()
        // Create an image file name
        var file: File? = null

        file = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            dir /* directory */
        )
        imageUri = FileProvider.getUriForFile(

            this, "com.clevel.goldinvest.android.provider",
            file
        )
        imgPath = file.absolutePath
//
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode !== RESULT_OK) return


        if (requestCode === IMAGE_CAPTURE_CODE) {
            addPhotoToGallery()
            val inputStream: InputStream? =
                imageUri?.let { contentResolver.openInputStream(it) }
            val image = BitmapFactory.decodeStream(inputStream)
            imageUri?.path?.let {}
            val f = File(imgPath)
            val contentUri = Uri.fromFile(f)

            val builder: AlertDialog.Builder? = this.let {
                AlertDialog.Builder(it)
            }



            builder?.setTitle(getString(R.string.msg_confirm_send))
                ?.setPositiveButton(getString(R.string.btn_confirm),
                    DialogInterface.OnClickListener { dialog, id ->
                        contentUri?.let {
                            callUploadImageService(it)
                        }
                        builder.create().dismiss()
                    })
                ?.setNegativeButton(getString(R.string.btn_close),
                    DialogInterface.OnClickListener { dialog, id ->
                        builder.create().dismiss()
                    })

            val dialogLayout: View = layoutInflater.inflate(R.layout.dialog_payment, null)
            builder?.setView(dialogLayout)

            val imageViewA = dialogLayout.findViewById<View>(R.id.goProDialogImagea) as ImageView
            val resizeImage = getResizedBitmap(image, 1000)
            imageViewA.setImageBitmap(resizeImage)
            builder?.create()?.show()

        } else {
            if (null == data) return
            if (requestCode === GALLERY_INTENT_CALLED) {
                imageUri = data.data
            } else if (requestCode === GALLERY_KITKAT_INTENT_CALLED) {
                imageUri = data.data
                var takeFlags: Int = (data.flags
                        and (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                contentResolver.takePersistableUriPermission(
                    imageUri!!,
                    takeFlags
                )
            }

            val inputStream: InputStream? =
                imageUri?.let { contentResolver.openInputStream(it) }
            val image = BitmapFactory.decodeStream(inputStream)
            val builder: AlertDialog.Builder? = this.let {
                AlertDialog.Builder(it)
            }

            builder?.setTitle(getString(R.string.msg_confirm_send))
                ?.setPositiveButton(getString(R.string.btn_confirm),
                    DialogInterface.OnClickListener { dialog, id ->
                        imageUri?.let { getUpload(it) }

                        builder.create().dismiss()
                    })
                ?.setNegativeButton(getString(R.string.btn_close),
                    DialogInterface.OnClickListener { dialog, id ->
                        builder.create().dismiss()
                    })


            val dialogLayout: View = layoutInflater.inflate(R.layout.dialog_payment, null)
            builder?.setView(dialogLayout)

            val imageViewA = dialogLayout.findViewById<View>(R.id.goProDialogImagea) as ImageView
            val resizeImage = getResizedBitmap(image, 1000)
            imageViewA.setImageBitmap(resizeImage)
            builder?.create()?.show()


        }
    }

    protected fun addPhotoToGallery() {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imgPath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }

    private fun callUploadImageService(fileUri: Uri) {
        UploadUtility(this).uploadFile(fileUri, this)

        imageUri = null
        imgPath = null
    }

    private fun getUpload(fileUri: Uri) {
        UploadUtility(this).uploadFile(fileUri, this)
        imageUri = null
        imgPath = null
    }


    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun requestGalleryPermission() {
        if (this.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )

        } else {
            openGalleryForImage()
        }
    }


    fun openGalleryForImage() {
        if (Build.VERSION.SDK_INT < 19) {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_INTENT_CALLED)

        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/jpeg"
            startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED)
        }
    }

    override fun onResume() {
        super.onResume()

        delegate.applyDayNight()

        if (viewPager != null && viewPager.adapter != null) {
            viewPager.adapter!!.notifyDataSetChanged()
        }

        PropertiesKotlin.manageSocket(this)
        MobileEventReceiver.onEventUpdateListener = this
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterReceiver()
        PropertiesKotlin.stopAllBackGroundService(this)

    }

    override fun onPause() {
        super.onPause()
        unRegisterReceiver()
        PropertiesKotlin.stopAllBackGroundService(this)
    }


}

enum class TabMenu {
    TAB_HOME(R.drawable.ic_v_home, R.string.tab_home, R.id.tab_home, HomeFragment.newInstance()),
    TAB_PORTFOLIO(
        R.drawable.ic_v_chart,
        R.string.tab_port,
        R.id.tab_portfolio,
        PortPagerFragment.newInstance()
    ),
    TAB_HISTORY(
        R.drawable.ic_v_time,
        R.string.tab_hist,
        R.id.tab_history,
        HistoryFragment.newInstance()
    ),
    TAB_MORE(R.drawable.ic_v_more, R.string.tab_more, R.id.tab_more, MoreFragment.newInstance());

    private var tabImgId = 0
    private var labelId = 0
    private var tabId = 0
    private var fragment: MainFragment? = null

    constructor(tabImgId: Int, labelId: Int, tabId: Int, fragment: MainFragment) {
        this.tabImgId = tabImgId
        this.labelId = labelId
        this.tabId = tabId
        this.fragment = fragment
    }

    open fun getFragment(): MainFragment? {
        return fragment
    }


}
