package com.titin.crudcongsheets


import java.io.Serializable

data class User(
    val id: String,
    val name: String,
    val apellido: String,
    val phone: String,
    val imageUrl: String
) : Serializable