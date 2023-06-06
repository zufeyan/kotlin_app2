package com.sctgold.ltsgold.android.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.activities.MainActivity
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.sctgold.ltsgold.android.rest.MobileFrontService
import com.sctgold.android.tradeplus.util.ManageData
import com.sctgold.android.tradeplus.util.RSAKotlin
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class PassCodeFragment : Fragment() {
    private var shake: Int = 0
    private val maxPassCode: Int = 4

    companion object {

        @JvmStatic
        fun newInstance(fire: Boolean): PassCodeFragment {
            val fragment = PassCodeFragment()
            val bundle = Bundle()
            bundle.putBoolean("FIRE", fire)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.e("PassCodeFragment onCreate :", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pass_code, container, false)

        val status = v.findViewById<TextView>(R.id.lb_status_pass_code)
        status.text = getString(R.string.alert_pass_code)

        val passCode = v.findViewById<EditText>(R.id.pass_code)

        var sharedPreferences: SharedPreferences? =
            context?.getSharedPreferences(getString(R.string.key_prefs_session_initialized), Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        passCode?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passCode.filters += InputFilter.LengthFilter(maxPassCode)
                if (count == 4) {
                    var encryptCheck: String = checkEncrypt(s.toString())

                    MobileFrontService.invoke().checkPin(encryptCheck).enqueue(object :
                        retrofit2.Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            var jsonObject = JSONObject(response.body()!!.string())
                            var response: String = jsonObject.getString("response")

                            Log.e("Passcode response s :", response)
                            if (response.equals("SUCCESS", true)) {
                                val intent = Intent(activity, HomeActivity::class.java)
                                intent.putExtra("WELCOME", true)
                                startActivity(intent)
                                passCode.text = null
                                passCode.setText("")
                                Log.e("Passcode SUCCESS :", response)

                                if (editor != null) {
                                    editor.putString("PIN", s.toString())
                                    editor.apply()
                                }

                            } else if (response.equals("RESET_PASSWORD_REQUIRED", true)) {
                                parentFragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_left,
                                    R.anim.slide_out_right,
                                    R.anim.slide_in_right
                                ).replace(R.id.container_main, ForceChangeFragment(), "FORCE")
                                    .addToBackStack("SIGN_IN").commit()

                            } else if (response.equals("INVALID_CREDENTIAL", true)) {
                            } else if (response.equals("CHANGE_PWD_REQUIRED", true)) {
                                parentFragmentManager.beginTransaction().setCustomAnimations(
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_left,
                                    R.anim.slide_out_right,
                                    R.anim.slide_in_right
                                ).replace(R.id.container_main, ForceChangeFragment(), "FORCE")
                                    .addToBackStack("SIGN_IN").commit()
                            } else if (response.equals("FAILED", false)) {
                                val vibes: Animation =
                                    AnimationUtils.loadAnimation(activity, R.anim.shake)
                                v.startAnimation(vibes)
                                val vibe =
                                    activity?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibe.vibrate(VibrationEffect.createOneShot(500, -1))
                                status.text = getString(R.string.alert_sign_in)
                                passCode.text = null
                                passCode.setText("")

                                shake += 1

                                if (shake > 3) {
                                    //val back = arguments?.getBoolean("FIRE", true)
                                }
                                Log.e("FAILED :", response)
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("ERROR", t.toString())
                        }
                    })
                }
            }
        })

        return v
    }

    fun checkEncrypt(s: String): String {
        val pin: String? = ManageData().getMd5Base64(s)
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
//        Log.d("SUCCESS :", pin + pin?.let { ManageData().addSpace("PIN", it) })
//        Log.d("SUCCESS :", UserStateDto.branch)
//        Log.d("SUCCESS :", UserStateDto.token)
//        Log.d("SUCCESS :", UserStateDto.deviceId)
//        Log.e("ERROR :", sumText)
        val encryptText = context?.let { RSAKotlin().decryptRsa(sumText, it) }
//        if (encryptText != null) {
//            Log.d("SUCCESS :", encryptText)
//        }
        return encryptText.toString()
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.switch_language)
        item.isVisible = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}