package com.sctgold.wgold.android.fragments

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.activities.HomeActivity
import com.sctgold.wgold.android.util.OnItemSelectListener
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.row_seclet_theme.view.*
import kotlinx.android.synthetic.main.row_set_switch.view.*
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList
import kotlin.random.Random
import android.widget.RadioButton
import com.sctgold.wgold.android.activities.MainActivity


// TODO: Rename parameter argume\nts, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var settingsView: RecyclerView

    private var checkAccount: Boolean = false
    var checkPin: Boolean = false

    private var checkAlwaysOn: Boolean = false
    var checkNightTheme: Boolean = false
    var userAcc: String = ""
    var language: String = ""


    lateinit var onItemSelectListener: OnItemSelectListener

    private var mDayNightMode = 0
    var state: String = "SettingsFragment"


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
        val v = inflater.inflate(R.layout.fragment_settings, container, false)

        settingsView = v.findViewById(R.id.settings_view)

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(getString(R.string.key_prefs_session_initialized), Context.MODE_PRIVATE)
        checkAccount = sharedPreferences.getBoolean("USER_REMEMBER", false)
        checkPin = sharedPreferences.getBoolean("PIN_REMEMBER", false)
        checkScanAccess = sharedPreferences.getBoolean("SCAN_ACCESS", false)
        checkAlwaysOn = sharedPreferences.getBoolean("ALWAYS_ON", false)
        checkNightTheme = sharedPreferences.getBoolean("NIGHT_THEME", false)
        userAcc = PropertiesKotlin.account
        language = sharedPreferences.getString("LANGUAGE", "EN").toString()
        select_theme = sharedPreferences.getInt("select_theme", 0)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsView.apply {
            settingsView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            settingsView.itemAnimator = DefaultItemAnimator()
            adapter = ListSettingsAdapter(getSettingsLists(), context)
            { partItem: Boolean ->
                onItemSelectListener.onCheckSwitch(
                    partItem
                )
            }
        }

    }

    data class SetHeader(var header: String)
    data class SetSwitch(var topic: String, var label: String, var check: Boolean)
    data class SetChange(var topic: String, var label: String)
    data class SetAccount(var topic: String, var account: String)
    data class SetTheme(var topic: String, var label: String, var check: Int)
    data class SetLang(var topic: String, var label: String, var check: String)

    val i: ArrayList<Any> = ArrayList()
    private fun getSettingsLists(): ArrayList<Any> {


        val minAccount = 1001000
        val maxAccount = 9999999
        val rdAccount: Int = Random.nextInt(minAccount, maxAccount)

        i.add(SetHeader(getString(R.string.ms_sec1)))
        i.add(SetSwitch(getString(R.string.user), getString(R.string.sub_user), checkAccount))
        i.add(SetSwitch(getString(R.string.tpin), getString(R.string.sub_pin), checkPin))
        i.add(SetSwitch(getString(R.string.bio), getString(R.string.sub_bio), checkScanAccess))
        i.add(SetSwitch(getString(R.string.always), getString(R.string.sub_always), checkAlwaysOn))
        i.add(
            SetTheme(
                getString(R.string.dark_mode),
                getString(R.string.sub_dark_mode),
                select_theme
            )
        )
        i.add(
            SetLang(
                getString(R.string.tlang), getString(R.string.sub_lang), language
            )
        )
        i.add(SetHeader(getString(R.string.ms_sec2)))
        i.add(SetChange(getString(R.string.cpass), getString(R.string.sub_cpass)))
        i.add(SetChange(getString(R.string.cpin), getString(R.string.sub_cpin)))
        i.add(SetHeader(getString(R.string.ms_sec3)))
        i.add(SetAccount(getString(R.string.sub_account), userAcc))
        return i
    }

    class SetHeaderViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_set_header, parent, false)) {
        private var wgHeaderSection: TextView? = null

        init {
            wgHeaderSection = itemView.findViewById(R.id.settings_header)
        }

        fun bind(header: SetHeader) {
            val v: View = this.itemView
            wgHeaderSection?.text = header.header.uppercase(Locale.ROOT)
        }
    }

    class SetSwitchViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_set_switch, parent, false)) {
        private var wgTopic: TextView? = null
        private var wgSub: TextView? = null
        private var wgSwitch: SwitchCompat? = null

        init {
            wgTopic = itemView.findViewById(R.id.settings_switch_topic)
            wgSub = itemView.findViewById(R.id.settings_switch_sub)
            wgSwitch = itemView.findViewById(R.id.settings_switch_check)
        }

        fun bind(data: SetSwitch) {
            val v: View = this.itemView
            wgTopic?.text = data.topic
            wgSub?.text = data.label
            wgSwitch?.isChecked = data.check
        }


    }

    class SetSelectsThemeViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        val context: Context
    ) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_seclet_theme, parent, false)) {

        private val listTheme = arrayOf(
            "Auto", "Light", "Dark"
        )
        private var wgTopic: TextView? = null
        private var wgSub: TextView? = null
        private var wgLists: RadioGroup? = null
        private var auto: RadioButton? = null
        private var light: RadioButton? = null
        private var dark: RadioButton? = null

        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(context.resources.getString(R.string.key_prefs_session_initialized), Context.MODE_PRIVATE)
        private val editor: SharedPreferences.Editor? = sharedPreferences.edit()

        init {
            wgTopic = itemView.findViewById(R.id.settings_select_theme_topic)
            wgSub = itemView.findViewById(R.id.settings_select_theme_sub)
            wgLists = itemView.findViewById(R.id.select_theme)
            auto = itemView.findViewById(R.id.select_auto)
            light = itemView.findViewById(R.id.select_light)
            dark = itemView.findViewById(R.id.select_dark)
        }

        val white = ContextCompat.getColor(context.applicationContext, R.color.white)
        val tint = ContextCompat.getColor(context.applicationContext, R.color.tint)

        @SuppressLint("ResourceType")
        fun bind(data: SetTheme) {
            val v: View = this.itemView
            wgTopic?.text = data.topic
            wgSub?.text = data.label
            val uiManager: UiModeManager =
                context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

            when (data.check) {
                0 -> {
                        if (uiManager.nightMode == 1) {
                            auto?.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                            light?.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM1))
                            dark?.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM1))
                        } else {
                            //system dark
                            auto?.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                            light?.setTextColor( Color.parseColor(PropertiesKotlin.colorDarkM1))
                            dark?.setTextColor( Color.parseColor(PropertiesKotlin.colorDarkM1))
                        }
                        wgLists?.check(R.id.select_auto)
                    }


                1 -> {
                    auto?.setTextColor(Color.parseColor(PropertiesKotlin.colorMainM1))
                    light?.setTextColor( Color.parseColor(PropertiesKotlin.colorBlack))
                    dark?.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM1))
                    wgLists?.check(R.id.select_light)
                }
                2 -> {
                    auto?.setTextColor(Color.parseColor(PropertiesKotlin.colorDarkM1))
                    light?.setTextColor( Color.parseColor(PropertiesKotlin.colorDarkM1))
                    dark?.setTextColor( Color.parseColor(PropertiesKotlin.colorBlack))
                    wgLists?.check(R.id.select_dark)
                }
            }
            wgLists?.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.select_auto -> {
                        (context as HomeActivity).setThemeMode(0)
                        editor?.putInt("select_theme", 0)
                        editor?.apply()

                    }
                    R.id.select_light -> {
                        (context as HomeActivity).setThemeMode(1)
                        editor?.putInt("select_theme", 1)
                        editor?.apply()
                    }
                    R.id.select_dark -> {
                        (context as HomeActivity).setThemeMode(2)
                        editor?.putInt("select_theme", 2)
                        editor?.apply()

                    }

                }

            }

        }
    }


    class SetSelectsLanguageViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        val context: Context
    ) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_seclet_language, parent, false)) {
        private var wgTopic: TextView? = null
        private var wgSub: TextView? = null
        private var wgLists: RadioGroup? = null
        private var thRdo: RadioButton
        private var enRdo: RadioButton

        val white = ContextCompat.getColor(context.applicationContext, R.color.white)
        val tint = ContextCompat.getColor(context.applicationContext, R.color.tint)


        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(context.resources.getString(R.string.key_prefs_session_initialized), Context.MODE_PRIVATE)
        private val editor: SharedPreferences.Editor? = sharedPreferences.edit()

        init {
            wgTopic = itemView.findViewById(R.id.settings_select_lang_topic)
            wgSub = itemView.findViewById(R.id.settings_select_lang_sub)
            wgLists = itemView.findViewById(R.id.select_language)
            thRdo = itemView.findViewById(R.id.select_th)
            enRdo = itemView.findViewById(R.id.select_en)
        }

        @SuppressLint("ResourceType")
        fun bind(data: SetLang) {
            val v: View = this.itemView
            wgTopic?.text = data.topic
            wgSub?.text = data.label
            val uiManager: UiModeManager =
                context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            when (data.check) {
                "TH" -> {

                    if(select_theme == 0){
                        if (uiManager.nightMode == 1) {
                            thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                            enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM1))
                        } else {
                            //system dark
                            thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                            enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorDarkM1))
                        }
                    }else  if(select_theme == 1){

                        thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                        enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM1))

                    }else{
                        thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                        enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorDarkM1))

                    }

                    wgLists?.check(R.id.select_th)
                }
                "EN" -> {
                    if(select_theme == 0){
                        if (uiManager.nightMode == 1) {
                            thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorMainM1))
                            enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorBlack))
                        } else {
                            //system dark
                            thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorDarkM1))
                            enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorBlack))
                        }
                    }else  if(select_theme == 1){

                        thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorMainM1))
                        enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorBlack))

                    }else{
                        thRdo.setTextColor(Color.parseColor(PropertiesKotlin.colorDarkM1))
                        enRdo.setTextColor( Color.parseColor(PropertiesKotlin.colorBlack))

                    }
                    wgLists?.check(R.id.select_en)
                }
            }
            wgLists?.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.select_th -> {
                        val config = Configuration()
                        var locale: Locale? = null
                        locale = Locale("TH")
                        Locale.setDefault(locale)
                        config.locale = locale
                        context.resources.updateConfiguration(
                            config,
                            context.resources.displayMetrics
                        )
                        (context as HomeActivity).setLocale("TH")
                        editor?.putString("LANGUAGE", "TH")
                        editor?.apply()
                    }
                    R.id.select_en -> {
                        val config = Configuration()
                        var locale: Locale? = null
                        locale = Locale("EN")
                        Locale.setDefault(locale)
                        config.locale = locale
                        context.resources.updateConfiguration(
                            config,
                            context.resources.displayMetrics
                        )
                        (context as HomeActivity).setLocale("EN")
                        editor?.putString("LANGUAGE", "EN")
                        editor?.apply()
                    }
                }
            }
        }
    }


    class SetChangeViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_set_change, parent, false)) {
        private var wgTopic: TextView? = null
        private var wgSub: TextView? = null

        init {
            wgTopic = itemView.findViewById(R.id.settings_change_topic)
            wgSub = itemView.findViewById(R.id.settings_change_sub)
        }

        fun bind(data: SetChange) {
            val v: View = this.itemView
            wgTopic?.text = data.topic
            wgSub?.text = data.label
        }
    }

    class SetAccountViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_set_account, parent, false)) {
        private var wgTopic: TextView? = null
        private var wgAccount: TextView? = null

        init {
            wgTopic = itemView.findViewById(R.id.settings_account_topic)
            wgAccount = itemView.findViewById(R.id.settings_account_value)
        }

        fun bind(data: SetAccount) {
            val v: View = this.itemView
            wgTopic?.text = data.topic
            wgAccount?.text = data.account
            wgAccount?.setTextColor(ContextCompat.getColor(v.context, R.color.tint))
        }
    }

    internal interface OnRadioChangeListener {
        fun onRadioChange(radioGroup: RadioGroup?, checkedId: Int)
    }

    private class ListSettingsAdapter(
        val list: List<Any>,
        val context: Context,
        val clickListener: (Boolean) -> Unit
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        private val sections: Int = 0
        private val switches: Int = 1
        private val theme: Int = 2
        private val lang: Int = 3
        private val changes: Int = 4
        private val accounts: Int = 5


        override fun getItemCount() = list.size

        override fun getItemViewType(position: Int): Int {
            when (list[position]) {
                is SetHeader -> {
                    return sections
                }
                is SetSwitch -> {
                    return switches
                }
                is SetTheme -> {
                    return theme
                }
                is SetLang -> {
                    return lang
                }
                is SetChange -> {
                    return changes
                }
                is SetAccount -> {
                    return accounts
                }

            }
            return sections
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                sections -> {
                    return SetHeaderViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
                switches -> {
                    return SetSwitchViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
                theme -> {
                    return SetSelectsThemeViewHolder(
                        LayoutInflater.from(parent.context),
                        parent, context
                    )
                }
                lang -> {
                    return SetSelectsLanguageViewHolder(
                        LayoutInflater.from(parent.context),
                        parent, context
                    )
                }
                changes -> {
                    return SetChangeViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
                accounts -> {
                    return SetAccountViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
            }
            return SetHeaderViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                sections -> {
                    val v = holder as SetHeaderViewHolder
                    configureHeader(v, position)
                }
                switches -> {
                    val v = holder as SetSwitchViewHolder
                    configureSwitch(v, position)
                }
                theme -> {
                    val v = holder as SetSelectsThemeViewHolder
                    configureTheme(v, position)
                }
                lang -> {
                    val v = holder as SetSelectsLanguageViewHolder
                    configureLang(v, position)
                }
                changes -> {
                    val v = holder as SetChangeViewHolder
                    configureChange(v, position)
                }
                accounts -> {
                    val v = holder as SetAccountViewHolder
                    configureAccount(v, position)
                }
            }
        }

        private fun configureHeader(holder: SetHeaderViewHolder, position: Int) {
            val items: SetHeader = list[position] as SetHeader
            holder.bind(SetHeader(items.header))
        }

        private lateinit var executor: Executor
        private lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
        private lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo
        private val onRadioChangeListener: OnRadioChangeListener? = null

        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(context.resources.getString(R.string.key_prefs_session_initialized), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        @SuppressLint("ClickableViewAccessibility")
        private fun configureSwitch(holder: SetSwitchViewHolder, position: Int) {
            val items: SetSwitch = list[position] as SetSwitch
            holder.bind(SetSwitch(items.topic, items.label, items.check))



            when (position) {

                1 -> {
                    var isTouched = false
                    holder.itemView.settings_switch_check.setOnTouchListener(OnTouchListener { v, event ->
                        if (event.action == MotionEvent.ACTION_DOWN) {
                            holder.itemView.parent.requestDisallowInterceptTouchEvent(true)
                        }
                        isTouched = true

                        // always return false since we don't care about handling tapping, flinging, etc.
                        false
                    })
                    holder.itemView.settings_switch_check.setOnCheckedChangeListener { _, isChecked ->
                        if (isTouched) {
                            isTouched = false
                            editor.putBoolean("USER_REMEMBER", isChecked)
                            editor.apply()

                        }

                    }

                }

                2 -> {
                    holder.itemView.settings_switch_check.setOnCheckedChangeListener { _, isChecked ->
                        editor.putBoolean("PIN_REMEMBER", isChecked)
                        editor.apply()
                    }
                }
                3 -> {
                    var isFingerprintAvailable = HomeActivity().isFingerprintAvailable(context as HomeActivity)
                    holder.itemView.settings_switch_check.isClickable = isFingerprintAvailable
                    holder.itemView.settings_switch_check.setOnCheckedChangeListener { _, isChecked ->
                        editor.putBoolean("SCAN_ACCESS", isChecked)
                        editor.apply()

                        if (isChecked) {
                            context.onCheckAccessScan(
                                object : OnItemSelectListener {
                                    override fun onCheckSwitch(value: Boolean) {
                                        if (!value) {
                                            editor.putBoolean("SCAN_ACCESS", value)
                                            editor.apply()
                                            holder.itemView.settings_switch_check.isChecked = value
                                            context.value = false
                                        } else {
                                            holder.itemView.settings_switch_check.isChecked = value
                                            context.value = true
                                        }

                                    }


                                }
                            )
                        } else {
                            holder.itemView.settings_switch_check.isChecked = false
                            context.value = false
                        }
                    }
                }

                4 -> {
                    holder.itemView.settings_switch_check.setOnCheckedChangeListener { _, isChecked ->
                        editor.putBoolean("ALWAYS_ON", isChecked)
                        editor.apply()

                        (context as HomeActivity).setAlwaysOn(isChecked)
                    }
                }

            }

        }

        private fun configureTheme(holder: SetSelectsThemeViewHolder, position: Int) {
            val items: SetTheme = list[position] as SetTheme
            holder.bind(SetTheme(items.topic, items.label, items.check))

            val white = ContextCompat.getColor(context.applicationContext, R.color.white)
            val tint = ContextCompat.getColor(context.applicationContext, R.color.tint)



            holder.itemView.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val fm: FragmentManager = act.supportFragmentManager
                val uiManager: UiModeManager =
                    context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

                var auto: RadioButton = v.findViewById(R.id.select_auto)
                var light: RadioButton = v.findViewById(R.id.select_light)
                var dark: RadioButton = v.findViewById(R.id.select_dark)
                when (position) {

                    5 -> {
                        holder.itemView.select_theme.setOnCheckedChangeListener { group, _ ->

                            when (group.checkedRadioButtonId) {
                                R.id.select_auto -> {
                                    if(uiManager.nightMode == 1){
                                        auto.setTextColor(Color.parseColor(PropertiesKotlin.colorBlack))
                                        light.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM))
                                        dark.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM))
                                    }else{
                                        //system dark
                                        auto.setTextColor(Color.parseColor(PropertiesKotlin.colorDarkM))
                                        light.setTextColor( Color.parseColor(PropertiesKotlin.colorDarkM))
                                        dark.setTextColor( Color.parseColor(PropertiesKotlin.colorWhite))
                                    }
                                    (context as HomeActivity).setThemeMode(0)
                                    editor?.putInt("select_theme", 0)
                                    editor?.apply()
                                }
                                R.id.select_light -> {
                                    auto.setTextColor(Color.parseColor(PropertiesKotlin.colorMainM))
                                    light.setTextColor( Color.parseColor(PropertiesKotlin.colorWhite))
                                    dark.setTextColor( Color.parseColor(PropertiesKotlin.colorMainM))
                                    (context as HomeActivity).setThemeMode(1)
                                    editor?.putInt("select_theme", 1)
                                    editor?.apply()
                                }
                                R.id.select_dark -> {
                                    auto.setTextColor(Color.parseColor(PropertiesKotlin.colorDarkM))
                                    light.setTextColor( Color.parseColor(PropertiesKotlin.colorDarkM))
                                    dark.setTextColor( Color.parseColor(PropertiesKotlin.colorBlack))
                                    (context as HomeActivity).setThemeMode(2)
                                    editor?.putInt("select_theme", 2)
                                    editor?.apply()
                                }
                            }

                        }
                    }
                }

            }
        }


        private fun configureLang(holder: SetSelectsLanguageViewHolder, position: Int) {
            val items: SetLang = list[position] as SetLang
//            val sharedPreferences: SharedPreferences =
//                context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
            holder.bind(SetLang(items.topic, items.label, items.check))
//            holder.itemView.setOnClickListener { v ->
//                val act = v!!.context as AppCompatActivity
//                val fm: FragmentManager = act.supportFragmentManager
//            }
        }

        private fun configureChange(holder: SetChangeViewHolder, position: Int) {
            val items: SetChange = list[position] as SetChange
            holder.bind(SetChange(items.topic, items.label))
            holder.itemView.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val fm: FragmentManager = act.supportFragmentManager

                when (position) {
                    8 -> {
                        fm.beginTransaction().replace(R.id.frag_settings, ChangePasswordFragment())
                            .addToBackStack("SETTINGS").commit()
                    }
                    9 -> {
                        fm.beginTransaction().replace(R.id.frag_settings, ChangePinFragment())
                            .addToBackStack("SETTINGS").commit()
                    }
                }
            }
        }

        private fun configureAccount(holder: SetAccountViewHolder, position: Int) {
            val items: SetAccount = list[position] as SetAccount
            holder.bind(SetAccount(items.topic, items.account))
            holder.itemView.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val fm: FragmentManager = act.supportFragmentManager

                when (position) {
                    11 -> {
                        fm.beginTransaction().replace(R.id.frag_settings, UserDeletionFragment())
                            .addToBackStack("SETTINGS").commit()
                    }
                }
            }
        }

    }

    companion object {
        var checkScanAccess: Boolean = false

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        var select_theme: Int = 0
        var lastCheckedTheme: RadioButton? = null
        protected var mainActivity: MainActivity? = null
    }


    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.tsett)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        mDayNightMode = AppCompatDelegate.getDefaultNightMode()
        PropertiesKotlin.state = state
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

}
