import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import { AuthProvider, useAuth } from './context/AuthContext';
import AppLayout from './components/layout/AppLayout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import DoctorsPage from './pages/DoctorsPage';
import PatientsPage from './pages/PatientsPage';
import DiagnosesPage from './pages/DiagnosesPage';
import ExaminationsPage from './pages/ExaminationsPage';
import SickLeavesPage from './pages/SickLeavesPage';
import ReportsPage from './pages/ReportsPage';

function PrivateRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading-state"><div className="spinner" /></div>;
  return user ? children : <Navigate to="/login" />;
}

function PublicRoute({ children }) {
  const { user } = useAuth();
  return user ? <Navigate to="/" /> : children;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
          <Route path="/" element={<PrivateRoute><AppLayout /></PrivateRoute>}>
            <Route index element={<DashboardPage />} />
            <Route path="doctors" element={<DoctorsPage />} />
            <Route path="patients" element={<PatientsPage />} />
            <Route path="diagnoses" element={<DiagnosesPage />} />
            <Route path="examinations" element={<ExaminationsPage />} />
            <Route path="sick-leaves" element={<SickLeavesPage />} />
            <Route path="reports" element={<ReportsPage />} />
          </Route>
        </Routes>
        <ToastContainer position="bottom-right" autoClose={3000} hideProgressBar theme="colored" />
      </BrowserRouter>
    </AuthProvider>
  );
}
