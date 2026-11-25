package com.proyectofinal.moviles.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.proyectofinal.moviles.databinding.ActivityLoginBinding
import com.proyectofinal.moviles.viewmodel.AuthUiState
import com.proyectofinal.moviles.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val correo = binding.etCorreo.text?.toString().orEmpty()
            val password = binding.etPassword.text?.toString().orEmpty()
            viewModel.login(correo, password)
        }

        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is AuthUiState.Idle -> {
                    setLoading(false)
                }

                is AuthUiState.Loading -> {
                    setLoading(true)
                }

                is AuthUiState.Success -> {
                    setLoading(false)
                    val user = state.response.user
                    if (user != null) {
                        val intent = Intent(this, ProfileActivity::class.java).apply {
                            putExtra(ProfileActivity.EXTRA_USER, user)
                        }
                        viewModel.resetLoginState()
                        startActivity(intent)
                        finish()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "No se recibieron los datos del usuario.",
                            Snackbar.LENGTH_LONG
                        ).show()
                        viewModel.resetLoginState()
                    }
                }

                is AuthUiState.Error -> {
                    setLoading(false)
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    viewModel.resetLoginState()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.btnGoToRegister.isEnabled = !isLoading
    }
}
