// src/services/authService.ts
import axios from 'axios';

// Points to http://localhost:8080/api/auth
const authApi = axios.create({
  baseURL: import.meta.env.VITE_API_URL + '/auth',
});

// Backend expects: firstName, lastName, email, password, phoneNumber
export function register(data: {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber: string;
}) {
  return authApi.post('/register', data);
}

// IMPORTANT: backend expects email, not username
export function login(data: { email: string; password: string }) {
  return authApi.post('/login', data);
}

// import axios from 'axios';

// // baseURL points at your backend auth endpoints
// const authApi = axios.create({
//   baseURL: import.meta.env.VITE_API_URL + '/auth',
// });

// export function register(data: { firstName: string; lastName:string, email: string; password: string, phoneNumber:string }) {
//   return authApi.post('/register', data);
// }

// export function login(data: { username: string; password: string }) {
//   return authApi.post('/login', data);
// }