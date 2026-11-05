import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './hooks/useAuth';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Layout } from './components/Layout';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { RecuperarSenhaPage } from './pages/RecuperarSenhaPage';
import { DashboardPage } from './pages/DashboardPage';
import { AlunosPage } from './pages/AlunosPage';
import { AlunoDetailPage } from './pages/AlunoDetailPage';
import { FichaDetailPage } from './pages/FichaDetailPage';
import { ExerciciosPage } from './pages/ExerciciosPage';
import { PerfilPage } from './pages/PerfilPage';
import { VerificarEmailPage } from './pages/VerificarEmailPage'; // <-- IMPORTADO

export function App() {
  return (
    <BrowserRouter>
      <Toaster position="top-right" toastOptions={{ style: { fontSize: '0.9rem', fontWeight: '500' } }} />
      <AuthProvider>
        <Routes>
          {/* Rotas Públicas */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/recuperar-senha" element={<RecuperarSenhaPage />} />
          <Route path="/verificar-email" element={<VerificarEmailPage />} /> {/* <-- NOVA ROTA */}
          
          {/* Rotas Protegidas */}
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/dashboard" element={<DashboardPage />} />
              <Route path="/alunos" element={<AlunosPage />} />
              <Route path="/alunos/:id" element={<AlunoDetailPage />} />
              <Route path="/fichas/:id" element={<FichaDetailPage />} />
              <Route path="/exercicios" element={<ExerciciosPage />} />
              <Route path="/perfil" element={<PerfilPage />} />
            </Route>
          </Route>

          {/* Rota Padrão */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}