package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.android.tradeplus.util.ManageData

class ForceChangeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val imm: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_force_change, container, false)
        var newpassword = v.findViewById<EditText>(R.id.new_password)
        var confirmpassword = v.findViewById<EditText>(R.id.confirm_password)

        var newpin = v.findViewById<EditText>(R.id.new_pin)
        var confirmpin = v.findViewById<EditText>(R.id.confirm_pin)

        val signIn = v.findViewById<Button>(R.id.sign_in)
        var checkPwd: Boolean = false
        var checkPin: Boolean = false

        signIn?.setOnClickListener {
            if (newpassword.text.toString().isEmpty()) {
            } else if (confirmpassword.text.toString().isEmpty()) {
            } else if (newpin.text.toString().isEmpty()) {
            } else if (confirmpin.text.toString().isEmpty()) {
            } else if (newpassword.text.toString().length < 6) {
            } else if (newpassword.text.toString() != confirmpassword.text.toString()) {
            } else if (newpin.text.toString().length != 4) {
            } else if (newpin.text.toString() != confirmpin.text.toString()) {
            } else {
                if (newpassword.text.toString() == confirmpassword.text.toString()) {
                    checkPwd = ManageData().isBothLettersandDigit(newpassword.text.toString())
                }

                if (newpin.text.toString() == confirmpin.text.toString()) {
                    checkPin = true
                }

                if (checkPwd && checkPin) {
                    forceChange(newpassword.text.toString(), newpin.text.toString())
                } else if (!checkPwd) {
                } else if (!checkPin) {
                }
            }






        }


        val closeKeyBoard = v.findViewById<ConstraintLayout>(R.id.frag_force_change)
        closeKeyBoard.isClickable = true
        closeKeyBoard.setOnClickListener {
            imm.hideSoftInputFromWindow(signIn.windowToken, 0)
            newpassword.clearFocus()
            confirmpassword.clearFocus()
            newpin.clearFocus()
            confirmpin.clearFocus()
        }

        return v
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.switch_language)
        item.isVisible = false
    }


    private fun forceChange(newPwd: String, newPin: String) {
        context?.let {
            ServiceApiSpot.forceChangePasswordAndPin(newPwd, newPin, it) {
                parentFragmentManager.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right
                ).add(R.id.container_main, SignInFragment(), "SIGN_IN").addToBackStack(null)
                    .commit()
            }
        }
    }

}
