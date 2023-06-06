package com.sctgold.ltsgold.android.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.rest.ServiceApiSpot
import com.sctgold.ltsgold.android.util.ProgressDialog
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.row_clear_port.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClearPortFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClearPortFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var clearPortView: RecyclerView
    var dialog: Dialog? = null
    var state: String = "ClearPortFragment"


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
        val v = inflater.inflate(R.layout.fragment_clear_port, container, false)

        refresh = v.findViewById(R.id.clear_port_refresh)
        refresh.setColorSchemeResources(R.color.step1, R.color.step2, R.color.step3, R.color.step4)
        refresh.setOnRefreshListener { loadData() }

        clearPortView = v.findViewById(R.id.clear_port_view)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = ProgressDialog.progressDialog(context)
        dialog?.show()
        clearPortView.apply {
            clearPortView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            clearPortView.itemAnimator = DefaultItemAnimator()
            adapter = ClearPortAdapter(getClearPortLists(), context)
        }
    }

    data class DataClearPort(
        var colors: Int,
        var products: String,
        var productsCode: String
    )



    private fun getClearPortLists(): ArrayList<DataClearPort> {
        val item: ArrayList<DataClearPort> = ArrayList()

        ServiceApiSpot.getMyProduct(context) {
            if (it != null) {
                if (dialog?.isShowing == true) {
                    dialog?.dismiss()
                }
                var itemMyAdd: ArrayList<DataClearPort>
                for (myPd in it.mobileMyProductList) {
                    itemMyAdd =  ArrayList()

                    if(!myPd.viewable){
                        if(myPd.productColor.isNullOrEmpty()){
                            itemMyAdd.add(DataClearPort(Color.parseColor("#C7C7CC"), myPd.name!!,myPd.code!!))
                        }else {


                            itemMyAdd.add(
                                DataClearPort(
                                    Color.parseColor("#" + myPd.productColor),
                                    myPd.name!!,myPd.code!!
                                )
                            )
                        }

                    }
                    item.addAll(itemMyAdd)
                }
                var itemAdd: ArrayList<DataClearPort> = ArrayList()
                for (pd in it.mobileProductList) {
                    itemAdd =  ArrayList()
                    if(!pd.viewable){
                        if(pd.productColor.isNullOrEmpty()){
                            itemAdd.add(DataClearPort(Color.parseColor("#C7C7CC"), pd.name!!,pd.code!!))
                        }else {


                            itemAdd.add(
                                DataClearPort(
                                    Color.parseColor("#" + pd.productColor),
                                    pd.name!!,pd.code!!
                                )
                            )
                        }

                    }


                    item.addAll(itemAdd)
                }
                clearPortView.adapter?.notifyDataSetChanged()
            }
        }

        return item
    }

    private class ClearPortAdapter(private val lists: List<DataClearPort>, val context: Context) :
        RecyclerView.Adapter<ClearPortAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_clear_port, parent, false)
            )
        }

        override fun getItemCount() = lists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data: DataClearPort = lists[position]

            val boxes = ContextCompat.getDrawable(context, R.drawable.corner_view)
            boxes?.setTint(data.colors)

            holder.itemView.clear_port_row_color.background = boxes
            holder.itemView.clear_port_row_product.text = data.products
            holder.itemView.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val fm: FragmentManager = act.supportFragmentManager
                val clearPortActionFragment: Fragment = ClearPortActionFragment()
                val bundle = Bundle()
                bundle.putString("productsCode", data.productsCode)
                clearPortActionFragment.arguments = bundle
                fm.beginTransaction().replace(R.id.frag_clear_port, clearPortActionFragment)
                    .addToBackStack("CLEAR_PORT").commit()
            }
        }
    }

    private fun loadData() {
        refresh.isRefreshing = true
        // request reload data below
        refresh.isRefreshing = false
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClearPortFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClearPortFragment().apply {
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
    }

    override fun onResume() {
        super.onResume()
        val v = (activity as HomeActivity)
        v.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = v.findViewById<TextView>(R.id.title_main_text)
        title.visibility = View.VISIBLE
        title.text = getString(R.string.tclea)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        val selectOrders: RadioGroup = v.findViewById(R.id.clear_port_sl_order)
        selectOrders.visibility = View.GONE
        PropertiesKotlin.state = state
    }

    override fun onPause() {
        super.onPause()

    }


}