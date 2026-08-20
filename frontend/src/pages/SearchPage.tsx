import { useEffect, useState, type FormEvent } from 'react';
import { ApiError } from '../api/client';
import {
  searchSubsidies,
  SUBSIDY_STATUS_LABEL,
  type SearchCondition,
  type SubsidyPage,
  type SubsidyStatus,
} from '../api/subsidies';
import NavBar from '../components/NavBar';
import SubsidyDetailModal from '../components/SubsidyDetailModal';
import './SearchPage.css';

// 온통청년 schoolCd 공통코드 (backend SchoolCd enum과 동일).
const SCHOOL_OPTIONS = [
  { code: '49001', label: '고졸미만' },
  { code: '49002', label: '고교재학' },
  { code: '49003', label: '고졸예정' },
  { code: '49004', label: '고교졸업' },
  { code: '49005', label: '대학재학' },
  { code: '49006', label: '대졸예정' },
  { code: '49007', label: '대학졸업' },
  { code: '49008', label: '석박사' },
  { code: '49009', label: '기타' },
];

// zipCd 앞 2자리(시도 코드). 실제 DB의 지역 특화 정책(제목/기관명에 지역명이 들어간 건)으로 직접
// 대조해서 확인한 값 — 강원(51)/전북(52) 특별자치도 개편, 전남·광주(12) 통합 등 최신 행정구역을 반영한다.
const SIDO_OPTIONS = [
  { code: '11', label: '서울' },
  { code: '12', label: '전남·광주' },
  { code: '26', label: '부산' },
  { code: '27', label: '대구' },
  { code: '28', label: '인천' },
  { code: '30', label: '대전' },
  { code: '31', label: '울산' },
  { code: '36', label: '세종' },
  { code: '41', label: '경기' },
  { code: '43', label: '충북' },
  { code: '44', label: '충남' },
  { code: '47', label: '경북' },
  { code: '48', label: '경남' },
  { code: '50', label: '제주' },
  { code: '51', label: '강원' },
  { code: '52', label: '전북' },
];

const STATUS_OPTIONS: SubsidyStatus[] = ['ONGOING', 'UPCOMING', 'ENDED'];

const PAGE_SIZE = 12;

interface FormState {
  age: string;
  income: string;
  schoolCd: string;
  zipCd: string;
  status: SubsidyStatus | '';
}

const EMPTY_FORM: FormState = { age: '', income: '', schoolCd: '', zipCd: '', status: '' };

function toCondition(form: FormState): SearchCondition {
  const condition: SearchCondition = {};
  if (form.age.trim()) condition.age = Number(form.age);
  if (form.income.trim()) condition.income = Number(form.income);
  if (form.schoolCd) condition.schoolCd = form.schoolCd;
  if (form.zipCd) condition.zipCd = form.zipCd;
  if (form.status) condition.status = form.status;
  return condition;
}

export default function SearchPage() {
  const [form, setForm] = useState<FormState>(EMPTY_FORM);
  const [condition, setCondition] = useState<SearchCondition>({});
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<SubsidyPage | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);
    searchSubsidies(condition, page, PAGE_SIZE)
      .then((data) => {
        if (!cancelled) setResult(data);
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof ApiError ? err.message : '검색에 실패했습니다.');
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [condition, page]);

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setPage(0);
    setCondition(toCondition(form));
  }

  function handleReset() {
    setForm(EMPTY_FORM);
    setPage(0);
    setCondition({});
  }

  return (
    <div>
      <NavBar />
      <div className="container search-page">
        <h1>내 조건에 맞는 지원금 찾기</h1>
        <p className="search-sub">조건은 모두 선택 사항입니다. 비워두면 전체에서 찾습니다.</p>

        <form className="search-form card" onSubmit={handleSubmit}>
          <div className="field">
            <label htmlFor="age">나이(만)</label>
            <input
              id="age"
              type="number"
              min={0}
              max={120}
              value={form.age}
              onChange={(e) => setForm((f) => ({ ...f, age: e.target.value }))}
            />
          </div>
          <div className="field">
            <label htmlFor="income">연소득(만원)</label>
            <input
              id="income"
              type="number"
              min={0}
              value={form.income}
              onChange={(e) => setForm((f) => ({ ...f, income: e.target.value }))}
            />
          </div>
          <div className="field">
            <label htmlFor="schoolCd">학적 상태</label>
            <select
              id="schoolCd"
              value={form.schoolCd}
              onChange={(e) => setForm((f) => ({ ...f, schoolCd: e.target.value }))}
            >
              <option value="">전체</option>
              {SCHOOL_OPTIONS.map((opt) => (
                <option key={opt.code} value={opt.code}>
                  {opt.label}
                </option>
              ))}
            </select>
          </div>
          <div className="field field--wide">
            <label>시행 상태</label>
            <div className="region-chips">
              <button
                type="button"
                className={`region-chip${form.status === '' ? ' region-chip--active' : ''}`}
                onClick={() => setForm((f) => ({ ...f, status: '' }))}
              >
                전체
              </button>
              {STATUS_OPTIONS.map((s) => (
                <button
                  key={s}
                  type="button"
                  className={`region-chip${form.status === s ? ' region-chip--active' : ''}`}
                  onClick={() => setForm((f) => ({ ...f, status: s }))}
                >
                  {SUBSIDY_STATUS_LABEL[s]}
                </button>
              ))}
            </div>
          </div>

          <div className="field field--wide">
            <label>지역</label>
            <div className="region-chips">
              <button
                type="button"
                className={`region-chip${form.zipCd === '' ? ' region-chip--active' : ''}`}
                onClick={() => setForm((f) => ({ ...f, zipCd: '' }))}
              >
                전체
              </button>
              {SIDO_OPTIONS.map((opt) => (
                <button
                  key={opt.code}
                  type="button"
                  className={`region-chip${form.zipCd === opt.code ? ' region-chip--active' : ''}`}
                  onClick={() => setForm((f) => ({ ...f, zipCd: opt.code }))}
                >
                  {opt.label}
                </button>
              ))}
            </div>
          </div>

          <div className="search-form-actions">
            <button className="btn btn--primary btn--md" type="submit">
              검색
            </button>
            <button className="btn btn--ghost btn--md" type="button" onClick={handleReset}>
              초기화
            </button>
          </div>
        </form>

        {error && <p className="form-error">{error}</p>}

        {loading && <div className="page-loading">검색 중...</div>}

        {!loading && result && (
          <>
            <p className="search-count">총 {result.totalElements.toLocaleString()}건</p>

            {result.content.length === 0 ? (
              <div className="page-empty">조건에 맞는 지원금이 없습니다.</div>
            ) : (
              <div className="result-grid">
                {result.content.map((item) => (
                  <button
                    key={item.id}
                    type="button"
                    className="card result-card"
                    onClick={() => setSelectedId(item.id)}
                  >
                    <div className="result-top">
                      <span className="result-category">
                        {item.categoryLarge}
                        {item.categoryMid ? ` · ${item.categoryMid}` : ''}
                      </span>
                      <span className={`status-badge status-badge--${item.status.toLowerCase()}`}>
                        {SUBSIDY_STATUS_LABEL[item.status]}
                      </span>
                    </div>
                    <h3 className="result-title">{item.title}</h3>
                    <p className="result-org">{item.org}</p>
                    <p className="result-period">
                      {item.applyStart ?? '상시'} ~ {item.applyEnd ?? '상시'}
                    </p>
                  </button>
                ))}
              </div>
            )}

            {result.totalPages > 1 && (
              <div className="pagination">
                <button
                  className="btn btn--ghost btn--sm"
                  type="button"
                  disabled={page === 0}
                  onClick={() => setPage((p) => p - 1)}
                >
                  이전
                </button>
                <span className="pagination-status">
                  {page + 1} / {result.totalPages}
                </span>
                <button
                  className="btn btn--ghost btn--sm"
                  type="button"
                  disabled={!result.hasNext}
                  onClick={() => setPage((p) => p + 1)}
                >
                  다음
                </button>
              </div>
            )}
          </>
        )}
      </div>

      {selectedId !== null && (
        <SubsidyDetailModal subsidyId={selectedId} onClose={() => setSelectedId(null)} />
      )}
    </div>
  );
}
