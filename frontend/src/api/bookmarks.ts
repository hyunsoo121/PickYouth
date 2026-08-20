import { apiRequest } from './client';
import type { SubsidySummary } from './subsidies';

export function fetchBookmarks() {
  return apiRequest<SubsidySummary[]>('/api/bookmarks', { auth: true });
}

export function addBookmark(subsidyId: number) {
  return apiRequest<void>(`/api/bookmarks/${subsidyId}`, { method: 'POST', auth: true });
}

export function removeBookmark(subsidyId: number) {
  return apiRequest<void>(`/api/bookmarks/${subsidyId}`, { method: 'DELETE', auth: true });
}
