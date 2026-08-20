import { apiRequest } from './client';

export interface SubsidySummary {
  id: number;
  plcyNo: string;
  title: string;
  org: string;
  categoryLarge: string;
  categoryMid: string;
  applyStart: string | null;
  applyEnd: string | null;
}

export interface SubsidyDetail extends SubsidySummary {
  description: string;
  supportContent: string;
  ageMin: number | null;
  ageMax: number | null;
  ageLimitYn: string;
  schoolCd: string;
  jobCd: string;
  majorCd: string;
  marriageCd: string;
  incomeCondCd: string;
  incomeMin: number | null;
  incomeMax: number | null;
  specialCd: string;
  applyUrl: string | null;
  zipCodes: string[];
}

export interface SubsidyPage {
  content: SubsidySummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
}

export interface SearchCondition {
  age?: number;
  income?: number;
  schoolCd?: string;
}

export function searchSubsidies(condition: SearchCondition, page = 0, size = 12) {
  const params = new URLSearchParams();
  Object.entries(condition).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== ('' as unknown)) {
      params.set(key, String(value));
    }
  });
  params.set('page', String(page));
  params.set('size', String(size));
  return apiRequest<SubsidyPage>(`/api/subsidies?${params.toString()}`);
}

export function fetchSubsidyDetail(id: number) {
  return apiRequest<SubsidyDetail>(`/api/subsidies/${id}`);
}
