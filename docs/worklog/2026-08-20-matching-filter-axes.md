# 2026-08-20 — 매칭 필터 축 확장 (jobCd/plcyMajorCd/sbizCd)

## 배경

`PickYouth.md` §4는 MVP 매칭 필터를 나이/지역/학적상태/소득/대분류 5개 축으로 정의했지만, 실제로는 이보다 넓게 가져가기로 결정된 상태였다 (Milestone 2 착수 전 결정 필요 항목). 이번 세션에서 온통청년 API가 이미 제공하는 필드 중 추가 후보를 검토하고, 그중 3개를 골라 수집 파이프라인에 실제로 반영했다.

## 진행 상황 요약 (Milestone 기준)

| Milestone | 상태 |
|---|---|
| M0 — 스켈레톤 (엔티티/레포지토리/JWT 보안 스켈레톤) | 완료 |
| M1 — 온통청년 데이터 파이프라인 | 완료 (2026-08-18) |
| M2 — 조회/매칭 API (비회원 가능) | **미착수** — 이번 세션은 M2 착수 전 선결 작업(매칭 축 확정) |
| M3~M7 | 미착수 |

## 추가한 매칭 축

기존 5축(나이/지역/학적/소득/대분류) 외에, 온통청년 API가 이미 응답으로 내려주는 필드 중 아래 3개를 신규 매칭 축으로 채택:

| 필드 | 의미 | 비고 |
|---|---|---|
| `jobCd` | 취업요건 (재직자/자영업자/미취업자/프리랜서 등 10종) | 라벨까지 확정 — `JobCd` enum |
| `sbizCd` | 특화요건 (중소기업/여성/기초생활수급자/한부모/장애인 등 10종) | 라벨까지 확정 — `SbizCd` enum. 코드정의서는 `sBizCd`로 표기하지만 실제 응답 필드명은 소문자 `sbizCd` |
| `plcyMajorCd` | 전공요건 | 코드 범위(11001~11009)만 확인, 코드별 정확한 한글 라벨 미검증 → 라벨 enum으로 만들지 않고 `PlcyMajorCd.normalize()`만 제공 |

**채택 보류**: `plcyKywdNm`(키워드 17종)은 엄밀한 매칭 조건보다 태그성 필드로 판단해 이번엔 제외 — 향후 검색/브라우징 UX(다대다 태그)로 별도 설계 필요.

## 변경한 파일

- `domain/subsidy/code/JobCd.java`, `SbizCd.java`, `PlcyMajorCd.java` (신규) — 코드값 정규화(7자리 `00` prefix 제거) + 라벨 enum(확정된 2개만)
- `domain/subsidy/client/dto/YouthPolicyDto.java` — `jobCd`, `plcyMajorCd`, `sbizCd` 필드 추가
- `domain/subsidy/entity/Subsidy.java` — `jobCd`, `majorCd`, `specialCd` 컬럼 추가 (builder/update 시그니처 반영)
- `domain/subsidy/batch/SubsidyUpsertData.java`, `PolicyDtoToEntityProcessor.java`, `SubsidyUpsertWriter.java` — 파이프라인 전 구간에 3개 필드 전달
- `PickYouth.md` §4/§5 매핑표 갱신

`spring.jpa.hibernate.ddl-auto: update` 설정 덕분에 별도 마이그레이션 파일 없이 다음 앱 기동 시 컬럼이 자동 추가됨. `./gradlew compileJava` 클린 확인.

---

## 트러블슈팅: 기존 행 백필 안 됨

### 증상

배치(`collectPolicyJob`)를 정상적으로 재실행해 COMPLETED를 확인했는데, DB를 조회해보니 신규 컬럼이 거의 채워지지 않음.

```sql
SELECT count(*) AS total, count(job_cd), count(major_cd), count(special_cd) FROM subsidy;
-- total=2720, with_job_cd=5, with_major_cd=5, with_special_cd=5
```

2720건 중 5건만 채워짐.

### 원인

`SubsidyUpsertWriter.updateIfChanged()`는 API가 내려주는 `lastMdfcnDt`(정책 최종수정일시)가 DB에 저장된 값과 동일하면 **update 자체를 스킵**하는 idempotency 로직을 갖고 있다.

```java
private void updateIfChanged(Subsidy existing, SubsidyUpsertData data) {
  boolean unchanged =
      existing.getLastMdfcnDt() != null && existing.getLastMdfcnDt().equals(data.lastMdfcnDt());
  if (unchanged) {
    return; // 여기서 새 컬럼도 함께 스킵됨
  }
  existing.update(...);
}
```

이 로직은 "정책 내용이 안 바뀌었으면 불필요한 쓰기를 안 한다"는 목적으로는 옳지만, **엔티티에 새 컬럼을 추가한 경우까지 고려하진 않는다** — 정책 자체는 안 바뀌었어도 우리 스키마는 바뀌었기 때문에 새 컬럼은 채워져야 하는데, `lastMdfcnDt`만 보고 판단하다 보니 이 케이스를 놓친다. 실제로 이번 재실행에서 값이 채워진 5건은 마침 그 사이 정책이 실제로 갱신된 건들이었다.

### 해결

새 컬럼을 추가한 직후 1회성으로 `unchanged` 스킵을 우회해 전체 재수집을 강제했다.

1. `if (unchanged) return;` → `if (unchanged && false) return;` 로 임시 수정 (전량 update 강제)
2. 배치 재실행 (전량 update라 1분 36초 소요 — 최초엔 13초)
3. 결과 확인: `with_job_cd/major_cd/special_cd = 2718/2720` (나머지 2건은 API 원본 값 자체가 빈 값인 정상 케이스)
4. 임시 수정 원복, `grep`으로 원상복구 확인

### 교훈

이 프로젝트의 upsert는 `lastMdfcnDt` 기준 idempotency라, **`Subsidy`에 새 매핑 필드를 추가할 때마다 정상 재실행만으로는 기존 행이 백필되지 않는다.** 다음에 컬럼을 또 추가할 때도 동일한 임시 우회 + 재실행 + 원복 절차(또는 전용 1회성 마이그레이션 스크립트)가 필요하다.

---

## 다음 단계

Milestone 2 — `SubsidyQueryService` / `GET /api/subsidies` 설계 시:
- 새 3축(`jobCd`/`majorCd`/`specialCd`)을 쿼리 파라미터로 노출
- 동적 다축 필터에 JPA Specification vs QueryDSL 중 택 1 (QueryDSL 추천, 미확정)
- Redis 캐싱 전략
