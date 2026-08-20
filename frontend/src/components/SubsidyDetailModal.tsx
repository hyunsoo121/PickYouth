import { useEffect, useState } from 'react';
import { ApiError } from '../api/client';
import {
  fetchSubsidyDetail,
  SUBSIDY_STATUS_LABEL,
  type SubsidyDetail,
} from '../api/subsidies';
import './SubsidyDetailModal.css';

export default function SubsidyDetailModal({
  subsidyId,
  onClose,
  isBookmarked,
  onToggleBookmark,
}: {
  subsidyId: number;
  onClose: () => void;
  isBookmarked?: boolean;
  onToggleBookmark?: () => void;
}) {
  const [detail, setDetail] = useState<SubsidyDetail | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setDetail(null);
    setError(null);
    fetchSubsidyDetail(subsidyId)
      .then((data) => {
        if (!cancelled) setDetail(data);
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof ApiError ? err.message : '상세 정보를 불러오지 못했습니다.');
        }
      });
    return () => {
      cancelled = true;
    };
  }, [subsidyId]);

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-panel card" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" type="button" onClick={onClose} aria-label="닫기">
          ✕
        </button>
        {error && <p className="form-error">{error}</p>}
        {!detail && !error && <div className="page-loading">불러오는 중...</div>}
        {detail && (
          <div className="modal-body">
            <div className="modal-top">
              <span className="modal-category">
                {detail.categoryLarge}
                {detail.categoryMid ? ` · ${detail.categoryMid}` : ''}
              </span>
              <span className="modal-top-right">
                <span className={`status-badge status-badge--${detail.status.toLowerCase()}`}>
                  {SUBSIDY_STATUS_LABEL[detail.status]}
                </span>
                {onToggleBookmark && (
                  <button
                    type="button"
                    className="bookmark-toggle"
                    aria-label="관심 등록/해제"
                    onClick={onToggleBookmark}
                  >
                    {isBookmarked ? '♥' : '♡'}
                  </button>
                )}
              </span>
            </div>
            <h2>{detail.title}</h2>
            <p className="modal-org">{detail.org}</p>

            {detail.description && <p className="modal-desc">{detail.description}</p>}

            {detail.supportContent && (
              <div className="modal-section">
                <h3>지원 내용</h3>
                <p>{detail.supportContent}</p>
              </div>
            )}

            <div className="modal-section modal-meta">
              <div>
                <span className="modal-meta-label">신청 기간</span>
                <span>
                  {detail.applyStart ?? '상시'} ~ {detail.applyEnd ?? '상시'}
                </span>
              </div>
              {detail.ageLimitYn === 'Y' && (detail.ageMin || detail.ageMax) && (
                <div>
                  <span className="modal-meta-label">연령</span>
                  <span>
                    만 {detail.ageMin ?? '-'}세 ~ {detail.ageMax ?? '-'}세
                  </span>
                </div>
              )}
            </div>

            {detail.applyUrl && (
              <a
                className="btn btn--primary btn--md modal-apply"
                href={detail.applyUrl}
                target="_blank"
                rel="noreferrer"
              >
                신청 페이지로 이동
              </a>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
