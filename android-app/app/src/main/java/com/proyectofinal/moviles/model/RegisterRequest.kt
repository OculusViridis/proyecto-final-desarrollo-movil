package com.proyectofinal.moviles.model

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val password: String,
    val carnet: String,
    val carrera: String
)
