package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.util.PropertiesKotlin

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PortStatementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortStatementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var header: TextView
    private lateinit var portStateView: RecyclerView
    private lateinit var stateLayout: LinearLayout
    private lateinit var textNoData: TextView

    val i: ArrayList<Any> = ArrayList()
    var state: String = "PortStatementFragment"

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
        val v = inflater.inflate(R.layout.fragment_port_statement, container, false)

        header = v.findViewById(R.id.port_state_header)
        portStateView = v.findViewById(R.id.port_state_view)
        stateLayout = v.findViewById(R.id.layout_post_statement)
        textNoData = v.findViewById(R.id.text_no_data)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textNoData.visibility = View.INVISIBLE
        header.text = getString(R.string.port_pro)
        portStateView.apply {
            portStateView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            portStateView.itemAnimator = DefaultItemAnimator()
            adapter?.notifyDataSetChanged()
            adapter = PortStateAdapter(getPortStateLists(), context)
        }

        if (PropertiesKotlin.mobileCusStocks.isNullOrEmpty()) {
            stateLayout.removeView(portStateView)
            textNoData.visibility = View.VISIBLE
            textNoData.text = getString(R.string.port_not_data)

        } else {
            stateLayout.removeView(textNoData)
            i.add(DataHeader(getString(R.string.cp_pro)))
            var vColor = ""
            for (stocks in PropertiesKotlin.mobileCusStocks) {

                vColor = if (stocks.productColor == "") {
                    "#000000"
                } else {
                    "#" + stocks.productColor
                }
                i.add(
                    DataPortState(
                        stocks.productName!!, vColor,
                        getString(R.string.port_mag), stocks.depositMargin!!,
                        getString(R.string.port_dfs), stocks.depositSell!!,
                        getString(R.string.port_dfb), stocks.depositBuy!!,
                        getString(R.string.port_fix), stocks.goldSaving!!
                    )
                )
            }
            portStateView.adapter?.notifyDataSetChanged()
        }

    }

    data class DataHeader(var header: String)
    data class DataPortState(
        var product: String, var color: String,
        var label1: String, var value1: String,
        var label2: String, var value2: String,
        var label3: String, var value3: String,
        var label4: String, var value4: String,
    )

    private fun getPortStateLists(): ArrayList<Any> {
        return i
    }

    class HeaderViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_set_header, parent, false)) {
        private var wgHeaderBg: LinearLayout? = null
        private var wgHeader: TextView? = null

        init {
            wgHeaderBg = itemView.findViewById(R.id.settings_header_container)
            wgHeader = itemView.findViewById(R.id.settings_header)
        }

        fun bind(header: DataHeader) {
            val v: View = this.itemView

            val bg = ContextCompat.getDrawable(v.context, R.drawable.gd_header)
            wgHeaderBg?.background = bg

            wgHeader?.text = header.header
            wgHeader?.setTextColor(
                ContextCompat.getColor(v.context, R.color.text_black)
            )
            wgHeader?.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    class StateViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_port_state, parent, false)) {
        private var wgCont: LinearLayout? = null
        private var wgProd: TextView? = null
        private var wgLb1: TextView? = null
        private var wgVal1: TextView? = null
        private var wgLb2: TextView? = null
        private var wgVal2: TextView? = null
        private var wgLb3: TextView? = null
        private var wgVal3: TextView? = null
        private var wgLb4: TextView? = null
        private var wgVal4: TextView? = null

        init {
            wgCont = itemView.findViewById(R.id.port_state_row_container)

            wgProd = itemView.findViewById(R.id.port_state_prod)

            wgLb1 = itemView.findViewById(R.id.port_state_lb1)
            wgLb2 = itemView.findViewById(R.id.port_state_lb2)
            wgLb3 = itemView.findViewById(R.id.port_state_lb3)
            wgLb4 = itemView.findViewById(R.id.port_state_lb4)

            wgVal1 = itemView.findViewById(R.id.port_state_val1)
            wgVal2 = itemView.findViewById(R.id.port_state_val2)
            wgVal3 = itemView.findViewById(R.id.port_state_val3)
            wgVal4 = itemView.findViewById(R.id.port_state_val4)
        }

        fun bind(data: DataPortState) {
            val v: View = this.itemView

//            wgCont?.setBackgroundColor(
//                ContextCompat.getColor(v.context, Color.parseColor(data.color))
//            )

            wgCont?.setBackgroundColor(Color.parseColor(data.color))



            wgProd?.text = data.product

            wgLb1?.text = data.label1
            wgLb2?.text = data.label2
            wgLb3?.text = data.label3
            wgLb4?.text = data.label4

            wgVal1?.text = data.value1
            wgVal2?.text = data.value2
            wgVal3?.text = data.value3
            wgVal4?.text = data.value4
        }
    }

    private class PortStateAdapter(val list: List<Any>, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val heads: Int = 0
        private val rows: Int = 1

        override fun getItemCount() = list.size

        override fun getItemViewType(position: Int): Int {
            when (list[position]) {
                is DataHeader -> {
                    return heads
                }
                is DataPortState -> {
                    return rows
                }
            }
            return rows
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                heads -> {
                    return HeaderViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
                rows -> {
                    return StateViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
            }
            return StateViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                heads -> {
                    val v = holder as HeaderViewHolder
                    configureHeader(v, position)
                }
                rows -> {
                    val v = holder as StateViewHolder
                    configureRow(v, position)
                }
            }
        }

        private fun configureHeader(holder: HeaderViewHolder, position: Int) {
            val items: DataHeader = list[position] as DataHeader
            holder.bind(DataHeader(items.header))
        }

        private fun configureRow(holder: StateViewHolder, position: Int) {
            val items: DataPortState = list[position] as DataPortState
            holder.bind(
                DataPortState(
                    items.product, items.color,
                    items.label1, items.value1,
                    items.label2, items.value2,
                    items.label3, items.value3,
                    items.label4, items.value4,
                )
            )
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PortStatementFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PortStatementFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = PortStatementFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onResume() {
        super.onResume()
        PropertiesKotlin.state = state
    }


}
