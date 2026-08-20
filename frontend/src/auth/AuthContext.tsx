import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import {
  fetchMe,
  login as apiLogin,
  logout as apiLogout,
  signup as apiSignup,
  type UserResponse,
} from '../api/auth';
import { clearTokens, getAccessToken, setTokens } from '../api/client';

interface AuthContextValue {
  user: UserResponse | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string, name: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [loading, setLoading] = useState(true);

  async function loadUser() {
    if (!getAccessToken()) {
      setUser(null);
      return;
    }
    try {
      setUser(await fetchMe());
    } catch {
      clearTokens();
      setUser(null);
    }
  }

  useEffect(() => {
    loadUser().finally(() => setLoading(false));
  }, []);

  async function login(email: string, password: string) {
    const tokens = await apiLogin({ email, password });
    setTokens(tokens.accessToken, tokens.refreshToken);
    await loadUser();
  }

  async function signup(email: string, password: string, name: string) {
    await apiSignup({ email, password, name });
    await login(email, password);
  }

  async function logout() {
    try {
      await apiLogout();
    } finally {
      clearTokens();
      setUser(null);
    }
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
