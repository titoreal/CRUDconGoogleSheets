package com.titin.crudcongsheets

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.StringRequest;
import com.titin.crudcongsheets.databinding.ActivityUserAddBinding;

class UserAddActivity : BaseUserActivity<ActivityUserAddBinding>() {
    override fun getViewBinding(): ActivityUserAddBinding =
        ActivityUserAddBinding.inflate(layoutInflater)

    override fun initializeViews() {
        binding.btnAddUser.setOnClickListener(this)
        binding.btnImage.setOnClickListener(this)
    }

    override fun setImageViewBitmap(bitmap: Bitmap) {
        binding.ivUphoto.setImageBitmap(bitmap)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.btnAddUser -> addUser()
            binding.btnImage -> showFileChooser()
        }
    }

    private fun addUser() {
        binding.btnAddUser.isEnabled = false
        val loading = showLoadingDialog("Adding...")
        val stringRequest = createAddUserRequest { response ->
            loading.dismiss()
            binding.btnAddUser.isEnabled = true
            Toast.makeText(this, response, Toast.LENGTH_LONG).show()

        }
        (application as CrudcongsheetsApplication).requestQueue.add(stringRequest)
    }

    private fun createAddUserRequest(onSuccess: (String) -> Unit): StringRequest {
        val userId = binding.etUid.text.toString().trim()
        val userName = binding.etUname.text.toString().trim()
        val userApellido = binding.etUapellido.text.toString().trim()
        val userPhone = binding.etUphone.text.toString().trim()

        return object : StringRequest(
            Method.POST,
            Configuration.ADD_USER_URL,
            onSuccess,
            { handleError(it) }
        ) {
            override fun getParams(): MutableMap<String, String> = hashMapOf(
                Configuration.Keys.ACTION to "insert",
                Configuration.Keys.ID to userId,
                Configuration.Keys.NAME to userName,
                Configuration.Keys.APELLIDO to userApellido,
                Configuration.Keys.PHONE to userPhone,
                Configuration.Keys.IMAGE to (userImage ?: "")
            )
        }.apply {
            retryPolicy = DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }
    }



    private fun handleError(error: VolleyError) {
        Log.e(TAG, "Error adding user", error)
        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
    }
}
