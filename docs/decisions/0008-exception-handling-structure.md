# 0008. 예외 처리 구조: BusinessException + ErrorCode enum + 전역 핸들러

## 상태
확정

## 배경
Member 도메인에서 DuplicateEmailException, MemberNotFoundException을 만들었지만, 예외를 HTTP 응답으로 바꾸는 처리기가 없어서 모든 예외가 500으로 응답됨. 이메일 중복은 클라이언트 요청 문제(409)이고 회원 없음은 404여야 하는데 구분이 안 됐고, 이후 Product/Transaction 도메인에서 예외가 계속 늘어날 예정이라 매핑 규칙이 필요했음. 또한 입력 검증(@Valid) 실패 시 Spring 기본 응답이 나가 에러 응답 형식이 둘로 갈라지는 문제도 있었음.

## 고민한 선택지
1. 예외 클래스마다 @ExceptionHandler 메서드를 하나씩 둠
2. 공통 부모 BusinessException + ErrorCode enum(상태 코드, 메시지)을 두고, 핸들러는 BusinessException 하나만 처리

## 결정
2번 채택. 도메인별 예외는 BusinessException을 상속해 domain.{도메인}.exception에 두고, ErrorCode / BusinessException / ErrorResponseDto / GlobalExceptionHandler는 global.exception에 둠. 메시지는 ErrorCode가 들고 있는 고정 메시지를 사용.
입력 검증 실패(MethodArgumentNotValidException)는 Spring이 던지는 예외라 BusinessException 계열이 아니므로 핸들러 메서드를 별도로 두고, ErrorCode.INVALID_INPUT(400) 하나로 통일해 같은 ErrorResponseDto 형식으로 응답함.

## 이유
- 예외가 늘어도 ErrorCode에 상수 한 줄만 추가하면 되고, 핸들러는 수정할 필요가 없음.
- 도메인별 예외 클래스는 유지하면서 처리만 공통화할 수 있음.
- 의존 방향이 domain → global 단방향이라 역방향 의존이 생기지 않음.
- 고정 메시지로 사용자 입력(이메일 등)이 응답에 섞이는 것을 막음.
- BusinessException은 RuntimeException 상속이라 @Transactional 기본 롤백 규칙이 그대로 적용됨.
- 별도 code 필드 없이 errorCode.name()을 응답 코드로 사용해 필드 중복을 피함.
- 검증 실패도 ErrorResponseDto를 그대로 재사용하므로 클라이언트가 모든 에러를 동일한 구조로 처리할 수 있음.

## 트레이드오프
- 상황별 상세 메시지(예: 어떤 이메일이 중복인지, 어떤 필드가 왜 틀렸는지)를 응답에 담을 수 없음. 프론트에서 필드별 안내가 필요해지면 ErrorResponseDto에 필드 에러 목록을 추가하는 방식으로 별도 결정을 통해 확장.
- 예상치 못한 예외(Exception, 500) 핸들러는 아직 없음. 해당 처리가 필요해지는 시점에 추가 예정.