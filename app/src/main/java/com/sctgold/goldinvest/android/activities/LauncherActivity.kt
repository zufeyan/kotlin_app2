package com.sctgold.goldinvest.android.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.util.ApplicationLanguageHelper
import java.text.SimpleDateFormat
import java.util.*


class LauncherActivity : AppCompatActivity() {

    private val TAG = "LUANCH"
    private val NOTIFICATION_PERMISSION_CODE = 123
//    private var asyncMainTask: WeakReference<MainAsyncTask>? = null
    private lateinit var sharedPreferences: SharedPreferences
    var select_theme = 0
    private var requiredUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setContentView(R.layout.activity_launcher)

    }


    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) == PackageManager.PERMISSION_GRANTED
        ) return
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
        ) {
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
            NOTIFICATION_PERMISSION_CODE
        )

    }


    override fun attachBaseContext(newBase: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase)
        var lang = sharedPreferences.getString("LANGUAGE", "EN")
        super.attachBaseContext(ApplicationLanguageHelper.wrap(newBase, lang!!))
    }

    override fun onPause() {
        super.onPause()
        finish()
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

    override fun onResume() {
        super.onResume()
        sharedPreferences = getSharedPreferences(
            getString(R.string.key_prefs_session_initialized),
            Context.MODE_PRIVATE
        )
        select_theme = sharedPreferences.getInt("select_theme", 0)

        setThemeMode(select_theme)
        this.getPreferences(MODE_PRIVATE).getString("fb", "empty :(")?.let { Log.d("newToken", it) }

        val calendar = Calendar.getInstance()
        val current = calendar.time
        val df = SimpleDateFormat("yyyy", Locale.US)
        val credit = "\u00A9 " + 2014 + " \u2212 " + df.format(current) + " Powered by SCT Gold."

        val cr = findViewById<TextView>(R.id.copyrights)
        cr.text = credit

//        val timerRun = Thread() {
//            try {
//                sleep(3000)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            } finally {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
//            }
//        }
//        timerRun.start()

    }


}
