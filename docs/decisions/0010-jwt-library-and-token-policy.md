# 0010. JWT 구성: Nimbus JOSE + JWT, HS256 서명, Access Token 만료 1시간, 시크릿 키는 .env로 관리

## 상태
확정

## 배경
ADR 0004에서 Access Token 단일 발급 방식을 정했고, 이를 구현하려면 토큰 서명/검증을 수행할 라이브러리, 서명 알고리즘, 만료 시간, 서명용 시크릿 키 관리 방식이 필요했음. 이 프로젝트는 Spring Boot 4.1.1을 사용하며 기본 JSON 라이브러리가 Jackson 3(tools.jackson)이라, JWT 라이브러리가 내부에서 쓰는 Jackson 버전과의 호환성이 선택 기준이 됨.

## 고민한 선택지
라이브러리
1. jjwt + Jackson 2 호환 모듈 추가
2. jjwt + Gson 모듈 (jjwt-gson)
3. Nimbus JOSE + JWT
4. auth0 java-jwt

서명 알고리즘
1. 대칭키 방식 (HS256): 하나의 시크릿 키로 서명과 검증을 모두 수행
2. 비대칭키 방식 (RS256 등): 개인키로 서명하고 공개키로 검증

만료 시간
1. 1시간 단일 고정
2. 30분 + 활동 시 갱신

## 결정
- 라이브러리: Nimbus JOSE + JWT 채택. 토큰 생성/검증만 라이브러리에 맡기고, 토큰 제공자(JwtTokenProvider)와 인증 필터(JwtAuthenticationFilter)는 직접 구현함. Spring Security의 resource server 자동 구성은 사용하지 않음.
- 서명 알고리즘: HS256 (대칭키).
- 만료 시간: Access Token 1시간, 갱신 없음. 설정값 jwt.expiration-ms로 관리함.
- 시크릿 키: .env에 32바이트(256비트) 이상 문자열로 두고 설정값 jwt.secret으로 주입함. 코드에는 포함하지 않음. 문자열을 UTF-8 바이트로 변환해 서명 키로 사용함.
- 토큰에는 회원 id(sub), 발급 시각(iat), 만료 시각(exp)만 담음. JwtTokenProvider는 Member가 아니라 회원 id를 받아 도메인에 의존하지 않게 함.

## 이유
- jjwt는 JSON 처리를 Jackson 2 모듈에 의존하며, Jackson 3 지원은 메인테이너가 JDK 17 기준 문제로 일정이 없다고 밝힌 상태임. 사용하려면 폐기 예정인 Jackson 2를 추가하거나(1번),