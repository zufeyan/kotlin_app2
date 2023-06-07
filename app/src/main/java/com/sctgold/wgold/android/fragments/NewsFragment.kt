package com.sctgold.wgold.android.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.sctgold.wgold.android.R
import com.sctgold.wgold.android.rest.NewsService
import com.sctgold.wgold.android.activities.HomeActivity
import com.sctgold.wgold.android.dto.NewsListDTO
import com.sctgold.wgold.android.util.ProgressDialog
import com.sctgold.wgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout
import com.sctgold.wgold.android.rest.MobileFrontService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var newsView: RecyclerView
    val item: ArrayList<Any> = ArrayList()
    lateinit var dialog: Dialog
    var state: String = "NewsFragment"
    var beforeState: String = "NewsFragment"

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
        val v = inflater.inflate(R.layout.fragment_news, container, false)

        refresh = v.findViewById(R.id.news_refresh)
        refresh.setColorSchemeResources(R.color.step1, R.color.step2, R.color.step3, R.color.step4)
        refresh.setOnRefreshListener { loadData() }

        newsView = v.findViewById(R.id.news_view)
        dialog = ProgressDialog.progressDialog(context)
        dialog.show()
        getNews()
        setHasOptionsMenu(true)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsView.apply {
            newsView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            newsView.itemAnimator = DefaultItemAnimator()
            adapter = NewsAdapter(getNewsLists(), context)
        }
    }

    data class HeaderRow(var header: String)
    data class NewsRow(
        var photo: String,
        var title: String,
        var sub: String,
        var date: String,
        var id: Int
    )
//    data class NewsRow(var title: String, var sub: String, var date: String,var id: Int)

    private fun getNews() {
        val retrofit = Retrofit.Builder()
            .baseUrl(PropertiesKotlin.sctUrl).client(MobileFrontService.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(NewsService::class.java)
        val call = service.getNewsDaily()

        call.enqueue(object : Callback<NewsListDTO> {
            override fun onResponse(call: Call<NewsListDTO>, response: Response<NewsListDTO>) {
                when (response.body()?.response) {
                    "OPERATION_SUCCEED" -> {

                        Log.e("SUCCESS", response.body().toString())

                        dialog.dismiss()
                        if (response.body()!!.newsDTO.isNotEmpty()) {

                            for (news in response.body()!!.newsDTO) {
                                val itemNewsData: ArrayList<NewsRow> = ArrayList()
                                itemNewsData.add(
                                    NewsRow(
                                        news.picture,
                                        news.title,
                                        Html.fromHtml(news.description,0).toString(),
                                        news.postdate,
                                        news.id
                                    )
                                )


                                item.addAll(itemNewsData)
                            }
                        }
                    }
                }
                newsView.adapter?.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<NewsListDTO>, t: Throwable) {
                Log.e("ERROR ", t.message.toString())
            }

        })


    }

    private fun getNewsLists(): ArrayList<Any> {
        return item
    }

    class HeaderViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_set_header, parent, false)) {
        private var wgHeaderSection: TextView? = null

        init {
            wgHeaderSection = itemView.findViewById(R.id.settings_header)
        }

        fun bind(header: HeaderRow) {
            //val v: View = this.itemView
            wgHeaderSection?.text = header.header.uppercase(Locale.ROOT)
            wgHeaderSection?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
    }

    class NewsViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_news, parent, false)) {
        private var wgNewsContent: LinearLayout? = null
        private var wgNewsPhoto: ImageView? = null
        private var wgNewsTitle: TextView? = null
        private var wgNewsSub: TextView? = null
        private var wgNewsDate: TextView? = null
        private var wgNewsNext: ImageView? = null

        init {
            wgNewsContent = itemView.findViewById(R.id.news_container)
            wgNewsPhoto = itemView.findViewById(R.id.news_photo)
            wgNewsTitle = itemView.findViewById(R.id.news_title)
            wgNewsSub = itemView.findViewById(R.id.news_sub)
            wgNewsDate = itemView.findViewById(R.id.news_date)
            wgNewsNext = itemView.findViewById(R.id.news_read)
        }

        fun bind(newsRow: NewsRow) {
            val v: View = this.itemView
            wgNewsContent?.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                val newsDetailFragment: Fragment = NewsDetailFragment()
                val bundle = Bundle()
                bundle.putString("title", newsRow.title)
                bundle.putString("sub", newsRow.sub)
                bundle.putString("date", newsRow.date)
                bundle.putString("photo", newsRow.photo)
                newsDetailFragment.arguments = bundle
                val fm: FragmentManager = act.supportFragmentManager
                fm.beginTransaction().setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_out_right,
                    R.anim.slide_in_right
                ).replace(R.id.frag_news, newsDetailFragment)
                    .addToBackStack("NEWS").commit()
            }

            wgNewsPhoto?.let { Glide.with(v.context as AppCompatActivity).load(newsRow.photo).into(it) }

//            wgNewsPhoto?.setImageResource(newsRow.photo)
            wgNewsTitle?.text = newsRow.title
            wgNewsSub?.text = newsRow.sub
            val post: String = v.context.getString(R.string.pf_post) + " " + newsRow.date
            wgNewsDate?.text = post
        }

    }

    private class NewsAdapter(val lists: List<Any>, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val sections: Int = 0
        private val news: Int = 1

        override fun getItemCount() = lists.size

        override fun getItemViewType(position: Int): Int {
            when (lists[position]) {
                is HeaderRow -> {
                    return sections
                }
                is NewsRow -> {
                    return news
                }
            }
            return news
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                sections -> {
                    return HeaderViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
                news -> {
                    return NewsViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
            }
            return NewsViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                sections -> {
                    configureHeader(holder as HeaderViewHolder, position)
                }
                news -> {
                    configureNews(holder as NewsViewHolder, position)
                }
            }
        }

        private fun configureHeader(holder: HeaderViewHolder, position: Int) {
            val items: HeaderRow = lists[position] as HeaderRow
            holder.bind(HeaderRow(items.header))
        }

        private fun configureNews(holder: NewsViewHolder, position: Int) {
            val items: NewsRow = lists[position] as NewsRow
            holder.bind(NewsRow(items.photo, items.title, items.sub, items.date, items.id))
        }

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = false
        val item2 = menu.findItem(R.id.to_edit_product)
        item2.isVisible = false
        val item3 = menu.findItem(R.id.to_checked)
        item3.isVisible = false
        val item4 = menu.findItem(R.id.to_price_alert)
        item4.isVisible = false
        val item5 = menu.findItem(R.id.to_email_open)
        item5.isVisible = false
        val item6 = menu.findItem(R.id.to_delete)
        item6.isVisible = false
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
         * @return A new instance of fragment NewsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text =  getString(R.string.tnews)
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as AppCompatActivity

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text =  getString(R.string.tnews)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
        PropertiesKotlin.beforeState = beforeState

        Log.e("newa onResume PropertiesKotlin.state : ", PropertiesKotlin.state)

    }

}
