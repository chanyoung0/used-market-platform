# 중고거래 플랫폼 (Used Market Platform)

개인 포트폴리오 프로젝트로 만든 중고거래 플랫폼 백엔드입니다.

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| Data Access | Spring Data JPA, QueryDSL |
| Database | MySQL (운영), H2 (테스트) |
| Security | Spring Security, JWT |
| Infra | Docker, AWS EC2 |
| Build Tool | Gradle |

## 개발 방식

- **백엔드**: Claude와 대화하며 설계를 논의하고 직접 구현. 도메인 규칙
  (상품 삭제 정책, JWT 전략 등)은 AI의 제안을 검토·반박하며 다듬는
  방식으로 결정했고, 실제 코드는 직접 작성함.
- **프론트엔드**: Claude Code를 활용해 화면 뼈대(라우팅, API 연동 기본
  구조)를 스캐폴딩. 생성된 코드는 직접 검토·수정함. (관련 결정:
  [`0003-frontend-scope.md`](./docs/decisions/0003-frontend-scope.md))

## 프로젝트 구조

src/main/java/com/chanyoung/usedmarket/

├── domain/     (도메인별 패키지: item, member 등)

└── global/     (공통 설정, 예외처리, 시큐리티)

## ERD

전체 엔티티 관계는 [`docs/erd-overview.md`](./docs/erd-overview.md) 를 참고하세요.

## 설계 결정 기록

프로젝트 진행 중 내린 주요 기술적 결정과 그 이유는 docs/decisions/ 에 기록하고 있습니다.

## 실행 방법

(아직 작성 전 - 기능 구현 후 추가 예정)

## 주요 기능

MVP 범위는 [`docs/mvp-scope.md`](./docs/mvp-scope.md) 를 참고하세요.

## API 명세

(아직 작성 전 - 기능 구현 후 추가 예정)

## 프론트엔드

> 별도 레포로 진행 예정. 완성 후 링크 추가 예정.
> (관련 결정: [`docs/decisions/0003-frontend-scope.md`](./docs/decisions/0003-frontend-scope.md))