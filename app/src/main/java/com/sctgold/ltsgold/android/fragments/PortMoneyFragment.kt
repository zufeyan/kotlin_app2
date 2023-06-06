package com.sctgold.ltsgold.android.fragments

import android.content.Context
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
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PortMoneyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortMoneyFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var header: TextView
    private lateinit var portMoneyView: RecyclerView
    var state: String = "PortMoneyFragment"

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
        val v = inflater.inflate(R.layout.fragment_port_money, container, false)

        header = v.findViewById(R.id.port_money_header)
        portMoneyView = v.findViewById(R.id.port_money_view)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.text = getString(R.string.port_mon)
        portMoneyView.apply {
            portMoneyView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            portMoneyView.itemAnimator = DefaultItemAnimator()
            adapter?.notifyDataSetChanged()
            adapter = PortMoneyAdapter(getPortMoneyLists(), context)
        }
    }

    data class DataHeader(var header: String)
    data class DataPortMoney(
        var label1: String, var value1: String,
        var label2: String, var value2: String,
        var label3: String, var value3: String,
        var label4: String, var value4: String,
        var label5: String, var value5: String
    )

    private fun getPortMoneyLists(): ArrayList<Any> {
        val i: ArrayList<Any> = ArrayList()

        i.add(DataHeader(getString(R.string.port_mon_hd)))
        i.add(
            DataPortMoney(
                getString(R.string.port_mag),    PropertiesKotlin.cashDepositMargin ,
                getString(R.string.port_deb),   PropertiesKotlin.cashDeposit ,
                getString(R.string.port_avb),      PropertiesKotlin.cashDepositBuy ,
                getString(R.string.port_nus), PropertiesKotlin.holdMoney,
                getString(R.string.port_nub), PropertiesKotlin.holdMoneyTmp
            )
        )

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
                ContextCompat.getColor(v.context, R.color.text)
            )
            wgHeader?.gravity = Gravity.CENTER_HORIZONTAL
        }
    }

    class MoneyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_port_money, parent, false)) {
        private var wgCon: LinearLayout? = null
        private var wgLb1: TextView? = null
        private var wgVal1: TextView? = null
        private var wgLb2: TextView? = null
        private var wgVal2: TextView? = null
        private var wgLb3: TextView? = null
        private var wgVal3: TextView? = null
        private var wgLb4: TextView? = null
        private var wgVal4: TextView? = null
        private var wgLb5: TextView? = null
        private var wgVal5: TextView? = null

        init {
            wgCon = itemView.findViewById(R.id.port_money_row_container)

            wgLb1 = itemView.findViewById(R.id.port_money_lb1)
            wgLb2 = itemView.findViewById(R.id.port_money_lb2)
            wgLb3 = itemView.findViewById(R.id.port_money_lb3)
            wgLb4 = itemView.findViewById(R.id.port_money_lb4)
            wgLb5 = itemView.findViewById(R.id.port_money_lb5)

            wgVal1 = itemView.findViewById(R.id.port_money_val1)
            wgVal2 = itemView.findViewById(R.id.port_money_val2)
            wgVal3 = itemView.findViewById(R.id.port_money_val3)
            wgVal4 = itemView.findViewById(R.id.port_money_val4)
            wgVal5 = itemView.findViewById(R.id.port_money_val5)
        }

        fun bind(data: DataPortMoney) {
            val v: View = this.itemView

            wgCon?.setBackgroundColor(
                ContextCompat.getColor(v.context, R.color.white)
            )
            wgLb1?.text = data.label1
            wgLb2?.text = data.label2
            wgLb3?.text = data.label3
            wgLb4?.text = data.label4
            wgLb5?.text = data.label5

            wgVal1?.text = data.value1
            wgVal2?.text = data.value2
            wgVal3?.text = data.value3
            wgVal4?.text = data.value4
            wgVal5?.text = data.value5
        }
    }

    private class PortMoneyAdapter(val list: List<Any>, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val heads: Int = 0
        private val rows: Int = 1

        override fun getItemCount() = list.size

        override fun getItemViewType(position: Int): Int {
            when (list[position]) {
                is DataHeader -> {
                    return heads
                }
                is DataPortMoney -> {
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
                    return MoneyViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
            }
            return MoneyViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                heads -> {
                    val v = holder as HeaderViewHolder
                    configureHeader(v, position)
                }
                rows -> {
                    val v = holder as MoneyViewHolder
                    configurePort(v, position)
                }
            }
        }

        private fun configureHeader(holder: HeaderViewHolder, position: Int) {
            val items: DataHeader = list[position] as DataHeader
            holder.bind(DataHeader(items.header))
        }

        private fun configurePort(holder: MoneyViewHolder, position: Int) {
            val items: DataPortMoney = list[position] as DataPortMoney
            holder.bind(
                DataPortMoney(
                items.label1, items.value1,
                items.label2, items.value2,
                items.label3, items.value3,
                items.label4, items.value4,
                items.label5, items.value5
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
         * @return A new instance of fragment PortMoneyFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PortMoneyFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = PortMoneyFragment()
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