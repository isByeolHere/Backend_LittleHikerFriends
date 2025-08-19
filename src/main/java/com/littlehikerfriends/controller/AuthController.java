package com.littlehikerfriends.controller;

import com.littlehikerfriends.dto.SignupRequest;
import com.littlehikerfriends.dto.UserResponse;
import com.littlehikerfriends.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 새 계정을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "입력 데이터가 올바르지 않음"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일")
    })
    public ResponseEntity<UserResponse> signup(@RequestBody @Valid SignupRequest request) {
        // UserService의 signup 메서드 호출
        UserResponse userResponse = userService.signup(request);
        
        // 201 Created 상태코드와 함께 생성된 사용자 정보 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
    
    @GetMapping("/check-email")
    @Operation(summary = "이메일 중복 체크", description = "이메일이 이미 사용 중인지 확인합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "중복 체크 완료")
    })
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        
        EmailCheckResponse response = new EmailCheckResponse(email, exists);
        return ResponseEntity.ok(response);
    }
    
    // 이메일 중복 체크 응답 클래스
    public static class EmailCheckResponse {
        private String email;
        private boolean exists;
        private String message;
        
        public EmailCheckResponse(String email, boolean exists) {
            this.email = email;
            this.exists = exists;
            this.message = exists ? "이미 사용 중인 이메일입니다" : "사용 가능한 이메일입니다";
        }
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
