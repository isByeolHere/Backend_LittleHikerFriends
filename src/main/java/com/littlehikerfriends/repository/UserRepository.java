package com.littlehikerfriends.repository;

import com.littlehikerfriends.entity.Provider;
import com.littlehikerfriends.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // 이메일로 사용자 찾기 (로그인 시 사용)
    Optional<User> findByEmail(String email);
    
    // 이메일과 Provider로 사용자 찾기 (로그인 시 사용)
    Optional<User> findByEmailAndProvider(String email, Provider provider);
    
    // 소셜 로그인용: provider와 providerId로 사용자 찾기
    Optional<User> findByProviderAndProviderId(Provider provider, String providerId);
    
    // 닉네임으로 검색 (부분 일치)
    List<User> findByNicknameContaining(String nickname);
    
    // 특정 provider로 가입한 사용자들 조회
    List<User> findByProvider(Provider provider);
    
    // 이메일 중복 체크
    boolean existsByEmail(String email);
    
    // 이메일과 Provider 조합 중복 체크
    boolean existsByEmailAndProvider(String email, Provider provider);
    
    // 소셜 로그인 ID 중복 체크
    boolean existsByProviderAndProviderId(Provider provider, String providerId);
    
    // 특정 등산 모임에 참여한 사용자들 조회
    @Query("SELECT u FROM User u JOIN u.hikeMembers hm WHERE hm.hike.id = :hikeId")
    List<User> findUsersByHikeId(@Param("hikeId") Integer hikeId);
}
