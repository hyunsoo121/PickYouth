import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import './NavBar.css';

export default function NavBar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate('/');
  }

  return (
    <header className="nav-bar">
      <div className="container nav-bar-inner">
        <Link className="nav-brand" to="/">
          청년픽
        </Link>
        <nav className="nav-links">
          <Link to="/search">지원금 찾기</Link>
          {user ? (
            <>
              <Link to="/me">마이페이지</Link>
              <button type="button" className="btn btn--ghost btn--sm" onClick={handleLogout}>
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link to="/login">로그인</Link>
              <Link className="btn btn--primary btn--sm" to="/signup">
                회원가입
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
