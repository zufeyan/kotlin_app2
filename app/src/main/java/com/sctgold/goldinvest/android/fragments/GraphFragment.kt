package com.sctgold.goldinvest.android.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.R
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout

class GraphFragment : Fragment() {

    var state: String = "GraphFragment"
    private lateinit var web: WebView
    val url = "https://www.tradingview.com/chart/?symbol=XAUUSD"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_graph, container, false)

         web = v.findViewById(R.id.web_graph)
        loadWeb()


        return v
    }

    fun loadWeb(){
        web.loadUrl(url)
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        web.reload()
        val settings = web.settings
        settings.javaScriptEnabled = true
    }

    companion object {
        fun newInstance(): GraphFragment = GraphFragment()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text =  getString(R.string.tgrap)
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
//    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.to_delete)
        item.isVisible = false
    }

    override fun onOptionsItemSelected(itemMenu: MenuItem): Boolean {
        when (itemMenu.itemId) {
            R.id.to_delete -> {
                loadWeb()
                return true
            }
        }
        return super.onOptionsItemSelected(itemMenu)
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text =  getString(R.string.tgrap)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

}
