package com.sctgold.ltsgold.android.fragments

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sctgold.ltsgold.android.activities.MainActivity
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.rest.ServiceApiSpot

class ForgetPasswordFragment : Fragment() {

    lateinit var context: AppCompatActivity
    private lateinit var username: EditText
    private lateinit var email: EditText
    private lateinit var confirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_forget_password, container, false)
        username = v.findViewById(R.id.username)
        email = v.findViewById(R.id.register_email)
        confirm = v.findViewById(R.id.sign_in)

        confirm.setOnClickListener {
            onConfirm()
        }
        return v
    }

    fun onConfirm() {
        var msgResponse = ""
        ServiceApiSpot.forgotPassword(username.text.toString().trim(),email.text.toString().trim()){
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