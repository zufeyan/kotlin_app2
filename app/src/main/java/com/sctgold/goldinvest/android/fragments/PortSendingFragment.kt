package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import kotlinx.android.synthetic.main.fragment_port_sending.view.*
import kotlinx.android.synthetic.main.row_port_folio.view.*
import kotlinx.android.synthetic.main.row_port_send.view.*
import java.text.DecimalFormat
import kotlin.random.Random

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PortSendingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortSendingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var header: TextView
    private lateinit var portSendView: RecyclerView
    private lateinit var sendingLayout: LinearLayout
    private lateinit var textNoData: TextView
    var state: String = "PortSendingFragment"

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
        val v = inflater.inflate(R.layout.fragment_port_sending, container, false)

        header = v.findViewById(R.id.port_send_header)
        portSendView = v.findViewById(R.id.port_send_view)
        sendingLayout = v.findViewById(R.id.layout_post_sending)
        textNoData = v.findViewById(R.id.text_no_data)



        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textNoData.visibility = View.INVISIBLE
        header.text = getString(R.string.port_sto)
        portSendView.apply {
            portSendView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            portSendView.itemAnimator = DefaultItemAnimator()
            adapter?.notifyDataSetChanged()
            adapter = PortSendingAdapter(getPortSendLists(), context)
        }

    }

    data class DataPortSend(
        var header: String?,
        var color: String?,
        var lb1: String?,
        var lb2: String?,
        var val1: String?,
        var val2: String?
    ): Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(header)
            parcel.writeString(color)
            parcel.writeString(lb1)
            parcel.writeString(lb2)
            parcel.writeString(val1)
            parcel.writeString(val2)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<DataPortSend> {
            override fun createFromParcel(parcel: Parcel): DataPortSend {
                return DataPortSend(parcel)
            }

            override fun newArray(size: Int): Array<DataPortSend?> {
                return arrayOfNulls(size)
            }
        }

    }

    private fun getPortSendLists(): ArrayList<DataPortSend> {
        val item: ArrayList<DataPortSend> = ArrayList()

        val df = DecimalFormat("#,###.##")
        val rdCus: Double = Random.nextDouble(88888888.00, 99999999.99)
        val rdCom: Double = Random.nextDouble(88888888.00, 99999999.99)

        if (PropertiesKotlin.mobileCustomerPortAccruedBacklog.isNullOrEmpty()) {
            sendingLayout.removeView(portSendView)
            textNoData.visibility = View.VISIBLE
            textNoData.text = getString(R.string.port_not_data)

        } else {

            sendingLayout.removeView(textNoData)
            var colorString = ""
            for (send in PropertiesKotlin.mobileCustomerPortAccruedBacklog) {
                colorString = if (send.productColor == "") {
                    "#000000"
                } else {
                    "#" + send.productColor
                }
                item.add(
                    DataPortSend(
                        send.productName!!,
                        colorString,
                        getString(R.string.port_cus),
                        getString(R.string.port_com),
                        send.customerPortfolioAccruedDTO?.weight!!,
                        send.customerPortfolioBacklogDTO?.weight!!,
                    )
                )
            }
        }

//        item.add(
//            DataPortSend(
//                "Gold 99.99%",
//                R.color.g999,
//                getString(R.string.port_cus),
//                getString(R.string.port_com),
//                df.format(rdCus).toString(),
//                df.format(rdCom).toString()
//            )
//        )
        return item
    }

    private class PortSendingAdapter(private val lists: List<DataPortSend>, val context: Context) :
        RecyclerView.Adapter<PortSendingAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_port_send, parent, false)
            )
        }

        override fun getItemCount() = lists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data: DataPortSend = lists[position]

            holder.itemView.port_send_row_container.setBackgroundColor(Color.parseColor(data.color))

            holder.itemView.port_send_row_code.text = data.header
            holder.itemView.port_send_lb1.text = data.lb1
            holder.itemView.port_send_val1.text = data.val1
            holder.itemView.port_send_lb2.text = data.lb2
            holder.itemView.port_send_val2.text = data.val2
        }
    }

    companion object {
        /**+
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PortSendingFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PortSendingFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = PortSendingFragment()
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
