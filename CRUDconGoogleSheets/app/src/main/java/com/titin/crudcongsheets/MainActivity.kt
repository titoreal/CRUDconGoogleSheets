package com.titin.crudcongsheets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent;
import com.titin.crudcongsheets.databinding.ActivityMainBinding;

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddUser.setOnClickListener {
            startActivity(Intent(this, UserAddActivity::class.java))
        }
        binding.btnListUser.setOnClickListener {
            startActivity(Intent(this, UserList::class.java))
        }
    }
}