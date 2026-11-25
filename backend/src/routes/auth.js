const express = require('express');
const bcrypt = require('bcryptjs');
const pool = require('../db');

const router = express.Router();

/**
 * Registra un nuevo usuario en la tabla usuarios.
 */
router.post('/register', async (req, res) => {
  try {
    const { nombre, correo, password, carnet = null, carrera = null } = req.body || {};

    if (!nombre || !correo || !password) {
      return res.status(400).json({
        error: 'Los campos nombre, correo y password son obligatorios',
      });
    }

    const correoNormalizado = String(correo).trim().toLowerCase();
    const passwordHash = await bcrypt.hash(String(password), 10);

    const query = `
      INSERT INTO usuarios (nombre, correo, password_hash, carnet, carrera)
      VALUES ($1, $2, $3, $4, $5)
      RETURNING id, nombre, correo, carnet, carrera, creado_en
    `;
    const values = [
      String(nombre).trim(),
      correoNormalizado,
      passwordHash,
      carnet ? String(carnet).trim() : null,
      carrera ? String(carrera).trim() : null,
    ];

    const result = await pool.query(query, values);
    return res.status(201).json({
      message: 'Usuario registrado correctamente',
      usuario: result.rows[0],
    });
  } catch (err) {
    if (err.code === '23505') {
      return res.status(409).json({
        error: 'El correo ya está registrado',
      });
    }
    console.error('Error en /api/auth/register:', err);
    return res.status(500).json({ error: 'Error interno del servidor' });
  }
});

/**
 * Autentica un usuario existente por correo y contraseña.
 */
router.post('/login', async (req, res) => {
  try {
    const { correo, password } = req.body || {};

    if (!correo || !password) {
      return res.status(400).json({
        error: 'Los campos correo y password son obligatorios',
      });
    }

    const correoNormalizado = String(correo).trim().toLowerCase();
    const query = `
      SELECT id, nombre, correo, password_hash, carnet, carrera, creado_en
      FROM usuarios
      WHERE correo = $1
    `;
    const result = await pool.query(query, [correoNormalizado]);

    if (result.rows.length === 0) {
      return res.status(401).json({ error: 'Credenciales inválidas' });
    }

    const user = result.rows[0];
    const passwordValido = await bcrypt.compare(String(password), user.password_hash);

    if (!passwordValido) {
      return res.status(401).json({ error: 'Credenciales inválidas' });
    }

    const { password_hash, ...usuarioSinPassword } = user;
    return res.status(200).json({
      message: 'Inicio de sesión exitoso',
      usuario: usuarioSinPassword,
    });
  } catch (err) {
    console.error('Error en /api/auth/login:', err);
    return res.status(500).json({ error: 'Error interno del servidor' });
  }
});

module.exports = router;
