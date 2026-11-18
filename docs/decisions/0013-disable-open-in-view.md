# 0013. open-in-view 비활성화

## 상태
확정

## 배경
Spring Boot는 spring.jpa.open-in-view의 기본값이 true이며, 명시하지 않으면
서버 시작 시 경고 로그를 출력함. Member 도메인까지는 엔티티 간 연관관계가
없어 지연 로딩이 발생하지 않았지만, Product 도메인에서 Product → Member
(seller, ManyToOne, LAZY) 연관관계가 처음 생기므로 영속성 컨텍스트를 어디까지
열어둘지 명시적으로 정할 필요가 생김.

## 고민한 선택지
1. 기본값(true) 유지: 요청 처리가 끝날 때까지 영속성 컨텍스트를 유지
2. false로 명시: Service 트랜잭션 종료 시 영속성 컨텍스트도 종료

## 결정
2번 채택. application 설정에 spring.jpa.open-in-view=false를 명시함.
Service는 @Transactional 범위 안에서 필요한 데이터를 조회하고 DTO로 변환을
마친 뒤 반환하며, Controller는 완성된 DTO를 응답으로 포장만 함.
페이징 조회도 Service 안에서 Page.map()으로 변환해 Page<DTO>를 반환함.

## 이유
- 엔티티를 응답에 노출하지 않고 DTO로 변환한다는 원칙상, Controller가
  영속성 컨텍스트에 의존할 이유가 없음. 이 경계를 설정으로 강제하면
  트랜잭션 밖에서 지연 로딩을 시도하는 코드가 LazyInitializationException으로
  즉시 드러남.
- 기본 설정에서는 한 번 획득한 DB 커넥션이 영속성 컨텍스트 종료 시점까지
  유지될 수 있어, true일 경우 커넥션 점유가 요청 처리 종료까지 길어질 수 있음.
- 지연 로딩에 의존하는 기존 코드가 없는 지금 비활성화하는 것이 가장 비용이
  적음. 이후에 끄면 기존 코드에서 예외가 나는 지점을 찾아 수정해야 함.

## 트레이드오프
- 지연 로딩이 필요한 접근은 반드시 Service 트랜잭션 안에서 해야 하며,
  누락 시 LazyInitializationException이 발생함.
- 이 설정은 N+1 문제를 해결하지 않음. Service 트랜잭션 안에서 발생하는
  N+1은 정상 실행되므로, 조회 목적에 맞는 fetch join 또는 DTO 조회와
  실제 실행 SQL 확인으로 별도로 검증해야 함.
- 테스트 메서드에 @Transactional을 붙이면 테스트 트랜잭션이 경계 오류를
  가릴 수 있어, 조회 API 검증 테스트는 테스트 트랜잭션 없이 수행해야 함.
- Command/Query 서비스 분리는 현재 규모에서 과하다고 판단해 도입하지 않음.

## 주의
- 예외 객체나 로그(toString)에 엔티티와 연관관계를 담지 않음. 트랜잭션 밖에서
  지연 로딩이 발생해 원래 예외 대신 LazyInitializationException이 발생할 수 있음.
- Transaction 단계에서 비관적 락 조회를 구현할 때, 목록 조회용 fetch join을
  재사용하면 조인 대상 행까지 잠금 범위가 넓어질 수 있어 락 조회 쿼리는
  별도로 구성하고 실제 잠금 범위를 확인함.