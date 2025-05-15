import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth APIs
export const login = async (email: string, password: string) => {
  const response = await api.post('/auth/login', { email, password });
  if (response.data.token) {
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('currentRole', response.data.currentRole);
    localStorage.setItem('user', JSON.stringify(response.data));
  }
  return response;
};

export const register = async (userData: any) => {
  const response = await api.post('/auth/register', userData);
  if (response.data.token) {
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('currentRole', response.data.currentRole);
    localStorage.setItem('user', JSON.stringify(response.data));
  }
  return response;
};

export const logout = () => {
  localStorage.removeItem('token');
  localStorage.removeItem('currentRole');
  localStorage.removeItem('user');
};

// Restaurant APIs
export const searchRestaurants = (searchParams: any) =>
  api.post('/restaurants/public/search', searchParams);

export const getRestaurantDetails = (id: string) =>
  api.get(`/restaurants/public/${id}`);

export const createRestaurant = (restaurantData: any) =>
  api.post('/restaurants', restaurantData);

export const getManagerRestaurants = () =>
  api.get('/restaurants/manager');

export const updateRestaurant = (id: string, data: any) =>
  api.put(`/restaurants/${id}`, data);

// Reservation APIs
export const createReservation = (reservationData: any) =>
  api.post('/reservations', reservationData);

export const getUserReservations = () =>
  api.get('/reservations');

export const getRestaurantReservations = (restaurantId: string) =>
  api.get(`/reservations/restaurant/${restaurantId}`);

// Review APIs
export const createReview = (reviewData: any) =>
  api.post('/reviews', reviewData);

export const getRestaurantReviews = (restaurantId: string) =>
  api.get(`/reviews/public/restaurant/${restaurantId}`);

export const getUserReviews = () =>
  api.get('/reviews/user');

// Admin APIs
export const getAnalytics = () =>
  api.get('/admin/analytics/reservations');

export const approveRestaurant = (restaurantId: string) =>
  api.post(`/restaurants/${restaurantId}/approve`);

export const getPendingRestaurants = () =>
  api.get('/restaurants/pending');

export const getAllUsers = () =>
  api.get('/admin/users');

export default api;