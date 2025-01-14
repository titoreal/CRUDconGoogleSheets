package com.titin.crudcongsheets


import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class CrudcongsheetsApplication : Application() {
    lateinit var requestQueue: RequestQueue
        private set

    override fun onCreate() {
        super.onCreate()
        requestQueue = Volley.newRequestQueue(this)
    }
}
