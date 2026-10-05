# 0012. JWT 인증 필터 구조: 필터는 인증 정보만 채우고, 거부와 응답은 인가 규칙과 EntryPoint가 담당

## 상태
확정

## 배경
ADR 0010에서 정한 JWT 발급 이후, 요청마다 토큰을 검증해 사용자를 식별하는 인증 필터가 필요했음. 필터는 Controller보다 앞단에서 실행되어 @RestControllerAdvice(GlobalExceptionHandler)가 필터에서 발생한 예외를 처리하지 못하므로, 토큰이 없거나 잘못된 경우의 처리 방식과 응답 형식을 정해야 했음. 기본 동작에서는 인증이 없는 요청이 403과 빈 바디로 응답되어 기존 에러 응답 형식과 달랐음.

## 고민한 선택지
토큰이 잘못된 경우의 처리
1. 필터에서 직접 401 응답을 작성하고 요청을 종료
2. 필터는 인증 정보를 비운 채 다음 단계로 넘기고, 거부는 인가 규칙이, 응답 작성은 AuthenticationEntryPoint가 담당

토큰 없음과 토큰 잘못됨의 구분
1. 구분하지 않고 하나의 401 응답(AUTHENTICATION_REQUIRED)으로 통일
2. 필터가 request attribute로 원인을 전달하고 EntryPoint가 읽어 두 경우를 구분

필터 등록 방식
1. 필터에 @Component를 붙여 빈으로 등록
2. 빈으로 등록하지 않고 SecurityConfig에서 직접 생성해 보안 체인에 추가

## 결정
- 필터(JwtAuthenticationFilter)는 Authorization 헤더의 Bearer 토큰을 읽어 JwtTokenProvider로 검증하고, 성공하면 회원 id를 principal로 하는 인증 객체를 SecurityContext에 저장함. 토큰이 없거나 잘못된 경우에는 인증 정보를 저장하지 않고 다음 필터로 넘김. 어떤 경우에도 필터가 직접 요청을 거부하지 않음.
- 거부는 authorizeHttpRequests의 규칙(permitAll, authenticated)이 담당하고, 거부 응답은 JwtAuthenticationEntryPoint가 ErrorResponseDto 형식의 401로 작성함.
- 토큰이 잘못된 경우 필터가 request attribute에 ErrorCode를 남기고, EntryPoint가 이를 읽어 INVALID_TOKEN을 응답함. attribute가 없으면 토큰이 아예 없는 경우로 보고 AUTHENTICATION_REQUIRED를 응답함.
- 필터는 @Component를 붙이지 않고 SecurityConfig에서 JwtTokenProvider를 주입받아 직접 생성해 UsernamePasswordAuthenticationFilter 앞에 추가함.
- Controller는 @AuthenticationPrincipal Long memberId로 인증된 회원 id를 받음. 클라이언트가 id를 파라미터로 보내지 않음.
- 토큰 검증과 id 추출은 JwtTokenProvider.getMemberId 하나로 묶어, 서명 검증 없이 id만 꺼내는 호출이 불가능하게 함. 검증 실패는 global.exception의 InvalidTokenException(INVALID_TOKEN, 401)으로 통일함.

## 이유
- 필터가 직접 응답을 작성하면 에러 응답 작성 코드가 필터와 EntryPoint 두 곳에 생김. 필터를 "인증 정보를 채우는 역할", 인가 규칙과 EntryPoint를 "거부와 응답 역할"로 나누면 응답 형식을 한 곳에서 관리할 수 있음.
- 토큰이 없는 요청이 permitAll 경로(가입, 로그인)로 올 수 있으므로, 필터가 토큰 없는 요청을 막지 않고 통과시켜야 함. 거부 여부는 경로별 규칙이 판단함.
- 필터와 EntryPoint는 서로 호출하는 관계가 아니고 같은 request 객체를 지나가므로, 필터가 알게 된 "토큰이 잘못됨"을 request attribute로 전달함. 구분하지 않으면 INVALID_TOKEN이 사용되지 않는 코드가 되고, 프론트가 토큰 만료/위조와 미로그인을 구분하지 못함.
- 서블릿 필터를 빈으로 등록하면 Spring Boot가 서블릿 컨테이너에도 자동 등록해 같은 필터가 이중 실행될 수 있음. 보안 체인에만 직접 추가해 이를 피함. AuthenticationEntryPoint는 서블릿 필터가 아니므로 일반 빈으로 둠.
- JWT 서명은 위조를 막을 뿐 내용을 숨기지 않으므로, 서명 검증을 거친 뒤에만 claims를 신뢰함. 서명 검증은 만료를 확인하지 않아 만료 시각(exp)은 별도로 비교함.
- principal을 서버가 토큰에서 꺼낸 id로 두면 클라이언트가 다른 회원의 id를 보내 조회/수정하는 것을 구조적으로 막을 수 있음.
- 인증 객체를 만들 때 권한 목록을 포함한 생성자를 써야 인증 완료 상태가 되어 authenticated() 규칙을 통과함.

## 트레이드오프
- 필터와 EntryPoint가 request attribute 이름("errorCode")이라는 문자열로 연결되어, 철자가 어긋나면 컴파일 오류 없이 항상 AUTHENTICATION_REQUIRED로 응답됨. 현재는 두 곳뿐이라 상수로 분리하지 않음.
- 인증은 통과했지만 권한이 부족한 경우(403)의 처리(AccessDeniedHandler)는 만들지 않음. 현재 role이 USER 하나뿐이라 발생하지 않으며, 역할 구분이 필요해질 때 추가함. 토큰에도 role을 담지 않아 인증 객체의 권한 목록은 비어 있음.
- 토큰이 유효하면 회원이 삭제되었더라도 만료 전까지 인증을 통과하고, 서비스 단계에서 MEMBER_NOT_FOUND(404)로 처리됨. 토큰 시점에 회원 존재를 확인하지 않아 요청마다 DB 조회가 늘지 않음.
- 서명 키를 바꾸거나 토큰을 서버에서 무효화하는 수단이 없어, 발급된 토큰은 만료 전까지 유효함(ADR 0004, 0010과 동일).
- 필터에서 던진 예외는 GlobalExceptionHandler가 처리하지 못하므로, 응답 작성을 위해 ObjectMapper로 JSON을 직접 작성하는 코드가 EntryPoint에 존재함.