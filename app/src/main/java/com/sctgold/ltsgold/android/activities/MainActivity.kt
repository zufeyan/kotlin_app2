package com.sctgold.ltsgold.android.activities

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import com.sctgold.android.tradeplus.util.ManageData
import com.sctgold.ltsgold.android.GoldSpotApplication
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.dto.MobileVersion
import com.sctgold.ltsgold.android.dto.VersionResponse
import com.sctgold.ltsgold.android.fragments.SignInFragment
import com.sctgold.ltsgold.android.rest.MobileFrontService
import com.sctgold.ltsgold.android.util.AppConstants
import com.sctgold.ltsgold.android.util.AppUtil
import com.sctgold.ltsgold.android.util.ApplicationLanguageHelper
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {


    private var menu: Menu? = null
    private var titles: TextView? = null
    private var fm: FragmentManager = supportFragmentManager
    private var asyncMainTask: WeakReference<MainAsyncTask>? = null

    private val NOTIFICATION_PERMISSION_CODE = 123
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    private lateinit var manager: NotificationManager
    private lateinit var sharedPreferences: SharedPreferences
    var language = ""
    val list = listOf(
        Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
    )

    var checkNightTheme: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        var select_theme = 0
        titles = this.findViewById(R.id.title_main_text)
        titles?.text = getString(R.string.app_name)
        if (savedInstanceState == null) {
            setSignInPage()
        }

        sharedPreferences =
            this.getSharedPreferences(
                getString(R.string.key_prefs_session_initialized),
                Context.MODE_PRIVATE
            )
        if (sharedPreferences != null) {

            language = sharedPreferences.getString("LANGUAGE", "EN").toString()
            checkNightTheme = sharedPreferences.getBoolean("NIGHT_THEME", false)
            select_theme = sharedPreferences.getInt("select_theme", 0)

        }
        setLocale(language)

        setThemeMode(select_theme)
        val selectOrders: RadioGroup = findViewById(R.id.clear_port_sl_order)

        selectOrders.visibility = View.GONE


//        val intent = Intent(this, MyFirebaseMessagingService::class.java)
//        startService(intent)

        PropertiesKotlin.tokenStart = ManageData().getToken().toString()
        PropertiesKotlin.deviceId = ManageData().getDeviceId(this)

    }


    private fun setSignInPage() {
        fm.beginTransaction().replace(R.id.container_main, SignInFragment(), "SIGN_IN").commit()
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflate = menuInflater
        inflate.inflate(R.menu.menu_main, menu)
        this.menu = menu
        menu.findItem(R.id.switch_language)?.title = language
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val editor: SharedPreferences.Editor? = sharedPreferences.edit()
        when (item.itemId) {
            android.R.id.home -> {
                if (fm.backStackEntryCount > 0) {
                    onBackPressed()
                    if (fm.findFragmentByTag("SIGN_IN") is SignInFragment) {
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        titles?.text = getString(R.string.app_name)
                    }
                    return true
                }
            }
            R.id.switch_language -> {
                when (item.title) {

                    "TH" -> {


                        if (setLocale("EN")) {
                            setRfresh()
                        }

                        item.title == "EN"
                        editor?.putString("LANGUAGE", "EN")
                        editor?.apply()
                    }
                    "EN" -> {
                        if (setLocale("TH")) {
                            setRfresh()
                        }
                        item.title == "TH"
                        editor?.putString("LANGUAGE", "TH")
                        editor?.apply()


                    }
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        active = true
//        LogUtil.debug(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        active = false
//        LogUtil.debug(TAG, "onStop")
    }

    companion object {
        var active = false
        fun setNotification(context: Context?, enabled: Boolean) {

            val manager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (enabled) {
                val intent = context.packageManager
                    .getLaunchIntentForPackage(context.packageName)
                if (intent != null) {
                    val pi = PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        0
                    )
//                rViews.setOnClickPendingIntent(R.id.notification_button_1, pi)
                }
                val builder: Notification.Builder = Notification.Builder(context)
                manager.notify(0, builder.build())
            } else {
                manager.cancel(0)
            }
        }

        //        const val ENGLISH = "EN"
        const val SELECTED_LANGUAGE = "language"

        //        const val THAI = "TH"
        private const val CHANNEL_ID = "1"

        private val instance: MainActivity? = null
        fun getInstance(): MainActivity? {
            return instance
        }

    }


    fun setLocale(lang: String): Boolean {
        Log.e("lang  ", lang)
        val myLocale = Locale(lang)
        Locale.setDefault(myLocale)

        val res: Resources = baseContext.resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale

        res.updateConfiguration(conf, dm)
        // baseContext.resources.updateConfiguration(conf, baseContext.resources.displayMetrics);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
//            conf.setLocale(myLocale)
//        } else{
//            conf.locale=myLocale
//        }
//
//     if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
//            applicationContext.createConfigurationContext(conf);
//        } else {
//         res.updateConfiguration(conf, dm);
//        }

        return true
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
    }

    fun setRfresh() {
        val refresh = Intent(this, MainActivity::class.java)
        finish()
        startActivity(refresh)
    }

    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
        var lang = sharedPreferences.getString("LANGUAGE", "EN")
        super.attachBaseContext(ApplicationLanguageHelper.wrap(newBase, lang!!))
    }

    override fun onResume() {
        super.onResume()

        asyncMainTask = lastNonConfigurationInstance as WeakReference<MainAsyncTask>?
        if (asyncMainTask != null && asyncMainTask!!.get() != null && asyncMainTask!!.get()!!.status != AsyncTask.Status.FINISHED
        ) {

            //     Toast.makeText(this, "attach updateResult : $select_theme", Toast.LENGTH_SHORT).show()
            asyncMainTask!!.get()!!.attach(this)
        } else {
            //      Toast.makeText(this, "dddddddddddd : $select_theme", Toast.LENGTH_SHORT).show()
            val mainAsyncTask = MainAsyncTask(this)
            asyncMainTask = WeakReference(mainAsyncTask)
            mainAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }
    }

    override fun onDestroy(){
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (fm.findFragmentByTag("SIGN_IN") is SignInFragment) {
//            Log.e("main onBackPressed  :   " ,"ssssssssssssssssssssss")
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
//            val intent = Intent(Intent.ACTION_MAIN)
//            intent.addCategory(Intent.CATEGORY_HOME)
//            startActivity(intent)
        } else {
//            Log.e("main onBackPressed  :   " ,"00000000000000000000")
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
//            super.onBackPressed()
//            titles?.text = getString(R.string.app_name)
        }

         super.onBackPressed()
    }


    fun checkPermissions() {
        if (isPermissionsGranted() != PackageManager.PERMISSION_GRANTED) {
            showAlert()
        } else {
//            activity.toast("Permissions already granted.")
        }
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need permission(s)")
        builder.setMessage("Some permissions are required to do the task.")
        builder.setPositiveButton("OK") { dialog, which ->
//            requestPermissions()
            makeRequest()
        }
        builder.setNeutralButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
            NOTIFICATION_PERMISSION_CODE
        )
    }

    // Check permissions status
    private fun isPermissionsGranted(): Int {
        // PERMISSION_GRANTED : Constant Value: 0
        // PERMISSION_DENIED : Constant Value: -1
        var counter = 0
        for (permission in list) {
            counter += ContextCompat.checkSelfPermission(this, permission)
        }
        return counter
    }


    // Find the first denied permission
    private fun deniedPermission(): String {
        for (permission in list) {
            if (ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_DENIED
            ) return permission
        }
        return ""
    }


    // Request the permissions at run time
    fun requestPermissions() {
        val permission = deniedPermission()
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            // Show an explanation asynchronously
//            this.toast("Should show an explanation.")
        } else {
            ActivityCompat.requestPermissions(
                this,
                list.toTypedArray(),
                NOTIFICATION_PERMISSION_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            NOTIFICATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryForImage()
                } else {
                }
            }

        }
    }

    fun openGalleryForImage() {
        val settingIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        settingIntent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivityForResult(settingIntent, NOTIFICATION_PERMISSION_CODE)
    }

    fun showPermissionDialog(mContext: Context) {
        val builder = AlertDialog.Builder(mContext, R.style.Dialog)
        builder.setTitle("Need Permission")
//        builder.setMessage(msg)
        builder.setPositiveButton("YES") { dialogInterface, i ->
            dialogInterface.dismiss()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", mContext.packageName, null)
            intent.data = uri
            mContext.startActivity(intent)
        }

        builder.setNegativeButton("NO") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    private var closeApplication = false
    var inForeground = false
    private fun updateResult(
        goldSpotIntent: SignInFragment,
        version: String?,
        requiredUpdate: Boolean,
        serverVersionIsNull: Boolean,
        isConnection: Boolean
    ) {

//        setThemeMode(select_theme)

        val app = GoldSpotApplication.getInstance()
        val res = resources

        val builder: AlertDialog.Builder? = this.let {
            AlertDialog.Builder(it)
        }
         if (version != null) {
            if (app.inForeground) {
                var message: String

                if (requiredUpdate) {
                    message =
                        String.format(res.getString(R.string.new_version_message_true), version)
//                    dialog = StandardDialog().showMessageDialog(
//                        this, res.getString(R.string.title_new_version), message,
//                        res.getString(R.string.new_version_confirm_button), this
//                    )!!
                    builder
                        ?.setTitle(resources.getString(R.string.title_new_version))
                        ?.setMessage(message)
                        ?.setPositiveButton(getString(R.string.new_version_confirm_button),
                            DialogInterface.OnClickListener { dialog, id ->
                                PropertiesKotlin().launchAppStore(this)

                            })
                        ?.setNegativeButton(getString(R.string.new_version_cancel_button),
                            DialogInterface.OnClickListener { dialog, id ->
                                builder.create().dismiss()
                                if (closeApplication) {
                                    finish()
                                    exitProcess(0)
                                }
                            })
                } else {
                    message =
                        String.format(res.getString(R.string.new_version_message_false), version)
                    builder
                        ?.setTitle(resources.getString(R.string.title_new_version))
                        ?.setMessage(message)
                        ?.setPositiveButton(getString(R.string.new_version_confirm_button),
                            DialogInterface.OnClickListener { dialog, id ->
                                PropertiesKotlin().launchAppStore(this)
                            })
                        ?.setNegativeButton(getString(R.string.new_version_cancel_button),
                            DialogInterface.OnClickListener { dialog, id ->
                                builder.create().dismiss()
                                if (closeApplication) {
                                    finish()
                                    exitProcess(0)
                                }
                            })
                }
                closeApplication = false
                builder?.create()?.show()
            }
        }
//                 else {
//
//             Log.e("serverVersionIsNull  " ,serverVersionIsNull.toString())
//            if (serverVersionIsNull) {
//                Log.e("sdddddddddddddddddddddddd serverVersionIsNull  " ,serverVersionIsNull.toString())
//                val message: String
//                if (isConnection) {
//                    message = res.getString(R.string.version_message_null)
//
//                    builder
//                        ?.setTitle(resources.getString(R.string.title_version_null))
//                        ?.setMessage(message)
//                        ?.setPositiveButton(getString(R.string.btn_ok),
//                            DialogInterface.OnClickListener { dialog, id ->
//                                builder.create().dismiss()
//                            })
//                    closeApplication = true
//                    builder?.create()?.show()
//                } else {
//                    builder?.create()?.dismiss()
//                 //   doLaunchService()
//                }
//            } else {
//               // builder?.create()?.dismiss()
//               // doLaunchService()
//            }
//        }
    }

    private class MainAsyncTask(activity: MainActivity) :
        AsyncTask<Bundle?, Int?, Void?>() {
        private var myWeakContext: WeakReference<MainActivity>
        private var targetIntent: IntentActivityUtils.GoldSpotIntent =
            IntentActivityUtils.GoldSpotIntent.LOGIN
        private var newVersion: String? = null
        private var forceUpdate = false
        private var serverVersionIsNull = false
        private var isConnection = true

        init {
            myWeakContext =
                WeakReference<MainActivity>(activity)
        }

        override fun doInBackground(vararg params: Bundle?): Void? {
            if (myWeakContext != null && myWeakContext.get() != null) {
                val ctx: Context? = myWeakContext.get()

                //Reset Session Prefrences when upgrade version
                val resources: Resources? = myWeakContext.get()?.resources
                val sharedPreferences: SharedPreferences? = myWeakContext.get()
                    ?.getSharedPreferences(
                        resources?.getString(R.string.key_prefs_session_initialized),
                        MODE_PRIVATE
                    )
                val appVersionName: String? = ctx?.let { PropertiesKotlin().getVersionName(it) }


//                if (!cache.isJustLogin()) {
                var retry = 0
                while (retry++ < AppConstants.CONNECTION_maxRetry) {
                    try {
                        MobileFrontService.invoke().getLatestVersion().enqueue(object :
                            retrofit2.Callback<VersionResponse> {
                            override fun onResponse(
                                call: Call<VersionResponse>,
                                response: Response<VersionResponse>
                            ) {
                                if (response.code() == 200) {

                                    var versionResponse = response.body()

                                    if (versionResponse != null) {

                                        val appVersion: MobileVersion? = AppUtil.extractVersion(
                                            appVersionName!!
                                        )
                                        val serverVersion: MobileVersion =
                                            versionResponse.mobileVersion


                                        var hasNewversion = false
                                        if (appVersion!!.getMajor() < serverVersion.getMajor()) {
                                            hasNewversion = true


                                        } else if (appVersion!!.getMajor() === serverVersion.getMajor() && appVersion!!.getMinor() < serverVersion.getMinor()) {
                                            hasNewversion = true

                                        } else if (appVersion.getMajor() === serverVersion.getMajor() && appVersion.getMinor() === serverVersion.getMinor() && appVersion.getBuildNumber() < serverVersion.getBuildNumber()) {
                                            hasNewversion = true

                                        }


                                        if (hasNewversion) {
                                            try {
                                                newVersion = java.lang.String.format(
                                                    AppConstants.FORMAT_VERSION_NAME,
                                                    serverVersion.getMajor(),
                                                    serverVersion.getMinor(),
                                                    serverVersion.getBuildNumber()
                                                )
                                            } catch (ex: java.lang.Exception) {
                                                ex.printStackTrace()
                                            }
                                            forceUpdate = serverVersion.isForceUpdate()

                                        } else {
                                            if (serverVersion.getMajor() === 0 && serverVersion.getMinor() === 0 && serverVersion.getBuildNumber() === 0) {
                                                serverVersionIsNull = true
                                            }
                                        }
                                    } else {
                                        serverVersionIsNull = true
                                    }
//
                                }
                                myWeakContext.get()?.updateResult(
                                    SignInFragment(),
                                    newVersion,
                                    forceUpdate,
                                    serverVersionIsNull,
                                    isConnection
                                )
                            }

                            override fun onFailure(call: Call<VersionResponse>, t: Throwable) {
                                Log.e("onFailure  " ,t.message.toString())
                            }
                        })


                    } catch (ex: Exception) {
                        if (retry == AppConstants.CONNECTION_maxRetry) {
                            resources?.getString(R.string.msg_dialog_noconnectivity_found)?.let {
//                                Log.e(
//                                    myWeakContext!!.get()?.TAG,
//                                    it
//                                )
                            }
                            isConnection = false
                        }
                    }
                }
//
//                }
                val prefVersionName = sharedPreferences?.getString(
                    resources?.getString(R.string.key_pref_versionname),
                    "0.0.0"
                )
                if (prefVersionName != appVersionName) {
                    val editor = sharedPreferences?.edit()
                    if (resources != null) {
                        editor?.putString(
                            resources.getString(R.string.key_pref_versionname),
                            appVersionName
                        )
                    }
                    editor?.commit()
                }
//                if (!AppUtil.isEmpty(cache.getToken()) && !AppUtil.isEmpty(cache.getAccountId(ctx))
//                    && !AppUtil.isEmpty(cache.getPin()) && !AppUtil.isEmpty(cache.getGrade())
//                ) {
//                    targetIntent = GoldSpotIntent.HOME
//                    val tokenUtil = TokenUtil()
//                    val encryptToken: String = EncryptUtil.encryptForRequest(
//                        tokenUtil.getDefaultToken(
//                            cache.getBranchCode(),
//                            cache.getAndroidId(),
//                            cache.getToken()
//                        ), cache.getPublicKey()
//                    )
//                    val mobileFrontService: MobileFrontService =
//                        MobileRestServiceGenerator.createAPIService()
//                    var retry = 0
//                    var notificationResponse: NotificationResponse? = null
//                    while (retry++ < AppConstants.CONNECTION_maxRetry && notificationResponse == null) {
//                        try {
//                            notificationResponse =
//                                mobileFrontService.getNotifications(encryptToken, 0, 1)
//                        } catch (ex: Exception) {
//                            if (retry == AppConstants.CONNECTION_maxRetry) {
//                                Log.e(
//                                    myWeakContext!!.get()?.TAG,
//                                    "No connectivity found: {}",
//                                    ex
//                                )
//                            }
//                        }
//                    }
//                    if (notificationResponse != null) {
//                        cache.setUnNotificationRead(notificationResponse.getUnNotificationRead())
//                    }
//                }
//                if (myWeakContext != null && myWeakContext!!.get() != null) {

//                }
            }
//            ThreadUtil.sleepForInSecs(1)
            return null
        }

        override fun onPostExecute(result: Void?) {

//            Log.e("aaaaaaaaaaaaaaaaaaaaaaaaa  ", "onPostExecute")
//            Log.e("onPostExecute zzzzzzzzzzzzzzzzz  :", newVersion.toString())
//            if (myWeakContext != null && myWeakContext!!.get() != null) {
//                myWeakContext!!.get()?.updateResult(
//                    SignInFragment(),
//                    newVersion,
//                    forceUpdate,
//                    serverVersionIsNull,
//                    isConnection
//                )
//            }
        }

        fun attach(activity: MainActivity) {
            myWeakContext =
                WeakReference<MainActivity>(activity)
        }
    }

}

