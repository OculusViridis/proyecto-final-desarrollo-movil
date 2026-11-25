package com.proyectofinal.moviles.network

import com.proyectofinal.moviles.model.AuthResponse
import com.proyectofinal.moviles.model.LoginRequest
import com.proyectofinal.moviles.model.RegisterRequest
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
}

class AuthRepository(private val apiService: ApiService = RetrofitClient.apiService) {

    suspend fun register(request: RegisterRequest): ApiResult<AuthResponse> {
        return executeAuthCall { apiService.register(request) }
    }

    suspend fun login(request: LoginRequest): ApiResult<AuthResponse> {
        return executeAuthCall { apiService.login(request) }
    }

    private suspend fun executeAuthCall(
        call: suspend () -> Response<AuthResponse>
    ): ApiResult<AuthResponse> {
        return try {
            val response = call()
            val body = response.body()

            if (response.isSuccessful && (response.code() == 200 || response.code() == 201) && body != null) {
                ApiResult.Success(body)
            } else {
                val parsedError = extractErrorMessage(response.errorBody()?.string())
                val fallbackMessage = when (response.code()) {
                    400 -> "Datos inválidos o campos obligatorios faltantes."
                    401 -> "Credenciales inválidas. Verifica tu correo y contraseña."
                    409 -> "El correo ya está registrado."
                    else -> "Error del servidor (${response.code()})."
                }
                ApiResult.Error(parsedError ?: fallbackMessage, response.code())
            }
        } catch (e: IOException) {
            ApiResult.Error("No se pudo conectar con el servidor. Verifica que el backend esté activo.")
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Error inesperado.")
        }
    }

    private fun extractErrorMessage(rawError: String?): String? {
        if (rawError.isNullOrBlank()) return null
        return try {
            val json = JSONObject(rawError)
            json.optString("error").takeIf { it.isNotBlank() }
                ?: json.optString("message").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
