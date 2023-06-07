package com.sctgold.wgold.android.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException


class ImageInputHelper {

    val REQUEST_PICTURE_FROM_GALLERY = 23
    val REQUEST_PICTURE_FROM_CAMERA = 24
    val REQUEST_CROP_PICTURE = 25
    private val TAG = "ImageInputHelper"


    private var tempFileFromSource: File? = null
    private var tempUriFromSource: Uri? = null

    private var tempFileFromCrop: File? = null
    private var tempUriFromCrop: Uri? = null


    private lateinit var mContext: Activity

    private var fragment: Fragment? = null
    private var imageActionListener: ImageActionListener? = null

    fun ImageInputHelper(mContext: Activity?) {
        if (mContext != null) {
            this.mContext = mContext
        }
    }

    fun ImageInputHelper(fragment: Fragment) {
        this.fragment = fragment
        mContext = fragment.requireActivity()
    }

    fun setImageActionListener(imageActionListener: ImageActionListener?) {
        this.imageActionListener = imageActionListener
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_PICTURE_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Image selected from gallery")
            imageActionListener?.onImageSelectedFromGallery(data.data, tempFileFromSource)
        } else if (requestCode == REQUEST_PICTURE_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Image selected from camera")
            imageActionListener?.onImageTakenFromCamera(tempUriFromSource, tempFileFromSource)
        } else if (requestCode == REQUEST_CROP_PICTURE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Image returned from crop")
            imageActionListener?.onImageCropped(tempUriFromCrop, tempFileFromCrop)
        }
    }

    fun selectImageFromGallery() {
        checkListener()
        if (tempFileFromSource == null) {
            try {
                tempFileFromSource =
                    File.createTempFile("choose", "png", mContext.externalCacheDir)
                tempUriFromSource = Uri.fromFile(tempFileFromSource)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUriFromSource)
        if (fragment == null) {
            mContext.startActivityForResult(intent, REQUEST_PICTURE_FROM_GALLERY)
        } else {
            fragment!!.startActivityForResult(intent, REQUEST_PICTURE_FROM_GALLERY)
        }
    }


    fun takePhotoWithCamera() {
        checkListener()
        if (tempFileFromSource == null) {
            try {
                tempFileFromSource =
                    File.createTempFile("choose", "png", mContext.externalCacheDir)
                tempUriFromSource = Uri.fromFile(tempFileFromSource)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUriFromSource)
        if (fragment == null) {
            mContext.startActivityForResult(intent, REQUEST_PICTURE_FROM_CAMERA)
        } else {
            fragment!!.startActivityForResult(intent, REQUEST_PICTURE_FROM_CAMERA)
        }
    }

    fun requestCropImage(uri: Uri?, outputX: Int, outputY: Int, aspectX: Int, aspectY: Int) {
        checkListener()
        if (tempFileFromCrop == null) {
            try {
                tempFileFromCrop = File.createTempFile("crop", "png", mContext.externalCacheDir)
                tempUriFromCrop = Uri.fromFile(tempFileFromCrop)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // open crop intent when user selects image
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("output", tempUriFromCrop)
        intent.putExtra("outputX", outputX)
        intent.putExtra("outputY", outputY)
        intent.putExtra("aspectX", aspectX)
        intent.putExtra("aspectY", aspectY)
        intent.putExtra("scale", true)
        intent.putExtra("noFaceDetection", true)
        if (fragment == null) {
            mContext.startActivityForResult(intent, REQUEST_CROP_PICTURE)
        } else {
            fragment!!.startActivityForResult(intent, REQUEST_CROP_PICTURE)
        }
    }


    private fun checkListener() {
        if (imageActionListener == null) {
            throw RuntimeException("ImageSelectionListener must be set before calling openGalleryIntent(), openCameraIntent() or requestCropImage().")
        }
    }

    /**
     * Listener interface for receiving callbacks from the ImageInputHelper.
     */
    interface ImageActionListener {
        fun onImageSelectedFromGallery(uri: Uri?, imageFile: File?)
        fun onImageTakenFromCamera(uri: Uri?, imageFile: File?)
        fun onImageCropped(uri: Uri?, imageFile: File?)
    }


}
