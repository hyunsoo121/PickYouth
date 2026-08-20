import { useEffect, useState } from 'react';
import { fetchBookmarks, removeBookmark } from '../api/bookmarks';
import { ApiError } from '../api/client';
import { SUBSIDY_STATUS_LABEL, type SubsidySummary } from '../api/subsidies';
import NavBar from '../components/NavBar';
import SubsidyDetailModal from '../components/SubsidyDetailModal';
import './SearchPage.css';

export default function BookmarksPage() {
  const [items, setItems] = useState<SubsidySummary[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);

  function load() {
    setError(null);
    fetchBookmarks()
      .then(setItems)
      .catch((err) => {
        setError(err instanceof ApiError ? err.message : '관심 목록을 불러오지 못했습니다.');
      });
  }

  useEffect(() => {
    load();
  }, []);

  async function handleRemove(e: React.MouseEvent, subsidyId: number) {
    e.stopPropagation();
    await removeBookmark(subsidyId);
    setItems((prev) => prev?.filter((it) => it.id !== subsidyId) ?? prev);
  }

  return (
    <div>
      <NavBar />
      <div className="container search-page">
        <h1>내 관심 정책</h1>
        <p className="search-sub">관심 등록한 지원금 목록입니다.</p>

        {error && <p className="form-error">{error}</p>}

        {!items && !error && <div className="page-loading">불러오는 중...</div>}

        {items && items.length === 0 && (
          <div className="page-empty">아직 관심 등록한 정책이 없습니다.</div>
        )}

        {items && items.length > 0 && (
          <div className="result-grid">
            {items.map((item) => (
              <div
                key={item.id}
                role="button"
                tabIndex={0}
                className="card result-card"
                onClick={() => setSelectedId(item.id)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' || e.key === ' ') setSelectedId(item.id);
                }}
              >
                <div className="result-top">
                  <span className="result-category">
                    {item.categoryLarge}
                    {item.categoryMid ? ` · ${item.categoryMid}` : ''}
                  </span>
                  <span className="result-top-right">
                    <span className={`status-badge status-badge--${item.status.toLowerCase()}`}>
                      {SUBSIDY_STATUS_LABEL[item.status]}
                    </span>
                    <button
                      type="button"
                      className="bookmark-toggle"
                      aria-label="관심 해제"
                      onClick={(e) => handleRemove(e, item.id)}
                    >
                      ♥
                    </button>
                  </span>
                </div>
                <h3 className="result-title">{item.title}</h3>
                <p className="result-org">{item.org}</p>
                <p className="result-period">
                  {item.applyStart ?? '상시'} ~ {item.applyEnd ?? '상시'}
                </p>
              </div>
            ))}
          </div>
        )}
      </div>

      {selectedId !== null && (
        <SubsidyDetailModal
          subsidyId={selectedId}
          onClose={() => setSelectedId(null)}
          isBookmarked
          onToggleBookmark={() => {
            removeBookmark(selectedId);
            setItems((prev) => prev?.filter((it) => it.id !== selectedId) ?? prev);
            setSelectedId(null);
          }}
        />
      )}
    </div>
  );
}
