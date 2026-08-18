# 개발 규칙

## 브랜치 전략
- `main`: 배포 가능한 상태만 유지
- `develop`: 통합 개발 브랜치
- `feature/{scope}-{설명}` — 예: `feature/backend-jwt-login`, `feature/frontend-match-page`
- `fix/{scope}-{설명}` — 버그 수정
- 작업 브랜치는 `develop`에서 분기, PR로 `develop`에 병합

## 커밋 컨벤션 (Conventional Commits)
```
<type>(<scope>): <설명>
```
- `type`: `feat` `fix` `refactor` `docs` `test` `chore` `style` `perf`
- `scope`: `backend` `frontend` `infra` `docs` (선택)

예:
```
feat(backend): 온통청년 API 응답 → Subsidy 엔티티 매핑 구현
fix(backend): schoolCd 7자리 zero-padding 미처리로 매칭 실패하는 문제 수정
chore(infra): docker-compose에 Redis 캐시 컨테이너 추가
```

## PR 규칙
- PR 템플릿의 체크리스트를 채운다
- 가능하면 작은 단위로 나눠서 리뷰 가능하게 유지

## 코드 포맷팅
- Backend: Spotless(Google Java Format) — `./gradlew spotlessApply`로 커밋 전 정리
- Frontend: ESLint + Prettier — `npm run lint`, `npm run format`
