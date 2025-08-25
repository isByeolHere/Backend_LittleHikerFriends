# Little Hiker Friends 백엔드 개발 대화 기록

**프로젝트**: 등산 그룹 위치 공유 및 메시징 백엔드 서버  
**개발 기간**: 2025년 8월 15일  
**개발자**: byeolkim  
**AI 어시스턴트**: Amazon Q  

---

## 📋 프로젝트 개요

### 프로젝트 소개
1. 등산을 여러명이 모인 그룹이 같이 할때 서로 위치를 공유할수 있도록 지도 기반으로 서로 위치 공유
2. 서로 위치는 실시간으로 계속 공유하는게 아니라 앱을 켰을 때만 서로 위치 공유하도록 함.
3. 그리고 위치 기반 메세지도 남길 수 있음.

### 기술 스택
- **Backend**: Spring Boot 3.3.2, Java 21
- **Database**: PostgreSQL + PostGIS (개발 시 H2)
- **ORM**: JPA/Hibernate with Spatial support
- **API 문서**: Swagger/OpenAPI 3
- **보안**: Spring Security + JWT (예정)
- **빌드 도구**: Gradle (Kotlin DSL)

---

## 🗄️ 데이터베이스 구조

### 엔티티 설계

#### User (사용자)
```java
- id: Integer (PK)
- email: String (unique, not null)
- passwordHash: String
- provider: Provider (EMAIL, GOOGLE, APPLE, KAKAO)
- providerId: String (unique, 소셜 로그인용)
- nickname: String (not null)
- imageUrl: String
- lastLocationId: Integer
- createdAt: LocalDateTime
```

#### Hike (등산 모임)
```java
- id: Integer (PK)
- name: String (not null)
- mountainName: String (not null)
- status: HikeStatus (PLANNED, IN_PROGRESS, COMPLETED)
- startAt: LocalDateTime (not null)
- endAt: LocalDateTime
- createdAt: LocalDateTime
```

#### HikeMember (등산 모임 멤버)
```java
- id: Integer (PK)
- hikeId: Integer (FK)
- userId: Integer (FK)
- role: MemberRole (ADMIN, MEMBER)
- joinedAt: LocalDateTime
- unique(hikeId, userId)
```

#### Location (위치 정보)
```java
- id: Integer (PK)
- userId: Integer (FK)
- geom: geometry(Point,4326) // PostGIS 공간 데이터
- altitude: Double
- timestamp: LocalDateTime
```

#### Message (메시지)
```java
- id: Integer (PK)
- hikeId: Integer (FK)
- userId: Integer (FK)
- content: TEXT (not null)
- locationId: Integer (FK, optional)
- createdAt: LocalDateTime
```

---

## 🏗️ 개발 진행 과정

### 1단계: 프로젝트 구조 파악 및 엔티티 생성

**문제**: 엔티티 생성이 덜 완료된 상태
**해결**: DB 구조를 기반으로 모든 엔티티 클래스 생성

#### 생성된 파일들:
- `User.java` - 사용자 엔티티 (기존 업데이트)
- `Hike.java` - 등산 모임 엔티티
- `HikeMember.java` - 멤버십 중간 테이블
- `Location.java` - 위치 정보 (PostGIS Point 타입)
- `Message.java` - 메시지 엔티티
- `Provider.java`, `HikeStatus.java`, `MemberRole.java` - Enum 클래스들

#### 주요 특징:
- JPA 연관관계 매핑 완료
- PostGIS geometry 타입 활용
- 편의 메서드 및 생성자 제공

### 2단계: Repository 인터페이스 생성

**목적**: Spring Data JPA를 활용한 데이터 접근 계층 구현

#### 생성된 Repository들:
- `UserRepository` - 이메일/소셜 로그인 기반 사용자 조회
- `HikeRepository` - 상태별, 산 이름별, 기간별 등산 모임 조회
- `HikeMemberRepository` - 멤버십 관리, 역할별 조회
- `LocationRepository` - **PostGIS 공간 쿼리 활용** (거리 계산, 주변 검색)
- `MessageRepository` - 메시징, 위치 기반 메시지 검색

#### 특별한 기능:
```java
// PostGIS 공간 쿼리 예시
@Query(value = "SELECT * FROM locations l WHERE ST_DWithin(l.geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326), :distanceMeters)", 
       nativeQuery = true)
List<Location> findLocationsWithinDistance(@Param("latitude") double latitude, 
                                          @Param("longitude") double longitude, 
                                          @Param("distanceMeters") double distanceMeters);
```

### 3단계: 백엔드 vs 프론트엔드 역할 이해

**질문**: "이런 기능들을 프론트엔드에서 만드는게 아니라 백엔드에서 코드를 짜야하는거 맞아?"

**답변**: 
- **iOS**: UI/UX, 사용자 입력 처리, API 호출, 지도 표시
- **백엔드**: 데이터 저장/조회, 비즈니스 로직, API 제공, 보안 처리

#### 실제 플로우 예시:
```swift
// iOS에서
let locationData = LocationRequest(latitude: 37.5665, longitude: 126.9780, altitude: 123.5)
APIService.shared.updateLocation(locationData) { result in /* 처리 */ }
```

```java
// 백엔드에서
@PostMapping("/locations")
public ResponseEntity<LocationResponse> updateLocation(@RequestBody LocationRequest request) {
    // 1. 데이터 검증
    // 2. PostGIS Point 객체 생성
    // 3. DB 저장
    // 4. 응답 반환
}
```

### 4단계: API 문서화 및 명세서 작성

**문제**: "api가 프론트에서도 연동되어야하니까 api 명세서를 따로 만들지 안항?"

**해결**: Swagger/OpenAPI 기반 API 문서화 시스템 구축

#### 구현된 문서화 시스템:
1. **Swagger UI** - 서버 실행 시 자동 생성 (`/swagger-ui.html`)
2. **정적 YAML 파일** - 서버 실행 없이도 확인 가능
3. **HTML 문서** - 오프라인에서도 접근 가능
4. **iOS 개발자 가이드** - Swift 코드 예시 포함

#### 생성된 문서들:
- `docs/api-spec.yaml` - OpenAPI 3.0 명세서
- `docs/api-documentation.html` - 정적 HTML 문서
- `docs/API_DOCUMENTATION_GUIDE.md` - 사용 가이드
- `API_GUIDE_FOR_IOS.md` - iOS 개발자용 가이드

### 5단계: 회원가입 API 구현

**목적**: 실제 동작하는 API 엔드포인트 구현

#### 구현 순서와 이유:
1. **DTO 클래스** → 데이터 구조 먼저 정의
2. **예외 처리** → 에러 상황 대응 준비
3. **Service 클래스** → 핵심 비즈니스 로직 구현
4. **Config 클래스** → 필요한 설정 (비밀번호 암호화, 보안)
5. **Controller 클래스** → API 엔드포인트 노출

#### 생성된 파일들:
```
dto/
├── SignupRequest.java      # 회원가입 요청 DTO
├── UserResponse.java       # 사용자 응답 DTO
└── LocationRequest.java    # 위치 요청 DTO (기존)

exception/
├── DuplicateEmailException.java    # 중복 이메일 예외
└── GlobalExceptionHandler.java     # 전역 예외 처리

service/
└── UserService.java        # 사용자 비즈니스 로직

config/
├── PasswordConfig.java     # 비밀번호 암호화 설정
└── SecurityConfig.java     # Spring Security 기본 설정

controller/
└── AuthController.java     # 인증 관련 API 엔드포인트
```

#### 핵심 비즈니스 로직:
```java
public UserResponse signup(SignupRequest request) {
    // 1. 이메일 중복 체크
    if (userRepository.existsByEmail(request.getEmail())) {
        throw new DuplicateEmailException("이미 존재하는 이메일입니다");
    }
    
    // 2. 비밀번호 암호화
    String hashedPassword = passwordEncoder.encode(request.getPassword());
    
    // 3. User 엔티티 생성 및 저장
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPasswordHash(hashedPassword);
    user.setNickname(request.getNickname());
    user.setProvider(Provider.EMAIL);
    
    User savedUser = userRepository.save(user);
    return new UserResponse(savedUser);
}
```

### 6단계: 서버 실행 및 테스트 환경 구축

**문제**: PostgreSQL 환경변수 설정 문제로 서버 실행 실패

**해결**: 개발 편의를 위해 H2 인메모리 데이터베이스로 임시 변경

#### 설정 변경:
```properties
# H2 Database (테스트용)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA 설정
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

#### Location 엔티티 임시 수정:
PostGIS Point 타입을 H2 호환 latitude/longitude 컬럼으로 변경

### 7단계: 불필요한 코드 정리

**문제**: TestController가 보안 검증 없이 사용자를 생성하는 위험한 API 제공

**해결**: TestController 삭제, AuthController만 사용하도록 정리

---

## 🎯 HTTP 메서드 이해 (GET vs POST)

### GET - "가져와줘!"
- **용도**: 데이터 조회, 읽기 전용
- **특징**: URL에 데이터 노출, 캐시 가능, 북마크 가능
- **예시**: 
  - `GET /api/hikes` → 등산 모임 목록 조회
  - `GET /api/users/123` → 123번 사용자 정보 조회

### POST - "처리해줘!"
- **용도**: 데이터 생성, 변경, 전송
- **특징**: 요청 본문에 데이터 숨김, 보안상 안전, 캐시 안됨
- **예시**:
  - `POST /api/auth/signup` → 회원가입 (새 사용자 생성)
  - `POST /api/locations` → 위치 업데이트

### 구분 방법:
- **"뭔가 변화가 생기나?"** → YES: POST, NO: GET
- **"URL만으로 설명 가능한가?"** → YES: GET, NO: POST

---

## 📊 현재 완성도

### ✅ 완료된 작업 (8개)
- [x] 엔티티 클래스 생성 (User, Hike, HikeMember, Location, Message)
- [x] Enum 클래스 생성 (Provider, HikeStatus, MemberRole)
- [x] Repository 인터페이스 생성 (PostGIS 쿼리 포함)
- [x] API 문서화 시스템 구축 (Swagger + 정적 문서)
- [x] 회원가입 API 완전 구현
- [x] 예외 처리 시스템 구축
- [x] 개발 환경 설정 (H2 데이터베이스)
- [x] 불필요한 코드 정리

### ⏳ 남은 작업 (25개)
- [ ] 로그인 API 구현 (JWT 토큰)
- [ ] 위치 공유 API 완성
- [ ] 등산 모임 CRUD API
- [ ] 메시지 API 구현
- [ ] 소셜 로그인 연동
- [ ] PostgreSQL + PostGIS 환경 구축
- [ ] 테스트 코드 작성
- [ ] 배포 환경 구성

---

## 🚀 API 엔드포인트 현황

### 구현 완료
```
POST /api/auth/signup           # 회원가입
GET  /api/auth/check-email      # 이메일 중복 체크
GET  /actuator/health           # 헬스체크
GET  /swagger-ui.html           # API 문서
```

### 구현 예정
```
POST /api/auth/login            # 로그인
POST /api/auth/refresh          # 토큰 갱신

GET  /api/users/me              # 내 정보 조회
PUT  /api/users/me              # 내 정보 수정

GET  /api/hikes                 # 등산 모임 목록
POST /api/hikes                 # 등산 모임 생성
GET  /api/hikes/{id}            # 등산 모임 상세

POST /api/locations             # 위치 업데이트
GET  /api/locations/hikes/{id}/members  # 멤버 위치 조회

GET  /api/hikes/{id}/messages   # 메시지 목록
POST /api/hikes/{id}/messages   # 메시지 전송
```

---

## 💡 개발 과정에서 배운 점

### 1. 백엔드 개발자의 역할
- 단순히 "데이터를 찾아서 보내주는 것" 이상
- 데이터 무결성 보장, 비즈니스 규칙 구현, 성능 최적화, 보안 처리

### 2. API 설계의 중요성
- 프론트엔드와 백엔드 간의 계약서 역할
- 서버 실행 없이도 개발 가능한 문서화 시스템 필요

### 3. 점진적 개발의 중요성
- DTO → Service → Controller 순서로 단계별 구현
- 각 단계에서 충분한 검증 후 다음 단계 진행

### 4. 개발 환경의 유연성
- 프로덕션(PostgreSQL)과 개발(H2) 환경 분리
- 빠른 프로토타이핑을 위한 환경 구성

---

## 🔮 다음 개발 계획

### Phase 1: 핵심 기능 (1-2주)
1. **로그인 API** - JWT 토큰 기반 인증
2. **등산 모임 CRUD** - 모임 생성/조회/수정/삭제
3. **위치 공유 API** - PostGIS 활용한 실시간 위치 공유
4. **기본 보안 설정** - JWT 필터, 권한 체크

### Phase 2: 고급 기능 (1주)
1. **메시지 API** - 일반/위치 기반 메시징
2. **공간 쿼리 고도화** - 거리 계산, 주변 검색
3. **소셜 로그인** - Google, Apple 연동

### Phase 3: 완성도 향상 (1주)
1. **PostgreSQL 환경** - 실제 PostGIS 환경 구축
2. **테스트 코드** - 단위/통합 테스트
3. **배포 준비** - Docker, CI/CD

---

## 📞 참고 자료

### 프로젝트 파일 구조
```
Backend_LittleHikerFriends/
├── src/main/java/com/littlehikerfriends/
│   ├── entity/          # 엔티티 클래스들
│   ├── repository/      # Repository 인터페이스들
│   ├── service/         # 비즈니스 로직
│   ├── controller/      # API 엔드포인트
│   ├── dto/            # 데이터 전송 객체
│   ├── exception/      # 예외 처리
│   └── config/         # 설정 클래스들
├── docs/               # API 문서들
├── build.gradle.kts    # 빌드 설정
└── README.md          # 프로젝트 설명
```

### 주요 의존성
```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.hibernate:hibernate-spatial:6.2.7.Final")
    implementation("org.locationtech.jts:jts-core:1.19.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
    runtimeOnly("com.h2database:h2")
}
```

### 서버 실행 방법
```bash
cd "/Users/byeolkim/Desktop/리틀하이커프렌즈/Backend_LittleHikerFriends"
./gradlew bootRun
```

### 접속 URL
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
- **헬스체크**: http://localhost:8080/actuator/health

---

**개발 완료일**: 2025년 8월 15일  
**다음 개발 예정**: 로그인 API 구현

---

*이 문서는 Little Hiker Friends 백엔드 개발 과정의 모든 대화와 결정사항을 기록한 것입니다.*
