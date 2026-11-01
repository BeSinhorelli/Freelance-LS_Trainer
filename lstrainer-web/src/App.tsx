import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './hooks/useAuth';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Layout } from './components/Layout';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { AlunosPage } from './pages/AlunosPage';
import { AlunoDetailPage } from './pages/AlunoDetailPage';
import { FichaDetailPage } from './pages/FichaDetailPage';
import { ExerciciosPage } from './pages/ExerciciosPage';

export function App() {
  return (
    <BrowserRouter>
      <Toaster position="top-right" toastOptions={{ style: { fontSize: '0.9rem', fontWeight: '500' } }} />
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/alunos" element={<AlunosPage />} />
              <Route path="/alunos/:id" element={<AlunoDetailPage />} />
              <Route path="/fichas/:id" element={<FichaDetailPage />} />
              <Route path="/exercicios" element={<ExerciciosPage />} />
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}