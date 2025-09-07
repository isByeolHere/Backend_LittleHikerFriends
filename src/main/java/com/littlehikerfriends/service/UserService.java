package com.littlehikerfriends.service;

import com.littlehikerfriends.config.JwtUtil;
import com.littlehikerfriends.dto.*;
import com.littlehikerfriends.entity.Provider;
import com.littlehikerfriends.entity.User;
import com.littlehikerfriends.exception.DuplicateEmailException;
import com.littlehikerfriends.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SocialAuthService socialAuthService;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, SocialAuthService socialAuthService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.socialAuthService = socialAuthService;
    }
    
    /**
     * 이메일 로그인
     * 
     * @param request 로그인 요청 데이터
     * @return JWT 토큰과 사용자 정보
     * @throws RuntimeException 로그인 실패시
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 이메일과 EMAIL Provider로 사용자 조회
        Optional<User> userOptional = userRepository.findByEmailAndProvider(request.getEmail(), Provider.EMAIL);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("존재하지 않는 사용자입니다.");
        }
        
        User user = userOptional.get();
        
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        
        // 3. JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getProvider());
        
        // 4. 응답 생성
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getImageUrl(),
            user.getProvider()
        );
        
        return new LoginResponse(token, jwtExpiration / 1000, userInfo); // 초 단위로 변환
    }
    
    /**
     * 소셜 로그인
     * 
     * @param request 소셜 로그인 요청 데이터
     * @return JWT 토큰과 사용자 정보
     */
    public LoginResponse socialLogin(SocialLoginRequest request) {
        // 1. 기존 사용자 조회 (이메일 + Provider)
        Optional<User> existingUser = userRepository.findByEmailAndProvider(request.getEmail(), request.getProvider());
        
        User user;
        if (existingUser.isPresent()) {
            // 2-1. 기존 사용자인 경우 - 정보 업데이트
            user = existingUser.get();
            user.setNickname(request.getNickname()); // 닉네임 업데이트
            if (request.getImageUrl() != null) {
                user.setImageUrl(request.getImageUrl()); // 프로필 이미지 업데이트
            }
            user = userRepository.save(user);
        } else {
            // 2-2. 신규 사용자인 경우 - 사용자 생성
            user = createSocialUser(request);
        }
        
        // 3. JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getProvider());
        
        // 4. 응답 생성
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            user.getId(),
            user.getEmail(),
            user.getNickname(),
            user.getImageUrl(),
            user.getProvider()
        );
        
        return new LoginResponse(token, jwtExpiration / 1000, userInfo);
    }
    
    /**
     * 소셜 사용자 생성
     */
    private User createSocialUser(SocialLoginRequest request) {
        // 같은 이메일로 다른 Provider 사용자가 있는지 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 다른 방식으로 가입된 이메일입니다. (" + request.getEmail() + ")");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());
        user.setImageUrl(request.getImageUrl());
        user.setProvider(request.getProvider());
        user.setPasswordHash(null); // 소셜 로그인은 비밀번호 없음
        
        return userRepository.save(user);
    }
    
    /**
     * 이메일 회원가입
     * 
     * @param request 회원가입 요청 데이터
     * @return 생성된 사용자 정보
     * @throws DuplicateEmailException 이메일이 이미 존재하는 경우
     */
    public UserResponse signup(SignupRequest request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다: " + request.getEmail());
        }
        
        // 2. 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // 3. User 엔티티 생성
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);
        user.setNickname(request.getNickname());
        user.setImageUrl(request.getImageUrl());
        user.setProvider(Provider.EMAIL); // 이메일 가입이므로 EMAIL로 설정
        // createdAt은 User 엔티티의 기본 생성자에서 자동 설정됨
        
        // 4. 데이터베이스에 저장
        User savedUser = userRepository.save(user);
        
        // 5. UserResponse로 변환해서 반환
        return new UserResponse(savedUser);
    }
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 조회할 이메일
     * @return 사용자 정보 (없으면 null)
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::new)
                .orElse(null);
    }
    
    /**
     * 사용자 ID로 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 정보 (없으면 null)
     */
    @Transactional(readOnly = true)
    public UserResponse findById(Integer userId) {
        return userRepository.findById(userId)
                .map(UserResponse::new)
                .orElse(null);
    }
    
    /**
     * 이메일 중복 체크
     * 
     * @param email 체크할 이메일
     * @return 중복이면 true, 아니면 false
     */
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * 소셜 로그인 체크 - 기존 회원인지 확인
     */
    public SocialLoginCheckResponse socialLoginCheck(SocialLoginCheckRequest request) {
        // 1. 소셜 토큰으로 이메일 조회
        String email = socialAuthService.getEmailFromSocialToken(request.getProvider(), request.getSocialToken());
        
        // 2. 기존 사용자 조회
        Optional<User> existingUser = userRepository.findByEmailAndProvider(email, request.getProvider());
        
        if (existingUser.isPresent()) {
            // 기존 회원 - 바로 로그인 처리
            User user = existingUser.get();
            String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getProvider());
            
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getImageUrl(),
                user.getProvider()
            );
            
            LoginResponse loginResponse = new LoginResponse(token, jwtExpiration / 1000, userInfo);
            return new SocialLoginCheckResponse(true, email, loginResponse);
            
        } else {
            // 신규 회원 - 이메일만 반환
            return new SocialLoginCheckResponse(false, email);
        }
    }
    
    /**
     * 프로필 생성으로 회원가입 완료
     */
    public LoginResponse createProfile(ProfileCreateRequest request) {
        // 1. 소셜 토큰으로 이메일 조회
        String email = socialAuthService.getEmailFromSocialToken(request.getProvider(), request.getSocialToken());
        
        // 2. 이미 가입된 사용자인지 확인
        if (userRepository.existsByEmailAndProvider(email, request.getProvider())) {
            throw new RuntimeException("이미 가입된 사용자입니다");
        }
        
        // 3. 사용자 생성
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setNickname(request.getNickname());
        newUser.setImageUrl(request.getImageUrl());
        newUser.setProvider(request.getProvider());
        
        User savedUser = userRepository.save(newUser);
        
        // 4. JWT 토큰 발급
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getId(), savedUser.getProvider());
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getNickname(),
            savedUser.getImageUrl(),
            savedUser.getProvider()
        );
        
        return new LoginResponse(token, jwtExpiration / 1000, userInfo);
    }
}
