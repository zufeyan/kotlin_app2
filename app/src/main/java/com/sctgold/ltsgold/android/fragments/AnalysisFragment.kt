package com.sctgold.ltsgold.android.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.rest.NewsService
import com.sctgold.ltsgold.android.dto.NewsListDTO
import com.sctgold.ltsgold.android.util.ProgressDialog
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.sctgold.ltsgold.android.rest.MobileFrontService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AnalysisFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AnalysisFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var wgAnalPhoto: ImageView
    private lateinit var wgAnalHeader: TextView
    private lateinit var wgAnalBody: TextView
    private lateinit var wgAnalPost: TextView

    lateinit var dialog: Dialog
    var state: String = "AnalysisFragment"

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
        val v = inflater.inflate(R.layout.fragment_analysis, container, false)

        wgAnalPhoto = v.findViewById(R.id.analysis_photo)
        wgAnalHeader = v.findViewById(R.id.analysis_header)
        wgAnalBody = v.findViewById(R.id.analysis_body)
        wgAnalPost = v.findViewById(R.id.analysis_post)
//        llProgressBar = v.findViewById(R.id.progressBar)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = ProgressDialog.progressDialog(context)
        dialog.show()

//        val builder = AlertDialog.Builder(context)
//        builder.setCancelable(false) // if you want user to wait for some process to finish,
//        builder.setView(R.layout.layout_progress_bar)
//        val dialog = builder.create()
//        dialog.show()
        getAnalysis()
//        dialog.dismiss()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AnalysisFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AnalysisFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getAnalysis() {
        val retrofit = Retrofit.Builder()
            .baseUrl(PropertiesKotlin.sctUrl).client(MobileFrontService.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(NewsService::class.java)
        val call = service.getDailyAnalysisList()
        Log.e("xxxxxxxxxxxxxxxxxxx555  : ", "getAnalysis")
        call.enqueue(object : Callback<NewsListDTO> {
            override fun onResponse(call: Call<NewsListDTO>, response: Response<NewsListDTO>) {

                Log.e("xxxxxxxxxxxxxxxxxxx555  : ", response.body().toString())
                when (response.body()?.response) {
                    "OPERATION_SUCCEED" -> {
                        if (response.body()!!.newsDTO.isNotEmpty()) {
                            dialog.dismiss()
                            for (analysis in response.body()!!.newsDTO) {
                                Glide.with(context).load(analysis.picture).into(wgAnalPhoto)

                                wgAnalHeader.text = analysis.title
                                wgAnalBody.text = analysis.description
                                wgAnalPost.text = getString(R.string.pf_post) + analysis.postdate
                            }
                        }


                    }
                }
            }

            override fun onFailure(call: Call<NewsListDTO>, t: Throwable) {
                Log.e("ERROR ", t.message.toString())
            }

        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text =  getString(R.string.tanal)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

}