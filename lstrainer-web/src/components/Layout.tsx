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

  const menuItems = [
    { path: '/dashboard', icon: '🏠', label: 'Início' },
    { path: '/alunos', icon: '👥', label: 'Alunos' },
    { path: '/exercicios', icon: '🏋️', label: 'Exercícios' }
  ];

  return (
      <div className={styles.container}>
        {/* Topbar */}
        <header className={styles.topbar}>
          <button className={styles.menuBtn} onClick={toggleSidebar}>☰</button>

          <div className={styles.logo}>
            {/* Logo maior e mais larga */}
            <img
                src="/logo2.jfif"
                alt="LS Trainer"
                style={{ height: '50px', width: 'auto', objectFit: 'contain' }}
            />
            <div className={styles.logoText}>
              <h1>LS TRAINER</h1>
              <p>Sistema de Treinos</p>
            </div>
          </div>

          <div className={styles.userInfo}>
            <span>Olá, {professor?.nome.split(' ')[0]}</span>
            <a
              href="https://lstrainer-api.onrender.com/oauth2/authorization/google"
              className={styles.switchAccountBtn}
            >
              Trocar conta
            </a>
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