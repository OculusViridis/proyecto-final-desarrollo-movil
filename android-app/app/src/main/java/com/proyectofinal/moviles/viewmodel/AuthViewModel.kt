package com.proyectofinal.moviles.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyectofinal.moviles.model.AuthResponse
import com.proyectofinal.moviles.model.LoginRequest
import com.proyectofinal.moviles.model.RegisterRequest
import com.proyectofinal.moviles.network.ApiResult
import com.proyectofinal.moviles.network.AuthRepository
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val response: AuthResponse) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val loginState: LiveData<AuthUiState> = _loginState

    private val _registerState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val registerState: LiveData<AuthUiState> = _registerState

    fun login(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _loginState.value = AuthUiState.Error("Por favor ingresa tu correo y contraseña.")
            return
        }

        _loginState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = repository.login(LoginRequest(correo.trim(), password))) {
                is ApiResult.Success -> _loginState.value = AuthUiState.Success(result.data)
                is ApiResult.Error -> _loginState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun register(
        nombre: String,
        correo: String,
        password: String,
        carnet: String,
        carrera: String
    ) {
        if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
            _registerState.value = AuthUiState.Error("Nombre, correo y contraseña son obligatorios.")
            return
        }

        _registerState.value = AuthUiState.Loading
        viewModelScope.launch {
            val request = RegisterRequest(
                nombre = nombre.trim(),
                correo = correo.trim(),
                password = password,
                carnet = carnet.trim(),
                carrera = carrera.trim()
            )
            when (val result = repository.register(request)) {
                is ApiResult.Success -> _registerState.value = AuthUiState.Success(result.data)
                is ApiResult.Error -> _registerState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = AuthUiState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = AuthUiState.Idle
    }
}
