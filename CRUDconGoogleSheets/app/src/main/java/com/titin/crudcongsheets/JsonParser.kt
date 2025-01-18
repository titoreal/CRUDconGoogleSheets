package com.titin.crudcongsheets

import org.json.JSONException
import org.json.JSONObject
import android.util.Log
import java.util.ArrayList

class JsonParser(private val json: String) {
    private val users = ArrayList<User>()

    init {
        parseJSON()
    }

    private fun parseJSON() {
        try {
            val jsonObject = JSONObject(json)
            val jsonArray = jsonObject.getJSONArray(Configuration.Keys.USERS)

            for (i in 0 until jsonArray.length()) {
                jsonArray.getJSONObject(i).let { jo ->
                    users.add(User(
                        id = jo.getString(Configuration.Keys.ID),
                        name = jo.getString(Configuration.Keys.NAME),
                        apellido = jo.getString(Configuration.Keys.APELLIDO),
                        phone = jo.getString(Configuration.Keys.PHONE),
                        imageUrl = jo.optString(Configuration.Keys.IMAGE, "")
                    ))
                }
            }
        } catch (e: JSONException) {
            Log.e("JsonParser", "Error parsing JSON", e)
        }
    }

    fun getUsers(): List<User> = users.toList()
}