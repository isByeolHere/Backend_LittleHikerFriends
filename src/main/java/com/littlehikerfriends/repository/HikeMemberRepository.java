package com.littlehikerfriends.repository;

import com.littlehikerfriends.entity.HikeMember;
import com.littlehikerfriends.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HikeMemberRepository extends JpaRepository<HikeMember, Integer> {
    
    // 특정 등산 모임의 모든 멤버 조회
    List<HikeMember> findByHikeId(Integer hikeId);
    
    // 특정 사용자의 모든 등산 모임 참여 기록 조회
    List<HikeMember> findByUserId(Integer userId);
    
    // 특정 등산 모임에서 특정 사용자 조회
    Optional<HikeMember> findByHikeIdAndUserId(Integer hikeId, Integer userId);
    
    // 특정 등산 모임의 관리자들 조회
    List<HikeMember> findByHikeIdAndRole(Integer hikeId, MemberRole role);
    
    // 특정 사용자가 관리자인 등산 모임들 조회
    List<HikeMember> findByUserIdAndRole(Integer userId, MemberRole role);
    
    // 특정 등산 모임의 멤버 수 조회
    long countByHikeId(Integer hikeId);
    
    // 특정 사용자가 특정 등산 모임에 참여했는지 확인
    boolean existsByHikeIdAndUserId(Integer hikeId, Integer userId);
    
    // 특정 등산 모임에서 특정 역할의 멤버 수 조회
    long countByHikeIdAndRole(Integer hikeId, MemberRole role);
    
    // 특정 등산 모임의 관리자 조회 (단일)
    @Query("SELECT hm FROM HikeMember hm WHERE hm.hike.id = :hikeId AND hm.role = 'ADMIN'")
    List<HikeMember> findAdminsByHikeId(@Param("hikeId") Integer hikeId);
}
