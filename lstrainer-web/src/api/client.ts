import axios from 'axios';

const API_URL = 'https://freelance-ls-trainer.vercel.app/login';

export const api = axios.create({
  baseURL: API_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});