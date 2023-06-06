package com.sctgold.ltsgold.android.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.google.android.material.tabs.TabLayout

class GuideFragment : Fragment() {
    var state: String = "GuideFragment"
    private lateinit var web: WebView
    val url = "https://knowledge.sctgold.com/Mobile-Guide/index.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        PropertiesKotlin.state = state
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_guide, container, false)

        web = v.findViewById(R.id.web_guide)
        loadWeb()

        return v
    }

    companion object {
        fun newInstance(): GuideFragment = GuideFragment()
    }

    fun loadWeb(){
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        web.reload()

        web.settings.javaScriptEnabled = true
        web.settings.allowContentAccess = true
        web.settings.domStorageEnabled = true
        web.settings.useWideViewPort = true
        web.loadUrl(url)
    }

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
        val title =  (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.tguid)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

}