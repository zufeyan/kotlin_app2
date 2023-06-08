package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.sctgold.android.tradeplus.util.ManageData

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChangePasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePasswordFragment : Fragment(), DialogInterface.OnCancelListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var currentPass: EditText
    private lateinit var newPass: EditText
    private lateinit var confirmPass: EditText
    private lateinit var lbMain: TextView
    private lateinit var lb1: TextView
    private lateinit var lb2: TextView
    private lateinit var lb3: TextView
    private lateinit var lb4: TextView
    private lateinit var save: Button
    var state: String = "ChangePasswordFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        PropertiesKotlin.state = state
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_change_password, container, false)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.cpass)
        currentPass = v.findViewById(R.id.change_password_current)
        newPass = v.findViewById(R.id.change_password_new)
        confirmPass = v.findViewById(R.id.change_password_confirm)

        lbMain = v.findViewById(R.id.change_password_label)
        lb1 = v.findViewById(R.id.change_password_opt1)
        lb2 = v.findViewById(R.id.change_password_opt2)
        lb3 = v.findViewById(R.id.change_password_opt3)
        lb4 = v.findViewById(R.id.change_password_opt4)

        save = v.findViewById(R.id.change_password_save)
        save.isEnabled = false

        newPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                formatPwd(s.toString())
            }

        })

        confirmPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == newPass.text.toString()) {
                    save.isEnabled = true
                }

            }

        })



        save.setOnClickListener {
            if (currentPass.text.toString().isNullOrEmpty()) {
//                    testmsg.text = "please fill current password"
            } else {

                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }
    //                if (newPass.text.toString() != confirmPass.text.toString()) {
    //                    builder?.setMessage(getString(R.string.alert_pass))
    //                        ?.setTitle(getString(R.string.cpass))
    //                        ?.setPositiveButton(getString(R.string.btn_close),
    //                            DialogInterface.OnClickListener { dialog, id ->
    //
    //                                builder?.create()?.dismiss()
    //                            })
    //                } else {
                builder?.setMessage(getString(R.string.alert_pass))
                    ?.setTitle(getString(R.string.cpass))
                    ?.setPositiveButton(getString(R.string.btn_confirm),
                        DialogInterface.OnClickListener { dialog, id ->
                            changeNewPassword(
                                currentPass.text.toString(),
                                newPass.text.toString()
                            )
                        })
                    ?.setNegativeButton(
                        getString(R.string.btn_close),
                        DialogInterface.OnClickListener { dialog, id ->
                            builder.create().dismiss()
                            // User cancelled the dialog
    //                                getDialog().cancel()

                        })

    //                }
                builder?.create()?.show()
            }

        }
        return v
    }


    private fun changeNewPassword(currentPass: String, newPass: String) {
        var msgResponse = ""
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        ServiceApiSpot.changeNewPassword(currentPass, newPass, context) {
            if (it != null) {

                when (it.response) {
                    "SUCCESS" -> {
                        builder?.setTitle(getString(R.string.alert_ok))
                            ?.setPositiveButton(getString(R.string.btn_close),
                                DialogInterface.OnClickListener { dialog, id ->
                                    (activity as HomeActivity).onBackPressed()
                                })
                    }
                    "RECORD_NOT_FOUND" -> {
                        msgResponse = it.response!!
                        builder
                            ?.setTitle(msgResponse)
                            ?.setPositiveButton(getString(R.string.btn_close),
                                DialogInterface.OnClickListener { dialog, id ->
                                    builder.create().dismiss()
                                })
                    }
                    else -> {
                        msgResponse = it.response.toString()
                        builder
                            ?.setTitle(msgResponse)
                            ?.setPositiveButton(getString(R.string.btn_close),
                                DialogInterface.OnClickListener { dialog, id ->
                                    builder.create().dismiss()
                                })
                    }
                }


                builder?.create()?.show()

            }
        }

    }

    private fun formatPwd(newPass: String): Boolean {


        var checkAll: Boolean = ManageData().isBothLettersandDigitAll(newPass.toString())
        var checkAlpha: Boolean = ManageData().isBothLettersandUpCase(newPass.toString())
        var checkSmall: Boolean = ManageData().isBothLettersandAlphaLowCase(newPass.toString())
        var checkNum: Boolean = ManageData().isBothLettersandDigit(newPass.toString())
        if (checkAll && newPass.length >= 6) {
            lb1.setTextColor(Color.parseColor("#007C00"))
        } else {
            lb1.setTextColor(Color.parseColor("#990000"))
        }
        if (checkAlpha) {
            lb2.setTextColor(Color.parseColor("#007C00"))
        } else {
            lb2.setTextColor(Color.parseColor("#990000"))
        }
        if (checkSmall) {
            lb3.setTextColor(Color.parseColor("#007C00"))
        } else {
            lb3.setTextColor(Color.parseColor("#990000"))
        }
        if (checkNum) {
            lb4.setTextColor(Color.parseColor("#007C00"))
        } else {
            lb4.setTextColor(Color.parseColor("#990000"))
        }

        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lbMain.text = getString(R.string.vl_all)
        lb1.text = getString(R.string.vl_1)
        lb2.text = getString(R.string.vl_2)
        lb3.text = getString(R.string.vl_3)
        lb4.text = getString(R.string.vl_4)

        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val closeKeyBoard = view.findViewById<ConstraintLayout>(R.id.frag_change_password)
        closeKeyBoard.isClickable = true
        closeKeyBoard.setOnClickListener {
            imm.hideSoftInputFromWindow(closeKeyBoard.windowToken, 0)
            newPass.clearFocus()
            confirmPass.clearFocus()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangePasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onCancel(dialog: DialogInterface?) {
        TODO("Not yet implemented")
    }
}
