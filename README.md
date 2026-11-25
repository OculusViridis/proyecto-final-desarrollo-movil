# Proyecto Final - Desarrollo Móvil

- **Universidad:** Universidad Da Vinci de Guatemala
- **Facultad:** Facultad de Ingeniería, Industria y Tecnología
- **Autor:** Pablo Fernando Contreras Guillermo

---

## Descripción y Arquitectura

Este proyecto implementa una solución completa **Cliente-Servidor** para el registro, autenticación y visualización de perfil de estudiantes universitarios.

### 1. Backend (API REST + PostgreSQL en Docker)
- **Base de datos (`PostgreSQL 16`):** Contenedor administrado mediante Docker Compose (`moviles_postgres`), inicializado automáticamente con la tabla `usuarios` (`id`, `nombre`, `correo`, `password_hash`, `carnet`, `carrera`, `creado_en`).
- **Servidor (`Node.js` + `Express`):** Expone una API REST en el puerto `3000` conectada a PostgreSQL mediante el pool de `pg`. Las contraseñas se cifran con `bcryptjs` antes de almacenarse y se verifican mediante comparación de hashes en el inicio de sesión.

### 2. Aplicación Móvil (`Android Nativo` en Kotlin + MVVM)
La aplicación Android (`minSdk 24`, `targetSdk 34`) está construida en **Kotlin** utilizando **XML con ViewBinding** y **Material Design 3**, siguiendo la arquitectura **MVVM (Model-View-ViewModel)**:

- **Model (`model/`):** Data classes `RegisterRequest`, `LoginRequest`, `User` y `AuthResponse`.
- **Network (`network/`):** `RetrofitClient` configurado hacia `http://10.0.2.2:3000/api/` con `OkHttp Logging Interceptor` y `GsonConverterFactory`, interfaz `ApiService` y `AuthRepository` con manejo de códigos HTTP (`200`, `201`, `400`, `401`, `409`).
- **ViewModel (`viewmodel/`):** `AuthViewModel` gestiona los estados de UI (`Idle`, `Loading`, `Success`, `Error`) mediante `LiveData` y `viewModelScope` (Coroutines).
- **View (`ui/`):**
  - `LoginActivity`: Pantalla inicial de autenticación; al iniciar sesión navega a `ProfileActivity` enviando el objeto `User` mediante `Intent` y limpiando el stack con `finish()`.
  - `RegisterActivity`: Formulario de registro de usuario (`nombre`, `correo`, `password`, `carnet`, `carrera`) que redirige a `LoginActivity` tras completar el registro.
  - `ProfileActivity`: Pantalla única post-login que muestra los datos del usuario autenticado, un `ShapeableImageView` circular para la foto de perfil, botones **"Tomar Foto"** (`ActivityResultContracts.TakePicturePreview()` con permiso `CAMERA` en tiempo de ejecución) y **"Elegir de Galería"** (`ActivityResultContracts.GetContent()` con `"image/*"`), además del botón **"Cerrar Sesión"**.

> **Nota sobre la fotografía de perfil:** La imagen de perfil se maneja **únicamente en memoria / estado de la vista local** (`Bitmap` o `Uri` dentro de `ProfileActivity`). No se envía al backend ni se almacena en la base de datos ni en almacenamiento permanente.

---

## Estructura del Repositorio

```text
ProyectoFinalMoviles/
├── .gitignore
├── README.md
├── backend/
│   ├── docker-compose.yml
│   ├── init.sql
│   ├── package.json
│   ├── package-lock.json
│   └── src/
│       ├── db.js
│       ├── index.js
│       └── routes/
│           └── auth.js
└── android-app/
    ├── build.gradle.kts
    ├── gradle.properties
    ├── settings.gradle.kts
    ├── gradle/
    │   └── wrapper/
    │       └── gradle-wrapper.properties
    └── app/
        ├── build.gradle.kts
        ├── proguard-rules.pro
        └── src/
            └── main/
                ├── AndroidManifest.xml
                ├── java/com/proyectofinal/moviles/
                │   ├── model/
                │   │   ├── AuthResponse.kt
                │   │   ├── LoginRequest.kt
                │   │   ├── RegisterRequest.kt
                │   │   └── User.kt
                │   ├── network/
                │   │   ├── ApiService.kt
                │   │   ├── AuthRepository.kt
                │   │   └── RetrofitClient.kt
                │   ├── ui/
                │   │   ├── LoginActivity.kt
                │   │   ├── ProfileActivity.kt
                │   │   └── RegisterActivity.kt
                │   └── viewmodel/
                │       └── AuthViewModel.kt
                └── res/
                    ├── drawable/
                    │   └── ic_person_placeholder.xml
                    ├── layout/
                    │   ├── activity_login.xml
                    │   ├── activity_profile.xml
                    │   └── activity_register.xml
                    └── values/
                        ├── colors.xml
                        ├── strings.xml
                        └── themes.xml
```

---

## Guía de Ejecución Paso a Paso

### Requisitos Previos
- **Docker Desktop** (en ejecución)
- **Node.js** (v18 o superior recomendado)
- **Android Studio** (con SDK 34 instalado y un emulador AVD configurado)

---

### 1. Levantar la Base de Datos y el Servidor Backend

1. Navega a la carpeta `backend/`:
   ```bash
   cd backend
   ```
2. Inicia el contenedor de PostgreSQL 16 en segundo plano (esto creará automáticamente la base de datos `moviles_db` y la tabla `usuarios` usando `init.sql`):
   ```bash
   docker compose up -d
   ```
3. Instala las dependencias de Node.js:
   ```bash
   npm install
   ```
4. Inicia el servidor Express en el puerto `3000`:
   ```bash
   npm start
   ```
   Verás en consola: `Servidor escuchando en http://localhost:3000`.

#### Endpoints de la API

| Método | Ruta | Descripción | Cuerpo JSON requerido | Respuestas |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Registra un nuevo usuario con contraseña cifrada (`bcryptjs`). | `{ "nombre", "correo", "password", "carnet", "carrera" }` (`carnet` y `carrera` opcionales) | `201` Creado · `400` Datos faltantes · `409` Correo duplicado |
| `POST` | `/api/auth/login` | Autentica un usuario verificando su contraseña contra `password_hash`. | `{ "correo", "password" }` | `200` OK (retorna `usuario`) · `400` Datos faltantes · `401` Credenciales inválidas |

---

### 2. Ejecutar la Aplicación Android en el Emulador

1. Abre **Android Studio** y selecciona **Open** sobre la carpeta `ProyectoFinalMoviles/android-app/`.
2. Espera a que Android Studio descargue las dependencias y sincronice el proyecto con Gradle.
3. Asegúrate de que el backend esté corriendo en tu máquina local en el puerto `3000` (la app está configurada en `RetrofitClient.kt` con la URL `http://10.0.2.2:3000/api/`, dirección especial del emulador de Android Studio para acceder al `localhost` del equipo anfitrión).
4. Selecciona un emulador Android (API 24 o superior) y presiona **Run** (`Shift + F10`).
