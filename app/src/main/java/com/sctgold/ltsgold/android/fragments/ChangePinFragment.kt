package com.sctgold.ltsgold.android.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.rest.ServiceApiSpot
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclin.
 * Use the [ChangePinFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangePinFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var currentPin: EditText
    private lateinit var newPin: EditText
    private lateinit var confirmPin: EditText
    private lateinit var save: Button
    private lateinit var testmsg: TextView
    var state: String = "ChangePinFragment"

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
        val v = inflater.inflate(R.layout.fragment_change_pin, container, false)

        currentPin = v.findViewById(R.id.change_pin_current)
        newPin = v.findViewById(R.id.change_pin_new)
        confirmPin = v.findViewById(R.id.change_pin_confirm)

        save = v.findViewById(R.id.change_pin_save)

        testmsg = v.findViewById(R.id.testmsg)
        save.setOnClickListener {
            when {
                currentPin.text.toString().isEmpty() -> {
                    testmsg.text = "please fill current pin"
                }
                newPin.text.toString().isEmpty() -> {
                    testmsg.text = "please fill new pin"
                }
                confirmPin.text.toString().isEmpty() -> {
                    testmsg.text = "please fill confirm new pin"
                }
                newPin.text.toString() != confirmPin.text.toString() -> {
                    testmsg.text = "new pin != confirm new pin"
                }
                newPin.text.toString().length != 4 -> {
                    testmsg.text = "please fill new pin != 4 "
                }
                else -> {
                    val builder: AlertDialog.Builder? = activity?.let {
                        AlertDialog.Builder(it)
                    }


                    builder?.setMessage(getString(R.string.alert_pin))
                        ?.setTitle(getString(R.string.cpin))
                        ?.setPositiveButton(getString(R.string.btn_confirm),
                            DialogInterface.OnClickListener { dialog, id ->

                                changeNewPin(currentPin.text.toString(), newPin.text.toString())

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


        }

        return v

    }

    private fun changeNewPin(currentPin: String, newPin: String) {
        var msgResponse = ""
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        ServiceApiSpot.changeNewPin(currentPin, newPin, context) {
            if (it != null) {
                when (it.response) {
                    "SUCCESS" -> {
                        msgResponse = context.resources.getString(R.string.title_dialog_success)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val closeKeyBoard = view.findViewById<ConstraintLayout>(R.id.frag_change_pin)
        closeKeyBoard.isClickable = true
        closeKeyBoard.setOnClickListener {
            imm.hideSoftInputFromWindow(closeKeyBoard.windowToken, 0)
            currentPin.clearFocus()
            newPin.clearFocus()
            confirmPin.clearFocus()

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChangePinFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChangePinFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.cpin)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

}