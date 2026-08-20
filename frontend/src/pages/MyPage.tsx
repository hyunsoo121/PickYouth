import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import NavBar from '../components/NavBar';
import './MyPage.css';

export default function MyPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate('/');
  }

  if (!user) return null;

  return (
    <div>
      <NavBar />
      <div className="container my-page">
        <h1>마이페이지</h1>
        <div className="card my-info">
          <div className="my-info-row">
            <span className="my-info-label">이름</span>
            <span>{user.name}</span>
          </div>
          <div className="my-info-row">
            <span className="my-info-label">이메일</span>
            <span>{user.email}</span>
          </div>
        </div>
        <button className="btn btn--ghost btn--md my-logout" type="button" onClick={handleLogout}>
          로그아웃
        </button>
      </div>
    </div>
  );
}
