package com.proyectofinal.moviles.network

import com.proyectofinal.moviles.model.AuthResponse
import com.proyectofinal.moviles.model.LoginRequest
import com.proyectofinal.moviles.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}
