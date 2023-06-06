//package com.clevel.tmgold.android.activities
//
//import android.annotation.SuppressLint
//import android.app.Dialog
//import android.content.IntentFilter
//import android.net.ConnectivityManager
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import com.clevel.tmgold.android.ConnectivityReceiver
//import com.clevel.tmgold.android.ProgressDialog
//import com.clevel.tmgold.android.PropertiesKotlin
//import android.net.Network
//
//
//@SuppressLint("Registered")
//open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
//    private var isFirst = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        isFirst = true
//
//        registerReceiver(
//            ConnectivityReceiver(),
//            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        )
//    }
//
//    private lateinit var dialogNetwork: Dialog
//    private fun showMessage(isConnected: Boolean) {
//        dialogNetwork = ProgressDialog.networkDialog(this)
//
//
//
//        if (!isConnected) {
//           // dialogNetwork.show()
//
//        } else {
//
//
////            HomeFragment.newInstance().getSocket()
////            HomeFragment.newInstance().getSocket()
//
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d("BaseActivity", " ")
//        ConnectivityReceiver.connectivityReceiverListener = this
//        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//
//
//
//        connectivityManager.let {
//            it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    //take action when network connection is gained
//                    Log.d("myFragment", "onAvailable " +PropertiesKotlin.frag.toString())
//                    if (isFirst) {
//                        isFirst = false
//                    } else {
//                        Log.d("isFirst", ":  $isFirst")
//
//                        //    PropertiesKotlin.manageSocket(context)
//                    }
//
//                }
//
//                override fun onLost(network: Network) {
//                    //take action when network connection is lost
//                    Log.d("onLost", "event $network")
//                }
//            })
//        }
//    }
//
//    /**
//     * Callback will be called when there is change
//     */
//    override fun onNetworkConnectionChanged(isConnected: Boolean) {
//        showMessage(isConnected)
//    }
//
//
//}