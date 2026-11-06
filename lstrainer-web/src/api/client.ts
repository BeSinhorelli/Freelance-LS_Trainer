import axios from 'axios';

const API_URL = 'https://lstrainer-api.onrender.com/api';

export const api = axios.create({
  baseURL: API_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json; charset=utf-8', // <-- FORÇA O ENVIO EM UTF-8
    'Accept': 'application/json; charset=utf-8'        // <-- FORÇA O RECEBIMENTO EM UTF-8
  },
});