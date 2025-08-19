# API 문서 확인 방법 가이드

## 🚀 서버 실행 없이 API 문서 확인하는 방법들

### 1. **Swagger Editor 온라인** ⭐️ (가장 쉬운 방법)

1. [Swagger Editor](https://editor.swagger.io/) 접속
2. 왼쪽 편집기에 `docs/api-spec.yaml` 파일 내용 복사 붙여넣기
3. 오른쪽에서 실시간으로 API 문서 확인 가능
4. **Try it out** 버튼으로 API 테스트도 가능

### 2. **VS Code Extension 사용**

1. VS Code에서 **Swagger Viewer** 확장 설치
2. `api-spec.yaml` 파일 열기
3. `Ctrl+Shift+P` → "Preview Swagger" 실행
4. VS Code 내에서 API 문서 확인

### 3. **Redoc 정적 HTML 생성**

```bash
# redoc-cli 설치 (Node.js 필요)
npm install -g redoc-cli

# HTML 파일 생성
redoc-cli build docs/api-spec.yaml --output docs/api-documentation.html

# 생성된 HTML 파일을 브라우저로 열기
open docs/api-documentation.html
```

### 4. **GitHub에서 직접 확인**

GitHub에 푸시하면 YAML 파일을 자동으로 렌더링해서 보여줍니다:
- `docs/api-spec.yaml` 파일을 GitHub에서 클릭하면 자동으로 포맷팅되어 표시

## 📱 iOS 개발자를 위한 빠른 참조

### 주요 엔드포인트 요약

| 기능 | Method | URL | 설명 |
|------|--------|-----|------|
| 로그인 | POST | `/auth/login` | 이메일/비밀번호 로그인 |
| 위치 업데이트 | POST | `/locations` | 현재 위치 전송 |
| 멤버 위치 조회 | GET | `/locations/hikes/{hikeId}/members` | 등산 모임 멤버들 위치 |
| 주변 위치 조회 | GET | `/locations/nearby` | 특정 지점 주변 위치 |
| 등산 모임 목록 | GET | `/hikes` | 등산 모임 리스트 |
| 등산 모임 생성 | POST | `/hikes` | 새 등산 모임 만들기 |
| 메시지 조회 | GET | `/hikes/{hikeId}/messages` | 모임 메시지 목록 |
| 메시지 전송 | POST | `/hikes/{hikeId}/messages` | 메시지 보내기 |

### Swift 코드 생성 도구

**OpenAPI Generator 사용:**
```bash
# Swift 클라이언트 코드 자동 생성
openapi-generator generate \
  -i docs/api-spec.yaml \
  -g swift5 \
  -o generated-swift-client
```

이렇게 하면 Swift 모델 클래스와 API 클라이언트가 자동으로 생성됩니다!

## 🔄 문서 업데이트 워크플로우

### 개발 중 문서 관리
1. **코드 변경** → Controller/DTO 수정
2. **문서 업데이트** → `api-spec.yaml` 수정
3. **검증** → Swagger Editor에서 확인
4. **공유** → GitHub에 푸시

### 자동화 (선택사항)
```bash
# 서버 실행 후 OpenAPI JSON 추출
curl http://localhost:8080/api-docs > docs/api-spec.json

# JSON을 YAML로 변환
yq eval -P docs/api-spec.json > docs/api-spec.yaml
```

## 📋 문서 작성 팁

### 좋은 API 문서의 특징
- **명확한 설명**: 각 엔드포인트의 목적과 사용법
- **실제 예시**: 요청/응답 데이터 예시 제공
- **에러 코드**: 가능한 에러 상황과 대응 방법
- **인증 정보**: JWT 토큰 사용법 등

### iOS 개발자가 필요한 정보
- **데이터 타입**: Swift에서 사용할 타입 정보
- **필수/선택 필드**: 어떤 필드가 필수인지 명시
- **에러 처리**: 네트워크 에러 시 대응 방법
- **페이징**: 대용량 데이터 처리 방법

## 🛠️ 개발 도구 추천

- **API 테스트**: Postman, Insomnia
- **문서 작성**: Swagger Editor, Stoplight Studio
- **코드 생성**: OpenAPI Generator
- **협업**: Notion, Confluence에 문서 링크 공유
