package com.sctgold.ltsgold.android.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sctgold.ltsgold.android.R
import com.sctgold.ltsgold.android.activities.HomeActivity
import com.sctgold.ltsgold.android.dto.ActionResult
import com.sctgold.ltsgold.android.listener.OnResultPermissionCallbackListener
import com.sctgold.ltsgold.android.util.PropertiesKotlin
import com.sctgold.ltsgold.android.util.AppConstants
import com.google.android.material.tabs.TabLayout
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PaymentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PaymentFragment : Fragment() , OnResultPermissionCallbackListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val TAG = "PAYMENT-FM"

    lateinit var context: AppCompatActivity
    private lateinit var payView: RecyclerView
    private lateinit var payPreView: RecyclerView

    private var asyncUploadTask: WeakReference<UploadFileAsyncTask>? = null

    var mCurrentPhotoPath: String? = null
    var mCapturedImageURI: Uri? = null
    var encryptTokenKey: String? = null
    var uploadUri: String? = null
    var state: String = "PaymentFragment"

    val REQUEST_TAKE_PHOTO = 1001
    val SELECT_IMAGE = 1002

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
        val v = inflater.inflate(R.layout.fragment_payment, container, false)

        payView = v.findViewById(R.id.pay_view)
        payPreView = v.findViewById(R.id.pay_preview)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        payView.apply {
            payView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false) }
            payView.itemAnimator = DefaultItemAnimator()
            adapter = PaymentAdapter(getPaymentList(), context)
        }

        // set add photo previews
        payPreView.apply {
            payPreView.layoutManager =
                activity?.let { LinearLayoutManager(it, LinearLayoutManager.HORIZONTAL, false) }
            payPreView.itemAnimator = DefaultItemAnimator()
            adapter = PaymentAdapter(addPreviewList(), context)
        }

    }

    data class DataHeader(var header: String)
    data class DataAttach(
        var icon: Int,
        var names: String,
        var detail: String
    )

    data class DataPreview(var photo: Uri)

    private fun getPaymentList(): ArrayList<Any> {
        val item: ArrayList<Any> = ArrayList()

        item.add(DataHeader(getString(R.string.py_head)))

        item.add(
            DataAttach(
                R.drawable.ic_v_photo_camera,
                getString(R.string.py_cam),
                getString(R.string.py_cams)
            )
        )

        item.add(
            DataAttach(
                R.drawable.ic_v_photo_library,
                getString(R.string.py_gal),
                getString(R.string.py_gals)
            )
        )

        return item
    }

    private fun addPreviewList(): ArrayList<Any> {
        return ArrayList()
    }

    class HeaderViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_set_header, parent, false)) {
        private var wgHeaderSection: TextView? = null

        init {
            wgHeaderSection = itemView.findViewById(R.id.settings_header)
        }

        fun bind(header: DataHeader) {
            //val v: View = this.itemView
            wgHeaderSection?.text = header.header.uppercase(Locale.ROOT)
            wgHeaderSection?.textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
    }

    class PaymentViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_pay, parent, false)) {

        private var wgIcon: ImageView? = null
        private var wgType: TextView? = null
        private var wgDetail: TextView? = null

        init {
            wgIcon = itemView.findViewById(R.id.pay_photo)
            wgType = itemView.findViewById(R.id.pay_title)
            wgDetail = itemView.findViewById(R.id.pay_sub)
        }

        fun bind(data: DataAttach) {
            val v: View = this.itemView

            wgIcon?.setImageDrawable(ContextCompat.getDrawable(v.context, data.icon))
            wgIcon?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(v.context, R.color.tint),
                PorterDuff.Mode.SRC_IN
            )

            wgType?.text = data.names
            wgType?.setTextColor(ContextCompat.getColor(v.context, R.color.tint))

            wgDetail?.text = data.detail
            wgDetail?.setTextColor(ContextCompat.getColor(v.context, R.color.text_tag))


        }
    }

    class PreviewViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.row_thumbnail, parent, false)) {

        private var wgPhoto: ImageView? = null

        init {
            wgPhoto = itemView.findViewById(R.id.pay_thumbnail)
        }

        fun bind(data: DataPreview) {
            wgPhoto?.setImageURI(data.photo)
        }
    }

    private class PaymentAdapter(private val lists: MutableList<Any>, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val sections: Int = 0
        private val pays: Int = 1
        private val previews: Int = 2

        override fun getItemCount() = lists.size

        override fun getItemViewType(position: Int): Int {
            when (lists[position]) {
                is DataHeader -> {
                    return sections
                }
                is DataAttach -> {
                    return pays
                }
                is DataPreview -> {
                    return previews
                }
            }
            return pays
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            when (viewType) {
                sections -> {
                    return HeaderViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
                pays -> {
                    return PaymentViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
                previews -> {
                    return PreviewViewHolder(
                        LayoutInflater.from(parent.context),
                        parent
                    )
                }
            }
            return PaymentViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (holder.itemViewType) {
                sections -> {
                    configureHeader(holder as HeaderViewHolder, position)
                }
                pays -> {
                    configurePays(holder as PaymentViewHolder, position)
                }
                previews -> {
                    configurePreviews(holder as PreviewViewHolder, position)
                }
            }
        }

        fun addItem(data: DataPreview) {
            lists.add(data)
            notifyItemInserted(lists.size)
        }

        private fun configureHeader(holder: HeaderViewHolder, position: Int) {
            val items: DataHeader = lists[position] as DataHeader
            holder.bind(DataHeader(items.header))
        }

        private fun configurePays(holder: PaymentViewHolder, position: Int) {
            val items: DataAttach = lists[position] as DataAttach
            holder.bind(DataAttach(items.icon, items.names, items.detail))
            holder.itemView.setOnClickListener { v ->
                val act = v!!.context as AppCompatActivity
                when (position) {
                    1 -> {
//                        PaymentFragment().openCamera()
                        (context as HomeActivity).requestCameraPermission()
                    }
                    2 -> {
                        (context as HomeActivity).requestGalleryPermission()
                    }
                }
            }
        }

        private fun configurePreviews(holder: PreviewViewHolder, position: Int) {
            val items: DataPreview = lists[position] as DataPreview
            holder.bind(DataPreview(items.photo))
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PaymentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaymentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        fun newInstance(): PaymentFragment = PaymentFragment()

    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
//        title.text = getString(R.string.tpaym)
//        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
//        tabLayout.visibility = View.GONE
//    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT < 19) {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            requireActivity().startActivityForResult(intent, SELECT_IMAGE)

        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/jpeg"
            requireActivity().startActivityForResult(intent, SELECT_IMAGE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        actionResult(requestCode, resultCode, data)
    }
    fun actionResult(requestCode:Int ,  resultCode:Int ,  data:Intent?) {
        Log.d("testddddddddddd  : ",REQUEST_TAKE_PHOTO.toString())
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

        }
    }

//
//    fun actionResult(requestCode: Int, resultCode: Int, data: Intent) {
//    //    LogUtil.debug(TAG, "requestCode + $requestCode")
//        if (cache.getApiEncryptToken(goldSpotMainActivity) == null) {
//          //  LogUtil.debug(TAG, "catch null")
//        } else {
//            if (requestCode == MoreFragment.newInstance().REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
//                try {
////                    LogUtil.debug(TAG, "Take Photo")
////                    //Upload File with file path captured by camera
////                    val asyncTask = UploadFileAsyncTask(this, mCurrentPhotoPath)
////                    asyncUploadTask = WeakReference(asyncTask)
////                    asyncTask.execute()
////                    addPhotoToGallery()
////                    mCurrentPhotoPath = null
////                    mCapturedImageURI = null
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            } else if (requestCode == MoreFragment.newInstance().SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
//              //  LogUtil.debug(TAG, "Select Image")
//
//                val selectedImageUri = data.data
//                val asyncTask = selectedImageUri?.let { UploadFileAsyncTask(this, it) }
//                asyncUploadTask = WeakReference(asyncTask)
//                asyncTask.execute()
//                mCurrentPhotoPath = null
//                mCapturedImageURI = null
//            }
//        }
//       // LogUtil.debug(TAG, "Upload Photo Complete")
//    }



    @kotlin.Throws(/*@@srdbgu@@*/java.io.IOException::class)  open  fun /*@@vocwuv@@*/createImageFile(): /*@@sbbdxz@@*/java.io.File?{
        // Create an image file name
        var  image: /*@@sbbdxz@@*/java.io.File? = null

        val timeStamp: /*@@acljcb@@*/kotlin.String = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val imageFileName: /*@@acljcb@@*/kotlin.String = "IMG_" + timeStamp + "_"
        val storageDir: /*@@sbbdxz@@*/java.io.File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        image = java.io.File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        //        parent.setmCurrentPhotoPath(image.getAbsolutePath());
        return image
    }




    class UploadFileAsyncTask : AsyncTask<Void?, Void?, ActionResult> {
        val MEDIA_TYPE_JPG: MediaType? = AppConstants.UPLOAD_FILE_TYPE.toMediaTypeOrNull()
        private var myWeakContext: WeakReference<PaymentFragment>? = null

        //        private Dialog progressBar = null;
        private val client = OkHttpClient()
        private var filePath: String? = null
        private var fileUri: Uri? = null

        private constructor(moreMainFragment: PaymentFragment, filePath: String) {
            myWeakContext = WeakReference(moreMainFragment)
            //            progressBar = StandardDialog.createProgressDialog(moreMainFragment.getActivity(), R.string.msg_upload_file);
            this.filePath = filePath
        }

        constructor(moreMainFragment: PaymentFragment, fileUri: Uri) {
            myWeakContext = WeakReference(moreMainFragment)
            //            progressBar = StandardDialog.createProgressDialog(moreMainFragment.getActivity(), R.string.msg_upload_file);
            this.fileUri = fileUri
        }

        override fun onPreExecute() {
//            progressBar.show();
        }

        override fun doInBackground(vararg params: Void?): ActionResult? {
            val actionResult = ActionResult()
            actionResult.setNewIntent(false)
            if (myWeakContext?.get() != null) {
                var bos: ByteArrayOutputStream? = null
                try {
                    bos = ByteArrayOutputStream()
                    if (filePath != null) {
                        PropertiesKotlin().scaleImage(filePath!!, bos)
                    } else if (fileUri != null) {
                    //    LogUtil.debug(TAG, "begin finding real path")
                        myWeakContext!!.get()!!.activity?.let {
                            PropertiesKotlin().scaleImage(fileUri!!, bos,
                                it
                            )
                        }
                    }
                    if (PropertiesKotlin.isConnected(myWeakContext!!.get()?.activity) && bos.size() > 0) {
//                        GoldSpotCache cache = GoldSpotCache.getInstance();
                        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart(
                                "filename",
                                "payment.jpg",
                                RequestBody.create(MEDIA_TYPE_JPG, bos.toByteArray())
                            )
                            .build()
                        //  LogUtil.debug(TAG, "requestBody", requestBody)
                        val request: Request? = myWeakContext!!.get()?.encryptTokenKey?.let {
                            myWeakContext!!.get()?.uploadUri?.let { it1 ->
                                Request.Builder()
                                    .header("token", it)
                                    .url(it1)
                                    .post(requestBody)
                                    .build()
                            }
                        }
                        var response: Response
                        var retry = 0
                        do {
                            retry++
                            response = request?.let { client.newCall(it).execute() }!!
//                            LogUtil.debug(
//                                TAG,
//                                "Response Code: {}, str: {}",
//                                response.code(),
//                                response
//                            )
                        } while (!response.isSuccessful || retry >= AppConstants.CONNECTION_maxRetry)
                        if (response.isSuccessful) {
                            actionResult.setMessageId(R.string.msg_dialog_upload_success)
                        } else {
                            actionResult.setMessageId(R.string.msg_dialog_upload_fail)
                        }
                    } else {
                        actionResult.setMessageId(R.string.msg_dialog_noconnectivity_found)
                    }

//                    Thread.sleep(10 * 1000);
                } catch (ex: Exception) {
                    //LogUtil.error(TAG, "Upload Failed with reason:", ex)
                    actionResult.setMessageId(R.string.msg_dialog_upload_fail)
                } finally {
                    try {
                        bos?.close()
                    } catch (ioe: IOException) {
                        //LogUtil.warn(TAG, "Cannot close outputstream", ioe)
                    }
                }
                //Do something with response...
            } else {
                actionResult.setMessageId(R.string.msg_unexpected_error)
            }
            return actionResult
        }

        override fun onPostExecute(actionResult: ActionResult) {
//            progressBar.dismiss();
//            if (myWeakContext != null && myWeakContext!!.get() != null) {
//                myWeakContext!!.get()?.onUploadComplete(actionResult)
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title = (activity as HomeActivity).findViewById<TextView>(R.id.title_main_text)
        title.text = getString(R.string.tpaym)
        val tabLayout = (activity as HomeActivity).findViewById<TabLayout>(R.id.tabs)
        tabLayout.visibility = View.GONE
        PropertiesKotlin.state = state
    }

    override fun onPause() {
        super.onPause()
        Log.e("pay : ","onPause")
//        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
//        ft.remove(this)
//        ft.commit()
    }

    override fun permissionCallback(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
       // openCamera()
    }
}