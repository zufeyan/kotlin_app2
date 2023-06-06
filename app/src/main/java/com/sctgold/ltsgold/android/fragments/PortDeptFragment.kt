package com.sctgold.ltsgold.android.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import kotlinx.android.synthetic.main.row_port_dept.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PortDeptFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortDeptFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var header: TextView
    private lateinit var portDeptView: RecyclerView
    var state: String = "PortDeptFragment"


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
        val v = inflater.inflate(R.layout.fragment_port_dept, container, false)

        header = v.findViewById(R.id.port_dept_header)
        portDeptView = v.findViewById(R.id.port_dept_view)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        header.text = getString(R.string.port_out)
        portDeptView.apply {
            portDeptView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            portDeptView.itemAnimator = DefaultItemAnimator()
            adapter?.notifyDataSetChanged()
            adapter = PortDeptAdapter(getPortDeptLists(), context)
        }
    }

    data class DataPortDept(var labels: String, var values: String)

    private fun getPortDeptLists(): ArrayList<DataPortDept> {
        val item: ArrayList<DataPortDept> = ArrayList()

        item.add(
            DataPortDept(
                getString(R.string.port_cus),
                PropertiesKotlin.arAmount
            )
        )

        item.add(
            DataPortDept(
                getString(R.string.port_com),
                PropertiesKotlin.apAmount
            )
        )

        return item
    }

    private class PortDeptAdapter(private val lists: List<DataPortDept>, val context: Context) :
        RecyclerView.Adapter<PortDeptAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_port_dept, parent, false)
            )
        }

        override fun getItemCount() = lists.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data: DataPortDept = lists[position]

            holder.itemView.port_dept_row_container.setBackgroundColor(
                ContextCompat.getColor(context, R.color.tint)
            )
            holder.itemView.port_dept_lb.text = data.labels
            holder.itemView.port_dept_val.text = data.values
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PortDeptFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            PortDeptFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = PortDeptFragment()
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