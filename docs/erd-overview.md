# ERD 개요

MVP 범위 기준 엔티티 관계도입니다.

```mermaid
erDiagram
  MEMBER ||--o{ PRODUCT : registers
  MEMBER ||--o{ PURCHASEREQUEST : requests
  PRODUCT ||--o{ PURCHASEREQUEST : receives
  PURCHASEREQUEST ||--o| TRANSACTION : becomes
  PRODUCT ||--o| TRANSACTION : sold
  MEMBER ||--o{ TRANSACTION : buys
  MEMBER ||--o{ TRANSACTION : sells

  MEMBER {
    Long id PK
    String email UK
    String password
    String nickname
    String role
    DateTime createdAt
    DateTime updatedAt
  }
  PRODUCT {
    Long id PK
    Long sellerId FK
    String title
    String description
    String category
    int price
    String imageUrl
    String status
    boolean isDeleted
    DateTime createdAt
    DateTime updatedAt
  }
  PURCHASEREQUEST {
    Long id PK
    Long productId FK
    Long buyerId FK
    int suggestedPrice
    String status
    DateTime createdAt
    DateTime updatedAt
  }
  TRANSACTION {
    Long id PK
    Long productId FK
    Long buyerId FK
    Long sellerId FK
    Long requestId FK_UK
    int finalPrice
    DateTime completedAt
    DateTime createdAt
  }
```

## 관계 설명

- **Member ─ Product**: 회원 한 명이 여러 상품을 등록 (판매자)
- **Member ─ PurchaseRequest**: 회원 한 명이 여러 구매 요청을 보냄 (구매자)
- **Product ─ PurchaseRequest**: 상품 하나에 여러 구매 요청이 들어올 수 있음
- **PurchaseRequest ─ Transaction**: 수락된 요청 하나만 거래로 이어짐 (0 또는 1)
- **Product ─ Transaction**: 상품 하나당 확정된 거래는 하나뿐
- **Member ─ Transaction**: 같은 Member 테이블을 구매자(`buyerId`)/판매자(`sellerId`)
  두 개의 FK로 각각 참조 (조회 편의를 위한 의도적 비정규화)

## 설계 메모

- **Transaction에 status 대신 completedAt(nullable)**: Product.status와
  Transaction.status를 각각 관리하면 두 값이 어긋날 위험이 있어, 상태의
  단일 진실 공급원(source of truth)은 Product.status로 두고, Transaction은
  "언제 완료됐는지"만 기록. `completedAt`이 null이면 진행중, 값이 있으면 완료.
- **Transaction.requestId는 unique**: PurchaseRequest 하나당 Transaction이
  최대 1개까지만 생성되도록 보장.
- **Product.isDeleted**: 상품 삭제/수정 정책에 따른 소프트 삭제 플래그.
  구매 요청이 걸린 `판매중` 상품만 이 플래그로 숨김 처리되고, 요청이
  없는 상품은 하드 삭제됨. `예약중`/`판매완료` 상태는 수정/삭제 자체가
  금지됨. (관련 결정: [`0005-product-deletion-policy.md`](./decisions/0005-product-deletion-policy.md))
- **Member.role**: 지금은 `USER`만 존재하지만, 확장 가능성을 열어두기
  위해 문자열(enum) 컬럼으로 미리 둠.
- **Refresh Token 테이블 없음**: JWT는 Access Token만 발급하는 stateless
  방식으로 결정. (관련 결정: [`0004-jwt-token-strategy.md`](./decisions/0004-jwt-token-strategy.md))