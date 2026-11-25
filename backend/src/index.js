const express = require('express');
const authRoutes = require('./routes/auth');

const app = express();
const PORT = Number(process.env.PORT || 3000);

app.use(express.json());
app.use('/api/auth', authRoutes);

app.get('/health', (_req, res) => {
  res.status(200).json({ status: 'ok' });
});

app.listen(PORT, () => {
  console.log(`Servidor escuchando en http://localhost:${PORT}`);
});
