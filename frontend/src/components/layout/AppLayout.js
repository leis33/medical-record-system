import React from 'react';
import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const navConfig = {
  ADMIN: [
    { section: 'Overview', items: [{ to: '/', label: 'Dashboard', icon: '📊' }] },
    { section: 'Management', items: [
      { to: '/doctors', label: 'Doctors', icon: '👨‍⚕️' },
      { to: '/patients', label: 'Patients', icon: '🏥' },
      { to: '/diagnoses', label: 'Diagnoses', icon: '🔬' },
      { to: '/examinations', label: 'Examinations', icon: '📋' },
      { to: '/sick-leaves', label: 'Sick Leaves', icon: '📄' },
    ]},
    { section: 'Analytics', items: [{ to: '/reports', label: 'Reports', icon: '📈' }] },
  ],
  DOCTOR: [
    { section: 'Overview', items: [{ to: '/', label: 'Dashboard', icon: '📊' }] },
    { section: 'Clinical', items: [
      { to: '/patients', label: 'Patients', icon: '🏥' },
      { to: '/diagnoses', label: 'Diagnoses', icon: '🔬' },
      { to: '/examinations', label: 'Examinations', icon: '📋' },
      { to: '/sick-leaves', label: 'Sick Leaves', icon: '📄' },
    ]},
    { section: 'Analytics', items: [{ to: '/reports', label: 'Reports', icon: '📈' }] },
  ],
  PATIENT: [
    { section: 'My Records', items: [
      { to: '/', label: 'Dashboard', icon: '📊' },
      { to: '/examinations', label: 'My Examinations', icon: '📋' },
      { to: '/sick-leaves', label: 'My Sick Leaves', icon: '📄' },
    ]},
  ],
};

export default function AppLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const sections = navConfig[user?.role] || navConfig.PATIENT;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="app-layout">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="logo-mark">
            <div className="logo-icon">🏥</div>
            <span>MedRecord</span>
          </div>
        </div>

        <nav className="sidebar-nav">
          {sections.map(sec => (
            <div className="nav-section" key={sec.section}>
              <div className="nav-section-label">{sec.section}</div>
              {sec.items.map(item => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.to === '/'}
                  className={({ isActive }) => `nav-item${isActive ? ' active' : ''}`}
                >
                  <span className="nav-icon">{item.icon}</span>
                  {item.label}
                </NavLink>
              ))}
            </div>
          ))}
        </nav>

        <div className="sidebar-footer">
          <div className="user-info">
            <div className="user-avatar">{user?.username?.[0]?.toUpperCase()}</div>
            <div className="user-details">
              <div className="user-name">{user?.username}</div>
              <div className="user-role">{user?.role?.toLowerCase()}</div>
            </div>
            <button className="logout-btn" onClick={handleLogout} title="Logout">⏻</button>
          </div>
        </div>
      </aside>

      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
