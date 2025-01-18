package com.titin.crudcongsheets


import android.app.ProgressDialog
import android.graphics.Bitmap;
import android.net.Uri
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import androidx.activity.result.contract.ActivityResultContracts


abstract class BaseUserActivity<T : ViewBinding> : AppCompatActivity(), View.OnClickListener {
    protected lateinit var binding: T
    protected var userImage: String? = null
    protected var rbitmap: Bitmap? = null
    protected var hasNewImage = false

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { processImage(it) }
    }

    companion object {
        private const val TAG = "BaseUserActivity"
        private const val MAX_IMAGE_SIZE = 250
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        initializeViews()
    }

    protected abstract fun getViewBinding(): T
    protected abstract fun initializeViews()
    protected abstract fun setImageViewBitmap(bitmap: Bitmap)

    protected fun showFileChooser() {
        imagePickerLauncher.launch("image/*")
    }

    private fun processImage(filePath: Uri) {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
            rbitmap = getResizedBitmap(bitmap, MAX_IMAGE_SIZE)
            rbitmap?.let {
                userImage = getStringImage(it)
                setImageViewBitmap(it)
                hasNewImage = true
                Log.d(TAG, "Nueva imagen procesada")
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error procesando la imagen", e)
        }
    }
    protected fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        val scale = if (image.width > image.height) {
            maxSize.toFloat() / image.width
        } else {
            maxSize.toFloat() / image.height
        }

        val newWidth = (image.width * scale).toInt()
        val newHeight = (image.height * scale).toInt()

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
    }

    protected fun getStringImage(bmp: Bitmap): String =
        ByteArrayOutputStream().use { baos ->
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        }

    protected fun showLoadingDialog(message: String): ProgressDialog =
        ProgressDialog.show(this, null, message, false, false)
}