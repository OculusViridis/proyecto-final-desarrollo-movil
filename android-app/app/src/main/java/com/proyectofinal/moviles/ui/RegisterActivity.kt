package com.proyectofinal.moviles.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.proyectofinal.moviles.databinding.ActivityRegisterBinding
import com.proyectofinal.moviles.viewmodel.AuthUiState
import com.proyectofinal.moviles.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val nombre = binding.etNombre.text?.toString().orEmpty()
            val correo = binding.etCorreo.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            val carnet = binding.etCarnet.text?.toString().orEmpty()
            val carrera = binding.etCarrera.text?.toString().orEmpty()

            viewModel.register(nombre, correo, password, carnet, carrera)
        }

        binding.btnGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is AuthUiState.Idle -> {
                    setLoading(false)
                }

                is AuthUiState.Loading -> {
                    setLoading(true)
                }

                is AuthUiState.Success -> {
                    setLoading(false)
                    val message = state.response.message ?: "Usuario registrado correctamente"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    viewModel.resetRegisterState()

                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                }

                is AuthUiState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetRegisterState()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
        binding.btnGoToLogin.isEnabled = !isLoading
    }
}
