package com.littlehikerfriends.repository;

import com.littlehikerfriends.entity.Hike;
import com.littlehikerfriends.entity.HikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HikeRepository extends JpaRepository<Hike, Integer> {
    
    // 상태별 등산 모임 조회
    List<Hike> findByStatus(HikeStatus status);
    
    // 산 이름으로 검색
    List<Hike> findByMountainNameContaining(String mountainName);
    
    // 등산 모임 이름으로 검색
    List<Hike> findByNameContaining(String name);
    
    // 특정 기간 내 시작하는 등산 모임 조회
    List<Hike> findByStartAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 특정 사용자가 참여한 등산 모임들 조회
    @Query("SELECT h FROM Hike h JOIN h.hikeMembers hm WHERE hm.user.id = :userId")
    List<Hike> findHikesByUserId(@Param("userId") Integer userId);
    
    // 특정 사용자가 관리자인 등산 모임들 조회
    @Query("SELECT h FROM Hike h JOIN h.hikeMembers hm WHERE hm.user.id = :userId AND hm.role = 'ADMIN'")
    List<Hike> findHikesByUserIdAndAdminRole(@Param("userId") Integer userId);
    
    // 진행 중인 등산 모임들 조회 (현재 시간 기준)
    @Query("SELECT h FROM Hike h WHERE h.status = 'IN_PROGRESS' OR (h.status = 'PLANNED' AND h.startAt <= :now)")
    List<Hike> findActiveHikes(@Param("now") LocalDateTime now);
    
    // 특정 산에서 진행되는 등산 모임 개수
    long countByMountainName(String mountainName);
}
