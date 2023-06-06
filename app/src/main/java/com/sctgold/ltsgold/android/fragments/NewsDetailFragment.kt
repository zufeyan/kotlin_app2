package com.sctgold.ltsgold.android.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.util.ProgressDialog
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var wgNewsPhoto: ImageView
    private lateinit var wgNewsHeader: TextView
    private lateinit var wgNewsBody: TextView
    private lateinit var wgNewsPost: TextView
    lateinit var dialog: Dialog
    var state: String = "NewsDetailFragment"

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
        val v = inflater.inflate(R.layout.fragment_news_detail, container, false)
//        val toolbar = (activity as HomeActivity).findViewById<Toolbar>(R.id.main_toolbar)
//        toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
//        toolbar.setNavigationOnClickListener {   (activity as HomeActivity).popBackStack() }


        dialog = ProgressDialog.progressDialog(context)
        dialog.show()
        wgNewsPhoto = v.findViewById(R.id.news_detail_photo)
        wgNewsHeader = v.findViewById(R.id.news_detail_header)
        wgNewsBody = v.findViewById(R.id.news_detail_body)
        wgNewsPost = v.findViewById(R.id.news_detail_post)

        var title = arguments?.getString("title")
        var description = arguments?.getString("sub")
        var postdate = arguments?.getString("date")
        var photo = arguments?.getString("photo")

        Glide.with(context).load(photo).into(wgNewsPhoto)


        wgNewsHeader.text = title
        wgNewsBody.text = description
        wgNewsPost.text = getString(R.string.pf_post) + " " + postdate

        dialog.dismiss()


        return v
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val heads =
//            "Translations: Can you help translate this site into a foreign language ? Please email us with details if you can help."
//        val bodies =
//            "when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries. when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries.when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries."
//        val posts = getString(R.string.pf_post) + " xx/xx/xxxx | hh:mm:ss"
//
//        wgNewsPhoto.setImageResource(R.drawable.graph)
//        wgNewsHeader.text = heads
//        wgNewsBody.text = bodies
//        wgNewsPost.text = posts
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewsDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.INVISIBLE
//    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
        val item3 = menu.findItem(R.id.to_checked)
        item3.isEnabled = false
        val item4 = menu.findItem(R.id.to_price_alert)
        item4.isVisible = false

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.tnewd)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

}