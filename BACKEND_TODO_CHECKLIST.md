# 🏔️ Little Hiker Friends 백엔드 개발 체크리스트

## 📊 진행 상황 요약
- ✅ **완료**: 8개 작업
- 🔄 **진행 중**: 0개 작업  
- ⏳ **대기 중**: 25개 작업

---

## 1. 🗄️ 데이터베이스 & 엔티티 (4/6 완료)

### ✅ 완료된 작업
- [x] User, Hike, HikeMember, Location, Message 엔티티 생성
- [x] Provider, HikeStatus, MemberRole enum 생성
- [x] JPA 연관관계 매핑 설정
- [x] Repository 인터페이스 생성 (PostGIS 쿼리 포함)

### ⏳ 남은 작업
- [ ] **데이터베이스 스키마 생성 스크립트**
  ```sql
  -- schema.sql 파일 생성
  -- PostGIS extension 활성화
  -- 테이블 생성 DDL
  -- 인덱스 생성 (특히 geom 컬럼 GiST 인덱스)
  ```

- [ ] **초기 데이터 설정**
  ```sql
  -- data.sql 파일 생성
  -- 테스트용 사용자 데이터
  -- 샘플 등산 모임 데이터
  ```

---

## 2. 🔧 비즈니스 로직 (0/8 완료)

### ⏳ Service 클래스 생성
- [ ] **UserService**
  - 회원가입/로그인 로직
  - 프로필 관리
  - 소셜 로그인 처리

- [ ] **HikeService**
  - 등산 모임 생성/수정/삭제
  - 멤버 관리 (초대/추방/권한 변경)
  - 모임 상태 관리 (PLANNED → IN_PROGRESS → COMPLETED)

- [ ] **LocationService**
  - 위치 업데이트 로직
  - 거리 계산 (PostGIS 활용)
  - 주변 위치 검색
  - 등산 모임 멤버들 위치 조회

- [ ] **MessageService**
  - 메시지 전송/조회
  - 위치 기반 메시지 처리
  - 페이징 처리

### ⏳ 핵심 비즈니스 로직
- [ ] **권한 체크 로직**
  ```java
  // 등산 모임 멤버만 위치 공유 가능
  // 관리자만 모임 설정 변경 가능
  // 본인 위치만 업데이트 가능
  ```

- [ ] **위치 계산 로직**
  ```java
  // 두 지점 간 거리 계산
  // 특정 반경 내 사용자 검색
  // 등산로 경로 추적 (선택사항)
  ```

- [ ] **실시간 알림 로직** (선택사항)
  ```java
  // 새 메시지 알림
  // 멤버 위치 업데이트 알림
  // 등산 모임 상태 변경 알림
  ```

- [ ] **데이터 검증 로직**
  ```java
  // 위치 데이터 유효성 검사
  // 등산 모임 시간 검증
  // 중복 가입 방지
  ```

---

## 3. 🌐 API 엔드포인트 (1/6 완료)

### ✅ 완료된 작업
- [x] LocationController 기본 구조 생성 (Swagger 문서화 포함)

### ⏳ Controller 클래스 생성
- [ ] **AuthController**
  ```java
  POST /auth/login          // 로그인
  POST /auth/register       // 회원가입
  POST /auth/social-login   // 소셜 로그인
  POST /auth/refresh        // 토큰 갱신
  POST /auth/logout         // 로그아웃
  ```

- [ ] **UserController**
  ```java
  GET    /users/me          // 내 정보 조회
  PUT    /users/me          // 내 정보 수정
  GET    /users/{id}        // 사용자 정보 조회
  DELETE /users/me          // 회원 탈퇴
  ```

- [ ] **HikeController**
  ```java
  GET    /hikes             // 등산 모임 목록
  POST   /hikes             // 등산 모임 생성
  GET    /hikes/{id}        // 등산 모임 상세
  PUT    /hikes/{id}        // 등산 모임 수정
  DELETE /hikes/{id}        // 등산 모임 삭제
  
  POST   /hikes/{id}/join   // 모임 참가
  DELETE /hikes/{id}/leave  // 모임 탈퇴
  GET    /hikes/{id}/members // 멤버 목록
  ```

- [ ] **LocationController 완성**
  ```java
  // 기본 구조는 있지만 실제 구현 필요
  POST /locations           // 위치 업데이트
  GET  /locations/hikes/{hikeId}/members // 멤버 위치 조회
  GET  /locations/nearby    // 주변 위치 조회
  ```

- [ ] **MessageController**
  ```java
  GET  /hikes/{id}/messages      // 메시지 목록
  POST /hikes/{id}/messages      // 메시지 전송
  GET  /messages/nearby          // 주변 위치 기반 메시지
  DELETE /messages/{id}          // 메시지 삭제
  ```

### ⏳ DTO 클래스 완성
- [x] LocationRequest/Response (완료)
- [ ] UserRequest/Response
- [ ] HikeRequest/Response  
- [ ] MessageRequest/Response
- [ ] AuthRequest/Response

---

## 4. 🔐 보안 및 인증 (0/5 완료)

### ⏳ Spring Security 설정
- [ ] **SecurityConfig 클래스**
  ```java
  // JWT 기반 인증 설정
  // CORS 설정
  // 엔드포인트별 권한 설정
  ```

- [ ] **JWT 토큰 처리**
  ```java
  // JwtTokenProvider 클래스
  // 토큰 생성/검증/파싱
  // 토큰 만료 처리
  ```

- [ ] **소셜 로그인 연동**
  ```java
  // Google OAuth2 설정
  // Apple Sign In 설정
  // 소셜 로그인 콜백 처리
  ```

- [ ] **인증 필터**
  ```java
  // JwtAuthenticationFilter
  // 요청별 토큰 검증
  // SecurityContext 설정
  ```

- [ ] **예외 처리**
  ```java
  // AuthenticationEntryPoint
  // AccessDeniedHandler
  // 인증/인가 실패 응답
  ```

---

## 5. 🛠️ 유틸리티 & 설정 (0/4 완료)

### ⏳ 공통 컴포넌트
- [ ] **GlobalExceptionHandler**
  ```java
  // 전역 예외 처리
  // 에러 응답 표준화
  // 로깅 처리
  ```

- [ ] **ResponseWrapper**
  ```java
  // API 응답 표준화
  // 성공/실패 응답 통일
  ```

- [ ] **ValidationConfig**
  ```java
  // 입력 검증 설정
  // 커스텀 Validator
  ```

- [ ] **DatabaseConfig**
  ```java
  // PostGIS 설정
  // Connection Pool 설정
  // 트랜잭션 설정
  ```

---

## 6. 🧪 테스트 (0/4 완료)

### ⏳ 테스트 코드 작성
- [ ] **Repository 테스트**
  ```java
  // JPA 쿼리 테스트
  // PostGIS 공간 쿼리 테스트
  ```

- [ ] **Service 테스트**
  ```java
  // 비즈니스 로직 단위 테스트
  // Mock 객체 활용
  ```

- [ ] **Controller 테스트**
  ```java
  // API 엔드포인트 테스트
  // MockMvc 활용
  ```

- [ ] **통합 테스트**
  ```java
  // 전체 플로우 테스트
  // TestContainers 활용
  ```

---

## 🚀 개발 우선순위 추천

### Phase 1: 핵심 기능 (1-2주)
1. **UserService & AuthController** - 로그인/회원가입
2. **HikeService & HikeController** - 등산 모임 CRUD
3. **LocationService 완성** - 위치 공유 핵심 기능
4. **Spring Security 기본 설정** - JWT 인증

### Phase 2: 고급 기능 (1주)
1. **MessageService & MessageController** - 메시징 기능
2. **위치 기반 고급 기능** - 주변 검색, 거리 계산
3. **소셜 로그인** - Google, Apple 연동

### Phase 3: 완성도 향상 (1주)
1. **예외 처리 & 검증** - 안정성 향상
2. **테스트 코드** - 품질 보장
3. **API 문서 완성** - 프론트엔드 연동 준비

---

## 💡 개발 팁

- **PostGIS 활용**: 위치 기반 기능이 핵심이므로 공간 쿼리 학습 필요
- **JWT 보안**: 토큰 만료, 갱신 로직 신중하게 설계
- **실시간성**: WebSocket 대신 폴링 방식으로 시작 (단순함)
- **테스트 데이터**: 실제 등산로 좌표로 테스트하면 더 현실적

---

## 📞 다음 단계

어떤 부분부터 시작하고 싶으신가요?
1. **Service 클래스 생성** (비즈니스 로직)
2. **Spring Security 설정** (인증/인가)  
3. **Controller 완성** (API 엔드포인트)
4. **데이터베이스 스키마** (DDL 스크립트)
