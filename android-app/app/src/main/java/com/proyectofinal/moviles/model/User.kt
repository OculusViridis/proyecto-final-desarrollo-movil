package com.proyectofinal.moviles.model

import java.io.Serializable

data class User(
    val id: Int,
    val nombre: String,
    val correo: String,
    val carnet: String?,
    val carrera: String?
) : Serializable
