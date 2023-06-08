package com.sctgold.goldinvest.android.fragments

//import javax.xml.bind.DatatypeConverter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.dto.ResponseDTO
import com.sctgold.goldinvest.android.dto.ResponseTokenDTO
import com.sctgold.goldinvest.android.util.ProgressDialog
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.gson.GsonBuilder
import com.sctgold.goldinvest.android.rest.MobileFrontService
import com.sctgold.android.tradeplus.util.ManageData
import com.sctgold.android.tradeplus.util.RSAKotlin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executor
import androidx.core.content.ContextCompat


class SignInFragment : Fragment() {
    private lateinit var titles: TextView
    private var shake: Int = 0
    private lateinit var statusSignIn: TextView
    private lateinit var txtVersion: TextView
    private lateinit var signIn: Button

    private var checkAccount: Boolean = false
    var checkPin: Boolean = false
    var checkScanAccess: Boolean = false
    private var checkAlwaysOn: Boolean = false


    var passCode: String = ""
    var userAcc: String = ""
    var userPass: String = ""
    var language: String = ""

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo

    private var mToBeSignedMessage: String? = null

    // Unique identifier of a key pair
    private val KEY_NAME: String = UUID.randomUUID().toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_sign_in, container, false)
        titles = activity?.findViewById<TextView>(R.id.title_main_text)!!

        return v
    }

    fun setLocale(lang: String): Boolean {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf: Configuration = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = view.findViewById<EditText>(R.id.username)

        val pass = view.findViewById<EditText>(R.id.password)

        signIn = view.findViewById<Button>(R.id.sign_in)
        statusSignIn = view.findViewById<TextView>(R.id.lb_status_sign_in)
        txtVersion = view.findViewById<TextView>(R.id.lb_version)


        val appVersionName = context?.let { PropertiesKotlin().getVersionName(it) }


        txtVersion.text = context?.resources?.getString(R.string.version) + " ( " + getString(R.string.version_text)+ ' ' + appVersionName + " )"
        var sharedPreferences: SharedPreferences? =
            context?.getSharedPreferences(
                getString(R.string.key_prefs_session_initialized),
                Context.MODE_PRIVATE
            )


        if (sharedPreferences != null) {
            checkAccount = sharedPreferences.getBoolean("USER_REMEMBER", false)
            checkPin = sharedPreferences.getBoolean("PIN_REMEMBER", false)
            checkScanAccess = sharedPreferences.getBoolean("SCAN_ACCESS", false)
            checkAlwaysOn = sharedPreferences.getBoolean("ALWAYS_ON", false)
            userAcc = sharedPreferences.getString("ACCOUNT", "").toString()
            userPass = sharedPreferences.getString("PASSWORD", "").toString()
            language = sharedPreferences.getString("LANGUAGE", "EN").toString()
        }

        if (checkAccount) {
            user.setText(userAcc)
            pass.setText(userPass)
        }
        if (checkScanAccess) {

            executor = ContextCompat.getMainExecutor(requireContext())
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        checkAuthenticateToken(userAcc.trim(), userPass.trim(), view)
                    }

                })
            promptInfo = PromptInfo.Builder().apply {
                setTitle("Biometric login for my app")
                setSubtitle("Log in using your biometric credential")
                setNegativeButtonText("Use account password")
                setConfirmationRequired(false)
            }.build()
            // Prompt appears when user clicks "Log in".
            // Consider integrating with the keystore to unlock cryptographic operations,
            // if needed by your app.
            biometricPrompt.authenticate(promptInfo)
        }

        val imm: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager



        signIn.isEnabled = true

        signIn?.setOnClickListener {
//            Log.e("signIn :", "signIn==========================================")
            signIn.isEnabled = false
            val username: String = user.text.trim().toString()
            val password: String = pass.text.trim().toString()
            ManageData().hasInternetConnection().subscribe { hasInternet ->
                if (hasInternet) {
//                    startNewAsyncTask(username, password, "")
                    checkAuthenticateToken(
                        user.text.trim().toString(),
                        pass.text.trim().toString(),
                        view
                    )
                } else {
                    var dialogNetwork = ProgressDialog.networkDialog(requireContext())
                    dialogNetwork.show()
                }
            }

            user.clearFocus()
            pass.clearFocus()
        }

        val forget = view.findViewById<TextView>(R.id.forget_password)
        forget.setOnClickListener {
            titles.text = getString(R.string.tforget)
            parentFragmentManager.beginTransaction().setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right
            ).replace(R.id.container_main, ForgetPasswordFragment(), "FORGET")
                .addToBackStack("SIGN_IN").commit()
            imm.hideSoftInputFromWindow(forget.windowToken, 0)
            user.clearFocus()
            pass.clearFocus()
        }

//        val open = view.findViewById<TextView>(R.id.open_account)
//        open.setOnClickListener {
//            titles.text = getString(R.string.topen)
//            parentFragmentManager.beginTransaction().setCustomAnimations(
//                R.anim.slide_in_left,
//                R.anim.slide_out_left,
//                R.anim.slide_out_right,
//                R.anim.slide_in_right
//            ).replace(R.id.container_main, OpenAccountFragment(), "OPEN_ACCOUNT")
//                .addToBackStack("SIGN_IN").commit()
//            imm.hideSoftInputFromWindow(open.windowToken, 0)
//            user.clearFocus()
//            pass.clearFocus()
//        }

        val closeKeyBoard = view.findViewById<ConstraintLayout>(R.id.frag_sign_in)
        closeKeyBoard.isClickable = true
        closeKeyBoard.setOnClickListener {
            imm.hideSoftInputFromWindow(closeKeyBoard.windowToken, 0)
            user.clearFocus()
            pass.clearFocus()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.switch_language)
        item.isVisible = true
    }

    private fun checkAuthenticateToken(_account: String, password: String, view: View) {
//        val param: Map<String, String> = mapOf(
//            "systemType" to "AND",
//            "clientTokenKey" to UserStateDto.token
//        )

        var sharedPreferences: SharedPreferences? =
            context?.getSharedPreferences(
                getString(R.string.key_prefs_session_initialized),
                Context.MODE_PRIVATE
            )
        val editor = sharedPreferences?.edit()

        val pwd: String? = ManageData().getMd5Base64(password)
        val sumText: String =
            PropertiesKotlin.branch + ManageData().addSpace(
                "BRC",
                PropertiesKotlin.branch
            ) + _account + ManageData().addSpace(
                "ACC",
                _account
            ) + PropertiesKotlin.deviceId + ManageData().addSpace(
                "DID",
                PropertiesKotlin.deviceId
            ) + pwd + pwd?.let { ManageData().addSpace("PWD", it) }

        val encryptText = context?.let { RSAKotlin().decryptRsa(sumText, it) }
        val jsonobj = JSONObject()
        jsonobj.put("systemType", "AND")
        jsonobj.put("clientTokenKey", PropertiesKotlin.tokenStart)
        val jsonObjectString = jsonobj.toString()
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        CoroutineScope(Dispatchers.IO).launch {
            encryptText?.let { MobileFrontService().authentication(it, requestBody) }
                ?.enqueue(object : retrofit2.Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {

                        Log.e("response.code() :", response.code().toString())
                        if (response.code() == 200) {


                            val jsonObject = JSONObject(response.body()!!.string())
                            val gson = GsonBuilder().registerTypeHierarchyAdapter(
                                ByteArray::class.java,
                                ByteArrayToBase64Adapter()
                            ).create()
                            var responseToken =
                                gson.fromJson(jsonObject.toString(), ResponseTokenDTO::class.java)
                            Log.e("signIn :", responseToken.response.toString())
                            if (responseToken.response == "SUCCESS") {
                                PropertiesKotlin.token = responseToken.token.toString()
                                PropertiesKotlin.grade = responseToken.grade.toString()
                                PropertiesKotlin.account = _account.trim()
                                if (editor != null) {
                                    editor.putString("ACCOUNT", _account)
                                    editor.putString("PASSWORD", password)
                                    editor.apply()
                                }
                                if (_account != userAcc) {
                                    checkPin = false
                                    if (editor != null) {
                                        editor.putBoolean("USER_REMEMBER", false)
                                        editor.putBoolean("PIN_REMEMBER", false)
                                        editor.putBoolean("SCAN_ACCESS", false)
                                        editor.putBoolean("ALWAYS_ON", false)
                                        editor.putBoolean("NIGHT_THEME", false)
                                        editor.apply()
                                    }
                                }


                                if (checkPin) {
                                    if (sharedPreferences != null) {
                                        passCode = sharedPreferences.getString("PIN", "").toString()
                                        checkPinValidate(passCode)
                                    }
                                } else {

                                    titles.text = getString(R.string.tpin)
                                    parentFragmentManager.beginTransaction().setCustomAnimations(
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_left,
                                        R.anim.slide_out_right,
                                        R.anim.slide_in_right
                                    ).replace(R.id.container_main, PassCodeFragment(), "PIN")
                                        .addToBackStack("SIGN_IN")
                                        .commit()
                                    statusSignIn.text = "SUCCESS"
                                }


                            } else if (responseToken.response == "APPROVAL_REQUIRED") {
                                Log.e("APPROVAL_REQUIRED :", "")
                                PropertiesKotlin.token = ""
                                signIn.isEnabled = true
                                statusSignIn.text = resources.getString(R.string.APPROVAL_REQUIRED)
                            } else if (responseToken.response == "AUTHENTICATION_FAILED") {
                                signIn.isEnabled = true
                                PropertiesKotlin.token = ""

                                val vibes: Animation =
                                    AnimationUtils.loadAnimation(activity, R.anim.shake)
                                view.startAnimation(vibes)
                                val vibe =
                                    activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibe.vibrate(VibrationEffect.createOneShot(500, -1))
                                shake += 1
                                if (shake > 3) {
                                    //val back = arguments?.getBoolean("FIRE", true)
                                }
                                statusSignIn.text = resources.getString(R.string.LOGIN_FAIL)
                            } else if (responseToken.response == "SYSTEM_CLOSE") {
                                signIn.isEnabled = true
                                PropertiesKotlin.token.let {
                                    PropertiesKotlin.token = null.toString()
                                }
                                PropertiesKotlin.token = ""
                                statusSignIn.text = resources.getString(R.string.SYSTEM_CLOSE)
                            } else {
                                signIn.isEnabled = true
                                PropertiesKotlin.token.let {
                                    PropertiesKotlin.token = null.toString()
                                }
                                PropertiesKotlin.token = ""
                                statusSignIn.text = resources.getString(R.string.LOGIN_FAIL)
                            }


                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("ERROR", t.message.toString())

                    }

                })

        }
    }


    fun checkPinValidate(passCode: String) {
        val pin: String? = ManageData().getMd5Base64(passCode)
        val sumText: String =
            PropertiesKotlin.branch + ManageData().addSpace("BRC", PropertiesKotlin.branch) +
                    PropertiesKotlin.deviceId + ManageData().addSpace(
                "DID",
                PropertiesKotlin.deviceId
            ) +
                    PropertiesKotlin.token + ManageData().addSpace(
                "TKN",
                PropertiesKotlin.token
            ) +
                    pin + pin?.let { ManageData().addSpace("PIN", it) }
        val decryptRsa = context?.let { RSAKotlin().decryptRsa(sumText, it) }

        if (decryptRsa != null) {
            MobileFrontService.invoke().checkPin(decryptRsa).enqueue(object :
                retrofit2.Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val jsonObject = JSONObject(response.body()!!.string())
                    val gson = GsonBuilder().registerTypeHierarchyAdapter(
                        ByteArray::class.java,
                        ByteArrayToBase64Adapter()
                    ).create()
                    var responsePin =
                        gson.fromJson(jsonObject.toString(), ResponseDTO::class.java)

                    if (responsePin.response == "SUCCESS") {
                        val intent = Intent(activity, HomeActivity::class.java)
                        intent.putExtra("WELCOME", true)
                        startActivity(intent)


                    } else {
                        titles.text = getString(R.string.tpin)
                        parentFragmentManager.beginTransaction().setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right
                        ).replace(R.id.container_main, PassCodeFragment(), "PIN")
                            .addToBackStack("SIGN_IN")
                            .commit()
                        statusSignIn.text = "SUCCESS"
                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ERROR", t.toString())
                }
            })


        }
    }

}
