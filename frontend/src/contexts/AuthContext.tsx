import React, { createContext, useState, useEffect, ReactNode, useContext } from 'react';
import axios from 'axios';
import * as authService from '../services/authService';

interface User { username: string; token: string; }
interface AuthContextType {
  user: User | null;
  register: (f: string, l:string, e: string, p: string,n:string) => Promise<void>;
  login: (u: string, p: string) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);

  // On mount, load any existing token from localStorage
  useEffect(() => {
    const stored = localStorage.getItem('user');
    if (stored) {
      const parsed: User = JSON.parse(stored);
      setUser(parsed);
      axios.defaults.headers.common['Authorization'] = `Bearer ${parsed.token}`;
    }
  }, []);

  const register = async (firstName: string, lastName:string, email: string, password: string,  phoneNumber:string ) => {
    const { data } = await authService.register({ firstName, lastName, email, password, phoneNumber });
    const newUser = { username: email, token: data.token };
    setUser(newUser);
    axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
    localStorage.setItem('user', JSON.stringify(newUser));
  };

  const login = async (email: string, password: string) => {
    const { data } = await authService.login({ email, password });
    const newUser = { username:email, token: data.token };
    setUser(newUser);
    axios.defaults.headers.common['Authorization'] = `Bearer ${data.token}`;
    localStorage.setItem('user', JSON.stringify(newUser));
  };

  const logout = () => {
    setUser(null);
    delete axios.defaults.headers.common['Authorization'];
    localStorage.removeItem('user');
  };

  

  return (
    <AuthContext.Provider value={{ user, register, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};
// import React, { createContext, useContext, useState, useEffect } from 'react';

// type UserRole = 'customer' | 'restaurant' | 'admin';

// interface User {
//   id: string;
//   name: string;
//   email: string;
//   role: UserRole;
// }

// interface AuthContextType {
//   currentUser: User | null;
//   isAuthenticated: boolean;
//   userRole: UserRole | null;
//   login: (email: string, password: string) => Promise<void>;
//   register: (name: string, email: string, password: string, role: UserRole) => Promise<void>;
//   logout: () => void;
// }

// const AuthContext = createContext<AuthContextType | undefined>(undefined);

// export const useAuth = () => {
//   const context = useContext(AuthContext);
//   if (context === undefined) {
//     throw new Error('useAuth must be used within an AuthProvider');
//   }
//   return context;
// };

// export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
//   const [currentUser, setCurrentUser] = useState<User | null>(null);
//   const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  
//   // Check if user is already logged in from localStorage
//   useEffect(() => {
//     const storedUser = localStorage.getItem('bookTableUser');
//     if (storedUser) {
//       const user = JSON.parse(storedUser);
//       setCurrentUser(user);
//       setIsAuthenticated(true);
//     }
//   }, []);
  
//   // Mock login function - in a real app, this would call your backend API
//   const login = async (email: string, password: string) => {
//     // Simulate API call
//     await new Promise(resolve => setTimeout(resolve, 1000));
    
//     // Mock users for demo (would come from your backend in reality)
//     const mockUsers: Record<string, User> = {
//       'customer@example.com': {
//         id: '1',
//         name: 'John Customer',
//         email: 'customer@example.com',
//         role: 'customer'
//       },
//       'restaurant@example.com': {
//         id: '2',
//         name: 'Jane Restaurant',
//         email: 'restaurant@example.com',
//         role: 'restaurant'
//       },
//       'admin@example.com': {
//         id: '3',
//         name: 'Admin User',
//         email: 'admin@example.com',
//         role: 'admin'
//       }
//     };
    
//     if (!mockUsers[email]) {
//       throw new Error('Invalid email or password');
//     }
    
//     const user = mockUsers[email];
//     setCurrentUser(user);
//     setIsAuthenticated(true);
//     localStorage.setItem('bookTableUser', JSON.stringify(user));
//   };
  
//   const register = async (name: string, email: string, password: string, role: UserRole) => {
//     // Simulate API call
//     await new Promise(resolve => setTimeout(resolve, 1000));
    
//     // In real app, this would create a new user in your database
//     const newUser: User = {
//       id: Date.now().toString(),
//       name,
//       email,
//       role
//     };
    
//     setCurrentUser(newUser);
//     setIsAuthenticated(true);
//     localStorage.setItem('bookTableUser', JSON.stringify(newUser));
//   };
  
//   const logout = () => {
//     setCurrentUser(null);
//     setIsAuthenticated(false);
//     localStorage.removeItem('bookTableUser');
//   };
  
//   const value = {
//     currentUser,
//     isAuthenticated,
//     userRole: currentUser?.role || null,
//     login,
//     register,
//     logout
//   };
  
//   return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
// };