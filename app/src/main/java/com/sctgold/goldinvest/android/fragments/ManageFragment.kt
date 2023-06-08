package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.sctgold.goldinvest.android.adapter.CustomAdapter
import com.sctgold.goldinvest.android.adapter.CustomListener
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.rest.ServiceApiSpot
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.dto.MyProductDTO
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageFragment : Fragment(), CustomListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var displayView: RecyclerView
    private lateinit var hiddenView: RecyclerView
    var productAll: java.util.ArrayList<MyProductDTO> = ArrayList()
    var state: String = "ManageFragment"

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
        val v = inflater.inflate(R.layout.fragment_manage, container, false)

        displayView = v.findViewById(R.id.display_view)
        hiddenView = v.findViewById(R.id.hidden_view)

        ButterKnife.bind(displayView)

        return v
    }


    private fun RecyclerView.init(list: ArrayList<String>) {
        this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        var adapter = CustomAdapter(list, this@ManageFragment)
        this.adapter = adapter
        this.setOnDragListener(adapter.dragInstance)
        adapter.onAttachedToRecyclerView(displayView)
    }

    override fun setEmptyList(visibility: Int, recyclerView: Int, emptyTextView: Int) {
        context.findViewById<RecyclerView>(recyclerView).visibility = visibility
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ManageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getProduct() {
        ServiceApiSpot.getMyProduct(context) {
            if (it != null) {
                var myProductView = ArrayList<String>()
                var myProductHide = ArrayList<String>()

                var pd: MyProductDTO
                if (it.mobileMyProductList != null) {

                    for (products in it.mobileMyProductList) {
                        myProductView.add("${products.name}")
                        pd = MyProductDTO()
                        pd.code = products.code
                        pd.name = products.name
                        productAll.add(pd)
                    }
                    displayView.init(myProductView)
                    displayView.isVerticalScrollBarEnabled = true
                }

                if (it.mobileProductList != null) {
                    for (products in it.mobileProductList) {
                        myProductHide.add("${products.name}")
                        pd = MyProductDTO()
                        pd.code = products.code
                        pd.name = products.name
                        productAll.add(pd)
                    }
                    hiddenView.init(myProductHide)
                    hiddenView.isVerticalScrollBarEnabled = true
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
        val item3 = menu.findItem(R.id.to_checked)
        item3.isVisible = true
//        item3.isEnabled = false
        val item4 = menu.findItem(R.id.to_price_alert)
        item4.isVisible = false

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.to_checked -> {


                var msgResponse = ""
                var codeList: ArrayList<String> = ArrayList()

                if (PropertiesKotlin.myProductUpdate.size == 0) {
                    (activity as HomeActivity).onBackPressed()
                } else {
                    for (p in PropertiesKotlin.myProductUpdate) {
                        var ss = productAll.find { actor -> p == actor.name }
                        productAll.find { actor -> p == actor.name }?.code
                        ss?.code?.let { codeList.add(it) }
                    }
                    ServiceApiSpot.updateProduct(codeList, context) {

                        if (it != null) {
                            val builder: AlertDialog.Builder? = activity?.let {
                                AlertDialog.Builder(it)
                            }

                            if (it.response == "SUCCESS") {

                                msgResponse = context.resources.getString(R.string.title_dialog_success)
                                builder?.setTitle(msgResponse)
                                    ?.setPositiveButton(getString(R.string.btn_close),
                                        DialogInterface.OnClickListener { dialog, id ->
                                            (activity as HomeActivity).onBackPressed()
                                            builder.create().dismiss()
                                        })

                            } else {
                                msgResponse = it.message.toString()
                                builder
                                    ?.setTitle(msgResponse)
                                    ?.setPositiveButton(getString(R.string.btn_close),
                                        DialogInterface.OnClickListener { dialog, id ->
                                            builder.create().dismiss()
                                        })
                            }
                            builder?.create()?.show()
                        }
                    }
                }


                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onPause() {
        super.onPause()
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        ft.remove(this)
        ft.commit()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.manage)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
        getProduct()
    }
}
