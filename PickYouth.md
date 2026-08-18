# 청년픽 (PickYouth) — 개발 착수 명세서

> 이 문서는 기획 단계 결정사항을 정리한 개발 착수용 핸드오프 문서입니다.

---

## 1. 서비스 개요

**한 줄 소개**
로그인 없이 조건만 입력하면 나에게 맞는 청년 지원금을 빠르게 찾아주고, 회원가입 시 신규 정책/마감 임박을 알림으로 받을 수 있는 서비스

**핵심 가치 제안**
"3초 컷, 로그인 프리, 딱 내 것만"
— 조회/매칭은 비회원도 가능, 알림·찜 기능만 회원 전용 (하이브리드 로그인 구조)

**문제 정의**
청년정책은 2,700여 개(온통청년 API 기준 `totCount`)에 달할 만큼 다양하지만, 부처·지자체별로 흩어져 있어 조건에 맞는 것을 찾기 어렵다. 또한 관심 있는 지원금이 있어도 신청 시점을 놓쳐 기회를 잃는 경우가 많은데, 이를 능동적으로 알려주는 채널이 부재하다.

**타겟**
지원금 제도는 알고 있지만, 기존 플랫폼(온통청년/청년몽땅 등)에서 정보 찾는 과정이 번거로워 포기하는 대학생·청년층

**참고 — 경쟁 서비스**
- 온통청년: 정부 공식, 맞춤 매칭이 로그인 후에만 열림 → 청년픽은 매칭을 비회원까지 개방해 차별화
- kfund.ai: 맞춤 필터+알림 기능은 이미 존재하나 타겟이 창업/사업자 중심 → 청년픽은 개인 생활비/주거 중심

---

## 2. MVP 기능 범위

### 비회원 가능
- 원페이지 조건 입력 (나이 / 지역 / 학적상태 / 소득)
- 실시간 카드형 매칭 결과 (대분류/중분류 카테고리 필터 포함)
- 상세 모달 + 신청 아웃링크

### 회원 전용 (Spring Security 기반 회원가입/로그인)
- 찜하기
- 정책 개시 알림 구독 (내 조건에 맞는 신규 정책 등록 시 발송)
- 마감 임박 알림 (찜한 정책 기준)

---

## 3. 기술 스택

| 영역 | 선택 | 비고 |
|---|---|---|
| Backend | **Spring Boot** | 취업 포트폴리오 목적 + 아래 근거 |
| 인증 | Spring Security + JWT | 회원 전용 기능(찜/알림) 분리에 필요 |
| 배치 | Spring Batch | 크롤링/API 수집 ETL — chunk 처리, 실패 재시작 |
| 스케줄링 | `@Scheduled` | 정책 개시 알림 / 마감 임박 알림 |
| Frontend | React | — |
| DB | **PostgreSQL** | 관계형 매칭 쿼리 + JSONB 혼용, MongoDB 불필요로 결론 |
| 캐시 | Redis (Spring Data Redis) | 조건별 매칭 결과 캐싱 → "3초 컷" 응답속도 확보 |
| 인프라 | Docker, GCP/AWS | — |

**Spring Boot를 선택한 이유 (포트폴리오 서사용)**
1. 목표 채용 스택(Spring Boot/JPA)을 실제 프로젝트로 증명
2. Spring Security 학습/어필 포인트 (회원 인증이 MVP에 포함되며 필요성 강화)
3. Spring Batch가 크롤링+API 수집 ETL에 적합
4. `@Scheduled`가 알림 기능 2종에 그대로 대응
5. Spring Data Redis로 캐싱 전략 구현 용이

---

## 4. 데이터 모델 (엔티티 설계)

| 엔티티 | 주요 필드 | 비고 |
|---|---|---|
| `User` | id, email, password(암호화), name, created_at | |
| `Subsidy` | id, plcyNo, title, org, category_large, category_mid, age_min, age_max, age_limit_yn, school_cd, marriage_cd, income_cond_cd, income_min, income_max, apply_period_raw, apply_start, apply_end, apply_url, ref_url1, ref_url2, first_reg_dt, last_mdfcn_dt | 아래 5절 필드 매핑표 참고 |
| `SubsidyRegion` | id, subsidy_id(FK), zip_cd | `Subsidy`와 1:N — zipCd가 콤마로 다중값이라 정규화 필요 (아래 6절 참고) |
| `Bookmark` | id, user_id(FK), subsidy_id(FK), created_at | |
| `AlertSubscription` | id, user_id(FK), age, region, school_cd, income, channel(EMAIL/KAKAO), active | |
| `NotificationLog` | id, user_id(FK), subsidy_id(FK), type(POLICY_OPEN/DEADLINE), channel, sent_at, status | 중복/누락 방지용 |

**공통코드 테이블 (`CommonCode` 또는 Enum)** — 온통청년 코드정의서 기준, 시드 데이터로 그대로 사용
- `schoolCd`(학력요건): 49001 고졸미만 · 49002 고교재학 · 49003 고졸예정 · 49004 고교졸업 · 49005 대학재학 · 49006 대졸예정 · 49007 대학졸업 · 49008 석박사 · 49009 기타 · 49010 제한없음
- `earnCndSeCd`(소득조건): 43001 무관 · 43002 연소득 · 43003 기타
- `mrgSttsCd`(결혼상태): 55001 기혼 · 55002 미혼 · 55003 제한없음
- `jobCd`(취업요건): 13001~13010 (재직자/자영업자/미취업자/프리랜서/일용근로자/예비창업자/단기근로자/영농종사자/기타/제한없음)
- `plcyMajorCd`(전공요건): 11001~11009 (계열별 + 제한없음)
- `sbizCd`(특화요건): 14001~14010 (중소기업/여성/기초생활수급자/한부모/장애인/농업인/군인/지역인재/기타/제한없음)
- `lclsfNm`(대분류, 5개): 일자리·주거·교육·복지문화·참여권리
- `mclsfNm`(중분류, 17개): 취업/재직자/창업/주택및거주지/기숙사/전월세및주거급여지원/미래역량강화/교육비지원/온라인교육/취약계층및금융지원/건강/예술인지원/문화활동/청년참여/정책인프라구축/청년국제교류/권익보호
- `plcyKywdNm`(키워드, 17개): 대출/보조금/바우처/금리혜택/교육지원/맞춤형상담서비스/인턴/벤처/중소기업/청년가장/장기미취업청년/공공임대주택/신용회복/육아/출산/해외진출/주거지원

MVP 매칭 필터는 나이 + 지역을 핵심 축으로, 학적상태/소득/대분류를 부가 필터로 설계.

---

## 5. 외부 데이터 연동 — 온통청년 오픈 API

**엔드포인트**
```
GET https://www.youthcenter.go.kr/go/ythip/getPlcy
```

**요청 파라미터**
| 파라미터 | 필수 | 설명 |
|---|---|---|
| `apiKeyNm` | Y | 발급받은 인증키 |
| `pageNum` | N | 페이지 번호 |
| `pageSize` | N | 페이지 크기 (최댓값 미확인 — 개발 착수 시 테스트 필요) |
| `rtnType` | N | `json` 권장 |
| `zipCd` | N | 법정시군구코드 (Phase1 서울/경기 필터링에 사용) |
| `plcyKywdNm`, `lclsfNm`, `mclsfNm`, `plcyNm`, `plcyExplnCn`, `plcyNo` | N | 검색/필터용 |

**응답 필드 → `Subsidy` 매핑 (실제 응답 기준으로 검증 완료)**
| API 필드 | 매핑 대상 | 주의사항 |
|---|---|---|
| `plcyNo` | id 매핑키 | |
| `plcyNm` / `plcyExplnCn` / `plcySprtCn` | title / 설명 / 지원내용 | |
| `lclsfNm` / `mclsfNm` | 대/중분류 | |
| `sprtTrgtMinAge` / `sprtTrgtMaxAge` / `sprtTrgtAgeLmtYn` | 나이 매칭 | 정상 동작 확인 |
| `zipCd` | `SubsidyRegion` | **콤마로 구분된 다중값** (`"12110,12130,..."`) — split 후 1:N 테이블에 삽입 |
| `schoolCd` / `earnCndSeCd` / `earnMinAmt` / `earnMaxAmt` / `mrgSttsCd` | 학적/소득/결혼 매칭 | 코드값은 아래 "코드값 자릿수 주의" 참고. `earnCndSeCd`가 `43002`(연소득)일 때만 `earnMinAmt`/`Max` 사용, 그 외는 무관 처리 |
| `aplyUrlAddr` | 신청 아웃링크 | **빈 문자열 케이스 있음** → `refUrlAddr1` → `refUrlAddr2` 순으로 폴백 |
| `aplyYmd` | 마감 임박 알림 계산 | `"20260807 ~ 20260930"` 형식, `" ~ "` split로 파싱. **빈 문자열(상시모집 등) 케이스 있음 → 알림 대상에서 제외 처리** |
| `bizPrdBgngYmd` / `bizPrdEndYmd` | (미사용) | **실사용 데이터는 공백 문자열**(`"        "`)로 채워지는 경우가 많아 신뢰 불가 — `aplyYmd`로 대체 |
| `frstRegDt` / `lastMdfcnDt` | 정책 개시 알림 트리거 | 배치가 DB 마지막 수집시각과 비교해 신규/변경 건 탐지 |
| `sbizCd` | 특화요건 | 문서상 `sBizCd`로 표기되어 있으나 **실제 응답은 소문자 `sbizCd`** — `@JsonProperty("sbizCd")`로 매핑 |

**⚠️ 코드값 자릿수 주의 (중요 — 실제 검증된 이슈)**
코드정의서(엑셀)의 코드는 5자리(예: `schoolCd` 제한없음 = `49010`)이지만, **실제 API 응답값은 앞에 `00`이 붙은 7자리**(`"0049010"`)로 내려온다. 코드 매핑 시 좌측 2자리(`"00"`)를 제거하거나, 코드북 값을 7자리로 zero-padding해서 비교해야 한다. 이 부분을 놓치면 모든 코드값 매칭이 조용히 실패한다.

**응답 규모**
- `totCount: 2715` (조사 시점 기준, 변동됨)
- 전량 수집 시 페이지네이션 배치 전략 필요 — `pageSize` 최댓값 확인 후 확정

---

## 6. API 설계 (우리 서비스 자체)

### 인증
| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 (JWT 발급) |
| GET | `/api/auth/me` | 내 정보 조회 |

### 조회/매칭 (비회원 가능)
| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/subsidies?age=&region=&income=&school=` | 조건 매칭 카드형 리스트 |
| GET | `/api/subsidies/{id}` | 상세 정보 |

### 찜 (회원 전용)
| Method | Endpoint | 설명 |
|---|---|---|
| POST / DELETE | `/api/bookmarks/{subsidyId}` | 찜 추가/해제 |
| GET | `/api/bookmarks` | 내 찜 목록 |

### 알림 구독 (회원 전용)
| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/alerts/subscriptions` | 알림 조건 등록 |
| GET / DELETE | `/api/alerts/subscriptions[/{id}]` | 조회/삭제 |

### 내부 배치 (외부 미노출, `@Scheduled`)
- 온통청년 API 주기 수집 → 신규/변경 정책 감지 → `AlertSubscription` 매칭 → 발송
- 매일 1회 → `Bookmark` 중 마감 D-3/D-1 → 발송

---

## 7. 로드맵

- **Phase 1 (MVP)**: 서울/경기 데이터, 비회원 조회/매칭 + 회원가입/로그인 + 찜 + 알림 2종 전부 포함
- **Phase 2**: 전국 데이터 확장, 카카오톡 알림 채널 연동, 마이페이지 고도화
- **Phase 3**: UX 플로우/와이어프레임 고도화 (기획 단계에서 의도적으로 후순위로 미룸)

---

## 8. 개발 착수 전 남은 TODO

1. `pageSize` 최댓값 실측 (배치 수집 전략 확정용)
2. 서울/경기(`zipCd`) 필터링 시 실제 데이터 볼륨 확인
3. 화면 설계/UX 와이어프레임 (Phase 3로 보류 확정되었으나, 개발 착수 시점엔 최소 수준으로 필요할 수 있음)