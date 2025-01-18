package com.titin.crudcongsheets

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

class UserListAdapter(
    private val context: Activity,
    private val users: MutableList<User>
) : ArrayAdapter<User>(context, R.layout.activity_list_row, users) {

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: context.layoutInflater.inflate(
            R.layout.activity_list_row,
            parent,
            false
        )

        val viewHolder = view.tag as? ViewHolder ?: ViewHolder(view).also { view.tag = it }
        getItem(position)?.let { user -> bindUser(viewHolder, user) }

        return view
    }

    private fun bindUser(holder: ViewHolder, user: User) {
        with(holder) {
            userId.text = user.id
            userName.text = user.name
            userApellido.text = user.apellido
            userPhone.text = user.phone

            Configuration.extractDirectImageLink(user.imageUrl).let { imageUrl ->
                Picasso.get()
                    .load(imageUrl)
                    .into(userPhoto)
            }

            deleteButton.setOnClickListener { deleteUser(user) }
            editButton.setOnClickListener { editUser(user) }
        }
    }

    private class ViewHolder(view: View) {
        val userId: TextView = view.findViewById(R.id.tv_uid)
        val userName: TextView = view.findViewById(R.id.tv_uname)
        val userApellido: TextView = view.findViewById(R.id.tv_uapellido)
        val userPhone: TextView = view.findViewById(R.id.tv_uphone)
        val userPhoto: ImageView = view.findViewById(R.id.imageView3)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_delete_item)
        val editButton: ImageButton = view.findViewById(R.id.btn_edit_item)
    }
    // UserListAdapter.kt
    private fun deleteUser(user: User) {
        val stringRequest = object : StringRequest(
            Method.POST,
            Configuration.ADD_USER_URL,
            {
                Toast.makeText(context, "User Deleted", Toast.LENGTH_SHORT).show()
                users.remove(user)
                notifyDataSetChanged()
            },
            {
                Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> = hashMapOf(
                Configuration.Keys.ACTION to "delete",
                Configuration.Keys.ID to user.id
            )
        }
        requestQueue.add(stringRequest)
    }

    private fun editUser(user: User) {
        val intent = Intent(context, UserUpdateActivity::class.java).apply {
            putExtra("UPDATE_USER", user)
        }
        context.startActivity(intent)
    }
}