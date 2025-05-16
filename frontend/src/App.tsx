// src/App.tsx
import React, { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/auth/ProtectedRoute';

import Layout from './components/layout/Layout';
import LoadingSpinner from './components/common/LoadingSpinner';
import { useAuth } from './contexts/AuthContext';
// import ProtectedRoute from './components/auth/ProtectedRoute';
import HomePage from './pages/HomePage';

// Lazy-loaded pages
const SearchPage = lazy(() => import('./pages/customer/SearchPage'));
const RestaurantDetailPage = lazy(() => import('./pages/customer/RestaurantDetailPage'));
const BookingPage = lazy(() => import('./pages/customer/BookingPage'));
const ProfilePage = lazy(() => import('./pages/customer/ProfilePage'));
const LoginPage = lazy(() => import('./pages/auth/LoginPage'));
const RegisterPage = lazy(() => import('./pages/auth/RegisterPage'));
const RestaurantDashboardPage = lazy(() => import('./pages/restaurant/RestaurantDashboardPage'));
const RestaurantEditPage = lazy(() => import('./pages/restaurant/RestaurantEditPage'));
const AdminDashboardPage = lazy(() => import('./pages/admin/AdminDashboardPage'));
// const AdminAnalyticsPage = lazy(() => import('./pages/admin/AdminAnalyticsPage'));

function App() {
  const auth = useAuth();
  const isAuthenticated = auth?.isAuthenticated ?? false;
  // const userRole = auth?.userRole ?? '';

  return (
    <Suspense fallback={<div className="min-h-screen flex items-center justify-center"><LoadingSpinner size="large" /></div>}>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<HomePage />} />
          <Route path="search" element={<SearchPage />} />
          <Route path="restaurant/:id" element={<RestaurantDetailPage />} />
          
          {/* Auth routes */}
          <Route path="login" element={<LoginPage />} />
          <Route path="register" element={<RegisterPage />} />
          
          {/* Protected customer routes */}
          <Route element={<ProtectedRoute isAllowed={isAuthenticated} redirectPath="/login" />}>
            <Route path="booking/:restaurantId" element={<BookingPage />} />
            <Route path="profile" element={<ProfilePage />} />
          </Route>
          
          
        </Route>
      </Routes>
    </Suspense>
  );
}

export default App;

// // Lazy-load pages
// const HomePage = lazy(() => import('./pages/HomePage'));
// const LoginPage = lazy(() => import('./pages/auth/LoginPage'));
// const RegisterPage = lazy(() => import('./pages/auth/RegisterPage'));
// // const CustomerPage = lazy(() => import('./pages/customer/CustomerPage').then(module => ({ default: module.default })));
// // const RestaurantPage = lazy(() => import('./pages/restaurant/RestaurantPage').then(module => ({ default: module.default })));
// // const AdminPage = lazy(() => import('./pages/admin/AdminPage').then(module => ({ default: module.default })));

// function App() {
//   return (
//     <AuthProvider>
//       <BrowserRouter>
//         <Suspense fallback={<div>Loading...</div>}>
//           <Routes>
//             {/* Public routes */}
//             <Route path="/" element={<HomePage />} />
//             <Route path="/login" element={<LoginPage />} />
//             <Route path="/register" element={<RegisterPage />} />

//             {/* Protected routes by role
//             <Route
//               path="/customer/*"
//               element={
//                 <ProtectedRoute>
//                   <CustomerPage />
//                 </ProtectedRoute>
//               }
//             />
//             <Route
//               path="/restaurant/*"
//               element={
//                 <ProtectedRoute>
//                   <RestaurantPage />
//                 </ProtectedRoute>
//               }
//             />
//             <Route
//               path="/admin/*"
//               element={
//                 <ProtectedRoute>
//                   <AdminPage />
//                 </ProtectedRoute>
//               }
//             /> */}

//             {/* Catch-all: redirect to home */}
//             <Route path="*" element={<Navigate to="/" replace />} />
//           </Routes>
//         </Suspense>
//       </BrowserRouter>
//     </AuthProvider>
//   );
// }

// export default App;
// import { lazy, Suspense } from 'react';
// import { Route, Routes } from 'react-router-dom';
