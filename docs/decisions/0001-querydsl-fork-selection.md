# 0001. QueryDSL 라이브러리 선택: com.querydsl 대신 io.github.openfeign.querydsl 포크 사용

## 상태
확정

## 배경
Spring Initializr는 QueryDSL을 기본 의존성 목록에 제공하지 않아서,
build.gradle에 직접 추가해야 했음. 이때 흔히 쓰이는 `com.querydsl` 그룹을
그대로 넣었더니, IntelliJ 의존성 검사에서 CVE-2024-49203(SQL/HQL 인젝션,
CVSS 9.8) 경고가 뜸.

## 고민한 선택지
1. `com.querydsl:querydsl-jpa:5.0.0:jakarta` (원조 프로젝트)
2. `io.github.openfeign.querydsl:querydsl-jpa:7.5` (OpenFeign 포크)

## 결정
2번(OpenFeign 포크)을 채택.

## 이유
- 원조 `com.querydsl` 프로젝트는 2022년 이후 사실상 유지보수가 중단된 상태.
  실제로 CVE 패치도 원조 쪽엔 반영되지 않음.
- OpenFeign이 포크해서 현재까지 지속적으로 릴리즈 중이며, Jakarta EE를
  기본으로 지원해 별도 `jakarta` classifier가 필요 없음.
- CVE 자체는 "사용자 입력을 orderBy에 검증 없이 직접 넣었을 때"만
  발생하는 조건부 취약점이라 즉시 위험한 건 아니었지만, 어차피 막
  시작하는 프로젝트에 유지보수가 끊긴 의존성을 처음부터 넣을 이유가
  없다고 판단.

## 트레이드오프
- OpenFeign 포크는 원조보다 커뮤니티/레퍼런스 자료가 상대적으로 적음
  (블로그 글 등이 대부분 `com.querydsl` 기준으로 작성됨).
- 다만 API 사용법 자체는 거의 동일해서 실질적인 학습 비용 차이는 크지 않음.