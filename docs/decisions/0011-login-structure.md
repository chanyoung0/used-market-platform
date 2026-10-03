# 0011. 로그인 구조: 인증 로직은 domain.auth로 분리, 실패 응답은 하나로 통일

## 상태
확정

## 배경
JWT 로그인을 구현하면서 로그인 서비스를 둘 위치와 로그인 실패 응답 방식을 정해야 했음. 기존 계획은 로그인 서비스를 global.security에 두는 것이었으나, 로그인은 회원 조회(MemberRepository)와 비밀번호 비교가 필요해서 global이 domain.member를 참조하게 됨. 또한 이메일이 없는 경우와 비밀번호가 틀린 경우를 구분해 응답하면 공격자가 가입된 이메일을 알아낼 수 있음.

## 고민한 선택지
위치
1. 로그인 서비스를 global.security에 둠
2. MemberService에 login 메서드를 추가
3. domain.auth 패키지를 새로 두고 로그인 서비스, Controller, DTO를 배치

실패 응답
1. 이메일 없음(404)과 비밀번호 불일치(401)를 구분
2. 둘 다 INVALID_CREDENTIALS(401) 하나로 통일

## 결정
- 위치: 3번 채택. 로그인 서비스, Controller, DTO는 domain.auth에 두고, 회원 도메인을 모르는 토큰 생성(JwtTokenProvider)과 이후 인증 필터는 global.security에 둠.
- 실패 응답: 2번 채택. 회원이 없는 경우와 비밀번호가 틀린 경우 모두 InvalidCredentialsException(INVALID_CREDENTIALS, 401)을 던짐. 회원 없음용 MemberNotFoundException은 재사용하지 않음.
- API: POST /api/auth/login, 성공 시 200과 accessToken 하나만 담은 응답을 반환. 로그인과 회원가입 경로는 인증 없이 접근 가능하도록 permitAll에 등록함.
- 요청 DTO는 email, password에 @NotBlank만 적용함(ADR 0009).

## 이유
- global이 domain.member를 참조하면 의존 방향이 domain → global 단방향이라는 기존 원칙(ADR 0008)이 깨짐. JwtTokenProvider가 Member 대신 회원 id만 받게 한 것과 같은 이유임.
- 회원 관리(가입, 조회, 수정)와 인증(로그인, 토큰 발급)은 변경 이유가 달라, MemberService에 합치면 이후 인증 기능이 붙을 때 서비스가 비대해짐. 도메인형 구조(ADR 0006)의 기준에서도 인증은 별도 도메인으로 보는 것이 자연스러움.
- domain.auth는 domain.member와 global을 참조하고 반대 방향 참조는 없어 의존이 한 방향으로만 흐름.
- 실패 응답을 통일하면 응답만으로 이메일 가입 여부를 알 수 없음. 이 목적을 지키려면 회원 없음 예외(404)를 재사용하면 안 됨.
- 로그인 성공은 DB에 새 리소스를 만들지 않고 토큰을 내려줄 뿐이라 201이 아닌 200을 사용함.
- 비밀번호 비교는 passwordEncoder.matches(평문, 저장된 해시)를 사용함. BCrypt는 인코딩할 때마다 솔트가 달라져 평문을 다시 인코딩해서 equals로 비교할 수 없음. matches는 저장된 해시에 들어 있는 솔트와 비용으로 입력값을 다시 계산해 비교함.
- 응답에 accessToken만 담음. 프론트가 Authorization 헤더에 Bearer 방식으로 보낼 것이 정해져 있고 만료 시간도 1시간 고정이라 토큰 타입이나 만료 시각을 담을 이유가 약함.

## 트레이드오프
- 인증 로직이 회원 도메인과 분리되어 패키지가 하나 늘고, 로그인 시 MemberRepository를 domain.auth가 직접 참조함.
- 회원이 없을 때는 비밀번호 비교(BCrypt 연산)를 건너뛰므로 응답 시간이 짧아, 시간 차이로 가입된 이메일을 추측할 수 있음. 회원이 없을 때도 더미 비교를 수행하는 방식으로 막을 수 있으나 현재 요구사항 대비 과해 적용하지 않음.
- 로그인 시도 횟수 제한(무차별 대입 방어)은 적용하지 않음. 필요해지면 별도 결정으로 다룸.
- 응답에 토큰 타입이나 만료 시각이 없어, 프론트는 만료 시간(1시간)을 별도로 알고 있어야 함.