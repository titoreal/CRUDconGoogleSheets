package com.titin.crudcongsheets

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.squareup.picasso.Picasso
import com.titin.crudcongsheets.databinding.ActivityUserUpdateBinding

class UserUpdateActivity : BaseUserActivity<ActivityUserUpdateBinding>() {
    private var currentUser: User? = null

    override fun getViewBinding(): ActivityUserUpdateBinding =
        ActivityUserUpdateBinding.inflate(layoutInflater)

    override fun initializeViews() {
        binding.btnUpdateUser.setOnClickListener(this)
        binding.btnImage.setOnClickListener(this)
    }

    override fun setImageViewBitmap(bitmap: Bitmap) {
        binding.ivUphoto.setImageBitmap(bitmap)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViews()

        @Suppress("DEPRECATION")
        intent.getSerializableExtra("UPDATE_USER")?.let { user ->
            currentUser = user as User
            populateUserFields(user)
        }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.btnUpdateUser -> updateUser()
            binding.btnImage -> showFileChooser()
        }
    }

    private fun populateUserFields(user: User) {
        binding.etUid.apply {
            setText(user.id)
            isEnabled = false
        }
        binding.etUname.setText(user.name)
        binding.etUapellido.setText(user.apellido)
        binding.etUphone.setText(user.phone)
        hasNewImage = false

        if (user.imageUrl.isNotEmpty()) {
            val directImageUrl = Configuration.extractDirectImageLink(user.imageUrl)
            userImage = user.imageUrl

            Picasso.get()
                .load(directImageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.ivUphoto)
        }
    }

    private fun updateUser() {
        binding.btnUpdateUser.isEnabled = false
        val loading = showLoadingDialog("Updating...")
        val stringRequest = createUpdateUserRequest(
            onSuccess = {
                handleUpdateSuccess(loading)
            },
            onError = {
                handleUpdateError(loading)
            }
        )
        (application as CrudcongsheetsApplication).requestQueue.add(stringRequest)
    }

    private fun createUpdateUserRequest(
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ): StringRequest {
        val userId = binding.etUid.text.toString().trim()
        val userName = binding.etUname.text.toString().trim()
        val userApellido = binding.etUapellido.text.toString().trim()
        val userPhone = binding.etUphone.text.toString().trim()

        return object : StringRequest(
            Method.POST,
            Configuration.ADD_USER_URL,
            onSuccess,
            onError
        ) {
            override fun getParams(): MutableMap<String, String> = hashMapOf(
                Configuration.Keys.ACTION to "update",
                Configuration.Keys.ID to userId,
                Configuration.Keys.NAME to userName,
                Configuration.Keys.APELLIDO to userApellido,
                Configuration.Keys.PHONE to userPhone

            ).apply {
                addImageParameter()
            }
        }.apply {
            retryPolicy = DefaultRetryPolicy(
                30000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }
    }

    private fun MutableMap<String, String>.addImageParameter() {
        when {
            hasNewImage && rbitmap != null ->
                put(Configuration.Keys.IMAGE, getStringImage(rbitmap!!))
            !userImage.isNullOrEmpty() ->
                put(Configuration.Keys.IMAGE, userImage!!)
        }
    }

    private fun handleUpdateSuccess(loading: ProgressDialog) {
        loading.dismiss()
        binding.btnUpdateUser.isEnabled = true
        Toast.makeText(this, "Usuario actualizado exitósamente", Toast.LENGTH_LONG).show()
        navigateToUserList()
    }

    private fun handleUpdateError(loading: ProgressDialog) {
        loading.dismiss()
        binding.btnUpdateUser.isEnabled = true
        Toast.makeText(this, "Update Failed", Toast.LENGTH_LONG).show()
    }

    private fun navigateToUserList() {
        Intent(this, UserList::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("REFRESH_LIST", true)
            startActivity(this)
        }
        finish()
    }
}