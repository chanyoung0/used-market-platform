# 중고거래 플랫폼 (Used Market Platform)

개인 포트폴리오 프로젝트로 만들고 있는 중고거래 플랫폼 백엔드입니다.
기능을 많이 만드는 것보다, **설계 결정마다 이유를 남기고 동시성 같은 실제 문제를 검증하는 것**을 목표로 합니다.

## 진행 상황

| 단계 | 상태 | 내용 |
|------|------|------|
| 설계 | 회원·상품 완료 | MVP 범위, ERD, 상품 상태 전이, 설계 결정 기록 15건 (거래 상세 설계는 거래 단계 직전에 작성) |
| 회원 · 인증 | 완료 | 회원가입, 내 정보 조회/수정, JWT 로그인, 인증 필터, 예외 처리 구조, 입력 검증 |
| 상품 | 진행 중 | 등록 / 목록(페이징) / 상세 / 수정 / 삭제, 카테고리 필터, 제목 검색 |
| 거래 | 예정 | 가격 제안 구매 요청, 수락/거절, 동시성 처리(비관적 락), 거래 완료 |
| 배포 | 예정 | Docker 이미지, AWS EC2 배포, API 명세 |

## 핵심 설계 포인트

- **수락과 요청이 겹쳐도 거래는 하나만 성립** (거래 단계에서 구현 예정)
  한 상품에 여러 구매 요청이 들어올 수 있고, 판매자가 하나를 수락하면 나머지는 자동 거절됩니다.
  수락 중에 새 구매 요청이 들어오거나 같은 수락이 중복으로 도착하는 경우를 상품 행의 비관적 락으로 막고, 운영과 같은 MySQL에서 동시성 테스트로 검증할 계획입니다.
  ([0002](./docs/decisions/0002-purchase-request-negotiation.md), [0007](./docs/decisions/0007-local-db-mysql.md))
- **상태의 기준은 하나만 둔다**
  상품 상태(판매중 → 예약중 → 판매완료)는 `Product.status`에만 두고, 거래는 완료 시각(`completedAt`)만 기록합니다.
  두 테이블에 상태를 따로 두면 값이 어긋날 수 있기 때문입니다. ([ERD 설계 메모](./docs/erd-overview.md))
- **삭제는 상품 상태에 따라 다르게**
  판매중이고 구매 요청이 없는 상품은 하드 삭제, 요청이 걸린 상품은 소프트 삭제(거래 단계에서 구현), 예약중·판매완료 상품은 수정·삭제 금지.
  참조 무결성이 깨지는 경우만 보호합니다. ([0005](./docs/decisions/0005-product-deletion-policy.md))
- **의존 방향은 domain → global 한 방향**
  로그인처럼 회원 도메인이 필요한 로직은 `global`이 아니라 `domain.auth`에 두어, 공통 모듈이 도메인을 참조하지 않게 했습니다.
  ([0006](./docs/decisions/0006-package-structure.md), [0011](./docs/decisions/0011-login-structure.md))
- **에러 응답 형식은 하나로 통일**
  비즈니스 예외, 입력 검증 실패, 인증 실패(필터 단계)까지 모두 같은 `ErrorResponseDto` 형식으로 응답합니다.
  ([0008](./docs/decisions/0008-exception-handling-structure.md), [0012](./docs/decisions/0012-jwt-authentication-filter.md))

## 설계 결정 기록 (ADR)

주요 기술 결정은 고민한 선택지, 결정, 이유, 트레이드오프를 `docs/decisions/`에 기록하고 있습니다.

| 번호 | 결정 |
|------|------|
| [0001](./docs/decisions/0001-querydsl-fork-selection.md) | QueryDSL: CVE-2024-49203 경고에 대응하기 위해 OpenFeign 포크 사용 |
| [0002](./docs/decisions/0002-purchase-request-negotiation.md) | 구매 요청에 가격 제안(네고) 포함, 재협상은 제외 |
| [0003](./docs/decisions/0003-frontend-scope.md) | 프론트엔드: 핵심 흐름만 다루는 최소 화면을 별도로 제작 |
| [0004](./docs/decisions/0004-jwt-token-strategy.md) | JWT: Access Token 단일 발급, Refresh Token 미도입 |
| [0005](./docs/decisions/0005-product-deletion-policy.md) | 상품 삭제·수정 정책: 상태와 구매 요청 여부에 따라 구분 |
| [0006](./docs/decisions/0006-package-structure.md) | 패키지 구조: 계층형 대신 도메인형 |
| [0007](./docs/decisions/0007-local-db-mysql.md) | 로컬 개발 DB: H2 대신 Docker MySQL (동시성 검증을 운영 엔진에서) |
| [0008](./docs/decisions/0008-exception-handling-structure.md) | 예외 처리: BusinessException + ErrorCode + 전역 핸들러 |
| [0009](./docs/decisions/0009-input-validation-policy.md) | 입력 검증: DB 제약과 애플리케이션 검증의 이중 방어 |
| [0010](./docs/decisions/0010-jwt-library-and-token-policy.md) | JWT 구성: Nimbus JOSE + JWT, HS256, 만료 1시간, 시크릿은 .env |
| [0011](./docs/decisions/0011-login-structure.md) | 로그인: 인증 로직은 domain.auth로 분리, 실패 응답은 하나로 통일 |
| [0012](./docs/decisions/0012-jwt-authentication-filter.md) | 인증 필터: 필터는 인증 정보만 채우고, 거부와 응답은 인가 규칙과 EntryPoint가 담당 |
| [0013](./docs/decisions/0013-disable-open-in-view.md) | open-in-view 비활성화: Service가 필요한 데이터를 DTO로 완성해 넘기도록 경계를 강제 |
| [0014](./docs/decisions/0014-category-and-status-enum.md) | 카테고리·상태는 enum + VARCHAR: MySQL 네이티브 ENUM은 상수 추가 시 저장 실패 |
| [0015](./docs/decisions/0015-public-product-read.md) | 상품 목록·상세 조회는 비로그인 공개, 수정·삭제는 판매자 본인 |

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Data Access | Spring Data JPA, QueryDSL (OpenFeign 7.5) |
| Database | MySQL 8.4 (개발·운영), H2 (테스트) |
| Security | Spring Security, JWT (Nimbus JOSE + JWT) |
| Infra | Docker (로컬 DB), AWS EC2 (배포 예정) |
| Build Tool | Gradle |

## 개발 방식

- **백엔드**: Claude와 대화하며 설계를 논의하고 직접 구현. 도메인 규칙
  (상품 삭제 정책, JWT 전략 등)은 AI의 제안을 검토·반박하며 다듬는
  방식으로 결정했고, 실제 코드는 직접 작성함.
- **프론트엔드**: Claude Code를 활용해 화면 뼈대(라우팅, API 연동 기본
  구조)를 스캐폴딩. 생성된 코드는 직접 검토·수정함. (관련 결정:
  [`0003-frontend-scope.md`](./docs/decisions/0003-frontend-scope.md))

## 문서

- [MVP 범위](./docs/mvp-scope.md): 기능 범위와 이번에 제외한 기능(Tier 2)
- [ERD 개요](./docs/erd-overview.md): 엔티티 관계와 설계 메모

## 프로젝트 구조

```
src/main/java/com/chanyoung/usedmarket/
├── domain/
│   ├── member/     회원 (가입, 내 정보)
│   └── auth/       인증 (로그인)
└── global/
    ├── config/     보안 설정
    ├── security/   JWT 발급·검증, 인증 필터, EntryPoint
    ├── exception/  ErrorCode, BusinessException, 전역 핸들러
    └── entity/     공통 엔티티 (생성·수정 시각)
```

## 현재 API

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| POST | `/api/members/signup` | 회원가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 (Access Token 발급) | 불필요 |
| GET | `/api/members/me` | 내 정보 조회 | 필요 |
| PATCH | `/api/members/me` | 내 정보 수정 | 필요 |

전체 API 명세는 상품·거래 구현 후 추가할 예정입니다.

## 로컬 실행

사전 준비: JDK 21, Docker

1. 프로젝트 루트에 `.env` 파일을 만들고 아래 값을 채웁니다. `JWT_SECRET`은 32바이트(256비트) 이상의 문자열이어야 합니다. ([0010](./docs/decisions/0010-jwt-library-and-token-policy.md))

```
MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=
MYSQL_USER=
MYSQL_PASSWORD=
JWT_SECRET=
```

2. MySQL 컨테이너를 띄우고 서버를 실행합니다.

```
docker compose up -d
./gradlew bootRun
```

## 프론트엔드

별도 레포로 진행 예정입니다. ([`0003-frontend-scope.md`](./docs/decisions/0003-frontend-scope.md))
