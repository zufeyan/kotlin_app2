package com.sctgold.goldinvest.android.fragments

import android.content.Context
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.sctgold.goldinvest.android.activities.HomeActivity
import com.sctgold.goldinvest.android.util.PropertiesKotlin
import com.sctgold.goldinvest.android.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.*
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout

//import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.*
import androidx.appcompat.app.AlertDialog

//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.cardview.widget.CardView
//import androidx.fragment.app.Fragment
//import com.clevel.goldinvest.android.activities.HomeActivity
//import com.clevel.goldinvest.android.PropertiesKotlin
//import com.google.android.material.tabs.TabLayout
//import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var context: AppCompatActivity
    private lateinit var frame: FrameLayout
    private lateinit var map: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var contact: CardView
    private lateinit var brand: ImageView
    private lateinit var us: TextView
    private lateinit var company: TextView
    private lateinit var info: TextView

    private var lat: Double = 0.00
    private var lot: Double = 0.00
    private lateinit var place: LatLng
    private lateinit var marker: Marker
    var state: String = "ContactFragment"

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
        val v = inflater.inflate(R.layout.fragment_contact, container, false)

        frame = v.findViewById(R.id.frag_contact)
        map = v.findViewById(R.id.contact_map_view)
        contact = v.findViewById(R.id.contact_card)
        brand = v.findViewById(R.id.contact_logo)
        us = v.findViewById(R.id.contact_us)
        company = v.findViewById(R.id.contact_org)
        info = v.findViewById(R.id.contact_detail)
//
        lat = getString(R.string.latitude).toDouble()
        lot = getString(R.string.longtitude).toDouble()
        place = LatLng(lat, lot)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        map.getMapAsync(this)
        map.onCreate(savedInstanceState)
        map.onResume()

        val valHeight = frame.layoutParams.height / 2
        contact.layoutParams.height = valHeight
        brand.setImageResource(R.drawable.logo_app1)
        us.text = getString(R.string.contact)
        company.text = getString(R.string.company)
        info.text = getString(R.string.info)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *...........................
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactFragment().apply {
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

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map.onLowMemory()
    }

    override fun onMapReady(p0: GoogleMap) {
        this.googleMap = p0
        val screen = resources.displayMetrics.heightPixels * 0.75
        val info = contact.height
        val pad = (screen - info).toInt()
        googleMap.setPadding(0, 0, 0, pad)
        setUpMap()
    }



    private fun setUpMap() {
        marker = googleMap.addMarker(
            MarkerOptions()
                .position(place)
                .title(getString(R.string.branch))
        )!!
        marker.showInfoWindow()
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.setAllGesturesEnabled(true)
        MapsInitializer.initialize(this.context)
        moveToCurrentLocation(place)
    }

    private fun moveToCurrentLocation(currentLocation: LatLng) {
        googleMap.moveCamera(newLatLngZoom(currentLocation, 16f))
        googleMap.animateCamera(CameraUpdateFactory.zoomIn())
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16f), 3000, null)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title =  (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = getString(R.string.tcont)
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
//    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title =  (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.tcont)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item1 = menu.findItem(R.id.to_call_hot_line)
        item1.isVisible = true
        val item2 = menu.findItem(R.id.to_www)
        item2.isVisible = true
    }

    override fun onOptionsItemSelected(itemMenu: MenuItem): Boolean {
        when (itemMenu.itemId) {
            R.id.to_call_hot_line -> {
                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }
                builder?.setTitle(getString(R.string.btn_call_contact))
                    ?.setMessage(getString(R.string.btn_call_contact_now))
                    ?.setPositiveButton(getString(R.string.btn_call),
                        DialogInterface.OnClickListener { dialog, id ->
                            builder.create().dismiss()
                        })
                    ?.setNegativeButton(getString(R.string.btn_close),
                        DialogInterface.OnClickListener { dialog, id ->
                            builder.create().dismiss()
                        })
                builder?.create()?.show()
                return true
            }
            R.id.to_www -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.goldinvestplus.com")))
                return true
            }
        }
        return super.onOptionsItemSelected(itemMenu)
    }


}
