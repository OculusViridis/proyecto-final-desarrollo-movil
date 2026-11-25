package com.proyectofinal.moviles.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val message: String? = null,
    @SerializedName(value = "usuario", alternate = ["user"])
    val user: User? = null
)
