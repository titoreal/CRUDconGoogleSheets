package com.titin.crudcongsheets

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.titin.crudcongsheets.databinding.ActivityUserListBinding

class UserList : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding
    private lateinit var adapter: UserListAdapter

    // UserList.kt
    private var usersList: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el adapter con la lista vacía
        adapter = UserListAdapter(this, usersList)
        binding.listView.adapter = adapter

        if (savedInstanceState == null) {
            sendRequest()
        }
    }

    override fun onResume() {
        super.onResume()
        // Recargar la lista cada vez que la actividad se resume
        sendRequest()
    }

    private fun sendRequest() {
        val loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false)

        val stringRequest = StringRequest(
            Configuration.LIST_USER_URL,
            { response ->
                showJSON(response)
                loading.dismiss()
            },
            { error ->
                val errorMessage = error?.message ?: "Unknown network error"
                Log.e("UserList", "Error fetching users: $errorMessage")
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                loading.dismiss()
            }
        )

        stringRequest.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        Volley.newRequestQueue(this).add(stringRequest)
    }

    private fun showJSON(json: String) {
        Log.d("UserList", "Full Response: $json")
        val newUsers = JsonParser(json).getUsers()
        usersList.clear()
        usersList.addAll(newUsers)
        adapter.notifyDataSetChanged()
    }
}