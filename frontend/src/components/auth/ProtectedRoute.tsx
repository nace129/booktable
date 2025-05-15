import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../../contexts/AuthContext';

export const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
  const auth = useContext(AuthContext);
  if (!auth?.user) {
    return <Navigate to="/login" replace />;
  }
  return children;
};
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