package com.proyectofinal.moviles.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.proyectofinal.moviles.databinding.ActivityProfileBinding
import com.proyectofinal.moviles.model.User

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val takePicturePreviewLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                binding.ivProfilePhoto.setImageBitmap(bitmap)
            }
        }

    private val getContentLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                binding.ivProfilePhoto.setImageURI(uri)
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                takePicturePreviewLauncher.launch(null)
            } else {
                Snackbar.make(
                    binding.root,
                    "Se requiere permiso de cámara para tomar una foto.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        displayUserData()
        setupListeners()
    }

    private fun displayUserData() {
        val user = extractUserFromIntent()
        if (user != null) {
            binding.tvNombre.text = user.nombre
            binding.tvCorreo.text = user.correo
            binding.tvCarnet.text = user.carnet?.takeIf { it.isNotBlank() } ?: "No especificado"
            binding.tvCarrera.text = user.carrera?.takeIf { it.isNotBlank() } ?: "No especificada"
        }
    }

    @Suppress("DEPRECATION")
    private fun extractUserFromIntent(): User? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_USER, User::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_USER) as? User
        }
    }

    private fun setupListeners() {
        binding.btnTakePhoto.setOnClickListener {
            val hasCameraPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasCameraPermission) {
                takePicturePreviewLauncher.launch(null)
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnChooseGallery.setOnClickListener {
            getContentLauncher.launch("image/*")
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
    }
}
