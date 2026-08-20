import { apiRequest } from './client';

export interface UserResponse {
  id: number;
  email: string;
  name: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

export function signup(payload: { email: string; password: string; name: string }) {
  return apiRequest<UserResponse>('/api/auth/signup', { method: 'POST', body: payload });
}

export function login(payload: { email: string; password: string }) {
  return apiRequest<TokenResponse>('/api/auth/login', { method: 'POST', body: payload });
}

export function logout() {
  return apiRequest<void>('/api/auth/logout', { method: 'POST', auth: true });
}

export function fetchMe() {
  return apiRequest<UserResponse>('/api/auth/me', { auth: true });
}
