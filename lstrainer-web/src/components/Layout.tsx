import { useState } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import styles from './Layout.module.css';

export function Layout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { professor, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const toggleSidebar = () => setSidebarOpen(!sidebarOpen);

  async function handleLogout() {
    await logout();
    navigate('/login');
  }

  // Menu baseado na imagem
  const menuItems = [
    { path: '/dashboard', icon: '🏠', label: 'Início' },
    { path: '/alunos', icon: '👥', label: 'Alunos' },
    { path: '/exercicios', icon: '🏋️', label: 'Exercícios' }, // <-- Adicionado
  ];

  return (
    <div className={styles.container}>
      {/* Topbar */}
      <header className={styles.topbar}>
        <button className={styles.menuBtn} onClick={toggleSidebar}>☰</button>
        
        <div className={styles.logo}>
          <div className={styles.logoIcon}>LS</div>
          <div>
            <h1>LS TRAINER</h1>
            <p>Sistema de Treinos</p>
          </div>
        </div>

        <div className={styles.userInfo}>
          <span>Olá, {professor?.nome.split(' ')[0]}</span>
          <button onClick={handleLogout} className={styles.logoutBtn}>Sair</button>
        </div>
      </header>

      {/* Sidebar */}
      <aside className={`${styles.sidebar} ${sidebarOpen ? styles.open : ''}`}>
        <nav className={styles.nav}>
          {menuItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={`${styles.navItem} ${location.pathname.startsWith(item.path) ? styles.active : ''}`}
              onClick={() => setSidebarOpen(false)}
            >
              <span className={styles.navIcon}>{item.icon}</span>
              {item.label}
            </Link>
          ))}
        </nav>
      </aside>

      {/* Overlay para mobile */}
      {sidebarOpen && <div className={styles.overlay} onClick={toggleSidebar} />}

      {/* Conteúdo */}
      <main className={styles.main}>
        <Outlet />
      </main>
    </div>
  );
}