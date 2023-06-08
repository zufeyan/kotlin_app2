package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.content.DialogInterface
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
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserDeletionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserDeletionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var name: EditText
    private lateinit var accId: EditText
    private lateinit var emails: EditText
    private lateinit var deletion: SwitchCompat
    private lateinit var info: TextView
    private lateinit var confirm: Button
    var state: String = "UserDeletionFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_user_deletion, container, false)

        name = v.findViewById(R.id.del_name)
        accId = v.findViewById(R.id.del_user)
        emails = v.findViewById(R.id.del_emails)

        deletion = v.findViewById(R.id.del_switch_check)
        info = v.findViewById(R.id.del_lb_confirm)
        confirm = v.findViewById(R.id.del_confirm)
        confirm.isEnabled = false
        //deletion.isEnabled = false
        var checkDeletion = false
        info.text = ""
        val colorDis = ContextCompat.getColor(context.applicationContext, R.color.s999)
        val colorFirm = ContextCompat.getColor(context.applicationContext, R.color.text)

        confirm.setTextColor(colorDis)




        name.addTextChangedListener(object : TextWatcher {
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

                if (s.toString().isNotEmpty() && accId.text.toString()
                        .isNotEmpty() && emails.text.toString().isNotEmpty() && checkDeletion
                ) {

                    confirm.isEnabled = true
                    if (PropertiesKotlin.checkMode) {
                        confirm.setTextColor(
                            ContextCompat.getColor(
                                context.applicationContext,
                                R.color.white
                            )
                        )
                    } else {
                        confirm.setTextColor(colorFirm)
                    }
                }else{
                    confirm.isEnabled = false
                    confirm.setTextColor(colorDis)
                }





            }
        })


        accId.addTextChangedListener(object : TextWatcher {
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


                if (s.toString().isNotEmpty() && name.text.toString()
                        .isNotEmpty() && emails.text.toString().isNotEmpty() && checkDeletion
                ) {

                    confirm.isEnabled = true
                    if (PropertiesKotlin.checkMode) {
                        confirm.setTextColor(
                            ContextCompat.getColor(
                                context.applicationContext,
                                R.color.white
                            )
                        )
                    } else {
                        confirm.setTextColor(colorFirm)
                    }
                }else{
                    confirm.isEnabled = false
                    confirm.setTextColor(colorDis)
                }

            }
        })



        emails.addTextChangedListener(object : TextWatcher {
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

                if (s.toString().isNotEmpty() && name.text.toString().isNotEmpty() && accId.text.toString().isNotEmpty() && checkDeletion
                ) {
                    confirm.isEnabled = true
                    if (PropertiesKotlin.checkMode) {
                        confirm.setTextColor(
                            ContextCompat.getColor(
                                context.applicationContext,
                                R.color.white
                            )
                        )
                    } else {
                        confirm.setTextColor(colorFirm)
                    }
                }else{
                    confirm.isEnabled = false
                    confirm.setTextColor(colorDis)
                }

            }
        })


        deletion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                info.text = getString(R.string.ud_info2)
                checkDeletion = true

                if (emails.text.toString().isNotEmpty() && name.text.toString().isNotEmpty() && accId.text.toString().isNotEmpty()
                ) {

                    confirm.isEnabled = true
                    if (PropertiesKotlin.checkMode) {
                        confirm.setTextColor(
                            ContextCompat.getColor(
                                context.applicationContext,
                                R.color.white
                            )
                        )
                    } else {
                        confirm.setTextColor(colorFirm)
                    }
                }else{
                    confirm.isEnabled = false
                    confirm.setTextColor(colorDis)
                }


            } else {
                info.text = ""
                checkDeletion = false
                confirm.isEnabled = false
                confirm.setTextColor(colorDis)
            }
        }



        confirm.setOnClickListener {
            onConfirm()
        }


        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val closeKeyBoard = v.findViewById<ConstraintLayout>(R.id.frag_user_del)
        closeKeyBoard.isClickable = true
        closeKeyBoard.setOnClickListener {
            imm.hideSoftInputFromWindow(closeKeyBoard.windowToken, 0)
            name.clearFocus()
            accId.clearFocus()
            emails.clearFocus()

        }

        return v
    }

    fun onConfirm() {
        var msgResponse = ""
        ServiceApiSpot.newAccount(
            name.text.toString(),
            accId.text.trim().toString(),
            emails.text.trim().toString()
        ) {
            if (it != null) {
                if (it.response == "SUCCESS") {
                    val builder: AlertDialog.Builder? = activity?.let {
                        AlertDialog.Builder(it)
                    }
                    msgResponse = context.resources.getString(R.string.title_dialog_success)
                    builder?.setTitle(msgResponse)
                        ?.setPositiveButton(getString(R.string.btn_close),
                            DialogInterface.OnClickListener { dialog, id ->
                                (activity as HomeActivity).onBackPressed()
                                builder.create().dismiss()
                            })
                    builder?.create()?.show()
                } else {
                    it.message?.let { it1 -> createMsgDialog(it1) }
                }

            }
        }
    }


    fun createMsgDialog(msg: String) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        builder?.setTitle(getString(R.string.title_dialog_error))
            ?.setMessage(msg)
            ?.setPositiveButton(getString(R.string.btn_ok),
                DialogInterface.OnClickListener { dialog, id ->
                    builder.create().dismiss()
                })
            ?.setNegativeButton(getString(R.string.btn_close),
                DialogInterface.OnClickListener { dialog, id ->
                    builder.create().dismiss()
                })
        builder?.create()?.show()
    }

    private fun delEnabled() {
        val sname = name.text.toString().isNullOrEmpty()
        val susername = accId.text.toString().isNullOrEmpty()
        val semails = emails.text.toString().isNullOrEmpty()

        when {
            sname -> {
                deletion.isEnabled = false
            }
            susername -> {
                deletion.isEnabled = false
            }
            semails -> {
                deletion.isEnabled = false
            }
            else -> {
                deletion.isEnabled = true
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserDeletionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserDeletionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = getString(R.string.ud_page)
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        //delEnabled()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.ud_page)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }
}
