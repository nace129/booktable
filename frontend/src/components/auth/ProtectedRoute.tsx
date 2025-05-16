import React, { useContext } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';


interface ProtectedRouteProps {
  isAllowed: boolean;
  redirectPath?: string;
  children?: React.ReactNode;
}

const ProtectedRoute = ({ isAllowed, redirectPath = '/login', children }: ProtectedRouteProps) => {
  if (!isAllowed) {
    return <Navigate to={redirectPath} replace />;
  }

  return children ? <>{children}</> : <Outlet />;
};

export default ProtectedRoute;

// src/components/auth/ProtectedRoute.tsx




// import { Navigate, Outlet } from 'react-router-dom';

// interface ProtectedRouteProps {
//   isAllowed: boolean;
//   redirectPath: string;
//   children?: React.ReactNode;
// }

// const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
//   isAllowed,
//   redirectPath,
//   children,
// }) => {
//   if (!isAllowed) {
//     return <Navigate to={redirectPath} replace />;
//   }

//   return children ? <>{children}</> : <Outlet />;
// };

// export default ProtectedRoute;