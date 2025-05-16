// src/pages/auth/LoginPage.tsx
import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { ChefHat, Lock, Mail } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const auth = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as any)?.from?.pathname || '/';

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Please enter both email and password.');
      return;
    }
    try {
      setError('');
      setLoading(true);
      if (!auth || !auth.login) {
        setError('Authentication service unavailable.');
        return;
      }
      await auth.login(email, password);
      navigate(from, { replace: true });
    } catch (err:any) {
      setError(err.response?.data?.message || 'Invalid credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="flex justify-center">
          <ChefHat className="h-12 w-12 text-primary-500" />
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Sign in to your account
        </h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          Or{' '}
          <Link to="/register" className="font-medium text-primary-600 hover:text-primary-500">
            create a new account
          </Link>
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {error && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md" role="alert">
              <p>{error}</p>
            </div>
          )}
          
          <form className="space-y-6" onSubmit={handleLogin}>
            <div>
              <label htmlFor="email" className="form-label flex items-center">
                <Mail className="h-4 w-4 mr-1" />
                Email address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="form-input"
                placeholder="customer@example.com, restaurant@example.com, or admin@example.com"
              />
              <p className="mt-1 text-xs text-gray-500">
                Demo accounts: customer@example.com, restaurant@example.com, admin@example.com
              </p>
            </div>

            <div>
              <label htmlFor="password" className="form-label flex items-center">
                <Lock className="h-4 w-4 mr-1" />
                Password
              </label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="form-input"
                placeholder="Any password will work for the demo"
              />
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  id="remember-me"
                  name="remember-me"
                  type="checkbox"
                  className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
                />
                <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
                  Remember me
                </label>
              </div>

              <div className="text-sm">
                <a href="#" className="font-medium text-primary-600 hover:text-primary-500">
                  Forgot your password?
                </a>
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="btn btn-primary w-full py-2 px-4"
                disabled={loading}
              >
                {loading ? (
                  <div className="flex items-center justify-center">
                    <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white"></div>
                    <span className="ml-2">Signing in...</span>
                  </div>
                ) : (
                  'Sign in'
                )}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );

};

export default LoginPage;
// // src/pages/auth/LoginPage.tsx
// import React, { useState } from 'react';
// import { Link, useNavigate, useLocation } from 'react-router-dom';
// import { ChefHat, Lock, Mail } from 'lucide-react';
// import { useAuth } from '../../contexts/AuthContext';

// const LoginPage: React.FC = () => {
//   const [email, setEmail] = useState('');
//   const [password, setPassword] = useState('');
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState('');

//   const { login } = useAuth();
//   const navigate = useNavigate();
//   const location = useLocation();
//   const from = (location.state as any)?.from?.pathname || '/';

//   const handleLogin = async (e: React.FormEvent) => {
//     e.preventDefault();
//     if (!email || !password) {
//       setError('Please enter both email and password.');
//       return;
//     }
//     try {
//       setError('');
//       setLoading(true);
//       await login(email, password);
//       navigate(from, { replace: true });
//     } catch (err:any) {
//       setError(err.response?.data?.message || 'Invalid credentials.');
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="min-h-screen flex flex-col justify-center p-6">
//       <div className="max-w-md mx-auto bg-white p-8 shadow-lg rounded-lg">
//         <div className="flex justify-center mb-4">
//           <ChefHat className="h-10 w-10 text-primary-500" />
//         </div>
//         <h2 className="text-center text-2xl font-bold mb-2">
//           Sign in to your account
//         </h2>
//         {error && <div className="text-red-600 mb-4">{error}</div>}
//         <form onSubmit={handleLogin} className="space-y-4">
//           <div>
//             <label htmlFor="email" className="flex items-center mb-1">
//               <Mail className="mr-2" /> Email address
//             </label>
//             <input
//               id="email"
//               type="email"
//               value={email}
//               onChange={e => setEmail(e.target.value)}
//               className="input"
//               placeholder="you@example.com"
//             />
//           </div>

//           <div>
//             <label htmlFor="password" className="flex items-center mb-1">
//               <Lock className="mr-2" /> Password
//             </label>
//             <input
//               id="password"
//               type="password"
//               value={password}
//               onChange={e => setPassword(e.target.value)}
//               className="input"
//               placeholder="••••••••"
//             />
//           </div>

//           <button
//             type="submit"
//             className="btn-primary w-full py-2"
//             disabled={loading}
//           >
//             {loading ? 'Signing in...' : 'Sign In'}
//           </button>
//         </form>
//         <p className="mt-4 text-center text-sm">
//           Don't have an account?{' '}
//           <Link to="/register" className="text-primary-600 hover:underline">
//             Register
//           </Link>
//         </p>
//       </div>
//     </div>
//   );
// };

// export default LoginPage;
// // import { useState } from 'react';
// // import { Link, useNavigate, useLocation } from 'react-router-dom';
// // import { ChefHat, Lock, Mail } from 'lucide-react';
// // import { useAuth } from '../../contexts/AuthContext';

// // const LoginPage = () => {
// //   const [email, setEmail] = useState('');
// //   const [password, setPassword] = useState('');
// //   const [loading, setLoading] = useState(false);
// //   const [error, setError] = useState('');
  
// //   const { login } = useAuth();
// //   const navigate = useNavigate();
// //   const location = useLocation();
  
// //   // Get the return URL from location state or default to home
// //   const from = location.state?.from?.pathname || '/';
  
// //   const handleLogin = async (e: React.FormEvent) => {
// //     e.preventDefault();
    
// //     if (!email || !password) {
// //       setError('Please enter both email and password');
// //       return;
// //     }
    
// //     try {
// //       setError('');
// //       setLoading(true);
// //       await login(email, password);
      
// //       // Redirect to the page they tried to visit or home
// //       navigate(from, { replace: true });
// //     } catch (err) {
// //       setError('Failed to log in. Please check your credentials.');
// //       setLoading(false);
// //     }
// //   };
  
// //   return (
// //     <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
// //       <div className="sm:mx-auto sm:w-full sm:max-w-md">
// //         <div className="flex justify-center">
// //           <ChefHat className="h-12 w-12 text-primary-500" />
// //         </div>
// //         <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
// //           Sign in to your account
// //         </h2>
// //         <p className="mt-2 text-center text-sm text-gray-600">
// //           Or{' '}
// //           <Link to="/register" className="font-medium text-primary-600 hover:text-primary-500">
// //             create a new account
// //           </Link>
// //         </p>
// //       </div>

// //       <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
// //         <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
// //           {error && (
// //             <div className="mb-4 bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-md" role="alert">
// //               <p>{error}</p>
// //             </div>
// //           )}
          
// //           <form className="space-y-6" onSubmit={handleLogin}>
// //             <div>
// //               <label htmlFor="email" className="form-label flex items-center">
// //                 <Mail className="h-4 w-4 mr-1" />
// //                 Email address
// //               </label>
// //               <input
// //                 id="email"
// //                 name="email"
// //                 type="email"
// //                 autoComplete="email"
// //                 value={email}
// //                 onChange={(e) => setEmail(e.target.value)}
// //                 className="form-input"
// //                 placeholder="customer@example.com, restaurant@example.com, or admin@example.com"
// //               />
// //               <p className="mt-1 text-xs text-gray-500">
// //                 Demo accounts: customer@example.com, restaurant@example.com, admin@example.com
// //               </p>
// //             </div>

// //             <div>
// //               <label htmlFor="password" className="form-label flex items-center">
// //                 <Lock className="h-4 w-4 mr-1" />
// //                 Password
// //               </label>
// //               <input
// //                 id="password"
// //                 name="password"
// //                 type="password"
// //                 autoComplete="current-password"
// //                 value={password}
// //                 onChange={(e) => setPassword(e.target.value)}
// //                 className="form-input"
// //                 placeholder="Any password will work for the demo"
// //               />
// //             </div>

// //             <div className="flex items-center justify-between">
// //               <div className="flex items-center">
// //                 <input
// //                   id="remember-me"
// //                   name="remember-me"
// //                   type="checkbox"
// //                   className="h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded"
// //                 />
// //                 <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
// //                   Remember me
// //                 </label>
// //               </div>

// //               <div className="text-sm">
// //                 <a href="#" className="font-medium text-primary-600 hover:text-primary-500">
// //                   Forgot your password?
// //                 </a>
// //               </div>
// //             </div>

// //             <div>
// //               <button
// //                 type="submit"
// //                 className="btn btn-primary w-full py-2 px-4"
// //                 disabled={loading}
// //               >
// //                 {loading ? (
// //                   <div className="flex items-center justify-center">
// //                     <div className="animate-spin rounded-full h-5 w-5 border-t-2 border-b-2 border-white"></div>
// //                     <span className="ml-2">Signing in...</span>
// //                   </div>
// //                 ) : (
// //                   'Sign in'
// //                 )}
// //               </button>
// //             </div>
// //           </form>
// //         </div>
// //       </div>
// //     </div>
// //   );
// // };

// // export default LoginPage;