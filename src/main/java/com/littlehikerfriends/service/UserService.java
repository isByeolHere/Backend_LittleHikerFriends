package com.littlehikerfriends.service;

import com.littlehikerfriends.dto.SignupRequest;
import com.littlehikerfriends.dto.UserResponse;
import com.littlehikerfriends.entity.Provider;
import com.littlehikerfriends.entity.User;
import com.littlehikerfriends.exception.DuplicateEmailException;
import com.littlehikerfriends.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
