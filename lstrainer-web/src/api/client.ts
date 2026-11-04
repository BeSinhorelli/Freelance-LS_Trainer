import axios from 'axios';

// IMPORTANTE: Substitua 'lstrainer-api' pela URL real que o Render te deu!
// Não esqueça do /api no final.
const API_URL = 'https://lstrainer-api.onrender.com/api'; 

export const api = axios.create({
  baseURL: API_URL,
  withCredentials: true, // CRUCIAL: Permite que o Axios envie e receba cookies
  headers: {
    'Content-Type': 'application/json',
  },
});